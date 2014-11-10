// Generated from /Users/alexreinking/Development/CodeRepair/RepairEngine/JavaP.g4 by ANTLR 4.4.1-dev
package coderepair.antlr;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link JavaPParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface JavaPVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link JavaPParser#memberDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMemberDeclaration(@NotNull JavaPParser.MemberDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaPParser#identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdentifier(@NotNull JavaPParser.IdentifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaPParser#methodDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethodDeclaration(@NotNull JavaPParser.MethodDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaPParser#extension}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExtension(@NotNull JavaPParser.ExtensionContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaPParser#implementation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImplementation(@NotNull JavaPParser.ImplementationContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaPParser#typeName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeName(@NotNull JavaPParser.TypeNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaPParser#fieldDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFieldDeclaration(@NotNull JavaPParser.FieldDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaPParser#throwsBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitThrowsBlock(@NotNull JavaPParser.ThrowsBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaPParser#modifiers}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModifiers(@NotNull JavaPParser.ModifiersContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaPParser#javap}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJavap(@NotNull JavaPParser.JavapContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaPParser#genericParameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGenericParameter(@NotNull JavaPParser.GenericParameterContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaPParser#classDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassDeclaration(@NotNull JavaPParser.ClassDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaPParser#typeList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeList(@NotNull JavaPParser.TypeListContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaPParser#generics}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGenerics(@NotNull JavaPParser.GenericsContext ctx);
}