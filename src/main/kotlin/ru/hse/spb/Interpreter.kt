package ru.hse.spb

class InterpreterException(message: String) : Exception(message)

object Interpreter {
    fun run(code: Block, functions: Map<String, IntFunction> = Scope.STD_FUNCTIONS) {
        val std = Scope(emptyMap(), functions)

        evaluate(code, std)
    }

    private fun evaluate(block: Block, scope: Scope) : Int? {
        for (statement in block.statements) {
            val result = evaluate(statement, scope)

            if (result != null) {
                return result
            }
        }

        return null
    }

    private fun evaluate(variable: Variable, scope: Scope) {
        val value = evaluate(variable.value, scope)
        scope.declareVariable(variable.name.toString(), value)
    }

    private fun evaluate(assigment: Assigment, scope: Scope) {
        val value = evaluate(assigment.value, scope)
        scope.setVariable(assigment.name.toString(), value)
    }

    private fun evaluate(ret: Return, scope: Scope) : Int {
        return evaluate(ret.value, scope)
    }

    private fun evaluate(functionCall: FunctionCall, scope: Scope) : Int {
        val function = scope.getFunction(functionCall.name.toString())
        val functionScope = Scope(scope)
        val args = evaluate(functionCall.args, functionScope)
        return function.invoke(args)
    }

    private fun evaluate(arguments: Arguments, scope: Scope) : List<Int> {
        return arguments.values.map { evaluate(it, scope)}
    }

    private fun evaluate(condition: If, scope: Scope) : Int? {
        val value = evaluate(condition.condition, scope)

        val ifScope = Scope(scope)

        if (value != 0) {
            return evaluate(condition.body, ifScope)
        } else {
            return evaluate(condition.elseBody, ifScope)
        }
    }

    private fun evaluate(loop: While, scope: Scope) : Int? {
        while (evaluate(loop.condition, scope) != 0) {
            val whileScope = Scope(scope)
            val result = evaluate(loop.body, whileScope)

            if (result != null) {
                return result
            }
        }

        return null
    }

    private fun evaluate(statement: Statement, scope: Scope) : Int? = when (statement) {
        is If -> evaluate(statement, scope)
        is While -> evaluate(statement, scope)
        is Expression -> { evaluate(statement, scope); null }
        is Function -> { evaluate(statement, scope); null }
        is Variable -> { evaluate(statement, scope); null }
        is Assigment -> { evaluate(statement, scope); null }
        is FunctionCall -> { evaluate(statement, scope); null }
        is Return -> evaluate(statement, scope)
        else -> throw UnsupportedOperationException(this.javaClass.canonicalName)
    }

    private fun evaluate(function: Function, scope: Scope) {
        scope.declareFunction(function.name.toString()) {
            if (it.size != function.params.params.size) {
                throw InterpreterException("Wrong number of arguments in function call")
            }

            val body = mutableListOf<Statement>()
            val functionScope = Scope(scope)

            for (i in 0 until it.size) {
                body.add(Variable(function.params.params.get(i), Literal(it.get(i))))
            }

            body.addAll(function.body.statements)
            val result = evaluate(Block(body), functionScope)

            if (result != null) result else 0
        }
    }

    private fun evaluate(expression: Expression, scope: Scope) : Int = when (expression) {
        is Literal -> expression.value
        is Identifier -> scope.getVariable(expression.name)
        is FunctionCall -> evaluate(expression, scope)
        is BinaryExpression -> {
            val left = evaluate(expression.left, scope)
            val right = evaluate(expression.right, scope)
            expression.op.apply(left, right)
        }
        else -> throw UnsupportedOperationException(this.javaClass.canonicalName)
    }
}