package at.searles.parsing.combinators

import at.searles.parsing.Reducer
import at.searles.parsing.printing.PartialTree

open class ReducerOrReducerWithReversedPrintOrder<T, U>(choice0: Reducer<T, U>, choice1: Reducer<T, U>) : ReducerOrReducer<T, U>(choice0, choice1) {
    override fun print(item: U): PartialTree<T>? {
        return choice1.print(item) ?: choice0.print(item)
    }
}