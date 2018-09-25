package ru.hse.spb

typealias IntFunction = (List<Int>) -> Int

class Scope(private val parent: Scope? = null){
    private var variables = mutableMapOf<String, Int>()
    private var functions = mutableMapOf<String, IntFunction>()

    companion object {
        val STD_FUNCTIONS = mapOf<String, IntFunction>(
                "println" to {args -> println(args.joinToString(" ")); 0}
        )
    }

    constructor(variables: Map<String, Int>, functions: Map<String, IntFunction>) : this() {
        this.variables = HashMap(variables)
        this.functions = HashMap(functions)
    }

    fun declareVariable(name: String, value: Int) {
        if (variables.containsKey(name)) {
            throw InterpreterException("There is already variable $name declared in the scope")
        }

        variables[name] = value
    }

    fun setVariable(name: String, value: Int) {
        findVariableScope(name)[name] = value
    }

    fun getVariable(name: String) : Int = findVariableScope(name)[name]!!

    fun declareFunction(name: String, body: IntFunction) {
        if (functions.containsKey(name)) {
            throw InterpreterException("There is already function $name declared in the scope")
        }

        functions[name] = body
    }

    fun getFunction(name: String) : IntFunction = findFunctionScope(name)[name]!!

    private fun findVariableScope(name: String) : MutableMap<String, Int> {
        if (variables.containsKey(name)) {
            return variables
        }

        if (parent != null) {
            return parent.findVariableScope(name)
        }

        throw InterpreterException("No such variable $name in scope")
    }

    private fun findFunctionScope(name: String) : MutableMap<String, IntFunction> {
        if (functions.containsKey(name)) {
            return functions
        }

        if (parent != null) {
            return parent.findFunctionScope(name)
        }

        throw InterpreterException("No such function $name in scope")
    }
}