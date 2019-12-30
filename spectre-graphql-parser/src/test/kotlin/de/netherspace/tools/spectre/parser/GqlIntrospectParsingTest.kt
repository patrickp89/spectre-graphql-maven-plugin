package de.netherspace.tools.spectre.parser

import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.parse
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.nullValue
import org.junit.Test
import java.io.InputStream
import org.hamcrest.Matchers.`is` as Is

class GqlIntrospectParsingTest {

    @Test
    @UseExperimental(ImplicitReflectionSerializer::class)
    fun testSimpleIntrospectResultParsing() {
        val res1 = load("introspection-result-01.json")
        assertThat(res1, Is(not(nullValue())))

        val (name, age, city) = Json.parse<User>("""{name: "John", age: 31, city: "New York"}""")
        assertThat(name, Is("John"))
        assertThat(age, Is(31))
        assertThat(city, Is("New York"))
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
