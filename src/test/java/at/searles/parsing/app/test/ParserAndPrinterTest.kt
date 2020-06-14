package at.searles.parsing.app.test

import at.searles.buf.CharStream
import at.searles.lexer.Lexer
import at.searles.lexer.TokenStream
import at.searles.parsing.*
import at.searles.parsing.Parser.Companion.fromRegex
import at.searles.parsing.ParserStream.Companion.createParserStream
import at.searles.parsing.Reducer.Companion.opt
import at.searles.parsing.Reducer.Companion.rep
import at.searles.parsing.printing.ConcreteSyntaxTree
import at.searles.regex.CharSet
import org.junit.Assert
import org.junit.Test
import java.util.*

class ParserAndPrinterTest {
    private var parser: Parser<Expr>? = null
    private lateinit var input: ParserStream
    private var result: Expr? = null
    private var output: ConcreteSyntaxTree? = null

    @Test
    fun testIdIterativeParser() {
        // prepare
        withParser(Parsers.ITERATIVE)
        withInput("a")

        // act
        parse()

        // test
        Assert.assertNotNull(result)
        Assert.assertEquals("a", result.toString())
    }

    @Test
    fun testIdRecursiveParser() {
        // prepare
        withParser(Parsers.RECURSIVE)
        withInput("a")

        // act
        parse()

        // test
        Assert.assertNotNull(result)
        Assert.assertEquals("a", result.toString())
    }

    @Test
    fun testIdWrappedIterativeParser() {
        // prepare
        withParser(Parsers.ITERATIVE)
        withInput("((a))")

        // act
        parse()

        // test
        Assert.assertNotNull(result)
        Assert.assertEquals("a", result.toString())
    }

    @Test
    fun testIdWrappedRecursiveParser() {
        // prepare
        withParser(Parsers.RECURSIVE)
        withInput("((a))")

        // act
        parse()

        // test
        Assert.assertNotNull(result)
        Assert.assertEquals("a", result.toString())
    }

    @Test
    fun testSimpleAppIterativeParser() {
        // prepare
        withParser(Parsers.ITERATIVE)
        withInput("ab")

        // act
        parse()
        print()

        // test
        Assert.assertNotNull(result)
        Assert.assertEquals("ab", output.toString())
    }

    @Test
    fun testSimpleAppRecursiveParser() {
        // prepare
        withParser(Parsers.RECURSIVE)
        withInput("ab")

        // act
        parse()
        print()

        // test
        Assert.assertNotNull(result)
        Assert.assertEquals("ab", output.toString())
    }

    @Test
    fun testLongAppIterativeParser() {
        // prepare
        withParser(Parsers.ITERATIVE)
        withInput("abc(def)")

        // act
        parse()
        print()

        // test
        Assert.assertNotNull(result)
        Assert.assertEquals("abc(def)", output.toString())
    }

    @Test
    fun testLongAppRecursiveParser() {
        // prepare
        withParser(Parsers.RECURSIVE)
        withInput("abc(def)")

        // act
        parse()
        print()

        // test
        Assert.assertNotNull(result)
        Assert.assertEquals("abcdef", output.toString())
    }

    @Test
    fun testIterativeParserRecursivePrinter() {
        // prepare
        withParser(Parsers.ITERATIVE)
        withInput("abcde")

        // act
        parse()
        withParser(Parsers.RECURSIVE)
        print()

        // test
        Assert.assertNotNull(result)
        Assert.assertEquals("(((ab)c)d)e", output.toString())
    }

    @Test
    fun testRecursiveParserIterativePrinter() {
        // prepare
        withParser(Parsers.RECURSIVE)
        withInput("abcde")

        // act
        parse()
        withParser(Parsers.ITERATIVE)
        print()

        // test
        Assert.assertNotNull(result)
        Assert.assertEquals("a(b(c(de)))", output.toString())
    }

    @Test
    fun testLotsOfData() {
        // about 3.5 seconds for 1000000
        // about 35 seconds for 10000000
        val startTime = System.currentTimeMillis()
        val duration = { (System.currentTimeMillis() - startTime).toFloat() / 1000f }

        input = ParserStream(TokenStream.fromCharStream(stream(1000000)))
        withParser(Parsers.ITERATIVE)
        parse()
        println("Parser successful: ${duration()}")
        print()
        println("Printer successful: ${duration()}")
        val str = output.toString()
        println(str.length)
        withInput(str)
        parse()
        println("Parsing output successful: ${duration()}")
        print()
        val str2 = output.toString()
        Assert.assertEquals(str, str2)
        println("duration: ${duration()}")
        //System.out.println(this.output);
    }

    private fun stream(sizeLimit: Int): CharStream {
        return object : CharStream {
            val rnd = Random()
            var countOpen = 0
            var count = 0
            var justOpened = true
            override fun next(): Int {
                var random = Math.abs(rnd.nextInt())
                if (count > sizeLimit && !justOpened) {
                    if (countOpen > 0) {
                        countOpen--
                        return ')'.toInt()
                    }
                    return -1
                }
                count++
                random = if (count > sizeLimit) random % 26 else random % 40
                if (random < 26) {
                    justOpened = false
                    return random + 'a'.toInt()
                }
                if (countOpen > 0 && random % 3 != 0 && !justOpened) {
                    countOpen--
                    return ')'.toInt()
                }
                justOpened = true
                countOpen++
                return '('.toInt()
            }
        }
    }

    private fun withInput(input: String) {
        this.input = input.createParserStream()
    }

    private fun parse() {
        result = null
        result = parser!!.parse(input)
    }

    private fun print() {
        output = null
        assert(result != null)
        output = parser!!.print(result!!)
    }

    private fun withParser(parser: Parser<Expr>) {
        this.parser = parser
    }

    private enum class Parsers : Parser<Expr> {
        RECURSIVE {
            override fun recognize(stream: ParserStream): Boolean {
                return false
            }

            val exprParser = Ref<Expr>("expr")
            val term = term(exprParser)
            val exprReducer: Reducer<Expr, Expr> = exprParser.fold(
                    object : Fold<Expr, Expr, Expr> {
                        override fun apply(stream: ParserStream, left: Expr, right: Expr): Expr {
                            return left.app(right)
                        }

                        override fun leftInverse(result: Expr): Expr? {
                            return result.left()
                        }

                        override fun rightInverse(result: Expr): Expr? {
                            return result.right()
                        }
                    }
            )

            override fun parse(stream: ParserStream): Expr? {
                return exprParser.parse(stream)
            }

            override fun print(item: Expr): ConcreteSyntaxTree? {
                return exprParser.print(item)
            }

            // this one is recursive, hence
            init {
                exprParser.set(term.plus(exprReducer.opt()))
            }
        },
        ITERATIVE {
            override fun recognize(stream: ParserStream): Boolean {
                return false
            }

            val exprParser = Ref<Expr>("expr")
            val term = term(exprParser)
            val appReducer: Reducer<Expr, Expr> = term.fold(
                    object : Fold<Expr, Expr, Expr> {
                        override fun apply(stream: ParserStream, left: Expr, right: Expr): Expr {
                            return left.app(right)
                        }

                        override fun leftInverse(result: Expr): Expr? {
                            return result.left()
                        }

                        override fun rightInverse(result: Expr): Expr? {
                            return result.right()
                        }
                    })

            override fun parse(stream: ParserStream): Expr? {
                return exprParser.parse(stream)
            }

            override fun print(item: Expr): ConcreteSyntaxTree? {
                return exprParser.print(item)
            }

            init {
                exprParser.set(term.plus(appReducer.rep()))
            }
        };

        fun term(exprParser: Ref<Expr>): Parser<Expr> {
            val tokenizer = Lexer()
            val idParser: Parser<Expr> = fromRegex(CharSet.interval('a'.toInt(), 'z'.toInt()), tokenizer, false,
                    object : Mapping<CharSequence, Expr> {
                        override fun parse(stream: ParserStream, input: CharSequence): Id {
                            return Id(input.toString())
                        }

                        override fun left(result: Expr): CharSequence? {
                            return result.id()
                        }
                    })
            val wrappedExprParser: Parser<Expr> = Recognizer.fromString("(", tokenizer, false).plus(exprParser).plus(Recognizer.fromString(")", tokenizer, false))
            return idParser.or(wrappedExprParser)
        }
    }
}