package at.searles.parsingtools.common

import at.searles.parsing.Fold
import at.searles.parsing.ParserStream

class SwappedPairCreator<T, U> : Fold<T, U, Pair<U, T>> {
    override fun apply(stream: ParserStream, left: T, right: U): Pair<U, T> {
        return Pair(right, left)
    }

    override fun leftInverse(item: Pair<U, T>): T? {
        return item.second
    }

    override fun rightInverse(item: Pair<U, T>): U? {
        return item.first
    }

    override fun toString(): String {
        return "{<y,x>}"
    }
}
