package de.netherspace.tools.spectre.parser;

import de.netherspace.tools.spectre.graphql_grammarParser;
import de.netherspace.tools.spectre.utils.CustomMavenPluginLogger;
import org.junit.Ignore;
import org.junit.Test;

import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class GqlParserTest {

    @Test
    public void testShorthandQueryParsing() {
        final GraphQlDocumentParser parser = new GraphQlDocumentParser(new CustomMavenPluginLogger());
        final InputStream shorthandQuery = load("shorthand-query-single-field.graphql");

        final graphql_grammarParser.DocumentContext parseTree = parser.parseGraphQlSchemaFile(shorthandQuery);
        assertThat(parseTree, is(not(nullValue())));

        final String stringTree = parser.parseTreeToStringTree(parseTree);
        assertThat(stringTree, is(not(nullValue())));

        final String expTree = "(document (definition (operationDefinition (selectionSet { (selection (field (name hero))) }))))";
        assertThat(stringTree, is(expTree));
    }

    @Test
    @Ignore
    public void testSimpleMutationParsing() {
        final GraphQlDocumentParser parser = new GraphQlDocumentParser(new CustomMavenPluginLogger());
        final InputStream simpleMutation = load("simple-mutation.graphql");

        final graphql_grammarParser.DocumentContext parseTree = parser.parseGraphQlSchemaFile(simpleMutation);
        assertThat(parseTree, is(not(nullValue())));
    }

    @Test
    @Ignore
    public void testSimpleQueryParsing() {
        final GraphQlDocumentParser parser = new GraphQlDocumentParser(new CustomMavenPluginLogger());
        final InputStream simpleQuery = load("simple-query.graphql");
        final graphql_grammarParser.DocumentContext parseTree = parser.parseGraphQlSchemaFile(simpleQuery);
        assertThat(parseTree, is(not(nullValue())));
    }

    @Test
    @Ignore
    public void testValidFragmentedStarWarsQueryParsing() {
        final GraphQlDocumentParser parser = new GraphQlDocumentParser(new CustomMavenPluginLogger());
        final InputStream validFragmentedStarWarsQuery = load("valid-star-wars-query-01.graphql");
        final graphql_grammarParser.DocumentContext parseTree = parser.parseGraphQlSchemaFile(validFragmentedStarWarsQuery);
        assertThat(parseTree, is(not(nullValue())));
    }

    private InputStream load(String file) {
        final String queryFilesFolderName = "graphql-documents/";
        return GqlParserTest
                .class
                .getClassLoader()
                .getResourceAsStream(queryFilesFolderName + file);
    }
}
