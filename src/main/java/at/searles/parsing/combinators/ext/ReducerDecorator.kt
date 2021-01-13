package at.searles.parsing.combinators.ext

import at.searles.parsing.ParserStream
import at.searles.parsing.Recognizer
import at.searles.parsing.Reducer
import at.searles.parsing.printing.PartialTree

open class ReducerDecorator<T, U>(private val reducer: Reducer<T, U>, private val printer: Reducer<T, U>): Reducer<T, U> {
    override fun reduce(left: T, stream: ParserStream): U? {
        return reducer.reduce(left, stream)
    }

    override fun recognize(stream: ParserStream): Boolean {
        return reducer.recognize(stream)
    }

    override fun print(item: U): PartialTree<T>? {
        return printer.print(item)
    }

    override fun <V> plus(right: Reducer<U, V>): Reducer<T, V> {
        return ReducerDecorator(reducer + right, printer + right)
    }

    override fun plus(right: Recognizer): Reducer<T, U> {
        return ReducerDecorator(reducer + right, printer + right)
    }

    override fun or(other: Reducer<T, U>): Reducer<T, U> {
        return ReducerDecorator(reducer or other, printer or other)
    }

    override fun orPrintSwap(other: Reducer<T, U>): Reducer<T, U> {
        return ReducerDecorator(reducer orPrintSwap other, printer orPrintSwap other)
    }
}