package coderepair.synthesis.trees;

/**
 * Created by alexreinking on 3/10/15.
 */
public abstract class ExpressionTreeVisitor<E> {
    protected E visit(ExpressionTree tree) {
        switch (tree.getLeaf().getKind()) {
            case ClassCast:
                return visitClassCast((ClassCastExpressionTree) tree);
            case Constructor:
                return visitConstructor((ConstructorExpressionTree) tree);
            case Method:
                return visitMethodCall((MethodCallExpressionTree) tree);
            case StaticMethod:
                return visitStaticMethodCall((StaticMethodCallExpressionTree) tree);
            case Value:
                return visitValue((ValueExpressionTree) tree);
            case Field:
                return visitFieldAccess((FieldAccessExpressionTree) tree);
            default:
                System.err.println("encountered " + tree.getLeaf().getKind());
                assert false;
        }
        return null;
    }

    protected abstract E visitClassCast(ClassCastExpressionTree tree);

    protected abstract E visitConstructor(ConstructorExpressionTree tree);

    protected abstract E visitMethodCall(MethodCallExpressionTree tree);

    protected abstract E visitStaticMethodCall(StaticMethodCallExpressionTree tree);

    protected abstract E visitValue(ValueExpressionTree tree);

    protected abstract E visitFieldAccess(FieldAccessExpressionTree tree);
}
