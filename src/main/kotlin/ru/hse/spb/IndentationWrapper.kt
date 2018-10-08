package ru.hse.spb

class IndentationWrapper(val level: Int) {
    init {
        require(level >= 0)
    }

    companion object {
        val INDENTATION = "  ";
    }

    fun indent(line: String): String = INDENTATION.repeat(level).plus(line)
}