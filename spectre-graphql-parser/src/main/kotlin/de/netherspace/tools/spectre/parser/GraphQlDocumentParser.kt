package de.netherspace.tools.spectre.parser

import de.netherspace.tools.spectre.graphql_grammarLexer
import de.netherspace.tools.spectre.graphql_grammarParser
import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CommonTokenFactory
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.UnbufferedCharStream
import org.apache.maven.plugin.logging.Log
import org.slf4j.LoggerFactory
import java.io.InputStream

class GraphQlDocumentParser(
        private val log: Log // TODO: this should NOT be a "org.apache.maven.plugin.logging.Log"!
) {

    private val logger = LoggerFactory.getLogger(GraphQlDocumentParser::class.java)
    private lateinit var parser: graphql_grammarParser

    fun parseGraphQlSchemaFile(schemaFile: InputStream): graphql_grammarParser.DocumentContext {
        // create lexer and token stream:
        val inputCharStream: CharStream = UnbufferedCharStream(schemaFile)
        val lexer = graphql_grammarLexer(inputCharStream)
        lexer.tokenFactory = CommonTokenFactory(true) // circumvents a bug in ANTLR v4.7!
        val tokenStream = CommonTokenStream(lexer)

        // create the actual parser and AST:
        parser = graphql_grammarParser(tokenStream)
        val parseTree = parser.document()

        // return the AST:
        log.debug("Parse tree is: " + parseTreeToStringTree(parseTree))
        return parseTree
    }

    fun walkGraphQlDocumentTree() {
        TODO("not implemented")
    }

    fun parseTreeToStringTree(parseTree: graphql_grammarParser.DocumentContext): String {
        return parseTree.toStringTree(parser)
    }
}
