package de.netherspace.tools.spectre.plugin;

import de.netherspace.tools.spectre.graphql_grammarParser;
import de.netherspace.tools.spectre.parser.GraphQlDocumentParser;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.*;
import java.util.stream.Collectors;

@Mojo(name = "generate-graphql-types")
public class SpectreGraphQlMojo extends AbstractMojo {
    /**
     * Path to the schema file that will be used for the code generation.
     */
    @Parameter(property = "generate-graphql-types.schemaFileLocation",
            defaultValue = "graphql/schema.gql")
    private String schemaFile;

    /**
     * The (absolute) path to the source folder of the Maven project
     * that invoked (!) the spectre plugin.
     */
    @Parameter(property = "project.basedir",
            readonly = true, required = true)
    private File projectBasedir;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Parsing schema file: " + schemaFile);

        final File resourcesFolder = new File(projectBasedir, "/src/main/resources/");
        final File absSchemaFile = new File(resourcesFolder, schemaFile);

        try {
            final InputStream schemaFileInputStream = new FileInputStream(absSchemaFile);
            //#######################################################
            // TODO: erase!
            final String schemaDoc = new BufferedReader(new InputStreamReader(new FileInputStream(absSchemaFile)))
                    .lines()
                    .collect(Collectors.joining("\n"));
            getLog().info("Schema content:\n" + schemaDoc);
            //#######################################################

            generateTypes(schemaFileInputStream);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            final String m = "Could not read '" + absSchemaFile.getAbsolutePath() + "'!";
            getLog().error(m);
            throw new MojoExecutionException(m);
        }
    }

    private void generateTypes(InputStream schemaFileInputStream) throws MojoFailureException {
        final GraphQlDocumentParser parser = new GraphQlDocumentParser(getLog());
        final graphql_grammarParser.DocumentContext parseTree = parser.parseGraphQlSchemaFile(schemaFileInputStream);

        if (parseTree == null) {
            final String m = "Could not parse schema!";
            getLog().error(m);
            throw new MojoFailureException(m);
        }

        //#######################################################
        // TODO: erase!
        final String stringTree = parser.parseTreeToStringTree(parseTree);
        getLog().info("Resulting parse tree: " + stringTree);
        //#######################################################
    }
}
