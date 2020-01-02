package de.netherspace.tools.spectre.parser.model

import kotlinx.serialization.Serializable

@Serializable
data class GraphQlIntrospectionResult( // TODO: use a sealed class instead!
        // either, or:
        var data: GraphQlWrapper? = null,
        var __type: GraphQlType? = null
)

@Serializable
data class GraphQlWrapper(
        val __type: GraphQlType
)

@Serializable
data class GraphQlType(
        var name: String? = null, // "name" can be null if "ofType" is given instead!
        var ofType: GraphQlType? = null,
        var fields: List<GraphQlField>? = null,
        var kind: String? = null
)

@Serializable
data class GraphQlField(
        val name: String,
        val type: GraphQlType
)
