package com.gabrielbmoro.popcorn.domain.entity


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

data class PopcornNoRelationShipRule(
    override val target: String
) : BaseArcRule

data class PopcornJustWithRule(
    override val target: String,
    val with: List<String>
) : BaseArcRule

data class PopcornDoNotWithRule(
    override val target: String,
    val notWith: List<String>
) : BaseArcRule
