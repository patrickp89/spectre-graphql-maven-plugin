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
        val t = graphQlIntrospectionResult.data?.__type ?: (graphQlIntrospectionResult.__type!!)

        // "name" can be null if "ofType" is given instead:
        val className = t.name ?: (t.ofType?.name!!)
        val clazz = createNewJClass(
                codeModel = codeModel,
                className = className,
                packageName = packageName
        )

        (t.fields ?: listOf())
                .asSequence()
                .map { clazz.field(JMod.PUBLIC, mapType(it.type, codeModel, packageName), it.name) }
                .forEach { logger.debug("added field '${it.name()}' with type ${it.type().name()}") }

        return codeModel
    }

    private fun mapType(type: GraphQlType, codeModel: JCodeModel, packageName: String): JType {
        val javaLangString = "java.lang.String"
        val javaUtilDate = "java.util.Date"
        val javaUtilList = "java.util.List"

        logger.debug("Mapping $type to a Java type...") // TODO: <-- erase!
        val typeName: String? = type.name

        // no type name in the outer object: is this a wrapper type (e.g. LIST or NON_NULL)?
        return if (typeName == null) {
            when (val kind: String = type.kind!!) {
                "NON_NULL" -> mapType(type.ofType!!, codeModel, packageName)
                "LIST" -> {
                    logger.debug("\nrecursive call: mapType(${type.ofType!!} , ...)") // TODO: <-- erase!!!
                    val innerType = mapType(type.ofType!!, codeModel, packageName) as JClass
                    val listType = jTypeForName(javaUtilList, codeModel, packageName) as JClass
                    logger.debug("I found a LIST type! The inner type is: ${innerType.fullName()}")
                    listType.narrow(innerType)
                }
                "INTERFACE" -> throw IllegalStateException("INTER-FUCKING-FACE: $kind !") // TODO: we must ALWAYS check for the type (i.e. if it is an interface), even when typeName != null!
                else -> throw IllegalStateException("Unrecognized kind: $kind !")
            }
        } else {
            when (typeName) {
                // these types map directly to Java platform types:
                "String" -> jTypeForName(javaLangString, codeModel, packageName)
                "Date" -> jTypeForName(javaUtilDate, codeModel, packageName)
                "ID" -> jTypeForName(javaLangString, codeModel, packageName) // TODO: see https://graphql.org/learn/schema/#scalar-types

                // all other types must explicitly be created:
                else -> jTypeForName(typeName, codeModel, packageName)
            }
        }
    }

    private fun jTypeForName(className: String, codeModel: JCodeModel, packageName: String): JType {
        return if (checkPackageBlacklist(className)) {
            val clazz: JClass? = codeModel.ref(className)
            clazz!!
        } else {
            val clazz: JDefinedClass? = codeModel._getClass(className)
            clazz ?: createNewJClass(
                    className = className,
                    codeModel = codeModel,
                    packageName = packageName
            )
        }
    }

    private fun checkPackageBlacklist(fullyQualifiedName: String): Boolean {
        val packageBlacklist = listOf(
                "java.util",
                "java.lang"
        )
        return packageBlacklist
                .map { fullyQualifiedName.startsWith(prefix = it, ignoreCase = true) }
                .filter { it }
                .count() > 0
    }

    private fun createNewJClass(className: String, codeModel: JCodeModel, packageName: String): JDefinedClass {
        val fullyQualifiedName = fullyQualifiedName(packageName = packageName, className = className)
        logger.debug("Creating new class: $fullyQualifiedName")
        return codeModel._class(fullyQualifiedName, ClassType.CLASS)
    }

    private fun fullyQualifiedName(clazz: JClass, packageName: String): Any {
        return fullyQualifiedName(
                className = clazz.name(),
                packageName = packageName
        )
    }

    private fun fullyQualifiedName(className: String, packageName: String): String {
        return "$packageName.$className"
    }
}
