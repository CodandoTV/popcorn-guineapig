package com.github.codandotv.popcorn.domain.input

import com.github.codandotv.popcorn.domain.rules.PopcornGuineaPigRule

@Deprecated(
    message = """
        The support for individual plugins will be removed soon, 
        please replace the current configuration to use popcornGuineaPigParent instead. 
        More information you can check 
        [here](https://codandotv.github.io/popcorn-guineapig/1-getting-started/#22-parent-plugin)
        """,
    level = DeprecationLevel.WARNING,
)
data class PopcornConfiguration(
    val project: PopcornProject,
    val rules: List<PopcornGuineaPigRule>
)
