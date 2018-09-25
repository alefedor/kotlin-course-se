package ru.hse.spb

import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors

fun main(args: Array<String>) {
    if (args.size != 1) {
        println("Wrong command line arguments!")
        println("Should be:")
        println("Command line args: [name of file with code]")
        return
    }

    val fileName = args[0]
    val lines = Files.readAllLines(Paths.get(fileName))
    val code = lines.stream().collect(Collectors.joining("\n"))

    try {
        val root = Parser.parse(code)
        Interpreter.run(root)
    } catch (e: InterpreterException) {
        println("Error while interpreting code: " + e.message)
    } catch (e: ParserException) {
        println(e.message)
    }
}