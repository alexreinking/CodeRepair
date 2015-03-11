package coderepair.synthesis.trees;

/**
 * Created by alexreinking on 3/10/15.
 */
public abstract class ExpressionTreeVisitor<E> {
    public E visit(ExpressionTree tree) {
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

    public abstract E visitClassCast(ClassCastExpressionTree tree);

    public abstract E visitConstructor(ConstructorExpressionTree tree);

    public abstract E visitMethodCall(MethodCallExpressionTree tree);

    public abstract E visitStaticMethodCall(StaticMethodCallExpressionTree tree);

    public abstract E visitValue(ValueExpressionTree tree);

    public abstract E visitFieldAccess(FieldAccessExpressionTree tree);
}
