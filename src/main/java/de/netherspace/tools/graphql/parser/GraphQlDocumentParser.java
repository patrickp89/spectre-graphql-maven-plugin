package de.netherspace.tools.graphql.parser;

import de.netherspace.tools.graphql_grammarLexer;
import de.netherspace.tools.graphql_grammarParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenFactory;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.UnbufferedCharStream;
import org.apache.maven.plugin.logging.Log;

import java.io.InputStream;

public class GraphQlDocumentParser {

    private final Log log;

    public GraphQlDocumentParser(Log log) {
        this.log = log;
    }

    public graphql_grammarParser.DocumentContext parseGraphQlSchemaFile(InputStream schemaFile) {
        // create lexer and token stream:
        final CharStream inputCharStream = new UnbufferedCharStream(schemaFile);
        final graphql_grammarLexer lexer = new graphql_grammarLexer(inputCharStream);
        lexer.setTokenFactory(new CommonTokenFactory(true)); // circumvents a bug in ANTLR v4.7!
        final CommonTokenStream tokenStream = new CommonTokenStream(lexer);

        // create the actual parser and AST:
        final graphql_grammarParser parser = new graphql_grammarParser(tokenStream);
        final graphql_grammarParser.DocumentContext parseTree = parser.document();

        // return the AST:
        final String stringTree = parseTree.toStringTree(parser);
        log.debug("Parse tree is: " + stringTree);
        return parseTree;
    }

    public void walkGraphQlDocumentTree() {
        // TODO: final GraphQlDocumentVisitor graphQlDocumentVisitor = new GraphQlDocumentVisitor();
    }
}
