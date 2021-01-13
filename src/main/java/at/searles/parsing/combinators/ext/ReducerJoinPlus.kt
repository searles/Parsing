package at.searles.parsing.combinators.ext

import at.searles.parsing.Recognizer
import at.searles.parsing.Reducer
import at.searles.parsing.Reducer.Companion.rep

class ReducerJoinPlus<T>(private val reducer: Reducer<T, T>, private val separator: Recognizer) :
    ReducerDecorator<T, T>(reducer + (separator + reducer).rep(), (reducer + separator).rep() + reducer) {

    override fun toString(): String {
        return "$reducer.rep1($separator)"
    }

}