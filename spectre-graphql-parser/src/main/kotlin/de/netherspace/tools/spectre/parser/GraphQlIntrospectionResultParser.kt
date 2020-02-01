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

    private val javaLangString = "java.lang.String"
    private val javaLangBoolean = "java.lang.Boolean"
    private val javaLangDouble = "java.lang.Double"
    private val javaLangInteger = "java.lang.Integer"
    private val javaUtilDate = "java.util.Date"
    private val javaUtilList = "java.util.List"

    private val blacklistedClassNamesToDefaultClasses = mapOf(
            "String" to javaLangString,
            "Boolean" to javaLangBoolean,
            "Float" to javaLangDouble,
            "Int" to javaLangInteger,
            "Date" to javaUtilDate,
            "ID" to javaLangString // TODO: see https://graphql.org/learn/schema/#scalar-types
    )

    @UseExperimental(ImplicitReflectionSerializer::class)
    fun unmarshalIntrospectionResult(graphQlIntrospectionResult: InputStream): GraphQlIntrospectionResult {
        val jsonString: String = graphQlIntrospectionResult
                .bufferedReader()
                .readText()
        return Json.parse(jsonString)
    }

    fun generateCodeModel(graphQlIntrospectionResult: GraphQlIntrospectionResult, packageName: String): JCodeModel {
        val codeModel = JCodeModel()

        val schemaTypes = graphQlIntrospectionResult.data?.__schema?.types
        return if (schemaTypes != null) { // TODO: use a "when", once this is a sealed class!
            createClassesForTypes(
                    types = schemaTypes,
                    packageName = packageName,
                    codeModel = codeModel
            )
            codeModel

        } else {
            val t = graphQlIntrospectionResult.data?.__type ?: (graphQlIntrospectionResult.__type!!)
            createClassForType(
                    gqlType = t,
                    packageName = packageName,
                    codeModel = codeModel
            )
            codeModel
        }
    }

    private fun createClassesForTypes(types: List<GraphQlType>, packageName: String, codeModel: JCodeModel): List<JDefinedClass> {
        return types
                .asSequence()
                .filter { !it.name!!.startsWith("__") } // skip "__xxx" types // TODO: the NPE check ("!!") is needed because "name | ofKind" is not a sum type yet!
                .filter { it.name !in blacklistedClassNamesToDefaultClasses }
                .map {
                    createClassForType(
                            gqlType = it,
                            packageName = packageName,
                            codeModel = codeModel
                    )
                }
                .toList()
    }

    private fun createClassForType(gqlType: GraphQlType, packageName: String, codeModel: JCodeModel): JDefinedClass {
        // "name" can be null if "ofType" is given instead:
        val className = gqlType.name ?: (gqlType.ofType?.name!!)
        val clazz = createNewJClass(
                codeModel = codeModel,
                className = className,
                packageName = packageName
        )

        (gqlType.fields ?: listOf())
                .asSequence()
                .map { clazz.field(JMod.PUBLIC, mapType(it.type, codeModel, packageName), it.name) }
                .forEach { logger.debug("added field '${it.name()}' with type ${it.type().name()}") }

        return clazz
    }

    private fun mapType(type: GraphQlType, codeModel: JCodeModel, packageName: String): JType {
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
            // some types map directly to Java platform types, all other types
            // are explicitly created via their typeName:
            val tn = blacklistedClassNamesToDefaultClasses[typeName] ?: typeName
            jTypeForName(tn, codeModel, packageName)
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
