// Generated from /Users/alexreinking/Development/CodeRepair/RepairEngine/JavaP.g4 by ANTLR 4.4.1-dev
package coderepair.antlr;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class JavaPParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.4.1-dev", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__7=1, T__6=2, T__5=3, T__4=4, T__3=5, T__2=6, T__1=7, T__0=8, ABSTRACT=9, 
		BOOLEAN=10, BYTE=11, CHAR=12, CLASS=13, DOUBLE=14, EXTENDS=15, FINAL=16, 
		FLOAT=17, IMPLEMENTS=18, INT=19, INTERFACE=20, LONG=21, NATIVE=22, PRIVATE=23, 
		PROTECTED=24, PUBLIC=25, SHORT=26, STATIC=27, STRICTFP=28, SUPER=29, SYNCHRONIZED=30, 
		THROWS=31, TRANSIENT=32, VOID=33, VOLATILE=34, WILDCARD=35, ELLIPSIS=36, 
		PACKAGEINFO=37, SEPARATOR=38, ArrayBrackets=39, BaseIdentifier=40, WS=41;
	public static final String[] tokenNames = {
		"<INVALID>", "'{'", "'>'", "')'", "','", "'('", "'<'", "'}'", "';'", "'abstract'", 
		"'boolean'", "'byte'", "'char'", "'class'", "'double'", "'extends'", "'final'", 
		"'float'", "'implements'", "'int'", "'interface'", "'long'", "'native'", 
		"'private'", "'protected'", "'public'", "'short'", "'static'", "'strictfp'", 
		"'super'", "'synchronized'", "'throws'", "'transient'", "'void'", "'volatile'", 
		"'?'", "'...'", "'package-info'", "SEPARATOR", "'[]'", "BaseIdentifier", 
		"WS"
	};
	public static final int
		RULE_javap = 0, RULE_classDeclaration = 1, RULE_memberDeclaration = 2, 
		RULE_methodDeclaration = 3, RULE_fieldDeclaration = 4, RULE_throwsBlock = 5, 
		RULE_typeList = 6, RULE_modifiers = 7, RULE_generics = 8, RULE_genericParameter = 9, 
		RULE_extension = 10, RULE_implementation = 11, RULE_typeName = 12, RULE_identifier = 13;
	public static final String[] ruleNames = {
		"javap", "classDeclaration", "memberDeclaration", "methodDeclaration", 
		"fieldDeclaration", "throwsBlock", "typeList", "modifiers", "generics", 
		"genericParameter", "extension", "implementation", "typeName", "identifier"
	};

	@Override
	public String getGrammarFileName() { return "JavaP.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public JavaPParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class JavapContext extends ParserRuleContext {
		public ClassDeclarationContext classDeclaration(int i) {
			return getRuleContext(ClassDeclarationContext.class,i);
		}
		public List<ClassDeclarationContext> classDeclaration() {
			return getRuleContexts(ClassDeclarationContext.class);
		}
		public JavapContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_javap; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JavaPListener ) ((JavaPListener)listener).enterJavap(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JavaPListener ) ((JavaPListener)listener).exitJavap(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaPVisitor ) return ((JavaPVisitor<? extends T>)visitor).visitJavap(this);
			else return visitor.visitChildren(this);
		}
	}

	public final JavapContext javap() throws RecognitionException {
		JavapContext _localctx = new JavapContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_javap);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(31);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ABSTRACT) | (1L << CLASS) | (1L << FINAL) | (1L << INTERFACE) | (1L << NATIVE) | (1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << STATIC) | (1L << STRICTFP) | (1L << SYNCHRONIZED) | (1L << TRANSIENT) | (1L << VOLATILE))) != 0)) {
				{
				{
				setState(28); classDeclaration();
				}
				}
				setState(33);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ClassDeclarationContext extends ParserRuleContext {
		public List<MemberDeclarationContext> memberDeclaration() {
			return getRuleContexts(MemberDeclarationContext.class);
		}
		public TypeNameContext typeName() {
			return getRuleContext(TypeNameContext.class,0);
		}
		public MemberDeclarationContext memberDeclaration(int i) {
			return getRuleContext(MemberDeclarationContext.class,i);
		}
		public ImplementationContext implementation() {
			return getRuleContext(ImplementationContext.class,0);
		}
		public ExtensionContext extension() {
			return getRuleContext(ExtensionContext.class,0);
		}
		public TerminalNode INTERFACE() { return getToken(JavaPParser.INTERFACE, 0); }
		public ModifiersContext modifiers() {
			return getRuleContext(ModifiersContext.class,0);
		}
		public TerminalNode CLASS() { return getToken(JavaPParser.CLASS, 0); }
		public ClassDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JavaPListener ) ((JavaPListener)listener).enterClassDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JavaPListener ) ((JavaPListener)listener).exitClassDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaPVisitor ) return ((JavaPVisitor<? extends T>)visitor).visitClassDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ClassDeclarationContext classDeclaration() throws RecognitionException {
		ClassDeclarationContext _localctx = new ClassDeclarationContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_classDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(34); modifiers();
			setState(35);
			_la = _input.LA(1);
			if ( !(_la==CLASS || _la==INTERFACE) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			setState(36); typeName(0);
			setState(38);
			_la = _input.LA(1);
			if (_la==EXTENDS) {
				{
				setState(37); extension();
				}
			}

			setState(41);
			_la = _input.LA(1);
			if (_la==IMPLEMENTS) {
				{
				setState(40); implementation();
				}
			}

			setState(43); match(T__7);
			setState(47);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__2) | (1L << ABSTRACT) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FINAL) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NATIVE) | (1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << SHORT) | (1L << STATIC) | (1L << STRICTFP) | (1L << SYNCHRONIZED) | (1L << TRANSIENT) | (1L << VOID) | (1L << VOLATILE) | (1L << PACKAGEINFO) | (1L << BaseIdentifier))) != 0)) {
				{
				{
				setState(44); memberDeclaration();
				}
				}
				setState(49);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(50); match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MemberDeclarationContext extends ParserRuleContext {
		public FieldDeclarationContext fieldDeclaration() {
			return getRuleContext(FieldDeclarationContext.class,0);
		}
		public MethodDeclarationContext methodDeclaration() {
			return getRuleContext(MethodDeclarationContext.class,0);
		}
		public MemberDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_memberDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JavaPListener ) ((JavaPListener)listener).enterMemberDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JavaPListener ) ((JavaPListener)listener).exitMemberDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaPVisitor ) return ((JavaPVisitor<? extends T>)visitor).visitMemberDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MemberDeclarationContext memberDeclaration() throws RecognitionException {
		MemberDeclarationContext _localctx = new MemberDeclarationContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_memberDeclaration);
		try {
			setState(54);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(52); methodDeclaration();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(53); fieldDeclaration();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MethodDeclarationContext extends ParserRuleContext {
		public GenericsContext generics() {
			return getRuleContext(GenericsContext.class,0);
		}
		public List<TypeNameContext> typeName() {
			return getRuleContexts(TypeNameContext.class);
		}
		public ThrowsBlockContext throwsBlock() {
			return getRuleContext(ThrowsBlockContext.class,0);
		}
		public TypeListContext typeList() {
			return getRuleContext(TypeListContext.class,0);
		}
		public TypeNameContext typeName(int i) {
			return getRuleContext(TypeNameContext.class,i);
		}
		public ModifiersContext modifiers() {
			return getRuleContext(ModifiersContext.class,0);
		}
		public MethodDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_methodDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JavaPListener ) ((JavaPListener)listener).enterMethodDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JavaPListener ) ((JavaPListener)listener).exitMethodDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaPVisitor ) return ((JavaPVisitor<? extends T>)visitor).visitMethodDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MethodDeclarationContext methodDeclaration() throws RecognitionException {
		MethodDeclarationContext _localctx = new MethodDeclarationContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_methodDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(56); modifiers();
			setState(58);
			_la = _input.LA(1);
			if (_la==T__2) {
				{
				setState(57); generics();
				}
			}

			setState(61);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				{
				setState(60); typeName(0);
				}
				break;
			}
			setState(63); typeName(0);
			setState(64); match(T__3);
			setState(66);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << SHORT) | (1L << VOID) | (1L << PACKAGEINFO) | (1L << BaseIdentifier))) != 0)) {
				{
				setState(65); typeList();
				}
			}

			setState(68); match(T__5);
			setState(70);
			_la = _input.LA(1);
			if (_la==THROWS) {
				{
				setState(69); throwsBlock();
				}
			}

			setState(72); match(T__0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FieldDeclarationContext extends ParserRuleContext {
		public TypeNameContext typeName() {
			return getRuleContext(TypeNameContext.class,0);
		}
		public ModifiersContext modifiers() {
			return getRuleContext(ModifiersContext.class,0);
		}
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public FieldDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fieldDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JavaPListener ) ((JavaPListener)listener).enterFieldDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JavaPListener ) ((JavaPListener)listener).exitFieldDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaPVisitor ) return ((JavaPVisitor<? extends T>)visitor).visitFieldDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FieldDeclarationContext fieldDeclaration() throws RecognitionException {
		FieldDeclarationContext _localctx = new FieldDeclarationContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_fieldDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(74); modifiers();
			setState(75); typeName(0);
			setState(76); identifier();
			setState(77); match(T__0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ThrowsBlockContext extends ParserRuleContext {
		public TerminalNode THROWS() { return getToken(JavaPParser.THROWS, 0); }
		public TypeListContext typeList() {
			return getRuleContext(TypeListContext.class,0);
		}
		public ThrowsBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_throwsBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JavaPListener ) ((JavaPListener)listener).enterThrowsBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JavaPListener ) ((JavaPListener)listener).exitThrowsBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaPVisitor ) return ((JavaPVisitor<? extends T>)visitor).visitThrowsBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ThrowsBlockContext throwsBlock() throws RecognitionException {
		ThrowsBlockContext _localctx = new ThrowsBlockContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_throwsBlock);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(79); match(THROWS);
			setState(80); typeList();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TypeListContext extends ParserRuleContext {
		public List<TypeNameContext> typeName() {
			return getRuleContexts(TypeNameContext.class);
		}
		public TypeNameContext typeName(int i) {
			return getRuleContext(TypeNameContext.class,i);
		}
		public TypeListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JavaPListener ) ((JavaPListener)listener).enterTypeList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JavaPListener ) ((JavaPListener)listener).exitTypeList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaPVisitor ) return ((JavaPVisitor<? extends T>)visitor).visitTypeList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeListContext typeList() throws RecognitionException {
		TypeListContext _localctx = new TypeListContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_typeList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(82); typeName(0);
			setState(87);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(83); match(T__4);
					setState(84); typeName(0);
					}
					} 
				}
				setState(89);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ModifiersContext extends ParserRuleContext {
		public TerminalNode PUBLIC() { return getToken(JavaPParser.PUBLIC, 0); }
		public TerminalNode TRANSIENT() { return getToken(JavaPParser.TRANSIENT, 0); }
		public TerminalNode FINAL() { return getToken(JavaPParser.FINAL, 0); }
		public TerminalNode NATIVE() { return getToken(JavaPParser.NATIVE, 0); }
		public TerminalNode PROTECTED() { return getToken(JavaPParser.PROTECTED, 0); }
		public TerminalNode PRIVATE() { return getToken(JavaPParser.PRIVATE, 0); }
		public TerminalNode ABSTRACT() { return getToken(JavaPParser.ABSTRACT, 0); }
		public TerminalNode STATIC() { return getToken(JavaPParser.STATIC, 0); }
		public TerminalNode VOLATILE() { return getToken(JavaPParser.VOLATILE, 0); }
		public TerminalNode SYNCHRONIZED() { return getToken(JavaPParser.SYNCHRONIZED, 0); }
		public TerminalNode STRICTFP() { return getToken(JavaPParser.STRICTFP, 0); }
		public ModifiersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_modifiers; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JavaPListener ) ((JavaPListener)listener).enterModifiers(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JavaPListener ) ((JavaPListener)listener).exitModifiers(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaPVisitor ) return ((JavaPVisitor<? extends T>)visitor).visitModifiers(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModifiersContext modifiers() throws RecognitionException {
		ModifiersContext _localctx = new ModifiersContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_modifiers);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(91);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC))) != 0)) {
				{
				setState(90);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				consume();
				}
			}

			setState(94);
			_la = _input.LA(1);
			if (_la==STATIC) {
				{
				setState(93); match(STATIC);
				}
			}

			setState(97);
			_la = _input.LA(1);
			if (_la==FINAL) {
				{
				setState(96); match(FINAL);
				}
			}

			setState(100);
			_la = _input.LA(1);
			if (_la==SYNCHRONIZED) {
				{
				setState(99); match(SYNCHRONIZED);
				}
			}

			setState(103);
			_la = _input.LA(1);
			if (_la==NATIVE) {
				{
				setState(102); match(NATIVE);
				}
			}

			setState(106);
			_la = _input.LA(1);
			if (_la==ABSTRACT) {
				{
				setState(105); match(ABSTRACT);
				}
			}

			setState(109);
			_la = _input.LA(1);
			if (_la==VOLATILE) {
				{
				setState(108); match(VOLATILE);
				}
			}

			setState(112);
			_la = _input.LA(1);
			if (_la==TRANSIENT) {
				{
				setState(111); match(TRANSIENT);
				}
			}

			setState(115);
			_la = _input.LA(1);
			if (_la==STRICTFP) {
				{
				setState(114); match(STRICTFP);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class GenericsContext extends ParserRuleContext {
		public List<GenericParameterContext> genericParameter() {
			return getRuleContexts(GenericParameterContext.class);
		}
		public GenericParameterContext genericParameter(int i) {
			return getRuleContext(GenericParameterContext.class,i);
		}
		public GenericsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_generics; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JavaPListener ) ((JavaPListener)listener).enterGenerics(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JavaPListener ) ((JavaPListener)listener).exitGenerics(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaPVisitor ) return ((JavaPVisitor<? extends T>)visitor).visitGenerics(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GenericsContext generics() throws RecognitionException {
		GenericsContext _localctx = new GenericsContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_generics);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(117); match(T__2);
			setState(118); genericParameter(0);
			setState(123);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(119); match(T__4);
				setState(120); genericParameter(0);
				}
				}
				setState(125);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(126); match(T__6);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class GenericParameterContext extends ParserRuleContext {
		public TypeNameContext typeName() {
			return getRuleContext(TypeNameContext.class,0);
		}
		public TerminalNode SUPER() { return getToken(JavaPParser.SUPER, 0); }
		public GenericParameterContext genericParameter() {
			return getRuleContext(GenericParameterContext.class,0);
		}
		public TypeListContext typeList() {
			return getRuleContext(TypeListContext.class,0);
		}
		public TerminalNode WILDCARD() { return getToken(JavaPParser.WILDCARD, 0); }
		public TerminalNode EXTENDS() { return getToken(JavaPParser.EXTENDS, 0); }
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public GenericParameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_genericParameter; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JavaPListener ) ((JavaPListener)listener).enterGenericParameter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JavaPListener ) ((JavaPListener)listener).exitGenericParameter(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaPVisitor ) return ((JavaPVisitor<? extends T>)visitor).visitGenericParameter(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GenericParameterContext genericParameter() throws RecognitionException {
		return genericParameter(0);
	}

	private GenericParameterContext genericParameter(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		GenericParameterContext _localctx = new GenericParameterContext(_ctx, _parentState);
		GenericParameterContext _prevctx = _localctx;
		int _startState = 18;
		enterRecursionRule(_localctx, 18, RULE_genericParameter, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(132);
			switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
			case 1:
				{
				setState(129); identifier();
				}
				break;
			case 2:
				{
				setState(130); typeName(0);
				}
				break;
			case 3:
				{
				setState(131); match(WILDCARD);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(142);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,22,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(140);
					switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
					case 1:
						{
						_localctx = new GenericParameterContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_genericParameter);
						setState(134);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(135); match(EXTENDS);
						setState(136); typeList();
						}
						break;
					case 2:
						{
						_localctx = new GenericParameterContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_genericParameter);
						setState(137);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(138); match(SUPER);
						setState(139); typeList();
						}
						break;
					}
					} 
				}
				setState(144);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,22,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class ExtensionContext extends ParserRuleContext {
		public TypeListContext typeList() {
			return getRuleContext(TypeListContext.class,0);
		}
		public TerminalNode EXTENDS() { return getToken(JavaPParser.EXTENDS, 0); }
		public ExtensionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_extension; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JavaPListener ) ((JavaPListener)listener).enterExtension(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JavaPListener ) ((JavaPListener)listener).exitExtension(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaPVisitor ) return ((JavaPVisitor<? extends T>)visitor).visitExtension(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExtensionContext extension() throws RecognitionException {
		ExtensionContext _localctx = new ExtensionContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_extension);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(145); match(EXTENDS);
			setState(146); typeList();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ImplementationContext extends ParserRuleContext {
		public TypeListContext typeList() {
			return getRuleContext(TypeListContext.class,0);
		}
		public TerminalNode IMPLEMENTS() { return getToken(JavaPParser.IMPLEMENTS, 0); }
		public ImplementationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_implementation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JavaPListener ) ((JavaPListener)listener).enterImplementation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JavaPListener ) ((JavaPListener)listener).exitImplementation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaPVisitor ) return ((JavaPVisitor<? extends T>)visitor).visitImplementation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ImplementationContext implementation() throws RecognitionException {
		ImplementationContext _localctx = new ImplementationContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_implementation);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(148); match(IMPLEMENTS);
			setState(149); typeList();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TypeNameContext extends ParserRuleContext {
		public GenericsContext generics() {
			return getRuleContext(GenericsContext.class,0);
		}
		public TerminalNode INT() { return getToken(JavaPParser.INT, 0); }
		public TerminalNode FLOAT() { return getToken(JavaPParser.FLOAT, 0); }
		public TerminalNode LONG() { return getToken(JavaPParser.LONG, 0); }
		public TerminalNode VOID() { return getToken(JavaPParser.VOID, 0); }
		public TerminalNode SEPARATOR() { return getToken(JavaPParser.SEPARATOR, 0); }
		public TerminalNode DOUBLE() { return getToken(JavaPParser.DOUBLE, 0); }
		public TerminalNode ELLIPSIS() { return getToken(JavaPParser.ELLIPSIS, 0); }
		public TerminalNode BYTE() { return getToken(JavaPParser.BYTE, 0); }
		public TerminalNode BOOLEAN() { return getToken(JavaPParser.BOOLEAN, 0); }
		public List<TypeNameContext> typeName() {
			return getRuleContexts(TypeNameContext.class);
		}
		public TypeNameContext typeName(int i) {
			return getRuleContext(TypeNameContext.class,i);
		}
		public TerminalNode ArrayBrackets() { return getToken(JavaPParser.ArrayBrackets, 0); }
		public TerminalNode SHORT() { return getToken(JavaPParser.SHORT, 0); }
		public TerminalNode CHAR() { return getToken(JavaPParser.CHAR, 0); }
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public TypeNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JavaPListener ) ((JavaPListener)listener).enterTypeName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JavaPListener ) ((JavaPListener)listener).exitTypeName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaPVisitor ) return ((JavaPVisitor<? extends T>)visitor).visitTypeName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeNameContext typeName() throws RecognitionException {
		return typeName(0);
	}

	private TypeNameContext typeName(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		TypeNameContext _localctx = new TypeNameContext(_ctx, _parentState);
		TypeNameContext _prevctx = _localctx;
		int _startState = 24;
		enterRecursionRule(_localctx, 24, RULE_typeName, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(162);
			switch (_input.LA(1)) {
			case PACKAGEINFO:
			case BaseIdentifier:
				{
				setState(152); identifier();
				}
				break;
			case VOID:
				{
				setState(153); match(VOID);
				}
				break;
			case BOOLEAN:
				{
				setState(154); match(BOOLEAN);
				}
				break;
			case BYTE:
				{
				setState(155); match(BYTE);
				}
				break;
			case CHAR:
				{
				setState(156); match(CHAR);
				}
				break;
			case SHORT:
				{
				setState(157); match(SHORT);
				}
				break;
			case INT:
				{
				setState(158); match(INT);
				}
				break;
			case LONG:
				{
				setState(159); match(LONG);
				}
				break;
			case FLOAT:
				{
				setState(160); match(FLOAT);
				}
				break;
			case DOUBLE:
				{
				setState(161); match(DOUBLE);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(175);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,25,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(173);
					switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
					case 1:
						{
						_localctx = new TypeNameContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_typeName);
						setState(164);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(165); match(SEPARATOR);
						setState(166); typeName(3);
						}
						break;
					case 2:
						{
						_localctx = new TypeNameContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_typeName);
						setState(167);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(168); generics();
						}
						break;
					case 3:
						{
						_localctx = new TypeNameContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_typeName);
						setState(169);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(170); match(ArrayBrackets);
						}
						break;
					case 4:
						{
						_localctx = new TypeNameContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_typeName);
						setState(171);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(172); match(ELLIPSIS);
						}
						break;
					}
					} 
				}
				setState(177);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,25,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class IdentifierContext extends ParserRuleContext {
		public TerminalNode PACKAGEINFO() { return getToken(JavaPParser.PACKAGEINFO, 0); }
		public TerminalNode BaseIdentifier() { return getToken(JavaPParser.BaseIdentifier, 0); }
		public IdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_identifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JavaPListener ) ((JavaPListener)listener).enterIdentifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JavaPListener ) ((JavaPListener)listener).exitIdentifier(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaPVisitor ) return ((JavaPVisitor<? extends T>)visitor).visitIdentifier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IdentifierContext identifier() throws RecognitionException {
		IdentifierContext _localctx = new IdentifierContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_identifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(178);
			_la = _input.LA(1);
			if ( !(_la==PACKAGEINFO || _la==BaseIdentifier) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 9: return genericParameter_sempred((GenericParameterContext)_localctx, predIndex);
		case 12: return typeName_sempred((TypeNameContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean typeName_sempred(TypeNameContext _localctx, int predIndex) {
		switch (predIndex) {
		case 2: return precpred(_ctx, 2);
		case 3: return precpred(_ctx, 4);
		case 4: return precpred(_ctx, 3);
		case 5: return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean genericParameter_sempred(GenericParameterContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0: return precpred(_ctx, 2);
		case 1: return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3+\u00b7\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\3\2\7\2 \n\2\f\2\16\2#\13\2\3"+
		"\3\3\3\3\3\3\3\5\3)\n\3\3\3\5\3,\n\3\3\3\3\3\7\3\60\n\3\f\3\16\3\63\13"+
		"\3\3\3\3\3\3\4\3\4\5\49\n\4\3\5\3\5\5\5=\n\5\3\5\5\5@\n\5\3\5\3\5\3\5"+
		"\5\5E\n\5\3\5\3\5\5\5I\n\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\b"+
		"\3\b\3\b\7\bX\n\b\f\b\16\b[\13\b\3\t\5\t^\n\t\3\t\5\ta\n\t\3\t\5\td\n"+
		"\t\3\t\5\tg\n\t\3\t\5\tj\n\t\3\t\5\tm\n\t\3\t\5\tp\n\t\3\t\5\ts\n\t\3"+
		"\t\5\tv\n\t\3\n\3\n\3\n\3\n\7\n|\n\n\f\n\16\n\177\13\n\3\n\3\n\3\13\3"+
		"\13\3\13\3\13\5\13\u0087\n\13\3\13\3\13\3\13\3\13\3\13\3\13\7\13\u008f"+
		"\n\13\f\13\16\13\u0092\13\13\3\f\3\f\3\f\3\r\3\r\3\r\3\16\3\16\3\16\3"+
		"\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\5\16\u00a5\n\16\3\16\3\16\3\16"+
		"\3\16\3\16\3\16\3\16\3\16\3\16\7\16\u00b0\n\16\f\16\16\16\u00b3\13\16"+
		"\3\17\3\17\3\17\2\4\24\32\20\2\4\6\b\n\f\16\20\22\24\26\30\32\34\2\5\4"+
		"\2\17\17\26\26\3\2\31\33\4\2\'\'**\u00cd\2!\3\2\2\2\4$\3\2\2\2\68\3\2"+
		"\2\2\b:\3\2\2\2\nL\3\2\2\2\fQ\3\2\2\2\16T\3\2\2\2\20]\3\2\2\2\22w\3\2"+
		"\2\2\24\u0086\3\2\2\2\26\u0093\3\2\2\2\30\u0096\3\2\2\2\32\u00a4\3\2\2"+
		"\2\34\u00b4\3\2\2\2\36 \5\4\3\2\37\36\3\2\2\2 #\3\2\2\2!\37\3\2\2\2!\""+
		"\3\2\2\2\"\3\3\2\2\2#!\3\2\2\2$%\5\20\t\2%&\t\2\2\2&(\5\32\16\2\')\5\26"+
		"\f\2(\'\3\2\2\2()\3\2\2\2)+\3\2\2\2*,\5\30\r\2+*\3\2\2\2+,\3\2\2\2,-\3"+
		"\2\2\2-\61\7\3\2\2.\60\5\6\4\2/.\3\2\2\2\60\63\3\2\2\2\61/\3\2\2\2\61"+
		"\62\3\2\2\2\62\64\3\2\2\2\63\61\3\2\2\2\64\65\7\t\2\2\65\5\3\2\2\2\66"+
		"9\5\b\5\2\679\5\n\6\28\66\3\2\2\28\67\3\2\2\29\7\3\2\2\2:<\5\20\t\2;="+
		"\5\22\n\2<;\3\2\2\2<=\3\2\2\2=?\3\2\2\2>@\5\32\16\2?>\3\2\2\2?@\3\2\2"+
		"\2@A\3\2\2\2AB\5\32\16\2BD\7\7\2\2CE\5\16\b\2DC\3\2\2\2DE\3\2\2\2EF\3"+
		"\2\2\2FH\7\5\2\2GI\5\f\7\2HG\3\2\2\2HI\3\2\2\2IJ\3\2\2\2JK\7\n\2\2K\t"+
		"\3\2\2\2LM\5\20\t\2MN\5\32\16\2NO\5\34\17\2OP\7\n\2\2P\13\3\2\2\2QR\7"+
		"!\2\2RS\5\16\b\2S\r\3\2\2\2TY\5\32\16\2UV\7\6\2\2VX\5\32\16\2WU\3\2\2"+
		"\2X[\3\2\2\2YW\3\2\2\2YZ\3\2\2\2Z\17\3\2\2\2[Y\3\2\2\2\\^\t\3\2\2]\\\3"+
		"\2\2\2]^\3\2\2\2^`\3\2\2\2_a\7\35\2\2`_\3\2\2\2`a\3\2\2\2ac\3\2\2\2bd"+
		"\7\22\2\2cb\3\2\2\2cd\3\2\2\2df\3\2\2\2eg\7 \2\2fe\3\2\2\2fg\3\2\2\2g"+
		"i\3\2\2\2hj\7\30\2\2ih\3\2\2\2ij\3\2\2\2jl\3\2\2\2km\7\13\2\2lk\3\2\2"+
		"\2lm\3\2\2\2mo\3\2\2\2np\7$\2\2on\3\2\2\2op\3\2\2\2pr\3\2\2\2qs\7\"\2"+
		"\2rq\3\2\2\2rs\3\2\2\2su\3\2\2\2tv\7\36\2\2ut\3\2\2\2uv\3\2\2\2v\21\3"+
		"\2\2\2wx\7\b\2\2x}\5\24\13\2yz\7\6\2\2z|\5\24\13\2{y\3\2\2\2|\177\3\2"+
		"\2\2}{\3\2\2\2}~\3\2\2\2~\u0080\3\2\2\2\177}\3\2\2\2\u0080\u0081\7\4\2"+
		"\2\u0081\23\3\2\2\2\u0082\u0083\b\13\1\2\u0083\u0087\5\34\17\2\u0084\u0087"+
		"\5\32\16\2\u0085\u0087\7%\2\2\u0086\u0082\3\2\2\2\u0086\u0084\3\2\2\2"+
		"\u0086\u0085\3\2\2\2\u0087\u0090\3\2\2\2\u0088\u0089\f\4\2\2\u0089\u008a"+
		"\7\21\2\2\u008a\u008f\5\16\b\2\u008b\u008c\f\3\2\2\u008c\u008d\7\37\2"+
		"\2\u008d\u008f\5\16\b\2\u008e\u0088\3\2\2\2\u008e\u008b\3\2\2\2\u008f"+
		"\u0092\3\2\2\2\u0090\u008e\3\2\2\2\u0090\u0091\3\2\2\2\u0091\25\3\2\2"+
		"\2\u0092\u0090\3\2\2\2\u0093\u0094\7\21\2\2\u0094\u0095\5\16\b\2\u0095"+
		"\27\3\2\2\2\u0096\u0097\7\24\2\2\u0097\u0098\5\16\b\2\u0098\31\3\2\2\2"+
		"\u0099\u009a\b\16\1\2\u009a\u00a5\5\34\17\2\u009b\u00a5\7#\2\2\u009c\u00a5"+
		"\7\f\2\2\u009d\u00a5\7\r\2\2\u009e\u00a5\7\16\2\2\u009f\u00a5\7\34\2\2"+
		"\u00a0\u00a5\7\25\2\2\u00a1\u00a5\7\27\2\2\u00a2\u00a5\7\23\2\2\u00a3"+
		"\u00a5\7\20\2\2\u00a4\u0099\3\2\2\2\u00a4\u009b\3\2\2\2\u00a4\u009c\3"+
		"\2\2\2\u00a4\u009d\3\2\2\2\u00a4\u009e\3\2\2\2\u00a4\u009f\3\2\2\2\u00a4"+
		"\u00a0\3\2\2\2\u00a4\u00a1\3\2\2\2\u00a4\u00a2\3\2\2\2\u00a4\u00a3\3\2"+
		"\2\2\u00a5\u00b1\3\2\2\2\u00a6\u00a7\f\4\2\2\u00a7\u00a8\7(\2\2\u00a8"+
		"\u00b0\5\32\16\5\u00a9\u00aa\f\6\2\2\u00aa\u00b0\5\22\n\2\u00ab\u00ac"+
		"\f\5\2\2\u00ac\u00b0\7)\2\2\u00ad\u00ae\f\3\2\2\u00ae\u00b0\7&\2\2\u00af"+
		"\u00a6\3\2\2\2\u00af\u00a9\3\2\2\2\u00af\u00ab\3\2\2\2\u00af\u00ad\3\2"+
		"\2\2\u00b0\u00b3\3\2\2\2\u00b1\u00af\3\2\2\2\u00b1\u00b2\3\2\2\2\u00b2"+
		"\33\3\2\2\2\u00b3\u00b1\3\2\2\2\u00b4\u00b5\t\4\2\2\u00b5\35\3\2\2\2\34"+
		"!(+\618<?DHY]`cfiloru}\u0086\u008e\u0090\u00a4\u00af\u00b1";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}