// Generated from /Users/alexreinking/Development/CodeRepair/RepairEngine/JavaP.g4 by ANTLR 4.4.1-dev
package coderepair.antlr;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.RuntimeMetaData;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class JavaPLexer extends Lexer {
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
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"'\\u0000'", "'\\u0001'", "'\\u0002'", "'\\u0003'", "'\\u0004'", "'\\u0005'", 
		"'\\u0006'", "'\\u0007'", "'\b'", "'\t'", "'\n'", "'\\u000B'", "'\f'", 
		"'\r'", "'\\u000E'", "'\\u000F'", "'\\u0010'", "'\\u0011'", "'\\u0012'", 
		"'\\u0013'", "'\\u0014'", "'\\u0015'", "'\\u0016'", "'\\u0017'", "'\\u0018'", 
		"'\\u0019'", "'\\u001A'", "'\\u001B'", "'\\u001C'", "'\\u001D'", "'\\u001E'", 
		"'\\u001F'", "' '", "'!'", "'\"'", "'#'", "'$'", "'%'", "'&'", "'''", 
		"'('", "')'"
	};
	public static final String[] ruleNames = {
		"T__7", "T__6", "T__5", "T__4", "T__3", "T__2", "T__1", "T__0", "ABSTRACT", 
		"BOOLEAN", "BYTE", "CHAR", "CLASS", "DOUBLE", "EXTENDS", "FINAL", "FLOAT", 
		"IMPLEMENTS", "INT", "INTERFACE", "LONG", "NATIVE", "PRIVATE", "PROTECTED", 
		"PUBLIC", "SHORT", "STATIC", "STRICTFP", "SUPER", "SYNCHRONIZED", "THROWS", 
		"TRANSIENT", "VOID", "VOLATILE", "WILDCARD", "ELLIPSIS", "PACKAGEINFO", 
		"SEPARATOR", "ArrayBrackets", "BaseIdentifier", "JavaLetter", "JavaLetterOrDigit", 
		"WS"
	};


	public JavaPLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "JavaP.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 40: return JavaLetter_sempred((RuleContext)_localctx, predIndex);
		case 41: return JavaLetterOrDigit_sempred((RuleContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean JavaLetterOrDigit_sempred(RuleContext _localctx, int predIndex) {
		switch (predIndex) {
		case 2: return Character.isJavaIdentifierPart(_input.LA(-1));
		case 3: return Character.isJavaIdentifierPart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)));
		}
		return true;
	}
	private boolean JavaLetter_sempred(RuleContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0: return Character.isJavaIdentifierStart(_input.LA(-1));
		case 1: return Character.isJavaIdentifierStart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)));
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2+\u0161\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3"+
		"\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\13\3\13"+
		"\3\13\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16"+
		"\3\16\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\20"+
		"\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\22"+
		"\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3\24"+
		"\3\24\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\26\3\26\3\26"+
		"\3\26\3\26\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\30\3\30\3\30\3\30\3\30"+
		"\3\30\3\30\3\30\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\32"+
		"\3\32\3\32\3\32\3\32\3\32\3\32\3\33\3\33\3\33\3\33\3\33\3\33\3\34\3\34"+
		"\3\34\3\34\3\34\3\34\3\34\3\35\3\35\3\35\3\35\3\35\3\35\3\35\3\35\3\35"+
		"\3\36\3\36\3\36\3\36\3\36\3\36\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37"+
		"\3\37\3\37\3\37\3\37\3\37\3 \3 \3 \3 \3 \3 \3 \3!\3!\3!\3!\3!\3!\3!\3"+
		"!\3!\3!\3\"\3\"\3\"\3\"\3\"\3#\3#\3#\3#\3#\3#\3#\3#\3#\3$\3$\3%\3%\3%"+
		"\3%\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3&\3\'\3\'\3(\3(\3(\3)\3)\7)\u0146"+
		"\n)\f)\16)\u0149\13)\3*\3*\3*\3*\3*\3*\5*\u0151\n*\3+\3+\3+\3+\3+\3+\5"+
		"+\u0159\n+\3,\6,\u015c\n,\r,\16,\u015d\3,\3,\2\2-\3\3\5\4\7\5\t\6\13\7"+
		"\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25"+
		")\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O"+
		")Q*S\2U\2W+\3\2\b\6\2&&C\\aac|\4\2\2\u0101\ud802\udc01\3\2\ud802\udc01"+
		"\3\2\udc02\ue001\7\2&&\62;C\\aac|\5\2\13\f\16\17\"\"\u0164\2\3\3\2\2\2"+
		"\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2"+
		"\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2"+
		"\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2"+
		"\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2"+
		"\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2"+
		"\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2"+
		"\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2W\3\2\2\2\3Y\3\2\2\2\5["+
		"\3\2\2\2\7]\3\2\2\2\t_\3\2\2\2\13a\3\2\2\2\rc\3\2\2\2\17e\3\2\2\2\21g"+
		"\3\2\2\2\23i\3\2\2\2\25r\3\2\2\2\27z\3\2\2\2\31\177\3\2\2\2\33\u0084\3"+
		"\2\2\2\35\u008a\3\2\2\2\37\u0091\3\2\2\2!\u0099\3\2\2\2#\u009f\3\2\2\2"+
		"%\u00a5\3\2\2\2\'\u00b0\3\2\2\2)\u00b4\3\2\2\2+\u00be\3\2\2\2-\u00c3\3"+
		"\2\2\2/\u00ca\3\2\2\2\61\u00d2\3\2\2\2\63\u00dc\3\2\2\2\65\u00e3\3\2\2"+
		"\2\67\u00e9\3\2\2\29\u00f0\3\2\2\2;\u00f9\3\2\2\2=\u00ff\3\2\2\2?\u010c"+
		"\3\2\2\2A\u0113\3\2\2\2C\u011d\3\2\2\2E\u0122\3\2\2\2G\u012b\3\2\2\2I"+
		"\u012d\3\2\2\2K\u0131\3\2\2\2M\u013e\3\2\2\2O\u0140\3\2\2\2Q\u0143\3\2"+
		"\2\2S\u0150\3\2\2\2U\u0158\3\2\2\2W\u015b\3\2\2\2YZ\7*\2\2Z\4\3\2\2\2"+
		"[\\\7+\2\2\\\6\3\2\2\2]^\7=\2\2^\b\3\2\2\2_`\7}\2\2`\n\3\2\2\2ab\7>\2"+
		"\2b\f\3\2\2\2cd\7.\2\2d\16\3\2\2\2ef\7\177\2\2f\20\3\2\2\2gh\7@\2\2h\22"+
		"\3\2\2\2ij\7c\2\2jk\7d\2\2kl\7u\2\2lm\7v\2\2mn\7t\2\2no\7c\2\2op\7e\2"+
		"\2pq\7v\2\2q\24\3\2\2\2rs\7d\2\2st\7q\2\2tu\7q\2\2uv\7n\2\2vw\7g\2\2w"+
		"x\7c\2\2xy\7p\2\2y\26\3\2\2\2z{\7d\2\2{|\7{\2\2|}\7v\2\2}~\7g\2\2~\30"+
		"\3\2\2\2\177\u0080\7e\2\2\u0080\u0081\7j\2\2\u0081\u0082\7c\2\2\u0082"+
		"\u0083\7t\2\2\u0083\32\3\2\2\2\u0084\u0085\7e\2\2\u0085\u0086\7n\2\2\u0086"+
		"\u0087\7c\2\2\u0087\u0088\7u\2\2\u0088\u0089\7u\2\2\u0089\34\3\2\2\2\u008a"+
		"\u008b\7f\2\2\u008b\u008c\7q\2\2\u008c\u008d\7w\2\2\u008d\u008e\7d\2\2"+
		"\u008e\u008f\7n\2\2\u008f\u0090\7g\2\2\u0090\36\3\2\2\2\u0091\u0092\7"+
		"g\2\2\u0092\u0093\7z\2\2\u0093\u0094\7v\2\2\u0094\u0095\7g\2\2\u0095\u0096"+
		"\7p\2\2\u0096\u0097\7f\2\2\u0097\u0098\7u\2\2\u0098 \3\2\2\2\u0099\u009a"+
		"\7h\2\2\u009a\u009b\7k\2\2\u009b\u009c\7p\2\2\u009c\u009d\7c\2\2\u009d"+
		"\u009e\7n\2\2\u009e\"\3\2\2\2\u009f\u00a0\7h\2\2\u00a0\u00a1\7n\2\2\u00a1"+
		"\u00a2\7q\2\2\u00a2\u00a3\7c\2\2\u00a3\u00a4\7v\2\2\u00a4$\3\2\2\2\u00a5"+
		"\u00a6\7k\2\2\u00a6\u00a7\7o\2\2\u00a7\u00a8\7r\2\2\u00a8\u00a9\7n\2\2"+
		"\u00a9\u00aa\7g\2\2\u00aa\u00ab\7o\2\2\u00ab\u00ac\7g\2\2\u00ac\u00ad"+
		"\7p\2\2\u00ad\u00ae\7v\2\2\u00ae\u00af\7u\2\2\u00af&\3\2\2\2\u00b0\u00b1"+
		"\7k\2\2\u00b1\u00b2\7p\2\2\u00b2\u00b3\7v\2\2\u00b3(\3\2\2\2\u00b4\u00b5"+
		"\7k\2\2\u00b5\u00b6\7p\2\2\u00b6\u00b7\7v\2\2\u00b7\u00b8\7g\2\2\u00b8"+
		"\u00b9\7t\2\2\u00b9\u00ba\7h\2\2\u00ba\u00bb\7c\2\2\u00bb\u00bc\7e\2\2"+
		"\u00bc\u00bd\7g\2\2\u00bd*\3\2\2\2\u00be\u00bf\7n\2\2\u00bf\u00c0\7q\2"+
		"\2\u00c0\u00c1\7p\2\2\u00c1\u00c2\7i\2\2\u00c2,\3\2\2\2\u00c3\u00c4\7"+
		"p\2\2\u00c4\u00c5\7c\2\2\u00c5\u00c6\7v\2\2\u00c6\u00c7\7k\2\2\u00c7\u00c8"+
		"\7x\2\2\u00c8\u00c9\7g\2\2\u00c9.\3\2\2\2\u00ca\u00cb\7r\2\2\u00cb\u00cc"+
		"\7t\2\2\u00cc\u00cd\7k\2\2\u00cd\u00ce\7x\2\2\u00ce\u00cf\7c\2\2\u00cf"+
		"\u00d0\7v\2\2\u00d0\u00d1\7g\2\2\u00d1\60\3\2\2\2\u00d2\u00d3\7r\2\2\u00d3"+
		"\u00d4\7t\2\2\u00d4\u00d5\7q\2\2\u00d5\u00d6\7v\2\2\u00d6\u00d7\7g\2\2"+
		"\u00d7\u00d8\7e\2\2\u00d8\u00d9\7v\2\2\u00d9\u00da\7g\2\2\u00da\u00db"+
		"\7f\2\2\u00db\62\3\2\2\2\u00dc\u00dd\7r\2\2\u00dd\u00de\7w\2\2\u00de\u00df"+
		"\7d\2\2\u00df\u00e0\7n\2\2\u00e0\u00e1\7k\2\2\u00e1\u00e2\7e\2\2\u00e2"+
		"\64\3\2\2\2\u00e3\u00e4\7u\2\2\u00e4\u00e5\7j\2\2\u00e5\u00e6\7q\2\2\u00e6"+
		"\u00e7\7t\2\2\u00e7\u00e8\7v\2\2\u00e8\66\3\2\2\2\u00e9\u00ea\7u\2\2\u00ea"+
		"\u00eb\7v\2\2\u00eb\u00ec\7c\2\2\u00ec\u00ed\7v\2\2\u00ed\u00ee\7k\2\2"+
		"\u00ee\u00ef\7e\2\2\u00ef8\3\2\2\2\u00f0\u00f1\7u\2\2\u00f1\u00f2\7v\2"+
		"\2\u00f2\u00f3\7t\2\2\u00f3\u00f4\7k\2\2\u00f4\u00f5\7e\2\2\u00f5\u00f6"+
		"\7v\2\2\u00f6\u00f7\7h\2\2\u00f7\u00f8\7r\2\2\u00f8:\3\2\2\2\u00f9\u00fa"+
		"\7u\2\2\u00fa\u00fb\7w\2\2\u00fb\u00fc\7r\2\2\u00fc\u00fd\7g\2\2\u00fd"+
		"\u00fe\7t\2\2\u00fe<\3\2\2\2\u00ff\u0100\7u\2\2\u0100\u0101\7{\2\2\u0101"+
		"\u0102\7p\2\2\u0102\u0103\7e\2\2\u0103\u0104\7j\2\2\u0104\u0105\7t\2\2"+
		"\u0105\u0106\7q\2\2\u0106\u0107\7p\2\2\u0107\u0108\7k\2\2\u0108\u0109"+
		"\7|\2\2\u0109\u010a\7g\2\2\u010a\u010b\7f\2\2\u010b>\3\2\2\2\u010c\u010d"+
		"\7v\2\2\u010d\u010e\7j\2\2\u010e\u010f\7t\2\2\u010f\u0110\7q\2\2\u0110"+
		"\u0111\7y\2\2\u0111\u0112\7u\2\2\u0112@\3\2\2\2\u0113\u0114\7v\2\2\u0114"+
		"\u0115\7t\2\2\u0115\u0116\7c\2\2\u0116\u0117\7p\2\2\u0117\u0118\7u\2\2"+
		"\u0118\u0119\7k\2\2\u0119\u011a\7g\2\2\u011a\u011b\7p\2\2\u011b\u011c"+
		"\7v\2\2\u011cB\3\2\2\2\u011d\u011e\7x\2\2\u011e\u011f\7q\2\2\u011f\u0120"+
		"\7k\2\2\u0120\u0121\7f\2\2\u0121D\3\2\2\2\u0122\u0123\7x\2\2\u0123\u0124"+
		"\7q\2\2\u0124\u0125\7n\2\2\u0125\u0126\7c\2\2\u0126\u0127\7v\2\2\u0127"+
		"\u0128\7k\2\2\u0128\u0129\7n\2\2\u0129\u012a\7g\2\2\u012aF\3\2\2\2\u012b"+
		"\u012c\7A\2\2\u012cH\3\2\2\2\u012d\u012e\7\60\2\2\u012e\u012f\7\60\2\2"+
		"\u012f\u0130\7\60\2\2\u0130J\3\2\2\2\u0131\u0132\7r\2\2\u0132\u0133\7"+
		"c\2\2\u0133\u0134\7e\2\2\u0134\u0135\7m\2\2\u0135\u0136\7c\2\2\u0136\u0137"+
		"\7i\2\2\u0137\u0138\7g\2\2\u0138\u0139\7/\2\2\u0139\u013a\7k\2\2\u013a"+
		"\u013b\7p\2\2\u013b\u013c\7h\2\2\u013c\u013d\7q\2\2\u013dL\3\2\2\2\u013e"+
		"\u013f\4\60\61\2\u013fN\3\2\2\2\u0140\u0141\7]\2\2\u0141\u0142\7_\2\2"+
		"\u0142P\3\2\2\2\u0143\u0147\5S*\2\u0144\u0146\5U+\2\u0145\u0144\3\2\2"+
		"\2\u0146\u0149\3\2\2\2\u0147\u0145\3\2\2\2\u0147\u0148\3\2\2\2\u0148R"+
		"\3\2\2\2\u0149\u0147\3\2\2\2\u014a\u0151\t\2\2\2\u014b\u014c\n\3\2\2\u014c"+
		"\u0151\6*\2\2\u014d\u014e\t\4\2\2\u014e\u014f\t\5\2\2\u014f\u0151\6*\3"+
		"\2\u0150\u014a\3\2\2\2\u0150\u014b\3\2\2\2\u0150\u014d\3\2\2\2\u0151T"+
		"\3\2\2\2\u0152\u0159\t\6\2\2\u0153\u0154\n\3\2\2\u0154\u0159\6+\4\2\u0155"+
		"\u0156\t\4\2\2\u0156\u0157\t\5\2\2\u0157\u0159\6+\5\2\u0158\u0152\3\2"+
		"\2\2\u0158\u0153\3\2\2\2\u0158\u0155\3\2\2\2\u0159V\3\2\2\2\u015a\u015c"+
		"\t\7\2\2\u015b\u015a\3\2\2\2\u015c\u015d\3\2\2\2\u015d\u015b\3\2\2\2\u015d"+
		"\u015e\3\2\2\2\u015e\u015f\3\2\2\2\u015f\u0160\b,\2\2\u0160X\3\2\2\2\7"+
		"\2\u0147\u0150\u0158\u015d\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}