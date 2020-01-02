package de.netherspace.tools.spectre.parser

import com.sun.codemodel.*
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

        val t = graphQlIntrospectionResult.data?.__type ?: (graphQlIntrospectionResult.__type!!)
        // "name" can be null if "ofType" is given instead:
        val className = t.name ?: (t.ofType?.name!!)
        val clazz = createNewJClass(
                codeModel = codeModel,
                fullyqualifiedName = className
        )

        (t.fields ?: listOf())
                .asSequence()
                .map { clazz.field(JMod.PUBLIC, mapType(it.type, codeModel), it.name) }
                .forEach { logger.debug("added field '${it.name()}' with type ${it.type().name()}") }

        return codeModel
    }

    private fun mapType(type: GraphQlType, codeModel: JCodeModel): JType {
        val javaLangString = "java.lang.String"
        val javaUtilDate = "java.util.Date"
        val javaUtilList = "java.util.List"

        logger.debug("Mapping $type to a Java type...") // TODO: <-- erase!
        val typeName = type.name ?: type.ofType!!.name

        // no type name in the outer object: is this a wrapper type (LIST or NON_NULL)?
        if (typeName == null) {
            val kind: String = type.kind ?: type.ofType!!.kind!!
            return when (kind) {
                "NON_NULL" -> mapType(type.ofType!!, codeModel)
                "LIST" -> {
                    val innerType = mapType(type.ofType!!, codeModel)
                    val listType = jTypeForName(javaUtilList, codeModel)
                    // TODO: add "innerType" as type parameter to listType!
                    listType
                }
                else -> throw IllegalStateException("Unrecognized kind: $kind !")
            }
        }

        return when (typeName) {
            // these types map directly to Java platform types:
            "String" -> jTypeForName(javaLangString, codeModel)
            "Date" -> jTypeForName(javaUtilDate, codeModel)
            "ID" -> jTypeForName(javaLangString, codeModel) // TODO: see https://graphql.org/learn/schema/#scalar-types

            // all other types must explicitly be created:
            else -> jTypeForName(typeName, codeModel)
        }
    }

    private fun jTypeForName(fullyqualifiedName: String, codeModel: JCodeModel): JType {
        val clazz: JDefinedClass? = codeModel._getClass(fullyqualifiedName)
        return clazz ?: createNewJClass(
                codeModel = codeModel,
                fullyqualifiedName = fullyqualifiedName
        )
    }

    private fun createNewJClass(fullyqualifiedName: String, codeModel: JCodeModel): JDefinedClass {
        logger.debug("Creating new class: $fullyqualifiedName")
        return codeModel._class(fullyqualifiedName, ClassType.CLASS)
    }
}
