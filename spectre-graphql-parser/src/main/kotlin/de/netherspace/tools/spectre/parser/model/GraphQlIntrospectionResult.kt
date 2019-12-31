package de.netherspace.tools.spectre.parser.model

import kotlinx.serialization.Serializable

@Serializable
data class GraphQlIntrospectionResult(
        val __type: GraphQlType
)

@Serializable
data class GraphQlType(
        val name: String,
        var fields: List<GraphQlField>? = null
)

@Serializable
data class GraphQlField(
        val name: String,
        val type: GraphQlType
)
