package ru.hse.spb

import java.io.OutputStream

class TexBuilderException(message: String) : Exception(message)

@DslMarker
annotation class TexMarker

@TexMarker
interface Element

abstract class IndentedElement(protected val wrapper: IndentationWrapper, val append: (String) -> Unit) : Element {
    protected fun appendIndented(line: String) {
        append(wrapper.indent(line))
    }

    protected fun endLine() {
        append("\n")
    }
}

enum class Alignment(val alignment: String) {
    CENTER("center"),
    LEFT("flushleft"),
    RIGHT("flushright")
}

open class CommonElement(wrapper: IndentationWrapper, append: (String) -> Unit) : IndentedElement(wrapper, append) {
    fun customTag(name: String, vararg params: Pair<String, String>, body: CommonElement.() -> Unit) {
        appendIndented("""\begin{$name}""")

        if (!params.isEmpty()) {
            append("[")
            for ((key, value) in params) {
                append("$key=$value")
            }
            append("]")
        }

        endLine()

        val indentedElement = CommonElement(IndentationWrapper(wrapper.level + 1), append)
        indentedElement.body()

        appendIndented("""\end{$name}""")
        endLine()
    }

    fun alignment(alignment: Alignment, body: CommonElement.() -> Unit) = customTag(alignment.alignment) {
                                                           body()
                                                       }
    fun frame(frameTitle: String, vararg params: Pair<String, String>, body: CommonElement.() -> Unit) {
        customTag("frame", *params) {
            appendIndented("""\frametitle{$frameTitle}""")
            endLine()
            body()
        }
    }

    fun math(formula: String) {
        appendIndented("$$formula$")
        endLine()
    }

    private fun buildItemElement(name: String, body: ItemElement.() -> Unit) {
        appendIndented("""\begin{$name}""")
        endLine()

        val itemElement = ItemElement(IndentationWrapper(wrapper.level + 1), append)
        itemElement.body()

        appendIndented("""\end{$name}""")
        endLine()
    }

    fun itemize(body: ItemElement.() -> Unit) = buildItemElement("itemize", body)

    fun enumerate(body: ItemElement.() -> Unit) = buildItemElement("enumerate", body)

    operator fun String.unaryPlus() {
        appendIndented(this)
        endLine()
    }
}

class ItemElement(wrapper: IndentationWrapper, append: (String) -> Unit) : IndentedElement(wrapper, append) {
    fun item(body: CommonElement.() -> Unit) {
        appendIndented("""\item""")
        endLine()
        val commonElement = CommonElement(IndentationWrapper(wrapper.level), append)

        commonElement.body()
    }
}

class TexElement(val append: (String) -> Unit): Element {
    private var wasDocumentClass = false
    private var wasDocumentBody = false

    fun documentClass(name: String) {
        if (wasDocumentClass) throw TexBuilderException("Double documentclass")
        wasDocumentClass = true

        append("""\documentclass{$name}""")
        append("\n")
    }

    fun usepackage(name: String, vararg params: String) {
        append("""\usepackage""")
        if (!params.isEmpty()) {
            append(params.joinToString(",", "[", "]"))
        }

        append("{$name}")
        append("\n")
    }

    fun document(body: CommonElement.() -> Unit) {
        if (wasDocumentBody) throw TexBuilderException("Double document body")
        wasDocumentBody = true

        if (!wasDocumentClass) throw TexBuilderException("Document body before documentclass")

        append("""\begin{document}""")
        append("\n")
        val commonElement = CommonElement(IndentationWrapper(1), append)
        commonElement.body()
        append("""\end{document}""")
        append("\n")
    }

    fun finish() {
        if (!wasDocumentClass) throw TexBuilderException("No documentclass")
        if (!wasDocumentBody) throw TexBuilderException("No document body")
    }
}

class Tex(val body: TexElement.() -> Unit) {
    fun toOutputStream(out: OutputStream) {
        val texElement = TexElement { out.write(it.toByteArray()) }
        texElement.body()
        texElement.finish()
    }

    override fun toString(): String = StringBuilder().apply {
            val texElement = TexElement { append(it) }
            texElement.body()
            texElement.finish()
        }.toString()
}

fun tex(body: TexElement.() -> Unit) = Tex(body)