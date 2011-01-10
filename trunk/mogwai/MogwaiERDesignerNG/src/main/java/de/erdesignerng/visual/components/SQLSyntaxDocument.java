/**
 * Mogwai ERDesigner. Copyright (C) 2002 The Mogwai Project.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package de.erdesignerng.visual.components;

import java.awt.Color;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 * Document highlighting Oracle PL/SQL syntax.
 * 
 * @author Tomáš Matoušek
 * @version 1.0
 */
class SQLSyntaxDocument extends DefaultStyledDocument {

	/**
	 * Document root.
	 */
	private Element root;

	/**
	 * Tokens highlighting attributes.
	 */
	private SimpleAttributeSet[] sas = new SimpleAttributeSet[SQLScanner.TOKEN_COUNT];

	/**
	 * Scanner used to find out tokens.
	 */
	private SQLScanner scanner = new SQLScanner();

	/**
	 * For each and every eoln in the text this array contains the token being
	 * scanned when the scanner lookaheads that eoln.
	 */
	private ExpandingArray lineToks = new ExpandingArray(1);

	/**
	 * Constructs document with SQL syntax highlighting.
	 */
	public SQLSyntaxDocument() {
		root = this.getDefaultRootElement();

		scanner.setKeywords(OracleKeywords);
		scanner.setBuiltinFunctions(OracleFunctions);

		StyleConstants.setForeground(
				sas[SQLScanner.TOKEN_STRING] = new SimpleAttributeSet(),
				Color.lightGray);
		StyleConstants.setForeground(
				sas[SQLScanner.TOKEN_COMMENT_ML] = new SimpleAttributeSet(),
				Color.red);
		StyleConstants.setForeground(
				sas[SQLScanner.TOKEN_COMMENT_SL] = new SimpleAttributeSet(),
				Color.red);
		StyleConstants.setForeground(
				sas[SQLScanner.TOKEN_KEYWORD] = new SimpleAttributeSet(),
				Color.blue);
		StyleConstants.setForeground(
				sas[SQLScanner.TOKEN_QUOTED_ID] = new SimpleAttributeSet(),
				Color.gray);
		StyleConstants
				.setForeground(
						sas[SQLScanner.TOKEN_BUILTIN_FUNCTION] = new SimpleAttributeSet(),
						new Color(100, 0, 200));
		sas[SQLScanner.TOKEN_WHITESPACE] = new SimpleAttributeSet();
	}

	public final String[] OracleKeywords = { "SELECT", "FROM", "WHERE",
			"UPDATE", "SET", "INSERT", "INTO", "CREATE", "REPLACE", "VALUES",
			"DROP", "CASCADE", "DELETE", "PROCEDURE", "FUNCTION", "TABLE",
			"VIEW", "PACKAGE", "BODY", "TRIGGER", "SEQUENCE", "AND", "OR",
			"NOT", "IN", "ON", "AS", "IS", "TO", "EXISTS", "ORDER", "GROUP",
			"HAVING", "BY", "MOD", "LIKE", "NUMBER", "VARCHAR", "DATE", "CHAR",
			"CHARACTER", "LONG", "CONSTANT", "BOOLEAN", "BEGIN", "END",
			"DECLARE", "IF", "THEN", "ELSE", "LOOP", "FOR", "NULL", "CHECK",
			"PRIMARY", "KEY", "FOREIGN", "REFERENCES", "DETERMINISTIC",
			"DEFFERABLE", "REF", "CURSOR", "OPEN", "INSTEAD", "OF", "EACH",
			"ROW", "INTO", "RETURN", "UNION", "MINUS", "ALL", "SOME", "ANY",
			"GRANT", "ALTER", "MODIFY", "PARTITION", "TABLESPACE", "INITIALLY",
			"DEFERRED", "COMMENT", "FORCE", "DEFAULT", "IDENTIFIED",
			"LANGUAGE", "NAME", "RETURNING", "TRUE", "FALSE", "EXIT", "WHEN",
			"BEFORE", "AFTER", "INDEX", "CLUSTER", "EXCEPTION", "OTHERS",
			"AUTHID", "CURRENT_USER", "EXECUTE", "IMMEDIATE", "TYPE", "WHILE",
			"NEXT", "FIRST", "ELSIF", "BETWEEN", "FOUND", "SQL", "ROWNUM",
			"NEW", "OLD", "CURRVAL", "ACCESS", "BFILE", "TRANSACTION", "BLOB",
			"BULK", "COLLECT", "CLOB", "CLOSE", "RAW", "ROWID", "NCLOB",
			"NCHAR", "UROWID", "RECORD", "VARRAY", "VARCHAR2", "DEC",
			"DECIMAL", "DOUBLE", "PRECISION", "FLOAT", "INTEGER", "INT",
			"NUMERIC", "REAL", "SMALLINT", "PLS_INTEGER", "USING" };

	public final String[] OracleFunctions = { "ABS", "ACOS", "ADD_MONTHS",
			"ATAN", "ATAN2", "CEIL", "COS", "COSH", "EXP", "FLOOR", "LN",
			"LOG", "MOD", "POWER", "ROUND", "SIGN", "SIN", "SINH", "SQRT",
			"TAN", "TANH", "CHR", "CONCAT", "INITCAP", "LOWER", "LPAD",
			"LTRIM", "NLS_INITCAP", "NLS_LOWER", "NLSSORT", "NLS_UPPER",
			"REPLACE", "RPAD", "RTRIM", "SOUNDEX", "SUBSTR", "SUBSTRB",
			"TRANSLATE", "TRIM", "UPPER", "ASCII", "INSTR", "INSTRB", "LENGTH",
			"LENGTHB", "ADD_MONTHS", "LAST_DAY", "MONTHS_BETWEEN", "NEW_TIME",
			"NEXT_DAY", "SYSDATE", "TRUNC", "CHARTOROWID", "CONVERT",
			"HEXTORAW", "RAWTOHEX", "ROWIDTOCHAR", "TO_CHAR", "TO_DATE",
			"TO_LOB", "TO_MULTI_BYTE", "TO_NUMBER", "TO_SINGLE_BYTE",
			"BFILENAME", "DUMP", "EMPTY_BLOB", "EMPTY_CLOB", "GREATEST",
			"LEAST", "NLS_CHARSET_DECL_LEN", "NLS_CHARSET_ID",
			"NLS_CHARSET_NAME", "NVL", "SYS_CONTEXT", "SYS_GUID", "UID",
			"USER", "USERENV", "VSIZE", "DEREF", "MAKE_REF", "REF", "REFTOHEX",
			"VALUE", "AVG", "COUNT", "GROUPING", "MAX", "MIN", "STDDEV", "SUM",
			"VARIANCE" };

	private void Highlight(SQLScanner.Token token) {
		setCharacterAttributes(token.start, token.end - token.start + 1,
				sas[token.id], true);
	}

	/**
	 * Scans the text from scanBegin to scanEnd augmenting scanned portion of
	 * text if necessary.
	 * 
	 * @param scanBegin
	 *            must be index of the first character of the firstLine to scan
	 * @param scanEnd
	 *            must be index of the last character of the last line to scan
	 *            (eoln or one character after end of the text)
	 * @param highlightBegin
	 *            highlighting starts when index highlightBegin is reached (an
	 *            optimalization).
	 * @param firstLine
	 *            auxiliary information which can be dervied from scanBegin
	 */
	private void HighlightAffectedText(int scanBegin, int scanEnd,
			int highlightBegin, int firstLine) {

		SQLScanner.Token token = null; // scanned token
		SQLScanner.Token last_line_tok = null; // last line tok affected by scan
		// cycle
		int last_line_idx = -1; // the index of the line of last_line_tok
		boolean eot = false;
		int current_pos = 0;

		// sets document content and interval to be scanned:
		scanner.setDocument(this);
		scanner.setInterval(scanBegin, scanEnd);

		// loads state from line token associated to the end of line before
		// first_line:
		if (firstLine > 0 && lineToks.items[firstLine - 1] != null) {
			SQLScanner.Token t = (SQLScanner.Token) lineToks.items[firstLine - 1];
			scanner.setState(t.id, t.start);
		}

		for (;;) {
			while (scanner.nextToken()) {
				token = scanner.getToken();
				eot = scanner.eot();
				current_pos = scanner.getCurrentPos();

				// highlight token:
				if (current_pos >= highlightBegin)
					Highlight(token);

				// update line toks for every eoln contained by scanned token:
				if (token.isMultiline()) {
					int fline = root.getElementIndex(token.start);
					int lline = root.getElementIndex(token.end) - 1;

					// if the token is not terminated (we are at the end of the
					// text):
					if (eot)
						lline++;

					lineToks.fill(fline, lline, new SQLScanner.Token(token.id,
							token.start, token.end));
				}

				// mark eoln by empty token:
				if (!eot && scanner.eoln()) {
					last_line_idx = root.getElementIndex(token.end);
					last_line_tok = (SQLScanner.Token) lineToks.items[last_line_idx];
					lineToks.items[last_line_idx] = null;
				}
			}
			// assertion: scanner.lookahead=='\n' || scanner.lookahead=='\0'
			// scan cycle ends when it visits all the lines between its starting
			// pos and ending pos,
			// eoln and end of token is reached.

			// The token associated with eoln of last scanned line was changed
			// by previous
			// scan cycle. Former token was saved in last_line_tok.
			// We should continue scanning until this token ends.
			if (last_line_tok != null) {
				// end of the text:
				if (eot)
					break;

				// Start pos: we can increment position because we are on \n
				// (optimalization)
				// End pos: sets the end of scanning to the end of last line
				// containing last_line_tok token:
				scanner
						.setInterval(current_pos + 1, root.getElement(
								root.getElementIndex(last_line_tok.end))
								.getEndOffset() - 1);
			} else
				break;
		}
	}

	/**
	 * Overrides any text insertion in the document. Inserted text is
	 * highlighted.
	 */
	@Override
	public void insertString(int offset, String str, AttributeSet a)
			throws BadLocationException {
		Element line = root.getElement(root.getElementIndex(offset)); // line
		// where
		// insertion
		// started
		int length = str.length(); // length of inserted text
		int former_line_count = root.getElementCount(); // the number of lines
		// before the insertion
		int scan_begin = line.getStartOffset(); // start scanning at the boln of
		// first affected line
		int scan_end = line.getEndOffset() + length - 1; // end scanning at the
		// eoln of last
		// affected line
		// (after insertion)

		// insert attribute-free text:
		super.insertString(offset, str, sas[SQLScanner.TOKEN_WHITESPACE]);

		int line_count = root.getElementCount(); // the number of lines after
		// the insertion
		int first_line = root.getElementIndex(scan_begin); // the first affected
		// line index
		int lines_inserted = line_count - former_line_count; // the number of
		// inserted
		// eolns

		// one or more eolns were added:
		if (lines_inserted > 0)
			lineToks.shift(first_line, lines_inserted);

		// highlight:
		HighlightAffectedText(scan_begin, scan_end, offset, first_line);
	}

	@Override
	public void remove(int offset, int length) throws BadLocationException {
		int former_line_count = root.getElementCount(); // the number of lines
		// before the insertion

		// delete:
		super.remove(offset, length);

		Element line = root.getElement(root.getElementIndex(offset)); // line
		// where
		// deletion
		// started
		int scan_begin = line.getStartOffset(); // start scanning at the boln of
		// first affected line
		int scan_end = line.getEndOffset() - 1; // end scanning at the eoln of
		// last affected line (after
		// deleteion)
		int line_count = root.getElementCount(); // the number of lines after
		// the insertion
		int first_line = root.getElementIndex(scan_begin); // the first affected
		// line index
		int lines_deleted = former_line_count - line_count; // the number of
		// inserted eolns

		// one or more eolns were deleted:
		if (lines_deleted > 0)
			lineToks.unshift(first_line, lines_deleted);

		// highlight:
		HighlightAffectedText(scan_begin, scan_end, offset, first_line);
	}
}