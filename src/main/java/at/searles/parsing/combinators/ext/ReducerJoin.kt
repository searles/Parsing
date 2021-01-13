package at.searles.parsing.combinators.ext

import at.searles.parsing.Recognizer
import at.searles.parsing.Reducer
import at.searles.parsing.Reducer.Companion.opt
import at.searles.parsing.Reducer.Companion.rep

class ReducerJoin<T>(private val reducer: Reducer<T, T>, private val separator: Recognizer) :
    ReducerDecorator<T, T>((reducer + (separator + reducer).rep()).opt(), ((reducer + separator).rep() + reducer).opt()) {
    override fun toString(): String {
        return "$reducer.rep($separator)"
    }
}