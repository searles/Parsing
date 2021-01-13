package at.searles.parsing.combinators.ext

import at.searles.parsing.Reducer
import at.searles.parsing.Reducer.Companion.rep

class ReducerRepN<T>(private val reducer: Reducer<T, T>, private val minCount: Int) :
    ReducerDecorator<T, T>(
        (1 until minCount).fold(reducer) { sequence, _ -> sequence + reducer } + reducer.rep(),
        reducer.rep() + (1 until minCount).fold(reducer) { sequence, _ -> sequence + reducer },
    ) {
    override fun toString(): String {
        return "$reducer.rep[$minCount]"
    }
}