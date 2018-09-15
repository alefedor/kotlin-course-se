package ru.hse.spb

import java.util.*
import kotlin.math.abs

/**
 * Structure for storing integer 2D points
 */
class Point(val x: Int, val y: Int)

fun Int.sqr(): Int = this * this

/**
 * Square of distance between points
 * @param a first point
 * @param b second point
 */
fun squareDistance(a: Point, b: Point) = (a.x - b.x).sqr() + (a.y - b.y).sqr()

/**
 * Interface for solver of problem of finding two nearest points in 2D
 */
interface NearestPointFinder {
    /**
     * Function for finding two nearest points in 2D
     * @param ps list of points in 2D
     * @return number in list of two nearest points or
     * (-1, -1) if there are less than two points in param
     */
    fun findNearestPoints(ps: List<Point>) : Pair<Int, Int>
}

/**
 * Implementation of NearestPointFinder using standart divide&conquer approach
 */
class NearestPointFinderImpl : NearestPointFinder {
    private var bestDistance = Integer.MAX_VALUE
    private var id1 = -1
    private var id2 = -1
    private var points = emptyList<Point>()

    private val xComparator  = Comparator<Int> { a: Int, b: Int -> points[a].x - points[b].x}
    private val yComparator  = Comparator<Int> { a: Int, b: Int -> points[a].y - points[b].y}

    companion object {
        private val MINIMUM_DIVIDE_SIZE = 20
    }

    override fun findNearestPoints(ps: List<Point>): Pair<Int, Int> {
        invalidateBest()

        points = ps

        val ids = MutableList(points.size) {it}

        ids.sortWith(xComparator)

        recursiveFind(ids)

        return Pair(id1, id2)
    }

    private fun recursiveFind(ids: MutableList<Int>) {
        if (ids.size < MINIMUM_DIVIDE_SIZE) {
            for (a in ids) {
                for (b in ids) {
                    if (a != b) {
                        updateBest(a, b)
                    }
                }
            }

            ids.sortWith(yComparator)
            return
        }

        val middle = ids.size / 2
        val middleX = points[ids[middle]].x

        val leftPart = ids.subList(0, middle)
        val rightPart = ids.subList(middle, ids.size)

        recursiveFind(leftPart)
        recursiveFind(rightPart)

        val copy = merge(leftPart, rightPart)
        for (i in 0 until copy.size) {
            ids.set(i, copy[i])
        }

        val closeIds = ArrayList<Int>()

        for (i in ids) {
            if ((points[i].x - middleX).sqr() < bestDistance) {
                for (j in closeIds.asReversed()) {
                    if ((points[i].y - points[j].y).sqr() > bestDistance) {
                        break
                    }

                    updateBest(i, j)
                }

                closeIds.add(i)
            }
        }
    }

    private fun invalidateBest() {
        bestDistance = Integer.MAX_VALUE
        id1 = -1
        id2 = -1
    }

    private fun updateBest(a: Int, b: Int) {
        val d = squareDistance(points[a], points[b])
        if (d < bestDistance) {
            bestDistance = d
            id1 = a
            id2 = b
        }
    }

    private fun merge(left: List<Int>, right: List<Int>): List<Int> {
        val result = ArrayList<Int>()
        var indexLeft = 0
        var indexRight = 0

        while (indexLeft != left.size && indexRight != right.size) {
            if (yComparator.compare(left[indexLeft], right[indexRight]) < 0) {
                result.add(left[indexLeft])
                indexLeft++
            } else {
                result.add(right[indexRight])
                indexRight++
            }
        }

        while (indexLeft < left.size) {
            result.add(left[indexLeft])
            indexLeft++
        }

        while (indexRight < right.size) {
            result.add(right[indexRight])
            indexRight++
        }

        return result
    }
}

/**
 * Structure for storing results of Solver solutions
 * i, j - numbers of vectors in initial list
 * k1, k2 - reflections of vectors
 */
class QueryResult(val i: Int, val k1: Int, val j: Int, val k2: Int)

/**
 * Class for solving problem minimum vector sum with possibility of 90-angled turns
 */
object Solver {
    /**
     * Function for finding minimum vector sum
     * @param points list of vectors
     */
    fun solve(points: List<Point>): QueryResult {
        val absolutePoints = points.map { p -> abs(p) }

        val pointFinder = NearestPointFinderImpl()

        val pair = pointFinder.findNearestPoints(absolutePoints)

        val firstReflection = getReflection(points[pair.first], absolutePoints[pair.first])

        val inversePoint = Point(-absolutePoints[pair.second].x, -absolutePoints[pair.second].y)

        val secondReflection = getReflection(points[pair.second], inversePoint)

        return QueryResult(pair.first, firstReflection, pair.second, secondReflection)
    }

    private fun abs(p: Point) = Point(abs(p.x), abs(p.y))

    private fun getReflection(base: Point, p: Point): Int {
        var result = 1

        if (p.x != base.x) {
            result++
        }

        if (p.y != base.y) {
            result += 2
        }

        return result
    }

}

/**
 * Read integer 2D points using scanner
 */
fun readPoints(scanner: Scanner): List<Point> {
    val n = scanner.nextInt()

    val result = ArrayList<Point>()

    for (i in 0 until n) {
        val x = scanner.nextInt()
        val y = scanner.nextInt()
        result.add(Point(x, y))
    }

    return result
}


fun main(args: Array<String>) {
    val points = readPoints(Scanner(System.`in`))

    val queryResult = Solver.solve(points)

    print("${queryResult.i + 1} ${queryResult.k1} ${queryResult.j + 1} ${queryResult.k2}")
}