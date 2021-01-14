package at.searles.parsingtools.common

import at.searles.parsing.Fold
import at.searles.parsing.ParserStream

/**
 * Append codePoint (!) to String.
 */
class CodePointAppender : Fold<String, Int, String> {
    override fun apply(stream: ParserStream, left: String, right: Int): String {
        return left + String(Character.toChars(right))
    }

    override fun leftInverse(item: String): String? {
        if (item.isEmpty()) {
            return null
        }

        return if (Character.isHighSurrogate(item[item.length - 1])) {
            item.substring(0, item.length - 2)
        } else {
            item.substring(0, item.length - 1)
        }
    }

    override fun rightInverse(item: String): Int? {
        return if (item.isEmpty()) {
            null
        } else item.codePointAt(item.length - 1)

    }

    override fun toString(): String {
        return "{string + codepoint}"
    }
}
