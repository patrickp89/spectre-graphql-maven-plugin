package de.netherspace.tools.spectre.parser

import com.sun.codemodel.JCodeModel
import com.sun.codemodel.JMod
import de.netherspace.tools.spectre.parser.model.GraphQlIntrospectionResult
import de.netherspace.tools.spectre.parser.model.GraphQlType
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.parse
import org.slf4j.LoggerFactory
import java.io.InputStream

/**
 * A parser for GraphQL introspection (JSON) results. Generates POJOs for all types it encounters.
 */
class GraphQlIntrospectionResultParser : BaseCodeModelWriter {

    private val logger = LoggerFactory.getLogger(GraphQlIntrospectionResultParser::class.java)

    @UseExperimental(ImplicitReflectionSerializer::class)
    fun unmarshalIntrospectionResult(graphQlIntrospectionResult: InputStream): GraphQlIntrospectionResult {
        val jsonString: String = graphQlIntrospectionResult
                .bufferedReader()
                .readText()
        return Json.parse(jsonString)
    }

    fun generateCodeModel(graphQlIntrospectionResult: GraphQlIntrospectionResult, packageName: String): JCodeModel {
        val codeModel = JCodeModel()
        codeModel._package(packageName) // TODO: this does not work! why??

        val className = graphQlIntrospectionResult.__type.name
        logger.info("Creating new class: $className")
        val clazz = codeModel._class(className)

        graphQlIntrospectionResult
                .__type
                .fields!!
                .asSequence()
                .map { clazz.field(JMod.PUBLIC, mapType(it.type), it.name) }
                .forEach { logger.debug("added field ${it.name()} with type ${it.type().name()}") }

        return codeModel
    }

    private fun mapType(type: GraphQlType): Class<java.lang.String> {
        return java.lang.String::class.java
        // TODO: do a proper mapping isntead:
        // TODO: String -> java.lang.String::class.java
        // TODO: ...
        // TODO: Date -> ...
    }
}
