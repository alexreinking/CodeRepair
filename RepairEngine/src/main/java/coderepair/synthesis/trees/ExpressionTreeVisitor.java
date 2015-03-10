package coderepair.synthesis.trees;

/**
 * Created by alexreinking on 3/10/15.
 */
public class ExpressionTreeVisitor<E> {
    private final E defaultValue;

    public ExpressionTreeVisitor() {
        this(null);
    }

    public ExpressionTreeVisitor(E defaultValue) {
        this.defaultValue = defaultValue;
    }

    public E visit(ExpressionTree tree) {
        switch (tree.getLeaf().getKind()) {
            case ClassCast:
                return visitClassCast((ClassCastExpressionTree) tree);
            case Constructor:
                return visitConstructor((ConstructorExpressionTree) tree);
            case Method:
                return visitMethodCall((MethodCallExpressionTree) tree);
            case Value:
                if (!tree.getLeaf().isStatic())
                    return visitValue((ValueExpressionTree) tree);
                return visitFieldAccess((FieldAccessExpressionTree) tree);
        }
        return defaultValue;
    }

    public E visitClassCast(ClassCastExpressionTree tree) {
        tree.getChildren().forEach(this::visit);
        return defaultValue;
    }

    public E visitConstructor(ConstructorExpressionTree tree) {
        tree.getChildren().forEach(this::visit);
        return defaultValue;
    }

    public E visitMethodCall(MethodCallExpressionTree tree) {
        tree.getChildren().forEach(this::visit);
        return defaultValue;
    }

    public E visitFieldAccess(FieldAccessExpressionTree tree) {
        tree.getChildren().forEach(this::visit);
        return defaultValue;
    }

    public E visitValue(ValueExpressionTree tree) {
        tree.getChildren().forEach(this::visit);
        return defaultValue;
    }
}
