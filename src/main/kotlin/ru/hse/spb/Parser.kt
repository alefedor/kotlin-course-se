package ru.hse.spb

import org.antlr.v4.runtime.*
import ru.hse.spb.parser.ExpLexer
import ru.hse.spb.parser.ExpParser

class ParserException(message: String) : Exception(message)

object Parser {
    fun parse(code: String) : Block {
        val lexer = ExpLexer(CharStreams.fromString(code + "\n"))
        val parser = ExpParser(BufferedTokenStream(lexer))

        lexer.addErrorListener(object : BaseErrorListener() {
            override fun syntaxError(
                    recognizer: Recognizer<*, *>?,
                    offendingSymbol: Any?,
                    line: Int,
                    charPositionInLine: Int,
                    msg: String?,
                    e: RecognitionException?
            ) {
                throw ParserException("Lexer exception at $line:$charPositionInLine.")
            }
        })

        parser.addErrorListener(object : BaseErrorListener() {
            override fun syntaxError(
                    recognizer: Recognizer<*, *>?,
                    offendingSymbol: Any?,
                    line: Int,
                    charPositionInLine: Int,
                    msg: String?,
                    e: RecognitionException?
            ) {
                throw ParserException("Parser exception at $line:$charPositionInLine.")
            }
        })

        return parser.file().toASTNode()
    }
}