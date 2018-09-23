package ru.hse.spb

import java.util.function.BinaryOperator
import java.util.function.IntBinaryOperator

interface ASTNode;

class Block(val statements: List<Statement>) : ASTNode

interface Statement : ASTNode

class Function(val name: Identifier, val params: ParameterNames, val body: Block) : Statement

class Variable(val name: Identifier, val value: Expression) : Statement

class ParameterNames(val params: List<Identifier>) : ASTNode

class While(val condition: Expression, val body: Block) : Statement

class If(val condition: Expression, val body: Block, val elseBody: Block = Block(emptyList())) : Statement

class Assigment(val name: Identifier, val value: Expression) : Statement

class Return(val value: Expression) : Statement

interface Expression : Statement

class FunctionCall(val name: Identifier, val args: Arguments) : Expression

class Arguments(val values: List<Expression>) : ASTNode

class Identifier(val name: String) : Expression {
    override fun toString(): String {
        return name
    }
}

class Literal(val value: Int) : Expression

fun Boolean.toInt() = if (this) 1 else 0

class BinaryExpression(val left: Expression, val op: Operator, val right: Expression) : Expression {
    enum class Operator : BinaryOperator<Int>, IntBinaryOperator {
        PLUS {
            override fun apply(a: Int, b: Int) = a + b
        },
        MINUS {
            override fun apply(a: Int, b: Int) = a - b
        },
        MULT {
            override fun apply(a: Int, b: Int) = a * b
        },
        DIV {
            override fun apply(a: Int, b: Int) = a / b
        },
        MOD {
            override fun apply(a: Int, b: Int) = a % b
        },
        GT {
            override fun apply(a: Int, b: Int) = (a > b).toInt()
        },
        LT {
            override fun apply(a: Int, b: Int) = (a < b).toInt()
        },
        GEQ {
            override fun apply(a: Int, b: Int) = (a >= b).toInt()
        },
        LEQ {
            override fun apply(a: Int, b: Int) = (a <= b).toInt()
        },
        EQ {
            override fun apply(a: Int, b: Int) = (a == b).toInt()
        },
        NEQ {
            override fun apply(a: Int, b: Int) = (a != b).toInt()
        },
        OR {
            override fun apply(a: Int, b: Int) = (a != 0 || b != 0).toInt()
        },
        AND {
            override fun apply(a: Int, b: Int) = (a != 0 && b != 0).toInt()
        };

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