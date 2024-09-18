package com.gabrielbmoro.popcorn.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class PopcornRules(
    val noRelationship: List<PopcornNoRelationShipRule>,
    val justWith: List<PopcornJustWithRule>,
    val doNotWith: List<PopcornDoNotWithRule>
)

@Serializable
data class PopcornNoRelationShipRule(
    val regexEnabled: Boolean,
    val target: String
)

@Serializable
data class PopcornJustWithRule(
    val regexEnabled: Boolean,
    val target: String,
    val with: List<String>
)

@Serializable
data class PopcornDoNotWithRule(
    val regexEnabled: Boolean,
    val target: String,
    val notWith: List<String>
)
