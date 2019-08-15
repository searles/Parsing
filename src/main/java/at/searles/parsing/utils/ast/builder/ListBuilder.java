package at.searles.parsing.utils.ast.builder;

import at.searles.parsing.ParserCallBack;
import at.searles.parsing.Mapping;
import at.searles.parsing.ParserStream;
import at.searles.parsing.PrinterCallBack;
import at.searles.parsing.utils.ast.AstNode;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ListBuilder<L, V> implements Mapping<List<V>, AstNode> {

    private final L label;
    private final AstNodeBuilder<L> builder;

    public ListBuilder(L label, AstNodeBuilder<L> builder) {
        this.label = label;
        this.builder = builder;
    }

    @Override
    public AstNode parse(ParserCallBack env, ParserStream stream, @NotNull List<V> left) {
        return builder.createList(stream.createSourceInfo(), label, left);
    }

    @Override
    public List<V> left(PrinterCallBack env, @NotNull AstNode result) {
        return builder.matchList(result);
    }

    @Override
    public String toString() {
        return "{listnode}";
    }
}
