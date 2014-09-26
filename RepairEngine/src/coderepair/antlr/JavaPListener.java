// Generated from /Users/alexreinking/Development/CodeRepair/RepairEngine/JavaP.g4 by ANTLR 4.4.1-dev
package coderepair.antlr;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link JavaPParser}.
 */
public interface JavaPListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link JavaPParser#throwsBlock}.
	 * @param ctx the parse tree
	 */
	void enterThrowsBlock(@NotNull JavaPParser.ThrowsBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaPParser#throwsBlock}.
	 * @param ctx the parse tree
	 */
	void exitThrowsBlock(@NotNull JavaPParser.ThrowsBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaPParser#generics}.
	 * @param ctx the parse tree
	 */
	void enterGenerics(@NotNull JavaPParser.GenericsContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaPParser#generics}.
	 * @param ctx the parse tree
	 */
	void exitGenerics(@NotNull JavaPParser.GenericsContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaPParser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterClassDeclaration(@NotNull JavaPParser.ClassDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaPParser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitClassDeclaration(@NotNull JavaPParser.ClassDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaPParser#javap}.
	 * @param ctx the parse tree
	 */
	void enterJavap(@NotNull JavaPParser.JavapContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaPParser#javap}.
	 * @param ctx the parse tree
	 */
	void exitJavap(@NotNull JavaPParser.JavapContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaPParser#extension}.
	 * @param ctx the parse tree
	 */
	void enterExtension(@NotNull JavaPParser.ExtensionContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaPParser#extension}.
	 * @param ctx the parse tree
	 */
	void exitExtension(@NotNull JavaPParser.ExtensionContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaPParser#typeName}.
	 * @param ctx the parse tree
	 */
	void enterTypeName(@NotNull JavaPParser.TypeNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaPParser#typeName}.
	 * @param ctx the parse tree
	 */
	void exitTypeName(@NotNull JavaPParser.TypeNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaPParser#memberDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterMemberDeclaration(@NotNull JavaPParser.MemberDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaPParser#memberDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitMemberDeclaration(@NotNull JavaPParser.MemberDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaPParser#implementation}.
	 * @param ctx the parse tree
	 */
	void enterImplementation(@NotNull JavaPParser.ImplementationContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaPParser#implementation}.
	 * @param ctx the parse tree
	 */
	void exitImplementation(@NotNull JavaPParser.ImplementationContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaPParser#fieldDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterFieldDeclaration(@NotNull JavaPParser.FieldDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaPParser#fieldDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitFieldDeclaration(@NotNull JavaPParser.FieldDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaPParser#typeList}.
	 * @param ctx the parse tree
	 */
	void enterTypeList(@NotNull JavaPParser.TypeListContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaPParser#typeList}.
	 * @param ctx the parse tree
	 */
	void exitTypeList(@NotNull JavaPParser.TypeListContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaPParser#modifiers}.
	 * @param ctx the parse tree
	 */
	void enterModifiers(@NotNull JavaPParser.ModifiersContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaPParser#modifiers}.
	 * @param ctx the parse tree
	 */
	void exitModifiers(@NotNull JavaPParser.ModifiersContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaPParser#identifier}.
	 * @param ctx the parse tree
	 */
	void enterIdentifier(@NotNull JavaPParser.IdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaPParser#identifier}.
	 * @param ctx the parse tree
	 */
	void exitIdentifier(@NotNull JavaPParser.IdentifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaPParser#genericParameter}.
	 * @param ctx the parse tree
	 */
	void enterGenericParameter(@NotNull JavaPParser.GenericParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaPParser#genericParameter}.
	 * @param ctx the parse tree
	 */
	void exitGenericParameter(@NotNull JavaPParser.GenericParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaPParser#methodDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterMethodDeclaration(@NotNull JavaPParser.MethodDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaPParser#methodDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitMethodDeclaration(@NotNull JavaPParser.MethodDeclarationContext ctx);
}