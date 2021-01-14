package at.searles.parsing

interface Fold<T, U, V> {
    fun apply(stream: ParserStream, left: T, right: U): V

    fun leftInverse(item: V): T? {
        return null
    }

    fun rightInverse(item: V): U? {
        return null
    }

    companion object {
        fun <T, U, V> create(fn: (T, U) -> V): Fold<T, U, V> {
            return object: Fold<T, U, V> {
                override fun apply(stream: ParserStream, left: T, right: U): V {
                    return fn(left, right)
                }

                override fun toString(): String {
                    return "{t,u->v}"
                }
            }
        }

        fun <T, U, V> create(leftFn: (V) -> T?, rightFn: (V) -> U?, fn: (T, U) -> V): Fold<T, U, V> {
            return object: Fold<T, U, V> {
                override fun apply(stream: ParserStream, left: T, right: U): V {
                    return fn(left, right)
                }

                override fun leftInverse(item: V): T? {
                    return leftFn(item)
                }

                override fun rightInverse(item: V): U? {
                    return rightFn(item)
                }

                override fun toString(): String {
                    return "{t,u->v}"
                }
            }
        }

        fun <T, U, V> create(leftFn: (V) -> T?, rightFn: (V) -> U?, fn: (Trace, T, U) -> V): Fold<T, U, V> {
            return object: Fold<T, U, V> {
                override fun apply(stream: ParserStream, left: T, right: U): V {
                    return fn(stream.createTrace(), left, right)
                }

                override fun leftInverse(item: V): T? {
                    return leftFn(item)
                }

                override fun rightInverse(item: V): U? {
                    return rightFn(item)
                }

                override fun toString(): String {
                    return "{t,u->v}"
                }
            }
        }
    }
}