package ru.hse.spb

import org.junit.Assert.assertEquals
import org.junit.Test

class InterpreterTest {
    fun mockedStl(output: StringBuilder) : Map<String, IntFunction> = mapOf<String, IntFunction>(
                "println" to { args -> output.append(args.joinToString(" ") + "\n"); 0 }
    )

    @Test
    fun testInterpretEmptyBlock() {
        val output = StringBuilder()
        val stl = mockedStl(output)
        Interpreter.run(Block(emptyList()), stl)
        assertEquals("", output.toString())
    }

    @Test
    fun testInterpretNoOutput() {
        val output = StringBuilder()
        val stl = mockedStl(output)
        val block = Block(listOf(
                Variable(Identifier("a"), Literal(0)),
                Assigment(
                        Identifier("a"),
                        BinaryExpression(
                                Literal(3),
                                BinaryExpression.Operator.MULT,
                                Literal(5)
                        )
                )
        ))

        Interpreter.run(block, stl)
        assertEquals("", output.toString())
    }

    @Test
    fun testInterpretSinglePrintln() {
        val output = StringBuilder()
        val stl = mockedStl(output)
        val block = Block(listOf(
                FunctionCall(
                        Identifier("println"),
                        Arguments(listOf(Literal(2), Literal(3), Literal(9)))
                )
        ))

        Interpreter.run(block, stl)
        assertEquals("2 3 9\n", output.toString())
    }

    @Test
    fun testInterpretMultiplePrintln() {
        val output = StringBuilder()
        val stl = mockedStl(output)
        val block = Block(listOf(
                FunctionCall(
                        Identifier("println"),
                        Arguments(listOf(Literal(2), Literal(3), Literal(9)))
                ),
                FunctionCall(
                        Identifier("println"),
                        Arguments(listOf(BinaryExpression(
                                Literal(100),
                                BinaryExpression.Operator.MOD,
                                Literal(3)
                        )))
                )
        ))

        val expected = """
            2 3 9
            1

        """.trimIndent()

        Interpreter.run(block, stl)
        assertEquals(expected, output.toString())
    }

    @Test
    fun testInterpretWhile() {
        val output = StringBuilder()
        val stl = mockedStl(output)
        val block = Block(listOf(
                Variable(Identifier("a"), Literal(1)),
                While(
                        BinaryExpression(
                                Identifier("a"),
                                BinaryExpression.Operator.LT,
                                Literal(5)
                        ),
                        Block(listOf(
                                FunctionCall(
                                        Identifier("println"),
                                        Arguments(listOf(Identifier("a")))
                                ),
                                Assigment(
                                        Identifier("a"),
                                        BinaryExpression(
                                                Identifier("a"),
                                                BinaryExpression.Operator.PLUS,
                                                Literal(1)
                                        )
                                )
                        ))
                )
        ))

        val expected = """
            1
            2
            3
            4

        """.trimIndent()

        Interpreter.run(block, stl)
        assertEquals(expected, output.toString())
    }

    @Test
    fun testInterpretFunctionCall() {
        val output = StringBuilder()
        val stl = mockedStl(output)
        val block = Block(listOf(
                Function(
                        Identifier("superPrintln"),
                        ParameterNames(listOf(Identifier("x"))),
                        Block(listOf(
                                FunctionCall(
                                        Identifier("println"),
                                        Arguments(listOf(Identifier("x")))
                                )
                        ))
                ),
                FunctionCall(
                        Identifier("superPrintln"),
                        Arguments(listOf(Literal(1)))
                ),
                FunctionCall(
                        Identifier("superPrintln"),
                        Arguments(listOf(Literal(2)))
                )
        ))

        val expected = """
            1
            2

        """.trimIndent()

        Interpreter.run(block, stl)
        assertEquals(expected, output.toString())
    }

    @Test
    fun testInterpretIfTrue() {
        val output = StringBuilder()
        val stl = mockedStl(output)
        val block = Block(listOf(
                If(
                        Literal(1),
                        Block(listOf(
                                FunctionCall(
                                        Identifier("println"),
                                        Arguments(listOf(Literal(1)))
                                )
                        )),
                        Block(listOf(
                                FunctionCall(
                                        Identifier("println"),
                                        Arguments(listOf(Literal(2)))
                                )
                        ))
                )
        ))

        Interpreter.run(block, stl)
        assertEquals("1\n", output.toString())
    }

    @Test
    fun testInterpretIfFalse() {
        val output = StringBuilder()
        val stl = mockedStl(output)
        val block = Block(listOf(
                If(
                        Literal(0),
                        Block(listOf(
                                FunctionCall(
                                        Identifier("println"),
                                        Arguments(listOf(Literal(1)))
                                )
                        )),
                        Block(listOf(
                                FunctionCall(
                                        Identifier("println"),
                                        Arguments(listOf(Literal(2)))
                                )
                        ))
                )
        ))

        Interpreter.run(block, stl)
        assertEquals("2\n", output.toString())
    }

    @Test(expected = InterpreterException::class)
    fun testInterpretExceptionNoVariable() {
        val output = StringBuilder()
        val stl = mockedStl(output)
        val block = Block(listOf(
                Assigment(Identifier("a"), Literal(4))
        ))

        Interpreter.run(block, stl)
    }

    @Test(expected = InterpreterException::class)
    fun testInterpretExceptionNoFunction() {
        val output = StringBuilder()
        val stl = mockedStl(output)
        val block = Block(listOf(
                FunctionCall(Identifier("abacaba"), Arguments(emptyList()))
        ))

        Interpreter.run(block, stl)
    }

    @Test(expected = InterpreterException::class)
    fun testInterpretExceptionDeclaredTwice() {
        val output = StringBuilder()
        val stl = mockedStl(output)
        val block = Block(listOf(
                Variable(Identifier("a"), Literal(3)),
                Variable(Identifier("a"), Literal(4))
        ))

        Interpreter.run(block, stl)
    }

    @Test
    fun testInterpretLocalRedeclaration() {
        val output = StringBuilder()
        val stl = mockedStl(output)
        val block = Block(listOf(
                Variable(Identifier("a"), Literal(3)),
                FunctionCall(
                        Identifier("println"),
                        Arguments(listOf(Identifier("a")))
                ),
                If(
                        Literal(1),
                        Block(listOf(
                                Variable(Identifier("a"), Literal(4)),
                                FunctionCall(
                                        Identifier("println"),
                                        Arguments(listOf(Identifier("a")))
                                )
                        ))
                ),
                FunctionCall(
                        Identifier("println"),
                        Arguments(listOf(Identifier("a")))
                )
        ))

        val expected = """
            3
            4
            3

        """.trimIndent()

        Interpreter.run(block, stl)
        assertEquals(expected, output.toString())
    }

    @Test(expected = InterpreterException::class)
    fun testInterpretExceptionFunctionVisibility() {
        val output = StringBuilder()
        val stl = mockedStl(output)
        val block = Block(listOf(
                If(
                        Literal(1),
                        Block(listOf(
                                Function(
                                        Identifier("superPrintln"),
                                        ParameterNames(listOf(Identifier("x"))),
                                        Block(listOf(
                                                FunctionCall(
                                                        Identifier("println"),
                                                        Arguments(listOf(Identifier("x")))
                                                )
                                        ))
                                )
                        ))
                ),
                FunctionCall(
                        Identifier("superPrintln"),
                        Arguments(listOf(Literal(3)))
                )
        ))

        Interpreter.run(block, stl)
    }
}