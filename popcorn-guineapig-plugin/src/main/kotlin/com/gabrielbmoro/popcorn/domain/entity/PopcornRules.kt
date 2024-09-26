package com.gabrielbmoro.popcorn.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class PopcornRules(
    val noRelationship: List<PopcornNoRelationShipRule>,
    val justWith: List<PopcornJustWithRule>,
    val doNotWith: List<PopcornDoNotWithRule>
) {
    fun all() = noRelationship + justWith + doNotWith
}

interface BaseArcRule {
    val target: String
}

@Serializable
data class PopcornNoRelationShipRule(
    override val target: String
) : BaseArcRule

@Serializable
data class PopcornJustWithRule(
    override val target: String,
    val with: List<String>
) : BaseArcRule

@Serializable
data class PopcornDoNotWithRule(
    override val target: String,
    val notWith: List<String>
) : BaseArcRule
