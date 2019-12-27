package de.netherspace.tools.graphql.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SpectreGraphQlMojoTest extends AbstractMojoTestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testMojoGoal() throws Exception {
        // TODO: ...
    }

    /*@Test
    public void testShorthandQueryParsing() throws MojoFailureException, MojoExecutionException {
        final SpectreGraphQlMojo spectreGraphQlMojo = new SpectreGraphQlMojo();
        assertThat(spectreGraphQlMojo, is(not(nullValue())));
        spectreGraphQlMojo.execute();
//        final GraphQlParser parser = new GraphQlParser(new CustomMavenPluginLogger());
//        final graphql_grammarParser.DocumentContext parseTree = parser.parseGraphQlSchemaFile(shorthandQuery());
    }*/
}
