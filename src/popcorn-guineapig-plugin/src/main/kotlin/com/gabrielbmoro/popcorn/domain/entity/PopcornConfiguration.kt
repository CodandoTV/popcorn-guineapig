package com.gabrielbmoro.popcorn.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class PopCornConfiguration(
    val project: PopCornProject,
    val rules: PopcornRules
)

@Serializable
data class PopCornProject(
    val type: String,
    val group: String
)

@Serializable
data class PopcornRules(
    val no_relation_ship: List<PopcornNoRelationShipRule>,
    val just_with: List<PopcornJustWithRule>,
    val do_not_with: List<PopcornDoNotWithRule>
)

@Serializable
data class PopcornNoRelationShipRule(
    val regex_enabled: Boolean,
    val target: String
)

@Serializable
data class PopcornJustWithRule(
    val regex_enabled: Boolean,
    val target: String,
    val with: List<String>
)

@Serializable
data class PopcornDoNotWithRule(
    val regex_enabled: Boolean,
    val target: String,
    val not_with: List<String>
)