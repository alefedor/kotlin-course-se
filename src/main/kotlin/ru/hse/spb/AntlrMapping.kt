package ru.hse.spb

import org.antlr.v4.runtime.tree.TerminalNode
import ru.hse.spb.parser.ExpParser

fun ExpParser.FileContext.toASTNode() = block().toASTNode()

fun ExpParser.BlockContext.toASTNode() = Block(this.statement().map { it.toASTNode()})

fun ExpParser.BlockWithBracketsContext.toASTNode() : Block = block().toASTNode()

fun ExpParser.StatementContext.toASTNode() = when (this) {
    is ExpParser.FunctionStatementContext -> function().toASTNode()
    is ExpParser.VariableStatementContext -> variable().toASTNode()
    is ExpParser.ExpressionStatementContext -> expression().toASTNode()
    is ExpParser.WhileStatementContext -> loop().toASTNode()
    is ExpParser.ConditionStatementContext -> condition().toASTNode()
    is ExpParser.AssigmentStatementContext -> assigment().toASTNode()
    is ExpParser.ReturnStatementContext -> ret().toASTNode()
    else -> throw UnsupportedOperationException(this.javaClass.canonicalName)
}


fun TerminalNode.toIdentifier() = Identifier(text)

fun TerminalNode.toLiteral() = Literal(text.toInt())

fun ExpParser.FunctionContext.toASTNode() = Function(
                                                Identifier().toIdentifier(),
                                                parameterNames()?.toASTNode() ?: ParameterNames(emptyList()),
                                                blockWithBrackets().toASTNode()
                                            )

fun ExpParser.VariableContext.toASTNode() = Variable(
                                                Identifier().toIdentifier(),
                                                expression().toASTNode()
                                            )

fun ExpParser.ParameterNamesContext.toASTNode() = ParameterNames(Identifier().map {it.toIdentifier()})

fun ExpParser.LoopContext.toASTNode() = While(expression().toASTNode(), blockWithBrackets().toASTNode())

fun ExpParser.ConditionContext.toASTNode() = If(
                                                 expression().toASTNode(),
                                                 mainBlock.toASTNode(),
                                         elseBlock?.toASTNode() ?: Block(emptyList())
                                             )

fun ExpParser.AssigmentContext.toASTNode() = Assigment(Identifier().toIdentifier(), expression().toASTNode())

fun ExpParser.RetContext.toASTNode() = Return(expression().toASTNode())

fun ExpParser.FunctionCallContext.toASTNode() = FunctionCall(Identifier().toIdentifier(), arguments().toASTNode())

fun ExpParser.ArgumentsContext.toASTNode() = Arguments(expression().map {it.toASTNode()})

fun ExpParser.ExpressionContext.toASTNode() : Expression = when (this) {
    is ExpParser.BinaryOperationContext -> BinaryExpression(
                                               left.toASTNode(),
                                               op.text.toOperator(),
                                               right.toASTNode()
                                           )

    is ExpParser.FunctionCallingContext -> functionCall().toASTNode()
    is ExpParser.VariableReferenceContext -> Identifier().toIdentifier()
    is ExpParser.IntLiteralContext -> Literal().toLiteral()
    is ExpParser.SurroundedExpressionContext -> expression().toASTNode()
    else -> throw UnsupportedOperationException(this.javaClass.canonicalName)
}


