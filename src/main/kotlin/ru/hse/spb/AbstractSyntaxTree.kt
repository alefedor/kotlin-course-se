package ru.hse.spb

import java.util.function.BinaryOperator
import java.util.function.IntBinaryOperator

sealed class ASTNode

data class Block(val statements: List<Statement>) : ASTNode()

sealed class Statement : ASTNode()

data class Function(val name: Identifier, val params: ParameterNames, val body: Block) : Statement()

data class Variable(val name: Identifier, val value: Expression) : Statement()

data class ParameterNames(val params: List<Identifier>) : ASTNode()

data class While(val condition: Expression, val body: Block) : Statement()

data class If(val condition: Expression, val body: Block, val elseBody: Block = Block(emptyList())) : Statement()

data class Assigment(val name: Identifier, val value: Expression) : Statement()

data class Return(val value: Expression) : Statement()

sealed class Expression : Statement()

data class FunctionCall(val name: Identifier, val args: Arguments) : Expression()

data class Arguments(val values: List<Expression>) : ASTNode()

data class Identifier(val name: String) : Expression() {
    override fun toString(): String {
        return name
    }
}

data class Literal(val value: Int) : Expression()

fun Boolean.toInt() = if (this) 1 else 0

data class BinaryExpression(val left: Expression, val op: Operator, val right: Expression) : Expression() {
    enum class Operator(val op: (Int, Int) -> Int) : BinaryOperator<Int>, IntBinaryOperator {
        PLUS({ a, b -> a + b }),
        MINUS({ a, b -> a - b }),
        MULT({ a, b -> a * b }),
        DIV({ a, b -> a / b }),
        MOD(({ a, b -> a % b })),
        GT(({ a, b -> (a > b).toInt() })),
        LT(({ a, b -> (a < b).toInt() })),
        GEQ(({ a, b -> (a >= b).toInt() })),
        LEQ(({ a, b -> (a <= b).toInt() })),
        EQ(({ a, b -> (a == b).toInt() })),
        NEQ(({ a, b -> (a != b).toInt() })),
        OR(({ a, b -> (a != 0 || b != 0).toInt() })),
        AND(({ a, b -> (a != 0 && b != 0).toInt() }));

        override fun apply(a: Int, b: Int) = op(a, b)
        override fun applyAsInt(a: Int, b: Int) = apply(a, b)
    }
}

fun String.toOperator() = when (this) {
    "+" -> BinaryExpression.Operator.PLUS
    "-" -> BinaryExpression.Operator.MINUS
    "*" -> BinaryExpression.Operator.MULT
    "/" -> BinaryExpression.Operator.DIV
    "%" -> BinaryExpression.Operator.MOD
    ">" -> BinaryExpression.Operator.GT
    "<" -> BinaryExpression.Operator.LT
    ">=" -> BinaryExpression.Operator.GEQ
    "<=" -> BinaryExpression.Operator.LEQ
    "==" -> BinaryExpression.Operator.EQ
    "!=" -> BinaryExpression.Operator.NEQ
    "||" -> BinaryExpression.Operator.OR
    "&&" -> BinaryExpression.Operator.AND
    else -> throw UnsupportedOperationException(this.javaClass.canonicalName)
}