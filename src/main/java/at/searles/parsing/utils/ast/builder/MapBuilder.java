package at.searles.parsing.utils.ast.builder;

import at.searles.parsing.ParserCallBack;
import at.searles.parsing.Mapping;
import at.searles.parsing.ParserStream;
import at.searles.parsing.PrinterCallBack;
import at.searles.parsing.utils.ast.AstNode;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class MapBuilder<L, V> implements Mapping<Map<L, V>, AstNode> {
    private final L label;
    private final AstNodeBuilder<L> builder;

    public MapBuilder(L label, AstNodeBuilder<L> builder) {
        this.label = label;
        this.builder = builder;
    }

    @Override
    public AstNode parse(ParserCallBack env, ParserStream stream, @NotNull Map<L, V> left) {
        return builder.createMap(stream.createSourceInfo(), label, left);
    }

    @Override
    public Map<L, V> left(PrinterCallBack env, @NotNull AstNode result) {
        return builder.matchMap(label, result);
    }

    @Override
    public String toString() {
        return "{node}";
    }
}
