package ru.hse.spb

import org.junit.Assert.assertEquals
import org.junit.Test

class IntegrationTest {
    fun mockedStl(output: StringBuilder) : Map<String, IntFunction> = mapOf<String, IntFunction>(
            "println" to { args -> output.append(args.joinToString(" ") + "\n"); 0 }
    )

    @Test
    fun testExample1() {
        val output = StringBuilder()
        val stl = mockedStl(output)

        val code = """
            var a = 10
            var b = 20
            if (a > b) {
                println(1)
            } else {
                println(0)
            }
        """.trimIndent()

        Interpreter.run(Parser.parse(code), stl)
        assertEquals("0\n", output.toString())
    }

    @Test
    fun testExample2() {
        val output = StringBuilder()
        val stl = mockedStl(output)

        val code = """
            fun fib(n) {
                if (n <= 1) {
                    return 1
                }
                return fib(n - 1) + fib(n - 2)
            }

            var i = 1
            while (i <= 5) {
                println(i, fib(i))
                i = i + 1
            }
        """.trimIndent()

        val expected = """
            1 1
            2 2
            3 3
            4 5
            5 8

        """.trimIndent()

        Interpreter.run(Parser.parse(code), stl)
        assertEquals(expected, output.toString())
    }

    @Test
    fun testExample3() {
        val output = StringBuilder()
        val stl = mockedStl(output)

        val code = """
            fun foo(n) {
                fun bar(m) {
                    return m + n
                }

                return bar(1)
            }

            println(foo(41)) // prints 42
        """.trimIndent()

        val expected = """
            42

        """.trimIndent()

        Interpreter.run(Parser.parse(code), stl)
        assertEquals(expected, output.toString())
    }

    @Test
    fun testIntegrationBinaryExp() {
        val output = StringBuilder()
        val stl = mockedStl(output)

        val code = """
            fun bin(a, n, mod) {
                if (n == 1) {
                    return a % mod
                }

                if (n % 2 == 1) {
                    return (a * bin(a, n - 1, mod)) % mod
                }

                var half = bin(a, n / 2, mod)

                return (half * half) % mod
            }

            println(bin(4, 1000, 10007))
            println(bin(3, 30000, 503))
        """.trimIndent()

        val expected = """
            5172
            463

        """.trimIndent()

        Interpreter.run(Parser.parse(code), stl)
        assertEquals(expected, output.toString())
    }

    @Test
    fun testIntegrationBinarySearch() {
        val output = StringBuilder()
        val stl = mockedStl(output)

        val code = """
            var secret = 507
            var l = 0
            var r = 10000

            while (l + 1 < r) {
                var sum = l + r
                var m = sum / 2
                if (secret < m) {
                    r = m
                } else {
                    l = m
                }
            }

            println(l)
        """.trimIndent()

        val expected = """
            507

        """.trimIndent()

        Interpreter.run(Parser.parse(code), stl)
        assertEquals(expected, output.toString())
    }
}