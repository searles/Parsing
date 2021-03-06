package at.searles.regexp

class Text(seq: CharSequence) : Regexp {
    private val string: String = seq.toString()

    override fun toString(): String {
        return string
    }

    override fun <A> accept(visitor: Visitor<A>): A {
        return visitor.visitText(string)
    }

    companion object {
        fun many(vararg seqs: CharSequence): Regexp {
            return seqs.drop(1).fold(Text(seqs.first()) as Regexp, { a, b -> a or Text(b) } )
        }

        fun caseInsensitive(seq: CharSequence): Regexp {
            return seq.drop(1).fold(CharSet.ichars(seq.first()) as Regexp, {rex, chr -> rex + CharSet.ichars(chr)} )
        }

        fun imany(vararg seqs: CharSequence): Regexp {
            return seqs.drop(1).fold(caseInsensitive(seqs.first()), { a, b -> a or caseInsensitive(b) } )
        }
    }
}