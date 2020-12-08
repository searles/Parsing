package at.searles.lexer

import at.searles.buf.*
import at.searles.lexer.utils.IntSet

/**
 * This wrapper around a FrameStream reads from the frame stream
 * as long as characters can be fetched from the used lexer.
 * The internal fields then indicate which tokens in the given matcher
 * were fetched.
 *
 *
 * If the token then is consumed, the underlying frame stream
 * is flushed so that it is ready for the next token.
 *
 *
 * If no token could be found, then the token set is empty.
 *
 *
 * If there is a match but it is not used, the reset-method
 * will revert all changes.
 */
class TokenStream(private val stream: FrameStream) {

    var listener: Listener? = null

    val frame: Frame get() {
        return stream.frame
    }

    /**
     * Values to determine current match
     */
    private var lexer: Lexer? = null
    private var acceptedTokens: IntSet? = null
    private var isConsumed = true

    /**
     * @return Returns the start position of the next token that will be parsed.
     */
    var offset: Long
        get() {
            // if lexer == null, use endPosition because framestream still captures last match.
            // if lexer is set, use startPosition.
            return if (isConsumed) stream.frame.end else stream.frame.start
        }
        set(value) {
            stream.reset()
            acceptedTokens = null
            lexer = null
            stream.position = value
        }

    /**
     * If the current token has not been consumed or a different lexer
     * is used, this method does nothing except for returning
     * whether a valid token has been fetched. Otherwise, it sets the
     * currently accepted tokens-field and sets the correct frame
     * in the underlying frameStream.
     *
     * @return null if there is no accepted token. This
     * is not necessarily equivalent to EOF, because EOF is just
     * defined as '-1' and thus can be recognized by
     * a lexer.
     */
    fun getAcceptedTokens(lexer: Lexer): IntSet? {
        // can we reuse the old result?
        if (!isConsumed && this.lexer == lexer) {
            return acceptedTokens
        }

        // no
        if (isConsumed) {
            stream.advance()
        } else  /* if(this.lexer != lexer) */ {
            stream.reset()
        }

        isConsumed = false
        this.lexer = lexer
        acceptedTokens = lexer.readNextToken(stream)
        return acceptedTokens
    }

    /**
     * A lazy advance method like in C iterators. Only the
     * next call to 'current' will actually advance. This
     * is because the frame must remain valid for now.
     *
     * @param consumedTokenId The id of the actually consumed token
     */
    fun advance(consumedTokenId: Int) {
        if (isConsumed) {
            // last was not consumed.
            // well, I guess a 'skip 3 tokens'
            // can be useful...
            getAcceptedTokens(lexer!!)
        }
        isConsumed = true

        // inform listeners
        notifyTokenConsumed(consumedTokenId, stream.frame)
    }

    private fun notifyTokenConsumed(tokenId: Int, frame: Frame) {
        listener?.onToken(tokenId, frame, this)
    }

    override fun toString(): String {
        return stream.toString()
    }

    interface Listener {
        fun onToken(tokenId: Int, frame: Frame, stream: TokenStream)
    }

    companion object {
        fun fromString(s: CharSequence): TokenStream {
            return TokenStream(StringWrapper(s))
        }

        fun fromCharStream(stream: CharStream): TokenStream {
            return TokenStream(FrameStreamImpl(BufferedStream.Impl(stream, 1024)))
        }
    }

}