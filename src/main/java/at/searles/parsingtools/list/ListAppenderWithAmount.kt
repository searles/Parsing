package at.searles.parsingtools.list

import at.searles.parsing.Fold
import at.searles.parsing.ParserStream
import kotlin.math.min

class ListAppenderWithAmount<T>(private val minSize: Int = 0) : Fold<List<T>, Pair<T, Int>, List<T>> {

    override fun apply(stream: ParserStream, left: List<T>, right: Pair<T, Int>): List<T> {
        require(right.second >= 0) { "Must be in range 0..${Int.MAX_VALUE}" }

        return (0 until right.second).fold(BacktrackingList.create(left)) { list, _ ->
            list.pushBack(right.first)
        }
    }

    private fun countSameAtEnd(l: List<T>): Int {
        if(l.isEmpty()) return 0

        var i = 1

        val iterator = l.listIterator(l.size - 1)
        while (iterator.hasPrevious()) {
            if (iterator.previous() == l.last()) {
                i++
            }
        }

        return i
    }

    private fun countSameAtEndKeepMin(l: List<T>): Int {
        return min(l.size - minSize, countSameAtEnd(l))
    }

    override fun leftInverse(item: List<T>): List<T>? {
        val count = countSameAtEndKeepMin(item)

        if(count <= 0) {
            return null
        }

        return item.subList(0, item.size - count)
    }

    override fun rightInverse(item: List<T>): Pair<T, Int>? {
        val count = countSameAtEndKeepMin(item)

        if(count <= 0) {
            return null
        }

        return Pair(item.last(), count)
    }

    override fun toString(): String {
        return "{appendRle}"
    }
}
