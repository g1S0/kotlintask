package org.example.dsl

@DslMarker
annotation class NewsDsl

@NewsDsl
class NewsPrinter {
    private val content = StringBuilder()

    operator fun String.unaryPlus() {
        content.append(this + "\n\n")
    }

    fun header(level: Int, block: () -> String) {
        val headerPrefix = "#".repeat(level)
        content.append("$headerPrefix ${block()}\n\n")
    }

    fun bold(block: () -> String): String {
        return "**${block()}**"
    }

    fun link(url: String, block: () -> String): String {
        return "[${block()}]($url)"
    }

    fun build(): String {
        return content.toString()
    }
}
