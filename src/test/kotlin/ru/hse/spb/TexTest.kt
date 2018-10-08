package ru.hse.spb

import org.junit.Assert.*
import org.junit.Test

class TexTest {
    @Test
    fun testMinimalDocument() {
        val expected = """\documentclass{article}
            |\begin{document}
            |\end{document}
            |
        """.trimMargin()

        val result = tex {
            documentClass("article")
            document { }
        }.toString()

        assertEquals(expected, result)
    }

    @Test(expected = TexBuilderException::class)
    fun testNoDocumentBLock() {
        val result = tex {
            documentClass("article")
        }.toString()
    }

    @Test(expected = TexBuilderException::class)
    fun testNoDocumentClass() {
        val result = tex {
            document { }
        }.toString()
    }

    @Test(expected = TexBuilderException::class)
    fun testDoubleDocumentBlock() {
        val result = tex {
            documentClass("article")
            document { }
            document { }
        }.toString()
    }

    @Test(expected = TexBuilderException::class)
    fun testDoubleDocumentClass() {
        val result = tex {
            documentClass("article")
            documentClass("beamer")
            document { }
        }.toString()
    }

    @Test
    fun testSinglePackage() {
        val expected = """\documentclass{article}
            |\usepackage{tikz}
            |\begin{document}
            |\end{document}
            |
        """.trimMargin()

        val result = tex {
            documentClass("article")
            usepackage("tikz")
            document { }
        }.toString()

        assertEquals(expected, result)
    }

    @Test
    fun testMultiplePackage() {
        val expected = """\documentclass{article}
            |\usepackage{tikz}
            |\usepackage{color}
            |\begin{document}
            |\end{document}
            |
        """.trimMargin()

        val result = tex {
            documentClass("article")
            usepackage("tikz")
            usepackage("color")
            document { }
        }.toString()

        assertEquals(expected, result)
    }

    @Test
    fun testSingleLineText() {
        val expected = """\documentclass{article}
            |\usepackage{tikz}
            |\usepackage{color}
            |\begin{document}
            |  abacaba
            |\end{document}
            |
        """.trimMargin()

        val result = tex {
            documentClass("article")
            usepackage("tikz")
            usepackage("color")
            document {
                + "abacaba"
            }
        }.toString()

        assertEquals(expected, result)
    }

    @Test
    fun testMultipleLineText() {
        val expected = """\documentclass{article}
            |\usepackage{tikz}
            |\usepackage{color}
            |\begin{document}
            |  abacaba
            |  NisiOisiN
            |\end{document}
            |
        """.trimMargin()

        val result = tex {
            documentClass("article")
            usepackage("tikz")
            usepackage("color")
            document {
                + "abacaba"
                + "NisiOisiN"
            }
        }.toString()

        assertEquals(expected, result)
    }

    @Test
    fun testAlignment() {
        val expected = """\documentclass{article}
            |\usepackage{tikz}
            |\usepackage{color}
            |\begin{document}
            |  \begin{flushright}
            |    abacaba
            |  \end{flushright}
            |\end{document}
            |
        """.trimMargin()

        val result = tex {
            documentClass("article")
            usepackage("tikz")
            usepackage("color")
            document {
                alignment(Alignment.RIGHT) {
                    + "abacaba"
                }
            }
        }.toString()

        assertEquals(expected, result)
    }

    @Test
    fun testCustomTag() {
        val expected = """\documentclass{article}
            |\usepackage{tikz}
            |\usepackage{color}
            |\begin{document}
            |  \begin{pyglist}[language=kotlin]
            |    val a = 1
            |
            |  \end{pyglist}
            |\end{document}
            |
        """.trimMargin()

        val result = tex {
            documentClass("article")
            usepackage("tikz")
            usepackage("color")
            document {
                customTag("pyglist", "language" to "kotlin") {
                    + """
                        |val a = 1
                        |
                    """.trimMargin()
                }
            }
        }.toString()

        assertEquals(expected, result)
    }

    @Test
    fun testFrame() {
        val expected = """\documentclass{article}
            |\usepackage{tikz}
            |\usepackage{color}
            |\begin{document}
            |  \begin{frame}[language=kotlin]
            |    \frametitle{GreatFrame}
            |    abacaba
            |  \end{frame}
            |\end{document}
            |
        """.trimMargin()

        val result = tex {
            documentClass("article")
            usepackage("tikz")
            usepackage("color")
            document {
                frame("GreatFrame", "language" to "kotlin") {
                    + "abacaba"
                }
            }
        }.toString()

        assertEquals(expected, result)
    }

    @Test
    fun testMath() {
        val expected = """\documentclass{article}
            |\usepackage{tikz}
            |\usepackage{color}
            |\begin{document}
            |  $2+3=5$
            |\end{document}
            |
        """.trimMargin()

        val result = tex {
            documentClass("article")
            usepackage("tikz")
            usepackage("color")
            document {
                math("2+3=5")
            }
        }.toString()

        assertEquals(expected, result)
    }

    @Test
    fun testItemize() {
        val expected = """\documentclass{article}
            |\usepackage{tikz}
            |\usepackage{color}
            |\begin{document}
            |  \begin{itemize}
            |    \item
            |    abacaba
            |    \item
            |    NisiOisiN
            |  \end{itemize}
            |\end{document}
            |
        """.trimMargin()

        val result = tex {
            documentClass("article")
            usepackage("tikz")
            usepackage("color")
            document {
                itemize {
                    item {
                        + "abacaba"
                    }
                    item {
                        + "NisiOisiN"
                    }
                }
            }
        }.toString()

        assertEquals(expected, result)
    }

    @Test
    fun testEnumerate() {
        val expected = """\documentclass{article}
            |\usepackage{tikz}
            |\usepackage{color}
            |\begin{document}
            |  \begin{enumerate}
            |    \item
            |    abacaba
            |    \item
            |    NisiOisiN
            |  \end{enumerate}
            |\end{document}
            |
        """.trimMargin()

        val result = tex {
            documentClass("article")
            usepackage("tikz")
            usepackage("color")
            document {
                enumerate {
                    item {
                        + "abacaba"
                    }
                    item {
                        + "NisiOisiN"
                    }
                }
            }
        }.toString()

        assertEquals(expected, result)
    }

    @Test
    fun testNestedEnumerate() {
        val expected = """\documentclass{article}
            |\usepackage{tikz}
            |\usepackage{color}
            |\begin{document}
            |  \begin{enumerate}
            |    \item
            |    abacaba
            |    \begin{enumerate}
            |      \item
            |      racecar
            |    \end{enumerate}
            |    \item
            |    NisiOisiN
            |  \end{enumerate}
            |\end{document}
            |
        """.trimMargin()

        val result = tex {
            documentClass("article")
            usepackage("tikz")
            usepackage("color")
            document {
                enumerate {
                    item {
                        + "abacaba"
                        enumerate {
                            item {
                                + "racecar"
                            }
                        }
                    }
                    item {
                        + "NisiOisiN"
                    }
                }
            }
        }.toString()

        assertEquals(expected, result)
    }

    @Test
    fun testSeveralComplexElements() {
        val expected = """\documentclass{beamer}
                    |\usepackage[russian]{babel}
                    |\begin{document}
                    |  \begin{frame}[arg1=arg2]
                    |    \frametitle{frametitle}
                    |    \begin{itemize}
                    |      \item
                    |      abacaba text
                    |      \item
                    |      NisiOisiN text
                    |      \item
                    |      racecar text
                    |    \end{itemize}
                    |    \begin{pyglist}[language=kotlin]
                    |      val a = 1
                    |
                    |    \end{pyglist}
                    |  \end{frame}
                    |\end{document}
                    |
        """.trimMargin()

        val result = tex {
            documentClass("beamer")
            usepackage("babel", "russian" /* varargs */)
            document {
                frame("frametitle", "arg1" to "arg2") {
                    itemize {
                        for (row in listOf("abacaba", "NisiOisiN", "racecar")) {
                            item { +"$row text" }
                        }
                    }

                    // begin{pyglist}[language=kotlin]...\end{pyglist}
                    customTag("pyglist", "language" to "kotlin") {
                        + """
                        |val a = 1
                        |
                        """.trimMargin()
                    }
                }
            }
        }.toString()

        assertEquals(expected, result)
    }


}