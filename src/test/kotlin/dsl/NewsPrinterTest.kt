package dsl

import org.example.dsl.NewsPrinter
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

class NewsPrinterTest {

    @Test
    fun `should create simple text using DSL`() {
        val printer = NewsPrinter()

        printer.apply {
            header(1) { "Breaking News" }
            +"This is a simple paragraph about the news."
        }

        val result = printer.build().trim()

        val expected = """
            # Breaking News
    
            This is a simple paragraph about the news.
        """.trimIndent().trim()

        assertEquals(expected, result)
    }

    @Test
    fun `should create text with bold and link`() {
        val printer = NewsPrinter()

        printer.apply {
            header(1) { "Tech Update" }
            +"We have just released our new **feature** for the app."
            +"For more details, check the [documentation](http://example.com)."
        }

        val result = printer.build()

        val expected = """
            # Tech Update
    
            We have just released our new **feature** for the app.
    
            For more details, check the [documentation](http://example.com).
        """.trimIndent()

        assertEquals(expected.replace("\\s+".toRegex(), ""), result.replace("\\s+".toRegex(), ""))
    }

    @Test
    fun `should format text using bold and link functions`() {
        val printer = NewsPrinter()

        val result = printer.apply {
            +"This is a ${bold { "bold" }} word."
            +"Here is a ${link("http://example.com") { "link" }} to the site."
        }.build()

        val expected = """
            This is a **bold** word.
    
            Here is a [link](http://example.com) to the site.
    
        """.trimIndent().trim()

        assertEquals(expected.replace("\\s+".toRegex(), ""), result.replace("\\s+".toRegex(), ""))
    }
}