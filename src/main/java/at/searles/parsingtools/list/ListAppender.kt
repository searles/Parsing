package at.searles.parsingtools.list

import at.searles.parsing.Fold
import at.searles.parsing.ParserStream

class ListAppender<T>(private val minSize: Int = 0) : Fold<List<T>, T, List<T>> {

    override fun apply(stream: ParserStream, left: List<T>, right: T): List<T> {
        return BacktrackingList.create(left).pushBack(right)
    }

    private fun cannotInvert(list: List<T>): Boolean {
        return list.size <= minSize
    }

    override fun leftInverse(item: List<T>): List<T>? {
        return if (cannotInvert(item)) {
            null
        } else item.subList(0, item.size - 1)

    }

    override fun rightInverse(item: List<T>): T? {
        return if (cannotInvert(item)) {
            null
        } else item[item.size - 1]
    }

    override fun toString(): String {
        return "{append}"
    }
}
