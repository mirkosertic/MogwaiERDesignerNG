package de.erdesignerng.visual.components;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.util.Hashtable;

/**
 * Scanner used to find out tokens.
 *
 * @author Tom Matouek
 * @version 1.0
 */
class SQLScanner {
	/**
	 * Represents a token.
	 */
	public static class Token {
		/**
		 * the type of token
		 */
		public int id = TOKEN_NONE;

		/**
		 * the index of first character of token
		 */
		public int start = -1;

		/**
		 * the index of last character of token
		 */
		public int end = -1;

		public Token() {
		}

		public Token(int id, int start, int end) {
			this.id = id;
			this.start = start;
			this.end = end;
		}

		/**
		 * May token contain eoln?
		 */
		public boolean isMultiline() {
			return id == TOKEN_COMMENT_ML || id == TOKEN_STRING
					|| id == TOKEN_QUOTED_ID;
		}
	}

	// Tokens recognized by scanner.
	public static final int TOKEN_STRING = 0;
	public static final int TOKEN_WORD = 1;
	public static final int TOKEN_QUOTED_ID = 2;
	public static final int TOKEN_COMMENT_SL = 3;
	public static final int TOKEN_COMMENT_ML = 4;
	public static final int TOKEN_KEYWORD = 5;
	public static final int TOKEN_BUILTIN_FUNCTION = 6;
	public static final int TOKEN_WHITESPACE = 7;

	/**
	 * No token.
	 */
	public static final int TOKEN_NONE = 8;

	/**
	 * Token count.
	 */
	public static final int TOKEN_COUNT = 8;

	/**
	 * Active states (states where tokens starts). Technical trick: have the
	 * same values as TOKEN_* constants if defined.
	 */
	private static final int STATE_STRING = TOKEN_STRING;
	private static final int STATE_WORD = TOKEN_WORD;
	private static final int STATE_QUOTED_ID = TOKEN_QUOTED_ID;
	private static final int STATE_COMMENT_SL = TOKEN_COMMENT_SL;
	private static final int STATE_COMMENT_ML = TOKEN_COMMENT_ML;
	private static final int STATE_WHITESPACE = TOKEN_WHITESPACE;

	/**
	 * Auxiliary states.
	 */
	private static final int STATE_INIT = TOKEN_NONE;
	private static final int STATE_COMMENT_ML_START_HINT = TOKEN_NONE + 1;
	private static final int STATE_COMMENT_ML_END_HINT = TOKEN_NONE + 2;
	private static final int STATE_COMMENT_SL_START_HINT = TOKEN_NONE + 3;

	/**
	 * Specifies wheather there exists a token starting in this state.
	 *
	 * @param state The state.
	 */
	private boolean isStateActive(int state) {
		return state >= 0 && state <= 5;
	}

	/**
	 * Loads keywords from the given array.
	 *
	 * @param keywords The array of keywords.
	 */
	public void setKeywords(String[] keywords) {
		keywordHashtable = new Hashtable(keywords.length);
        for (String keyword : keywords) keywordHashtable.put(keyword, True);
	}

	/**
	 * Loads built-in functions from the given array.
	 *
	 * @param functions The array of functions.
	 */
	public void setBuiltinFunctions(String[] functions) {
		builtinFunctionsHashtable = new Hashtable(functions.length);
        for (String function : functions) builtinFunctionsHashtable.put(function, True);
	}

	/**
	 * Returns current scanner's position.
	 */
	public int getCurrentPos() {
		return this.pos;
	}

	/**
	 * Returns true if the scanner is exactly one character behind the end of
	 * the text.
	 */
	public boolean eot() {
		return this.pos == this.textLength;
	}

	/**
	 * Returns true if the scanner is exactly on the end of the line.
	 */
	public boolean eoln() {
		return str.charAt(pos) == '\n';
	}

	/**
	 * Sets the interval to be scanned (from <code>start</code> to
	 * <code>end</code> including).
	 */
	public void setInterval(int start, int end) {
		this.pos = start;
		this.endPos = end;
	}

	/**
	 * Loads a content of the document <code>value</code>.
	 */
	public void setDocument(Document value) {
		textLength = value.getLength();
		try {
			str = value.getText(0, textLength);
		} catch (BadLocationException e) {
		}
	}

	/**
	 * Sets scanner's state.
	 *
	 * @param tokenId	The token currently being scanned.
	 * @param tokenStart Offset of the first char of that token.
	 */
	public void setState(int tokenId, int tokenStart) {
		this.state = tokenId;
		this.token.id = isStateActive(tokenId) ? tokenId : TOKEN_NONE;
		this.token.start = tokenStart;
		this.token.end = -1;
		this.initState = true;
	}

	/**
	 * Returns current token.
	 */
	public Token getToken() {
		return this.token;
	}

	// auxiliary constant:
	private final Boolean True = true;

	// the scanned string:
	private String str;

	// the length of <code>str</code> (the length of whole text in editor)
	private int textLength;

	// current position (one character after last character read):
	private int pos;

	// the last index to scan if it isn't necessary to augment scanning:
	private int endPos;

	// current state:
	private int state;

	// was initial state specified (by calling <code>setState</code> method)?
	private boolean initState = false;

	// the character at the <code>pos</code> position (next ):
	private char lookahead;

	// the list of keywords:
	private Hashtable keywordHashtable;

	// the list of built-in functions:
	private Hashtable builtinFunctionsHashtable;

	// current token being scanned:
	private final Token token = new Token();

	private boolean isKeyword(String word) {
		return keywordHashtable.containsKey(word.toUpperCase());
	}

	private boolean isBuiltinFunction(String word) {
		return builtinFunctionsHashtable.containsKey(word.toUpperCase());
	}

	private void next() {
		pos++;
		lookahead = (pos == textLength) ? '\0' : str.charAt(pos);
	}

	/**
	 * Scans the string until some token is found. Whitespace tokens are not
	 * multi-line so they ends before the line breaks. Line break can be found
	 * only in multi-line token or one character after a single-line token. If
	 * multi-line token overlaps the interval being scanned, scanning will
	 * continue until the line break after its end is reached.
	 */
	public boolean nextToken() {
		if (pos >= textLength)
			return false;

		lookahead = str.charAt(pos);

		if (pos >= endPos && (lookahead == '\r' || lookahead == '\n'))
			return false;

		if (!initState) {
			token.id = TOKEN_NONE;
			token.start = -1;
			token.end = -1;
			state = STATE_INIT;
		} else
			this.initState = false;

		for (; ;) {
			if (pos == endPos && !token.isMultiline())
				lookahead = '\0';

			switch (state) {
				case STATE_INIT:
					switch (lookahead) {
						case '\'':
							state = STATE_STRING;
							token.start = pos;
							token.id = TOKEN_STRING;
							next();
							break;
						case '"':
							state = STATE_QUOTED_ID;
							token.start = pos;
							token.id = TOKEN_QUOTED_ID;
							next();
							break;
						case '/':
							state = STATE_COMMENT_ML_START_HINT;
							next();
							break;
						case '-':
							state = STATE_COMMENT_SL_START_HINT;
							next();
							break;
						case '\0':
							return false;
						default:
							if (lookahead >= 'A' && lookahead <= 'Z'
									|| lookahead >= 'a' && lookahead <= 'z') {
								state = STATE_WORD;
								token.start = pos;
								token.id = TOKEN_WORD;
							} else {
								token.start = pos;
								token.id = TOKEN_WHITESPACE;
								state = STATE_WHITESPACE;
							}
							next();
					}
					break;

				case STATE_WHITESPACE:
					if (lookahead == '\'' || lookahead == '"' || lookahead == '/'
							|| lookahead == '-' || lookahead == '\n'
							|| lookahead == '\0' || lookahead >= 'A'
							&& lookahead <= 'Z' || lookahead >= 'a'
							&& lookahead <= 'z') {
						token.end = pos - 1;
						state = STATE_INIT;
						return true;
					}
					next();
					break;

				case STATE_WORD:
					if (lookahead >= 'A' && lookahead <= 'Z' || lookahead >= 'a'
							&& lookahead <= 'z' || lookahead >= '0'
							&& lookahead <= '9' || lookahead == '_'
							|| lookahead == '$' || lookahead == '#')
						next();
					else {
						String word = str.substring(token.start, pos); // substring
						// from
						// start to
						// pos-1
						if (isKeyword(word)) {
							state = STATE_INIT;
							token.id = TOKEN_KEYWORD;
							token.end = pos - 1;
							return true;
						} else if (isBuiltinFunction(word)) {
							state = STATE_INIT;
							token.id = TOKEN_BUILTIN_FUNCTION;
							token.end = pos - 1;
							return true;
						} else {
							token.id = TOKEN_WHITESPACE;
							state = STATE_WHITESPACE;
						}
					}
					break;

				case STATE_STRING:
					switch (lookahead) {
						case '\'':
							state = STATE_INIT;
							token.end = pos;
							next();
							return true;
						case '\0':
							state = STATE_INIT;
							token.end = pos - 1;
							return true;
						default:
							next();
					}
					break;

				case STATE_QUOTED_ID:
					switch (lookahead) {
						case '"':
							state = STATE_INIT;
							token.end = pos;
							next();
							return true;
						case '\0':
							state = STATE_INIT;
							token.end = pos - 1;
							return true;
						default:
							next();
					}
					break;

				case STATE_COMMENT_ML_START_HINT:
					switch (lookahead) {
						case '*':
							state = STATE_COMMENT_ML;
							token.start = pos - 1;
							token.id = TOKEN_COMMENT_ML;
							next();
							break;
						default:
							state = STATE_WHITESPACE;
							token.start = pos - 1;
							token.id = TOKEN_WHITESPACE;
					}
					break;

				case STATE_COMMENT_ML:
					switch (lookahead) {
						case '*':
							state = STATE_COMMENT_ML_END_HINT;
							next();
							break;
						case '\0':
							state = STATE_INIT;
							token.end = pos - 1;
							return true;
						default:
							next();
					}
					break;

				case STATE_COMMENT_ML_END_HINT:
					switch (lookahead) {
						case '/':
							state = STATE_INIT;
							token.end = pos;
							next();
							return true;
						case '\0':
							state = STATE_INIT;
							token.end = pos - 1;
							return true;
						default:
							state = STATE_COMMENT_ML;
					}
					break;

				case STATE_COMMENT_SL_START_HINT:
					switch (lookahead) {
						case '-':
							state = STATE_COMMENT_SL;
							token.start = pos - 1;
							token.id = TOKEN_COMMENT_SL;
							next();
							break;
						default:
							state = STATE_WHITESPACE;
							token.start = pos - 1;
							token.id = TOKEN_WHITESPACE;
					}
					break;

				case STATE_COMMENT_SL:
					switch (lookahead) {
						case '\n':
							state = STATE_INIT;
							token.end = pos - 1;
							next();
							return true;
						case '\0':
							state = STATE_INIT;
							token.end = pos - 1;
							return true;
						default:
							next();
					}
					break;
			}
		}
	}

}