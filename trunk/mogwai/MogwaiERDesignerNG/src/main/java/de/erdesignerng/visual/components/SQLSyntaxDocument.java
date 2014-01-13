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

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.Color;

/**
 * Document highlighting Oracle PL/SQL syntax.
 *
 * @author Tom Matouek
 * @version 1.0
 */
public final class SQLSyntaxDocument extends DefaultStyledDocument {

	/**
	 * Document root.
	 */
	private final Element root;

	/**
	 * Tokens highlighting attributes.
	 */
	private final SimpleAttributeSet[] sas = new SimpleAttributeSet[SQLScanner.TOKEN_COUNT];

	/**
	 * Scanner used to find out tokens.
	 */
	private final SQLScanner scanner = new SQLScanner();

	/**
	 * For each and every eoln in the text this array contains the token being
	 * scanned when the scanner lookaheads that eoln.
	 */
	private final ExpandingArray lineToks = new ExpandingArray(1);

	/**
	 * Constructs document with SQL syntax highlighting.
	 */
	public SQLSyntaxDocument() {
		root = getDefaultRootElement();

		scanner.setKeywords(SQLKEYWORDS);
		scanner.setBuiltinFunctions(SQLFUNCTIONS);

		StyleConstants.setForeground(sas[SQLScanner.TOKEN_STRING] = new SimpleAttributeSet(), new Color(127, 0, 127)); //Color.lightGray
		StyleConstants.setForeground(sas[SQLScanner.TOKEN_COMMENT_ML] = new SimpleAttributeSet(), new Color(0, 127, 0)); //Color.red
		StyleConstants.setForeground(sas[SQLScanner.TOKEN_COMMENT_SL] = new SimpleAttributeSet(), new Color(0, 127, 0)); //Color.red
		StyleConstants.setForeground(sas[SQLScanner.TOKEN_KEYWORD] = new SimpleAttributeSet(), new Color(0, 0, 127)); //Color.blue
		StyleConstants.setForeground(sas[SQLScanner.TOKEN_QUOTED_ID] = new SimpleAttributeSet(), new Color(127, 0, 127));//Color.gray
		StyleConstants.setForeground(sas[SQLScanner.TOKEN_BUILTIN_FUNCTION] = new SimpleAttributeSet(), new Color(100, 0, 200));
		sas[SQLScanner.TOKEN_WHITESPACE] = new SimpleAttributeSet();
	}

	public final String[] SQLKEYWORDS = {"ABORT", "ACCESS", "ACCESSIBLE", "ACL", "ACTION",
			"ADD", "AFTER", "AGGREGATE", "ALL", "ALTER", "ANALYZE", "AND", "ANY",
			"APPEND", "ARCH_STORE2", "ARCHIVE", "ARITH_OVERFLOW", "AS",
			"ASENSITIVE", "AUDIT", "AUTHID", "BACKWARD", "BEFORE", "BEGIN",
			"BETWEEN", "BFILE", "BIGINT", "BINARY", "BLOB", "BODY", "BOOLEAN",
			"BREAK", "BROWSE", "BULK", "BY", "CACHE", "CALL", "CASCADE", "CHANGE", "CHAR",
			"CHAR_CONVERT", "CHARACTER", "VARYING", "CHECK", "CHECKPOINT", "CLOB", "CLOSE",
			"CLUSTER", "CLUSTERED", "COLLECT", "COLUMN", "COMMENT", "COMPRESS",
			"COMPUTE", "CONDITION", "CONFIRM", "CONNECT", "CONSTANT", "CONSTRAINT",
			"CONTROLROW", "COPY", "CREATE", "CURRENT_USER", "CURRVAL", "CURSOR",
			"DATA_PGS", "DATABASE", "DATABASES", "DATE", "DAY_HOUR",
			"DAY_MICROSECOND", "DAY_MINUTE", "DAY_SECOND", "DBCC", "DEC", "DECIMAL",
			"DECLARE", "DEFAULT", "DEFERRED", "DEFFERABLE", "DELAYED", "DELETE",
			"DELIMITER", "DELIMITERS", "DETERMINISTIC", "DISK", "DISTINCTROW",
			"DIV", "DO", "DOMAIN", "DOUBLE", "DROP", "DUAL", "DUMMY", "DUMP", "EACH", "ELSE",
			"ELSEIF", "ELSIF", "ENCLOSED", "END", "ENDTRAN", "ENUM", "ERRLVL", "ERRORDATA",
			"ERROREXIT", "ESCAPED", "EXCEPTION", "EXCLUSIVE", "EXECUTE", "EXISTS",
			"EXIT", "EXPLAIN", "EXTEND", "FALSE", "FILE", "FILLFACTOR", "FIRST",
			"FLOAT", "FLOAT4", "FLOAT8", "FOR", "FORCE", "FOREIGN", "FORWARD",
			"FOUND", "FROM", "FULLTEXT", "FUNCTION", "GRANT", "GROUP", "HAVING",
			"HEAVY", "HIGH_PRIORITY", "HOLDLOCK", "HOUR_MICROSECOND", "HOUR_MINUTE",
			"HOUR_SECOND", "IDENTIFIED", "IDENTITY_INSERT", "IF", "IGNORE",
			"IMMEDIATE", "IN", "INCREMENT", "INDEX", "INFILE", "INHERITS",
			"INITIAL", "INITIALLY", "INNER", "INOUT", "INSERT", "INSTEAD", "INT",
			"INT1", "INT2", "INT3", "INT4", "INT8", "INTEGER", "INTERSECT", "INTO",
			"IS", "ISNULL", "ITERATE", "JOIN", "KEY", "KEYS", "KILL", "LANGUAGE",
			"LEAVE", "LEVEL", "LIGHT", "LIKE", "LIMIT", "LINEAR", "LINENO", "LINES",
			"LISTEN", "LOAD", "LOCALTIME", "LOCALTIMESTAMP", "LOCK", "LONG",
			"LONGBLOB", "LONGTEXT", "LOOP", "LOW_PRIORITY", "MAX_ROWS_PER_PAGE",
			"MAXEXTENTS", "MAXVALUE", "MEDIUMBLOB", "MEDIUMINT", "MEDIUMTEXT", "MERGE",
			"MIDDLEINT", "MINUS", "MINUTE_MICROSECOND", "MINUTE_SECOND", "MINVALUE", "MIRROR",
			"MIRROREXIT", "MOD", "MODE", "MODIFIES", "MODIFY", "NAME", "NCHAR",
			"NCLOB", "NEW", "NEXT", "NO", "NO_WRITE_TO_BINLOG", "NOAUDIT", "NOCOMPRESS",
			"NOHOLDLOCK", "NONCLUSTERED", "NOT", "NOTHING", "NOTIFY", "NOTNULL",
			"NOWAIT", "NULL", "NUMBER", "NUMERIC", "NUMERIC_TRUNCATION", "OF",
			"OFF", "OFFLINE", "OFFSETS", "OIDS", "OLD", "ON", "ONCE", "ONLINE",
			"OPEN", "OPTIMIZE", "OPTIONALLY", "OR", "ORDER", "OTHERS", "OUT",
			"OUTER", "OUTFILE", "OVER", "PACKAGE", "PARTITION", "PCTFREE", "PERM",
			"PERMANENT", "PLAN", "PLS_INTEGER", "PRECISION", "PRIMARY", "PRINT",
			"PRIOR", "PROC", "PROCEDURE", "PROCESSEXIT", "PURGE", "RAISERROR",
			"RANGE", "RAW", "READ", "READ_ONLY", "READ_WRITE", "READS", "READTEXT",
			"REAL", "RECIPE", "RECONFIGURE", "RECORD", "REF", "REFERENCES",
			"REGEXP", "RELEASE", "RENAME", "REPEAT", "REPLACE", "REQUIRE",
			"RESERVED_PGS", "RESTRICT", "RETRIEVE", "RETURN", "RETURNING", "RETURNS", "RLIKE",
			"ROLE", "ROW", "ROWCNT", "ROWCOUNT", "ROWID", "ROWNUM", "RULE", "SAVE",
			"SCHEMAS", "SECOND_MICROSECOND", "SELECT", "SENSITIVE", "SEPARATOR",
			"SEQUENCE", "SET", "SETOF", "SETUSER", "SHARED", "SHOW", "SHUTDOWN",
			"SMALLINT", "SOME", "SPATIAL", "SPECIFIC", "SQL", "SQL_BIG_RESULT",
			"SQL_CALC_FOUND_ROWS", "SQL_SMALL_RESULT", "SQLEXCEPTION", "SSL", "START",
			"STARTING", "STATISTICS", "STDIN", "STDOUT", "STORE", "STRAIGHT_JOIN",
			"STRIPE", "SYB_IDENTITY", "SYB_RESTREE", "SYB_TERMINATE", "SYSDATE",
			"SYSTIME", "SYSTIMESTAMP", "TABLE", "TABLESPACE", "TEMP", "TERMINATED",
			"TEXTSIZE", "THEN", "TINYBLOB", "TINYINT", "TINYTEXT", "TO", "TODAY",
			"TRAN", "TRANSACTION", "TRIGGER", "TRUE", "TRUNCATE", "TSEQUAL", "TYPE",
			"UNDO", "UNION", "UNIQUE", "UNLOCK", "UNPARTITION", "UNSIGNED",
			"UPDATE", "UROWID", "USE", "USED_PGS", "USER_OPTION", "USING",
			"UTC_DATE", "UTC_TIME", "UTC_TIMESTAMP", "VACUUM", "VALUES",
			"VARBINARY", "VARCHAR", "VARCHAR2", "VARCHARACTER", "VARRAY", "VERBOSE",
			"VERSION", "VIEW", "WAITFOR", "WHEN", "WHERE", "WHILE", "WRITETEXT",
			"X509", "XOR", "YEAR_MONTH", "ZEROFILL"};

	public final String[] SQLFUNCTIONS = {"ABS", "ACOS", "ADD_MONTHS", "ASCII",
			"ASIN", "ATAN", "ATAN2", "AVG", "BFILENAME", "BIN", "BIT_COUNT",
			"BIT_LENGTH", "BITAND", "BITOR", "BITXOR", "CEIL", "CEILING", "CHAR",
			"CHAR_LENGTH", "CHARACTER_LENGTH", "CHARTOROWID", "CHR", "COMPRESS",
			"CONCAT", "CONCAT_WS", "CONV", "CONVERT", "COS", "COSH", "COT", "COUNT",
			"CURDATE", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP",
			"CURTIME", "DATE_FORMAT", "DATEADD", "DATEDIFF", "DAY_OF_MONTH",
			"DAY_OF_WEEK", "DAY_OF_YEAR", "DAYNAME", "DAYOFMONTH", "DAYOFWEEK",
			"DAYOFYEAR", "DECRYPT", "DEGREES", "DEREF", "DIFFERENCE", "DUMP", "ELT",
			"EMPTY_BLOB", "EMPTY_CLOB", "ENCRYPT", "EXP", "EXPAND", "EXPORT_SET",
			"EXTRACT", "FIELD", "FIND_IN_SET", "FLOOR", "FORMATDATETIME",
			"FROM_DAYS", "FROM_UNIXTIME", "GREATEST", "GROUPING", "HASH", "HEX",
			"HEXTORAW", "HOUR", "INITCAP", "INSERT", "INSTR", "INSTRB", "LAST_DAY",
			"LCASE", "LEAST", "LEFT", "LENGTH", "LENGTHB", "LN", "LOAD_FILE",
			"LOCATE", "LOG", "LOG10", "LOWER", "LPAD", "LTRIM", "MAKE_REF",
			"MAKE_SET", "MATCH", "MAX", "MID", "MIN", "MINUTE", "MOD", "MONTH",
			"MONTHNAME", "MONTHS_BETWEEN", "NEW_TIME", "NEXT_DAY",
			"NLS_CHARSET_DECL_LEN", "NLS_CHARSET_ID", "NLS_CHARSET_NAME",
			"NLS_INITCAP", "NLS_LOWER", "NLS_UPPER", "NLSSORT", "NOW", "NVL",
			"OCT", "OCTET_LENGTH", "ORD", "PARSEDATETIME", "PERIOD_ADD",
			"PERIOD_DIFF", "PI", "POSITION", "POW", "POWER", "QUARTER", "QUOTE",
			"RADIANS", "RAND", "RANDOM_UUID", "RAWTOHEX", "REF", "REFTOHEX",
			"REGEXP_REPLACE", "REPEAT", "REPLACE", "REVERSE", "RIGHT", "ROUND",
			"ROUNDMAGIC", "ROWIDTOCHAR", "RPAD", "RTRIM", "SEC_TO_TIME", "SECOND",
			"SECONDS_SINCE_MIDNIGHT", "SECURE_RAND", "SIGN", "SIN", "SINH",
			"SOUNDEX", "SPACE", "SQRT", "STDDEV", "STRCMP", "STRINGDECODE",
			"STRINGENCODE", "STRINGTOUTF8", "SUBSTR", "SUBSTRB", "SUBSTRING",
			"SUBSTRING_INDEX", "SUM", "SYS_CONTEXT", "SYS_GUID", "SYSDATE", "TAN",
			"TANH", "TIME_FORMAT", "TIME_TO_SEC", "TIMESTAMPADD", "TIMESTAMPDIFF",
			"TO_CHAR", "TO_DATE", "TO_DAYS", "TO_LOB", "TO_MULTI_BYTE", "TO_NUMBER",
			"TO_SINGLE_BYTE", "TRANSLATE", "TRIM", "TRUNC", "TRUNCATE", "UCASE",
			"UID", "UNIX_TIMESTAMP", "UPPER", "USER", "USERENV", "UTF8TOSTRING",
			"VALUE", "VARIANCE", "VSIZE", "WEEK", "WEEKDAY", "XMLATTR", "XMLCDATA",
			"XMLCOMMENT", "XMLNODE", "XMLSTARTDOC", "XMLTEXT", "YEAR", "ZERO"};

	private void Highlight(SQLScanner.Token token) {
		setCharacterAttributes(token.start, token.end - token.start + 1, sas[token.id], true);
	}

	/**
	 * Scans the text from scanBegin to scanEnd augmenting scanned portion of
	 * text if necessary.
	 *
	 * @param scanBegin	  must be index of the first character of the firstLine to scan
	 * @param scanEnd		must be index of the last character of the last line to scan
	 *					   (eoln or one character after end of the text)
	 * @param highlightBegin highlighting starts when index highlightBegin is reached (an
	 *					   optimalization).
	 * @param firstLine	  auxiliary information which can be dervied from scanBegin
	 */
	private void HighlightAffectedText(int scanBegin, int scanEnd, int highlightBegin, int firstLine) {

		SQLScanner.Token token; // scanned token
		SQLScanner.Token last_line_tok = null; // last line tok affected by scan
		// cycle
		int last_line_idx; // the index of the line of last_line_tok
		boolean eot = false;
		int current_pos = 0;

		// sets document content and interval to be scanned:
		scanner.setDocument(this);
		scanner.setInterval(scanBegin, scanEnd);

		// loads state from line token associated to the end of line before first_line:
		if (firstLine > 0 && lineToks.items[firstLine - 1] != null) {
			SQLScanner.Token t = (SQLScanner.Token) lineToks.items[firstLine - 1];
			scanner.setState(t.id, t.start);
		}

		for (; ; ) {
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

					// if the token is not terminated (we are at the end of the text):
					if (eot)
						lline++;

					lineToks.fill(fline, lline, new SQLScanner.Token(token.id, token.start, token.end));
				}

				// mark eoln by empty token:
				if (!eot && scanner.eoln()) {
					last_line_idx = root.getElementIndex(token.end);
					last_line_tok = (SQLScanner.Token) lineToks.items[last_line_idx];
					lineToks.items[last_line_idx] = null;
				}
			}
			// assertion: scanner.lookahead=='\n' || scanner.lookahead=='\0'
			// scan cycle ends when it visits all the lines between its starting pos and ending pos, eoln and end of token is reached.

			// The token associated with eoln of last scanned line was changed by previous scan cycle. Former token was saved in last_line_tok.
			// We should continue scanning until this token ends.
			if (last_line_tok != null) {
				// end of the text:
				if (eot)
					break;

				// Start pos: we can increment position because we are on  (optimalization)
				// End pos: sets the end of scanning to the end of last line containing last_line_tok token:
				scanner.setInterval(current_pos + 1, root.getElement(root.getElementIndex(last_line_tok.end)).getEndOffset() - 1);
			} else
				break;
		}
	}

	/**
	 * Overrides any text insertion in the document. Inserted text is
	 * highlighted.
	 * 
	 * @param offset
	 * @throws javax.swing.text.BadLocationException
	 */
	@Override
	public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
		Element line = root.getElement(root.getElementIndex(offset)); // line where insertion started
		int length = str.length(); // length of inserted text
		int former_line_count = root.getElementCount(); // the number of lines before the insertion
		int scan_begin = line.getStartOffset(); // start scanning at the boln of first affected line
		int scan_end = line.getEndOffset() + length - 1; // end scanning at the eoln of last affected line (after insertion)

		// insert attribute-free text:
		super.insertString(offset, str, sas[SQLScanner.TOKEN_WHITESPACE]);

		int line_count = root.getElementCount(); // the number of lines after the insertion
		int first_line = root.getElementIndex(scan_begin); // the first affected line index
		int lines_inserted = line_count - former_line_count; // the number of inserted eolns

		// one or more eolns were added:
		if (lines_inserted > 0)
			lineToks.shift(first_line, lines_inserted);

		// highlight:
		HighlightAffectedText(scan_begin, scan_end, offset, first_line);
	}

	@Override
	public void remove(int offset, int length) throws BadLocationException {
		int former_line_count = root.getElementCount(); // the number of lines before the insertion

		// delete:
		super.remove(offset, length);

		Element line = root.getElement(root.getElementIndex(offset)); // line where deletion started
		int scan_begin = line.getStartOffset(); // start scanning at the boln of first affected line
		int scan_end = line.getEndOffset() - 1; // end scanning at the eoln of last affected line (after deleteion)
		int line_count = root.getElementCount(); // the number of lines after the insertion
		int first_line = root.getElementIndex(scan_begin); // the first affected line index
		int lines_deleted = former_line_count - line_count; // the number of inserted eolns

		// one or more eolns were deleted:
		if (lines_deleted > 0)
			lineToks.unshift(first_line, lines_deleted);

		// highlight:
		HighlightAffectedText(scan_begin, scan_end, offset, first_line);
	}
}