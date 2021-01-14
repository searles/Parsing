package at.searles.parsingtools.map

import at.searles.parsing.Fold
import at.searles.parsing.ParserStream

class MapPutter<K, V>(private val key: K) : Fold<Map<K, V>, V, Map<K, V>> {

    override fun apply(stream: ParserStream, left: Map<K, V>, right: V): Map<K, V> {
        return left + (key to right)
    }

    override fun leftInverse(item: Map<K, V>): Map<K, V>? {
        if (!item.containsKey(key)) {
            return null
        }

        return item - key
    }

    override fun rightInverse(item: Map<K, V>): V? {
        return item[key]
    }

    override fun toString(): String {
        return "{put $key}"
    }
}
