package at.searles.parsing.combinators

import at.searles.parsing.ParserStream
import at.searles.parsing.Recognizer
import at.searles.parsing.Reducer
import at.searles.parsing.printing.ConcreteSyntaxTree
import at.searles.parsing.printing.PartialTree
import java.util.ArrayList

/* This is left here to illustrate why this is a bad idea:
 * Imagine a parser a.rep() + b.rep(), it will always succeed and
 * consume nothing.
 */
class ReducerRepThenReducer<T, U>(private val reducer: Reducer<T, T>, private val right: Reducer<T, U>): Reducer<T, U> {
    override fun reduce(left: T, stream: ParserStream): U? {
        var item = left

        while (true) {
            stream.reduce(item, right)?.let {
                return it
            }

            val nextItem = stream.reduce(item, reducer) ?: return null
            item = nextItem
        }
    }

    override fun recognize(stream: ParserStream): Boolean {
        while (true) {
            if(stream.recognize(right, false)) {
                return true
            }

            if(!stream.recognize(reducer, false)) {
                return false
            }
        }
    }

    override fun print(item: U): PartialTree<T>? {
        val rightTree = right.print(item) ?: return null

        var left: T = rightTree.left
        val trees = ArrayList<ConcreteSyntaxTree>()

        while (true) {
            val next = reducer.print(left) ?: break
            trees.add(next.right)
            left = next.left
        }

        trees.reverse()

        return PartialTree(left, ConcreteSyntaxTree.fromList(trees + rightTree.right))
    }
//
//    override fun <V> plus(right: Reducer<U, V>): Reducer<T, V> {
//        return ReducerRepThenReducer(reducer, this.right + right)
//    }
//
//    override fun plus(right: Recognizer): Reducer<T, U> {
//        return ReducerRepThenReducer(reducer, this.right + right)
//    }

    override fun toString(): String {
        return "$reducer.rep.plus($right)"
    }
}