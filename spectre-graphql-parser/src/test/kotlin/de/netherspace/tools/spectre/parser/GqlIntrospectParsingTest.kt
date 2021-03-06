package de.netherspace.tools.spectre.parser

import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.parse
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.nullValue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.io.InputStream
import org.hamcrest.Matchers.`is` as Is

class GqlIntrospectParsingTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder() // TODO: creates a temp. folder under /tmp/ -> should rather be under /target!

    @Test
    @UseExperimental(ImplicitReflectionSerializer::class)
    fun testTrivialJsonParsing() {
        val (name, age, city) = Json.parse<User>("""{name: "John", age: 31, city: "New York"}""")
        assertThat(name, Is("John"))
        assertThat(age, Is(31))
        assertThat(city, Is("New York"))
    }

    @Test
    fun testSimpleIntrospectResultParsing() {
        val introspectionResult = load("introspection-result-01.json")
        assertThat(introspectionResult, Is(not(nullValue())))

        val introspectionResultParser = GraphQlIntrospectionResultParser()
        val gqlIntroResultObject = introspectionResultParser.unmarshalIntrospectionResult(introspectionResult)
        assertThat(gqlIntroResultObject.__type, Is(not(nullValue())))
        assertThat(gqlIntroResultObject.data, Is(nullValue()))
        assertThat(gqlIntroResultObject.__type!!.name, Is("User"))

        assertThat(gqlIntroResultObject.__type!!.fields, Is(not(nullValue())))
        assertThat(gqlIntroResultObject.__type!!.fields!!.isEmpty(), Is(false))
        val idFields = gqlIntroResultObject
                .__type!!
                .fields!!
                .asSequence()
                .filter { it.name == "id" }
                .toList()
        assertThat(idFields.size, Is(1))
        val idField = idFields.first()
        assertThat(idField.type.name, Is("String"))

        val packageName = "de.test.package.yeah"
        val codeModel = introspectionResultParser.generateCodeModel(
                graphQlIntrospectionResult = gqlIntroResultObject,
                packageName = packageName
        )
        val fieldCount = codeModel
                ._getClass("$packageName.User") // TODO: write extension function to get all classes
                .fields()
                .size
        assertThat(fieldCount, Is(not(0)))

        val genJavaFiles = introspectionResultParser.writeCodeModel(
                codeModel = codeModel,
                destFolder = temporaryFolder.newFolder()
        )
        assertThat(genJavaFiles.size, Is(1))
        compareToExpectedClasses(genJavaFiles, "simple-introspec-test")
    }


    @Test
    fun testDroidExampleIntroResParsing() {
        val introspectionResult = load("droid-type.json")
        assertThat(introspectionResult, Is(not(nullValue())))

        val introspectionResultParser = GraphQlIntrospectionResultParser()
        val gqlIntroResultObject = introspectionResultParser.unmarshalIntrospectionResult(introspectionResult)
        assertThat(gqlIntroResultObject.data, Is(not(nullValue())))
        assertThat(gqlIntroResultObject.__type, Is(nullValue()))

        assertThat(gqlIntroResultObject.data!!.__type, Is(not(nullValue())))
        assertThat(gqlIntroResultObject.data!!.__schema, Is(nullValue()))

        assertThat(gqlIntroResultObject.data!!.__type!!.name, Is("Droid"))

        assertThat(gqlIntroResultObject.data!!.__type!!.fields, Is(not(nullValue())))
        assertThat(gqlIntroResultObject.data!!.__type!!.fields!!.isEmpty(), Is(false))
        val pfFields = gqlIntroResultObject
                .data!!
                .__type!!
                .fields!!
                .asSequence()
                .filter { it.name == "primaryFunction" }
                .toList()
        assertThat(pfFields.size, Is(1))
        val pfField = pfFields.first()
        assertThat(pfField.type.name, Is("String"))

        val codeModel = introspectionResultParser.generateCodeModel(
                graphQlIntrospectionResult = gqlIntroResultObject,
                packageName = "de.test.package.sw"
        )
        // TODO: check field count on Droid class!

        val genJavaFiles = introspectionResultParser.writeCodeModel(
                codeModel = codeModel,
                destFolder = temporaryFolder.newFolder()
        )
        assertThat(genJavaFiles.size, Is(3))
        compareToExpectedClasses(genJavaFiles, "droid-example-test")
    }


    @Test
    fun testStarWarsExampleIntroResParsing() {
        val introspectionResult = load("starwars-intro-result.json")
        assertThat(introspectionResult, Is(not(nullValue())))

        val introspectionResultParser = GraphQlIntrospectionResultParser()
        val gqlIntroResultObject = introspectionResultParser.unmarshalIntrospectionResult(introspectionResult)
        assertThat(gqlIntroResultObject.data, Is(not(nullValue())))
        assertThat(gqlIntroResultObject.__type, Is(nullValue()))

        assertThat(gqlIntroResultObject.data!!.__type, Is(nullValue()))
        assertThat(gqlIntroResultObject.data!!.__schema, Is(not(nullValue())))

        assertThat(gqlIntroResultObject.data!!.__schema!!.types, Is(not(nullValue())))
        assertThat(gqlIntroResultObject.data!!.__schema!!.types!!.size, Is(27))

        // TODO: check the rest of the structure (of the deserialized gqlIntroResultObject)!

        val codeModel = introspectionResultParser.generateCodeModel(
                graphQlIntrospectionResult = gqlIntroResultObject,
                packageName = "de.test.package.sw"
        )

        // TODO: check field count(s)!

        val genJavaFiles = introspectionResultParser.writeCodeModel(
                codeModel = codeModel,
                destFolder = temporaryFolder.newFolder()
        )
        assertThat(genJavaFiles.size, Is(14))
        compareToExpectedClasses(genJavaFiles, "starwars-types-only-test")
    }


    /**
     * Compares the generated Java classes against expected ones.
     */
    private fun compareToExpectedClasses(genJavaFiles: List<File>, resourceFolderName: String) {
        val expFolder = "expectations/$resourceFolderName"
        // TODO: Honor the package name as well! (There might be multiple generated classes,
        // TODO: e.g. "User", in different packages.)
        val comparedClasses = genJavaFiles
                .map { it.absoluteFile to it.name }
                .map { it.first to load(expFolder, it.second) }
                .map { compareFiles(it) }
                .partition { it.first }

        val equalClasses = comparedClasses.first
        val differingClasses = comparedClasses.second
        assertThat(equalClasses.size, Is(genJavaFiles.size))
        assertThat(differingClasses.size, Is(0))
    }

    private fun compareFiles(files: Pair<File, File>): Pair<Boolean, File> {
        val genJavaClass = files.first.readText()
        val expJavaClass = files.second.readText()
        val filesAreEqual = genJavaClass == expJavaClass
        assertThat(genJavaClass, Is(expJavaClass))
        return Pair(filesAreEqual, files.first)
    }

    private fun load(expFolder: String, file: String): File {
        val resource: String = GqlIntrospectParsingTest::class
                .java
                .getResource("/$expFolder/$file")
                .file
        return File(resource)
    }

    private fun load(file: String): InputStream {
        val queryFilesFolderName = "graphql-introspect-results"
        return GqlIntrospectParsingTest::class
                .java
                .getResourceAsStream("/$queryFilesFolderName/$file")!!
    }

    @Serializable
    data class User(val name: String, val age: Int, val city: String)
}
