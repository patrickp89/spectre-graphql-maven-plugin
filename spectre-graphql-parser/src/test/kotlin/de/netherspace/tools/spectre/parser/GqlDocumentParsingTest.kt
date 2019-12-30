package de.netherspace.tools.spectre.parser

import de.netherspace.tools.spectre.utils.CustomMavenPluginLogger
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.nullValue
import org.junit.Test
import java.io.InputStream
import org.hamcrest.Matchers.`is` as Is

class GqlDocumentParsingTest {

    @Test
    fun testShorthandQueryParsing() {
        val parser = GraphQlDocumentParser(CustomMavenPluginLogger())
        val shorthandQuery = load("shorthand-query-single-field.graphql")
        val parseTree = parser.parseGraphQlSchemaFile(shorthandQuery)
        assertThat(parseTree, Is(not(nullValue())))

        val stringTree = parser.parseTreeToStringTree(parseTree)
        assertThat(stringTree, Is(not(nullValue())))

        val expTree = "(document (definition (operationDefinition (selectionSet { (selection (field (name hero))) }))))"
        assertThat(stringTree, Is(expTree))
    }

    private fun load(file: String): InputStream {
        val queryFilesFolderName = "graphql-documents"
        return GqlDocumentParsingTest::class
                .java
                .getResourceAsStream("/$queryFilesFolderName/$file")!!
    }
}
