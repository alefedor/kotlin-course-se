package ru.hse.spb

import org.junit.Assert.*
import org.junit.Test

class TestSource {
    fun applyReflection(p : Point, r : Int) : Point {
        when (r) {
            1 -> return p
            2 -> return Point(-p.x, p.y)
            3 -> return Point(p.x, -p.y)
            4 -> return Point(-p.x, -p.y)
            else -> fail()
        }

        return Point(0, 0)
    }

    @Test
    fun test1() {
        val points = listOf<Point>(
                Point(-7, -3),
                Point(9, 0),
                Point(-8, 6),
                Point(7, -8),
                Point(4, -5)
        )

        val queryResult = Solver.solve(points)
        assertTrue(queryResult.i in 0..4)
        assertTrue(queryResult.k1 in 1..4)
        assertTrue(queryResult.j in 0..4)
        assertTrue(queryResult.k2 in 1..4)


        val a = applyReflection(points[queryResult.i], queryResult.k1)
        val b = applyReflection(points[queryResult.j], queryResult.k2)
        val invB = Point(-b.x, -b.y)

        assertEquals(5, squareDistance(a, invB))
    }

    @Test
    fun test2() {
        val points = listOf<Point>(
                Point(3, 2),
                Point(-4, 7),
                Point(-6, 0),
                Point(-8, 4),
                Point(5, 1)
        )

        val queryResult = Solver.solve(points)
        assertTrue(queryResult.i in 0..4)
        assertTrue(queryResult.k1 in 1..4)
        assertTrue(queryResult.j in 0..4)
        assertTrue(queryResult.k2 in 1..4)

        val a = applyReflection(points[queryResult.i], queryResult.k1)
        val b = applyReflection(points[queryResult.j], queryResult.k2)
        val invB = Point(-b.x, -b.y)

        assertEquals(2, squareDistance(a, invB))
    }
}