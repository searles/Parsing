package at.searles.parsing.utils.common;

import at.searles.parsing.Environment;
import at.searles.parsing.Mapping;
import at.searles.parsing.ParserStream;
import org.jetbrains.annotations.NotNull;

public class Increment implements Mapping<Integer, Integer> {

    private final int min;

    public Increment(int min) {
        this.min = min;
    }

    @Override
    public Integer parse(Environment env, ParserStream stream, @NotNull Integer left) {
        return left + 1;
    }

    @Override
    public Integer left(Environment env, @NotNull Integer result) {
        return result > min ? result - 1 : null;
    }

    @Override
    public String toString() {
        return String.format("{%d...+1}", min);
    }
}
