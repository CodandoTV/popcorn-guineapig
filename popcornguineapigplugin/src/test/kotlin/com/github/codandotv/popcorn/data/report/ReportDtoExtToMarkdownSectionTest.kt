package com.github.codandotv.popcorn.data.report

import com.github.codandotv.popcorn.data.dto.HowCanIFixThisItemDto
import org.junit.Test
import kotlin.test.assertEquals

class ReportDtoExtToMarkdownSectionTest {
    @Test
    fun `Given a how to fix section object x when toMarkdownSection is invoked then check markdown result`() {
        // arrange
        val howCanIFixThisItemDto = listOf(
            HowCanIFixThisItemDto(
                "DoNotWith", "You should not depends on x, and, y"
            ),
            HowCanIFixThisItemDto("JustWith", "You should depends only on x")
        )

        // act
        val result = howCanIFixThisItemDto.toMarkdownSection()

        // assert
        assertEquals(
            "- DoNotWith: You should not depends on x, and, y\n" +
                    "- JustWith: You should depends only on x",
            result
        )
    }

    @Test
    fun `Given an empty how to fix section when toMarkdownSection is invoked then check markdown result`() {
        // arrange
        val howCanIFixThisItemDto = emptyList<HowCanIFixThisItemDto>()

        // act
        val result = howCanIFixThisItemDto.toMarkdownSection()

        // assert
        assertEquals(
            "",
            result
        )
    }

    @Test
    fun `Given some list of items when toEnumeratedMarkdownList is called then check the markdown text`() {
        val input = listOf("Chuck Norris", "Bruce lee", "Popo Freitas")

        val result = input.toEnumeratedMarkdownList()

        assertEquals(
            "1. Chuck Norris\n2. Bruce lee\n3. Popo Freitas",
            result
        )
    }

    @Test
    fun `Given an empty list of items when toEnumeratedMarkdownList is called then check the markdown text`() {
        val input = emptyList<String>()

        val result = input.toEnumeratedMarkdownList()

        assertEquals(
            "",
            result
        )
    }

    @Test
    fun `Given some list of items when toSimpleMarkdownList is called then check the markdown text`() {
        val input = listOf("Chuck Norris", "Bruce lee", "Popo Freitas")

        val result = input.toSimpleMarkdownList()

        assertEquals(
            "- Chuck Norris\n- Bruce lee\n- Popo Freitas",
            result
        )
    }

    @Test
    fun `Given an empty list of items when toSimpleMarkdownList is called then check the markdown text`() {
        val input = emptyList<String>()

        val result = input.toSimpleMarkdownList()

        assertEquals(
            "",
            result
        )
    }
}
