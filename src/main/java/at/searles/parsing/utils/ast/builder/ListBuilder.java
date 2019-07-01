package at.searles.parsing.utils.ast.builder;

import at.searles.parsing.Environment;
import at.searles.parsing.Mapping;
import at.searles.parsing.ParserStream;
import at.searles.parsing.utils.ast.AstNode;

import java.util.List;

public class ListBuilder<L, V> implements Mapping<List<V>, AstNode> {

    private final L label;
    private final AstNodeBuilder<L> builder;

    public ListBuilder(L label, AstNodeBuilder<L> builder) {
        this.label = label;
        this.builder = builder;
    }

    @Override
    public AstNode parse(Environment env, List<V> left, ParserStream stream) {
        return builder.createList(stream, label, left);
    }

    @Override
    public List<V> left(Environment env, AstNode result) {
        return builder.matchList(result);
    }

    @Override
    public String toString() {
        return "{listnode}";
    }
}
