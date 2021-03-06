package at.searles.parsingtools.opt

import at.searles.parsing.Mapping
import at.searles.parsing.ParserStream
import java.util.*

class SomeCreator<T> : Mapping<T, Optional<T>> {
    override fun reduce(left: T, stream: ParserStream): Optional<T> {
        return Optional.of(left)
    }

    override fun left(result: Optional<T>): T? {
        return result.orElse(null)
    }

    override fun toString(): String {
        return "{some}"
    }
}
