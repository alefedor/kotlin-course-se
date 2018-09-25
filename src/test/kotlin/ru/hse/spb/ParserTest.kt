package ru.hse.spb

import org.junit.Assert.assertEquals
import org.junit.Test

class ParserTest {
    @Test
    fun testParseEmpty() {
        val code = ""
        val node = Parser.parse(code)
        assertEquals(node, Block(emptyList()))
    }

    @Test(expected = ParserException::class)
    fun testPartialBlockParserException() {
        val code = "{ a = 3 * 5"
        Parser.parse(code)
    }

    @Test(expected = ParserException::class)
    fun testPartialStatementParserException() {
        val code = "{ a = 3 * }"
        Parser.parse(code)
    }

    @Test
    fun testParseIdentifier() {
        val code = "a"
        val node = Parser.parse(code)
        assertEquals(node, Block(listOf(Identifier("a"))))
    }

    @Test
    fun testParsePositiveLiteral() {
        val code = "239"
        val node = Parser.parse(code)
        assertEquals(node, Block(listOf(Literal(239))))
    }

    @Test
    fun testParseZeroLiteral() {
        val code = "0"
        val node = Parser.parse(code)
        assertEquals(node, Block(listOf(Literal(0))))
    }

    @Test
    fun testParseVariable() {
        val code = "var a = 3"
        val node = Parser.parse(code)
        assertEquals(node, Block(listOf(
                Variable(
                        Identifier("a"),
                        Literal(3)
                )
        )))
    }

    @Test
    fun testParseAssigment() {
        val code = "a = 3 * 5"
        val node = Parser.parse(code)
        assertEquals(node, Block(listOf(
                Assigment(
                        Identifier("a"),
                        BinaryExpression(
                                Literal(3),
                                BinaryExpression.Operator.MULT,
                                Literal(5)
                        )
                )
        )))
    }

    @Test
    fun testParseMultipleStatements() {
        val code = """
            var a = 0
            a = 3 * 5
        """.trimIndent()

        val node = Parser.parse(code)
        assertEquals(node, Block(listOf(
                Variable(Identifier("a"), Literal(0)),
                Assigment(
                        Identifier("a"),
                        BinaryExpression(
                                Literal(3),
                                BinaryExpression.Operator.MULT,
                                Literal(5)
                        )
                )
        )))
    }

    @Test
    fun testParseIf() {
        val code = "if (a == 0) { a = 3 * 5 } else { a = 0 }"
        val node = Parser.parse(code)
        assertEquals(node, Block(listOf(
                If(
                        BinaryExpression(
                                Identifier("a"),
                                BinaryExpression.Operator.EQ,
                                Literal(0)
                        ),
                        Block(listOf(
                                Assigment(
                                        Identifier("a"),
                                        BinaryExpression(
                                                Literal(3),
                                                BinaryExpression.Operator.MULT,
                                                Literal(5)
                                        )
                                )
                        )),
                        Block(listOf(
                                Assigment(
                                        Identifier("a"),
                                        Literal(0)
                                )
                        ))
                )
        )))
    }

    @Test
    fun testParseWhile() {
        val code = "while (a < 5) { a = a + 1 }"
        val node = Parser.parse(code)
        assertEquals(node, Block(listOf(
                While(
                        BinaryExpression(
                                Identifier("a"),
                                BinaryExpression.Operator.LT,
                                Literal(5)
                        ),
                        Block(listOf(
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
        )))
    }

    @Test
    fun testParseReturn() {
        val code = "return 1 - 2"
        val node = Parser.parse(code)
        assertEquals(node, Block(listOf(
                Return(
                        BinaryExpression(
                                Literal(1),
                                BinaryExpression.Operator.MINUS,
                                Literal(2)
                        )
                )
        )))
    }

    @Test
    fun testParseFunctionWithoutParameters() {
        val code = "fun incCnt() { cnt = cnt + 1 }"
        val node = Parser.parse(code)
        assertEquals(node, Block(listOf(
                Function(
                        Identifier("incCnt"),
                        ParameterNames(emptyList()),
                        Block(listOf(
                                Assigment(
                                        Identifier("cnt"),
                                        BinaryExpression(
                                                Identifier("cnt"),
                                                BinaryExpression.Operator.PLUS,
                                                Literal(1)
                                        )
                                )
                        ))
                )
        )))
    }

    @Test
    fun testParseFunctionWithParameters() {
        val code = "fun plus(a, b) { return a + b }"
        val node = Parser.parse(code)
        assertEquals(node, Block(listOf(
                Function(
                        Identifier("plus"),
                        ParameterNames(listOf(Identifier("a"), Identifier("b"))),
                        Block(listOf(
                                Return(
                                        BinaryExpression(
                                                Identifier("a"),
                                                BinaryExpression.Operator.PLUS,
                                                Identifier("b")
                                        )
                                )
                        ))
                )
        )))
    }

    @Test
    fun testParseFunctionCall() {
        val code = "plus(a, b)"
        val node = Parser.parse(code)
        assertEquals(node, Block(listOf(
                FunctionCall(
                        Identifier("plus"),
                        Arguments(listOf(
                                Identifier("a"),
                                Identifier("b")
                        ))
                )
        )))
    }

    @Test
    fun testParseOperatorPriority() {
        val code = "3-5/4"
        val node = Parser.parse(code)
        assertEquals(node, Block(listOf(
                BinaryExpression(
                        Literal(3),
                        BinaryExpression.Operator.MINUS,
                        BinaryExpression(
                                Literal(5),
                                BinaryExpression.Operator.DIV,
                                Literal(4)
                        )
                )
        )))
    }

    @Test
    fun testParseOperatorAssociativity() {
        val code = "3-4-5"
        val node = Parser.parse(code)
        assertEquals(node, Block(listOf(
                BinaryExpression(
                        BinaryExpression(
                                Literal(3),
                                BinaryExpression.Operator.MINUS,
                                Literal(4)
                        ),
                        BinaryExpression.Operator.MINUS,
                        Literal(5)
                )
        )))
    }

    @Test
    fun testParseComment() {
        val code = "var a = 3 // forbidden symbols: $%^#&@*!.;"
        val node = Parser.parse(code)
        assertEquals(node, Block(listOf(
                Variable(
                        Identifier("a"),
                        Literal(3)
                )
        )))
    }
}
