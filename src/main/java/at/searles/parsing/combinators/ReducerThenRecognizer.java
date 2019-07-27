package at.searles.parsing.combinators;

import at.searles.parsing.*;
import at.searles.parsing.printing.PartialConcreteSyntaxTree;
import at.searles.parsing.printing.ConcreteSyntaxTree;
import org.jetbrains.annotations.NotNull;

public class ReducerThenRecognizer<T, U> implements Reducer<T, U>, Recognizable.Then {
    private final Reducer<T, U> left;
    private final Recognizer right;

    public ReducerThenRecognizer(Reducer<T, U> left, Recognizer right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Recognizable left() {
        return left;
    }

    @Override
    public Recognizable right() {
        return right;
    }

    @Override
    public U parse(Environment env, ParserStream stream, @NotNull T left) {
        long offset = stream.offset();

        long preStart = stream.start();
        long preEnd = stream.end();

        U result = this.left.parse(env, stream, left);

        assert stream.start() == preStart;

        if(result == null) {
            return null;
        }

        if(!right.recognize(env, stream)) {
            env.notifyNoMatch(stream, this);

            stream.setOffset(offset);
            assert stream.start() == preStart;
            stream.setEnd(preEnd);

            return null;
        }

        stream.setStart(preStart);

        return result;
    }


    @Override
    public boolean recognize(Environment env, ParserStream stream) {
        long preStart = stream.start();

        boolean status = Recognizable.Then.super.recognize(env, stream);

        if(status) {
            stream.setStart(preStart);
        }

        return status;
    }

    @Override
    public PartialConcreteSyntaxTree<T> print(Environment env, @NotNull U u) {
        PartialConcreteSyntaxTree<T> leftOutput = left.print(env, u);

        if(leftOutput == null) {
            return null;
        }

        ConcreteSyntaxTree rightOutput = right.print(env);

        return new PartialConcreteSyntaxTree<>(leftOutput.left, leftOutput.right.consRight(rightOutput));
    }

    @Override
    public String toString() {
        return createString();
    }
}
