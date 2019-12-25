package de.netherspace.tools.graphql.parser;

import de.netherspace.tools.graphql_grammarParser;
import de.netherspace.tools.utils.CustomMavenPluginLogger;
import org.junit.Test;

import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class GqlParserTest {

    private final String queryFilesFolderName = "graphql-queries/";

    @Test
    public void testShorthandQueryParsing() {
        final GraphQlDocumentParser parser = new GraphQlDocumentParser(new CustomMavenPluginLogger());
        final graphql_grammarParser.DocumentContext parseTree = parser.parseGraphQlSchemaFile(shorthandQuery());
        assertThat(parseTree, is(not(nullValue())));
    }

    private InputStream shorthandQuery() {
        return load(queryFilesFolderName + "shorthand-query-single-field.graphql");
    }

    private InputStream simpleMutation() {
        return load(queryFilesFolderName + "simple-mutation.graphql");
    }

    private InputStream simpleQuery() {
        return load(queryFilesFolderName + "simple-query.graphql");
    }

    private InputStream validFragmentedStarWarsQuery() {
        return load(queryFilesFolderName + "valid-star-wars-query-01.graphql");
    }

    private InputStream load(String file) {
        return GqlParserTest
                .class
                .getClassLoader()
                .getResourceAsStream(file);
    }
}
