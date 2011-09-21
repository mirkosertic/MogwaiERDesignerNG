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
import javax.swing.text.*;

/**
 * Document highlighting Oracle PL/SQL syntax.
 *
 * @author Tom Matouek
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

        scanner.setKeywords(SQLKEYWORDS);
        scanner.setBuiltinFunctions(SQLFUNCTIONS);

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

    // There are a lot of duplicates, as this code was generated from JDBC DatabaseMetaData
    public final String[] SQLKEYWORDS = {"UNIQUE", "ADD", "SELECT", "FROM", "WHERE",
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
            "NUMERIC", "REAL", "SMALLINT", "PLS_INTEGER", "USING", "CONSTRAINT",
            "LIMIT", "MINUS", "ROWNUM", "SYSDATE", "SYSTIME", "SYSTIMESTAMP", "TODAY",
            "ARITH_OVERFLOW", "BREAK", "BROWSE", "BULK", "CHAR_CONVERT", "CHECKPOINT",
            "CLUSTERED", "COMPUTE", "CONFIRM", "CONTROLROW", "DATA_PGS", "DATABASE",
            "DBCC", "DISK", "DUMMY", "DUMP", "ENDTRAN", "ERRLVL", "ERRORDATA", "ERROREXIT",
            "EXIT", "FILLFACTOR", "HOLDLOCK", "IDENTITY_INSERT", "IF", "INDEX", "KILL",
            "LINENO", "LOAD", "MAX_ROWS_PER_PAGE", "MIRROR", "MIRROREXIT", "NOHOLDLOCK",
            "NONCLUSTERED", "NUMERIC_TRUNCATION", "OFF", "OFFSETS", "ONCE,ONLINE", "OVER",
            "PARTITION", "PERM", "PERMANENT", "PLAN", "PRINT", "PROC", "PROCESSEXIT", "RAISERROR",
            "READ", "READTEXT", "RECONFIGURE", "REPLACE", "RESERVED_PGS", "RETURN", "ROLE",
            "ROWCNT", "ROWCOUNT", "RULE", "SAVE", "SETUSER", "SHARED", "SHUTDOWN", "SOME",
            "STATISTICS", "STRIPE", "SYB_IDENTITY", "SYB_RESTREE", "SYB_TERMINATE", "TEMP",
            "TEXTSIZE", "TRAN", "TRIGGER", "TRUNCATE", "TSEQUAL,UNPARTITION", "USE", "USED_PGS",
            "USER_OPTION", "WAITFOR", "WHILE", "WRITETEXT", "ACCESS", "ADD", "ALTER", "AUDIT",
            "CLUSTER", "COLUMN", "COMMENT", "COMPRESS", "CONNECT", "DATE", "DROP", "EXCLUSIVE",
            "FILE", "IDENTIFIED", "IMMEDIATE", "INCREMENT", "INDEX", "INITIAL", "INTERSECT",
            "LEVEL", "LOCK", "LONG", "MAXEXTENTS", "MINUS", "MODE", "NOAUDIT", "NOCOMPRESS",
            "NOWAIT", "NUMBER", "OFFLINE", "ONLINE", "PCTFREE", "PRIOR", "abort", "acl", "add",
            "aggregate", "append", "archive", "arch_store2", "backward", "binary", "boolean", "change",
            "cluster", "copy", "database", "delimiter", "delimiters", "do", "extend", "explain", "forward",
            "heavy", "index", "inherits", "isnull", "light", "listen", "load", "merge", "nothing", "notify"
            , "notnull", "oids", "purge", "rename", "replace", "retrieve", "returns", "rule", "recipe", "setof",
            "stdin", "stdout", "store", "vacuum", "verbose", "version", "ACCESSIBLE", "ANALYZE", "ASENSITIVE",
            "BEFORE", "BIGINT", "BINARY", "BLOB", "CALL", "CHANGE", "CONDITION", "DATABASE", "DATABASES", "DAY_HOUR",
            "DAY_MICROSECOND", "DAY_MINUTE", "DAY_SECOND", "DELAYED", "DETERMINISTIC", "DISTINCTROW", "DIV", "DUAL",
            "EACH", "ELSEIF", "ENCLOSED", "ESCAPED", "EXIT", "EXPLAIN", "FLOAT4", "FLOAT8", "FORCE", "FULLTEXT", "HIGH_PRIORITY",
            "HOUR_MICROSECOND", "HOUR_MINUTE", "HOUR_SECOND", "IF", "IGNORE", "INFILE", "INOUT", "INT1", "INT2", "INT3",
            "INT4", "INT8", "ITERATE", "KEYS", "KILL", "LEAVE", "LIMIT", "LINEAR", "LINES", "LOAD",
            "LOCALTIME", "LOCALTIMESTAMP", "LOCK", "LONG", "LONGBLOB", "LONGTEXT", "LOOP", "LOW_PRIORITY",
            "MEDIUMBLOB", "MEDIUMINT", "MEDIUMTEXT", "MIDDLEINT", "MINUTE_MICROSECOND", "MINUTE_SECOND",
            "MOD,MODIFIES", "NO_WRITE_TO_BINLOG", "OPTIMIZE", "OPTIONALLY", "OUT", "OUTFILE",
            "PURGE", "RANGE", "READS", "READ_ONLY", "READ_WRITE", "REGEXP,RELEASE", "RENAME", "REPEAT",
            "REPLACE", "REQUIRE", "RETURN", "RLIKE", "SCHEMAS", "SECOND_MICROSECOND", "SENSITIVE",
            "SEPARATOR", "SHOW", "SPATIAL", "SPECIFIC", "SQLEXCEPTION", "SQL_BIG_RESULT", "SQL_CALC_FOUND_ROWS",
            "SQL_SMALL_RESULT", "SSL", "STARTING", "STRAIGHT_JOIN", "TERMINATED", "TINYBLOB", "TINYINT", "TINYTEXT",
            "TRIGGER", "UNDO", "UNLOCK", "UNSIGNED", "USE", "UTC_DATE", "UTC_TIME", "UTC_TIMESTAMP",
            "VARBINARY", "VARCHARACTER", "WHILE", "X509", "XOR", "YEAR_MONTH", "ZEROFILL", "INNER", "OUTER", "JOIN"};

    // There are a lot of duplicates, as this code was generated from JDBC DatabaseMetaData
    public final String[] SQLFUNCTIONS = {"ABS", "ACOS", "ADD_MONTHS",
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
            "VARIANCE", "ABS", "ACOS", "ASIN", "ATAN", "COS", "COT", "SIN", "TAN",
            "ATAN2", "BITAND", "BITOR", "BITXOR", "MOD", "CEILING", "DEGREES", "EXP",
            "FLOOR", "LOG", "LOG10", "RADIANS", "SQRT", "PI", "POWER", "RAND",
            "RANDOM_UUID", "ROUND", "ROUNDMAGIC", "SECURE_RAND", "SIGN", "ENCRYPT",
            "DECRYPT", "HASH", "TRUNCATE", "COMPRESS", "EXPAND", "ZERO", "ASCII",
            "BIT_LENGTH", "LENGTH", "OCTET_LENGTH", "CHAR", "CONCAT", "DIFFERENCE",
            "HEXTORAW", "RAWTOHEX", "INSTR", "INSERT", "LOWER", "UPPER", "LEFT",
            "RIGHT", "LOCATE", "POSITION", "LPAD", "RPAD", "LTRIM", "RTRIM",
            "TRIM", "REGEXP_REPLACE", "REPEAT", "REPLACE", "SOUNDEX", "SPACE",
            "STRINGDECODE", "STRINGENCODE", "STRINGTOUTF8", "SUBSTRING",
            "UTF8TOSTRING", "XMLATTR", "XMLNODE", "XMLCOMMENT", "XMLCDATA",
            "XMLSTARTDOC", "XMLTEXT", "CURRENT_DATE", "CURRENT_TIME",
            "CURRENT_TIMESTAMP", "DATEADD", "DATEDIFF", "DAYNAME", "DAY_OF_MONTH",
            "DAY_OF_WEEK", "DAY_OF_YEAR", "EXTRACT", "FORMATDATETIME", "HOUR",
            "MINUTE", "MONTH", "MONTHNAME", "PARSEDATETIME", "QUARTER", "SECOND", "WEEK",
            "YEAR", "ABS", "ACOS", "ASIN", "ATAN", "ATAN2", "BITAND", "BITOR", "BITXOR",
            "CEILING", "COS", "COT", "DEGREES", "EXP", "FLOOR", "LOG", "LOG10", "MOD",
            "PI", "POWER", "RADIANS", "RAND", "ROUND", "ROUNDMAGIC", "SIGN", "SIN",
            "SQRT", "TAN", "TRUNCATE", "ASCII", "CHAR", "CONCAT", "DIFFERENCE",
            "HEXTORAW", "INSERT", "LCASE", "LEFT", "LENGTH", "LOCATE", "LTRIM",
            "RAWTOHEX", "REPEAT", "REPLACE", "RIGHT", "RTRIM", "SOUNDEX", "SPACE",
            "SUBSTR", "UCASE", "CURDATE", "CURTIME", "DATEDIFF", "DAYNAME", "DAYOFMONTH",
            "DAYOFWEEK", "DAYOFYEAR", "HOUR", "MINUTE", "MONTH", "MONTHNAME",
            "NOW", "QUARTER", "SECOND", "SECONDS_SINCE_MIDNIGHT", "TIMESTAMPADD",
            "TIMESTAMPDIFF", "TO_CHAR", "WEEK", "YEAR", "abs", "acos", "asin", "atan",
            "atan2", "ceiling", "cos", "cot", "degrees", "exp", "floor", "log", "log10",
            "mod", "pi", "power", "radians", "rand", "round", "sign", "sin", "sqrt", "tan",
            "ascii", "char", "concat", "difference", "insert", "lcase", "left", "length",
            "locate", "ltrim", "repeat", "replace", "right", "rtrim", "soundex", "space",
            "substring", "ucase", "curdate", "curtime", "dayname", "dayofmonth", "dayofweek",
            "dayofyear", "hour", "minute", "month", "monthname", "now", "quarter",
            "timestampadd", "timestampdiff", "second", "week", "year", "ABS", "ACOS", "ASIN",
            "ATAN", "ATAN2", "BIT_COUNT", "CEILING", "COS", "COT", "DEGREES", "EXP", "FLOOR",
            "LOG", "LOG10", "MAX", "MIN", "MOD", "PI", "POW", "POWER", "RADIANS", "RAND",
            "ROUND", "SIN", "SQRT", "TAN", "TRUNCATE", "ASCII", "BIN", "BIT_LENGTH", "CHAR",
            "CHARACTER_LENGTH", "CHAR_LENGTH", "CONCAT", "CONCAT_WS", "CONV", "ELT", "EXPORT_SET",
            "FIELD", "FIND_IN_SET", "HEX", "INSERT", "INSTR", "LCASE", "LEFT", "LENGTH",
            "LOAD_FILE", "LOCATE", "LOCATE", "LOWER", "LPAD", "LTRIM", "MAKE_SET",
            "MATCH", "MID", "OCT", "OCTET_LENGTH", "ORD", "POSITION", "QUOTE", "REPEAT",
            "REPLACE", "REVERSE", "RIGHT", "RPAD", "RTRIM", "SOUNDEX", "SPACE", "STRCMP",
            "SUBSTRING", "SUBSTRING", "SUBSTRING", "SUBSTRING", "SUBSTRING_INDEX",
            "TRIM", "UCASE", "UPPER", "DAYOFWEEK", "WEEKDAY", "DAYOFMONTH", "DAYOFYEAR",
            "MONTH", "DAYNAME", "MONTHNAME", "QUARTER", "WEEK", "YEAR", "HOUR", "MINUTE",
            "SECOND", "PERIOD_ADD", "PERIOD_DIFF", "TO_DAYS", "FROM_DAYS", "DATE_FORMAT",
            "TIME_FORMAT", "CURDATE", "CURRENT_DATE", "CURTIME", "CURRENT_TIME", "NOW",
            "SYSDATE", "CURRENT_TIMESTAMP", "UNIX_TIMESTAMP", "FROM_UNIXTIME", "SEC_TO_TIME", "TIME_TO_SEC",
            "ABS", "ACOS", "ASIN", "ATAN", "ATAN2", "CEILING", "COS", "EXP", "FLOOR", "LOG",
            "LOG10", "MOD", "PI", "POWER", "ROUND", "SIGN", "SIN", "SQRT", "TAN", "TRUNCATE",
            "ASCII", "CHAR", "CONCAT", "LCASE", "LENGTH", "LTRIM", "REPLACE", "RTRIM", "SOUNDEX",
            "SUBSTRING", "UCASE", "HOUR", "MINUTE", "SECOND", "MONTH", "YEAR", "abs", "acos", "asin",
            "atan", "atan2", "ceiling", "cos", "cot", "degrees", "exp", "floor", "log", "log10",
            "mod", "pi", "power", "radians", "round", "sign", "sin", "sqrt", "tan", "truncate",
            "ascii", "char", "concat", "lcase", "left", "length", "ltrim", "repeat", "rtrim",
            "space", "substring", "ucase", "replace", "curdate", "curtime", "dayname", "dayofmonth",
            "dayofweek", "dayofyear", "hour", "minute", "month", "monthname", "now", "quarter",
            "second", "week", "year", "timestampadd",};

    private void Highlight(SQLScanner.Token token) {
        setCharacterAttributes(token.start, token.end - token.start + 1,
                sas[token.id], true);
    }

    /**
     * Scans the text from scanBegin to scanEnd augmenting scanned portion of
     * text if necessary.
     *
     * @param scanBegin      must be index of the first character of the firstLine to scan
     * @param scanEnd        must be index of the last character of the last line to scan
     *                       (eoln or one character after end of the text)
     * @param highlightBegin highlighting starts when index highlightBegin is reached (an
     *                       optimalization).
     * @param firstLine      auxiliary information which can be dervied from scanBegin
     */
    private void HighlightAffectedText(int scanBegin, int scanEnd,
                                       int highlightBegin, int firstLine) {

        SQLScanner.Token token; // scanned token
        SQLScanner.Token last_line_tok = null; // last line tok affected by scan
        // cycle
        int last_line_idx; // the index of the line of last_line_tok
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