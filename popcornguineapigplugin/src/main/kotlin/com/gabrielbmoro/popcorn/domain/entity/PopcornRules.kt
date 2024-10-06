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

    fun message(): String
}

data class PopcornNoRelationShipRule(
    override val target: String
) : BaseArcRule {
    override fun message(): String {
        return "Rule enforces you should not have relationships"
    }
}

data class PopcornJustWithRule(
    override val target: String,
    val with: List<String>
) : BaseArcRule {
    override fun message(): String {
        return "Rule enforces you should depends on $with"
    }
}

data class PopcornDoNotWithRule(
    override val target: String,
    val notWith: List<String>
) : BaseArcRule {
    override fun message(): String {
        return "Rule enforces you should not depends on $notWith"
    }
}
