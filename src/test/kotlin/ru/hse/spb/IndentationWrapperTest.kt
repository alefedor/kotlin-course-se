package ru.hse.spb

import org.junit.Assert.*
import org.junit.Test

class IndentationWrapperTest {
    @Test
    fun testIdentity() {
        val wrapper = IndentationWrapper(0)
        val string = "a sentence"
        assertEquals(string, wrapper.indent(string))
    }

    @Test
    fun testIndentation() {
        val wrapper = IndentationWrapper(2)
        val string = "a sentence"
        assertEquals("    $string", wrapper.indent(string))
    }
}