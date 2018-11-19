package ru.hse.spb

import java.io.File

fun main(args: Array<String>) {
    if (args.size != 1) {
        println("Wrong command line arguments!")
        println("Should be:")
        println("Command line args: [name of file with code]")
        return
    }

    val code = File(args[0]).readText()

    try {
        val root = Parser.parse(code)
        Interpreter.run(root)
    } catch (e: InterpreterException) {
        println("Error while interpreting code: " + e.message)
    } catch (e: ParserException) {
        println(e.message)
    }
}