package de.erdesignerng.plugins.sqleonardo;

/*
 * SQLeonardo :: java database frontend
 * Copyright (C) 2004 nickyb@users.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.ListIterator;

import nickyb.sqleonardo.querybuilder.QueryBuilder;
import nickyb.sqleonardo.querybuilder.QueryModel;
import nickyb.sqleonardo.querybuilder.syntax.QueryExpression;
import nickyb.sqleonardo.querybuilder.syntax.QuerySpecification;
import nickyb.sqleonardo.querybuilder.syntax.QueryTokens;
import nickyb.sqleonardo.querybuilder.syntax.SQLFormatter;
import nickyb.sqleonardo.querybuilder.syntax.SubQuery;
import nickyb.sqleonardo.querybuilder.syntax._ReservedWords;

public class SQLParser {
    public static QueryModel toQueryModel(String sql) throws IOException {
        return toQueryModel(new StringReader(sql));
    }

    private static QueryModel toQueryModel(Reader r) throws IOException {
        QueryModel qm = new QueryModel();

        ArrayList al = doTokenize(r);
        doAdjustSequence(al);

        ListIterator li = al.listIterator();
        doParseQuery(li, qm.getQueryExpression());

        if (li.hasNext() && li.next().toString().toUpperCase().equalsIgnoreCase(_ReservedWords.ORDER_BY))
            doParseOrderBy(li, qm);

        return qm;
    }

    private static void doParseQuery(ListIterator li, QueryExpression qe) throws IOException {
        while (li.hasNext()) {
            Object next = li.next();
            if (next.toString().equalsIgnoreCase(_ReservedWords.SELECT)) {
                doParseSelect(li, qe.getQuerySpecification());
            } else if (next.toString().equalsIgnoreCase(_ReservedWords.FROM)) {
                doParseFrom(li, qe.getQuerySpecification());
                doEnsureReferences(qe.getQuerySpecification());
            } else if (next.toString().equalsIgnoreCase(_ReservedWords.WHERE)) {
                QueryTokens.Condition[] tokens = doParseConditions(li);
                for (int i = 0; i < tokens.length; i++)
                    qe.getQuerySpecification().addWhereClause(tokens[i]);
            } else if (next.toString().equalsIgnoreCase(_ReservedWords.GROUP_BY)) {
                doParseGroupBy(li, qe.getQuerySpecification());
            } else if (next.toString().equalsIgnoreCase(_ReservedWords.HAVING)) {
                QueryTokens.Condition[] tokens = doParseConditions(li);
                for (int i = 0; i < tokens.length; i++)
                    qe.getQuerySpecification().addHavingClause(tokens[i]);
            } else if (next.toString().equalsIgnoreCase(_ReservedWords.UNION)) {
                QueryExpression union = new QueryExpression();
                doParseQuery(li, union);
                qe.setUnion(union);
            } else if (next.toString().equalsIgnoreCase(_ReservedWords.ORDER_BY)) {
                li.previous();
                break;
            } else if (next.toString().equals(")")) {
                break;
            }
        }
    }

    private static void doParseSelect(ListIterator li, QuerySpecification qs) throws IOException {
        int surrounds = 0;
        String value = new String();

        while (li.hasNext()) {
            Object next = li.next();

            if (next.toString().equalsIgnoreCase(_ReservedWords.DISTINCT)) {
                qs.setQuantifier(QuerySpecification.DISTINCT);
            } else if (next.toString().equals(",") && surrounds == 0) {
                qs.addSelectList(new QueryTokens.DefaultExpression(value.trim()));
                value = new String();
            // [MSE] Sometimes the token stream returns not comma in a single token, it returns
            // a value with an appended comma. This is the fix for this case.
            } else if (value.trim().endsWith(",") && surrounds == 0) {
                qs.addSelectList(new QueryTokens.DefaultExpression(value.substring(0, value.length() - 2).trim()));
                value = (String) next;
            } else if (isClauseWord(next.toString())) {
                li.previous();
                if (next.toString().equalsIgnoreCase(_ReservedWords.SELECT)) {
                    SubQuery sub = new SubQuery();
                    doParseQuery(li, sub);

                    qs.addSelectList(sub);
                    value = new String();
                } else {
                    if (!value.trim().equals(""))
                        qs.addSelectList(new QueryTokens.DefaultExpression(value.trim()));
                    break;
                }
            } else {
                if (next.toString().equals("("))
                    surrounds++;
                if (next.toString().equals(")"))
                    surrounds--;

                if (value.length() > 0 && next instanceof String) {
                    char last = value.charAt(value.length() - 1);
                    if (Character.isLetter(last) || String.valueOf(last).equals(QueryBuilder.identifierQuoteString))
                        value = value + SQLFormatter.SPACE;
                }

                value = value + next.toString();
            }
        }
    }

    private static void doParseFrom(ListIterator li, QuerySpecification qs) {
        int joinType = -1;
        QueryTokens.Table t = null;
        Hashtable tables = new Hashtable();

        for (int surrounds = 0; li.hasNext();) {
            String next = li.next().toString();
            if (isClauseWord(next) || next.equals(";")) {
                // System.out.println("end.");

                if (t != null)
                    qs.addFromClause(t);

                li.previous();
                break;
            } else if (next.equals(",")) {
                // System.out.println("cross");

                if (t != null)
                    qs.addFromClause(t);
                t = null;
            } else if (isJoinWord(next)) {
                // System.out.println("join");

                if (t != null)
                    tables.put(stripQuote(t.getReference()), t);
                t = null;

                joinType = QueryTokens.Join.getTypeInt(next);
            } else if (next.toString().equalsIgnoreCase(_ReservedWords.ON)
                    || next.toString().equalsIgnoreCase(_ReservedWords.AND)
                    || next.toString().equalsIgnoreCase(_ReservedWords.OR)) {
                // System.out.println("condition");

                if (t != null)
                    tables.put(stripQuote(t.getReference()), t);
                t = null;

                /* is AND/OR, then use previous/last type */
                if (joinType == -1) {
                    QueryTokens._TableReference[] ref = qs.getFromClause();
                    if (ref.length > 0 && ref[ref.length - 1] instanceof QueryTokens.Join)
                        joinType = ((QueryTokens.Join) ref[ref.length - 1]).getType();
                }

                String left = li.next().toString();
                while (left.equals("(")) {
                    surrounds++;
                    left = li.next().toString();
                }
                String op = li.next().toString();
                String right = li.next().toString();

                QueryTokens.Column tcl = null;
                QueryTokens.Column tcr = null;

                for (int side = 0; side < 2; side++) {
                    String e = side == 0 ? left : right;
                    e = stripQuote(e);

                    int dot = e.lastIndexOf(SQLFormatter.DOT);
                    String ref = dot == -1 ? new String() : e.substring(0, dot);

                    QueryTokens.Table tr = new QueryTokens.Table(null, ref);
                    if (tables.containsKey(ref)) {
                        tr = (QueryTokens.Table) tables.get(ref);
                    } else
                        tables.put(stripQuote(tr.getReference()), tr);

                    if (side == 0)
                        tcl = new QueryTokens.Column(tr, e.substring(dot + 1));
                    else
                        tcr = new QueryTokens.Column(tr, e.substring(dot + 1));
                }
                qs.addFromClause(new QueryTokens.Join(joinType, tcl, op, tcr));
                joinType = -1;
            } else if (next.toString().equals("(")) {
                surrounds++;
            } else if (next.toString().equals(")")) {
                // <bug=1914170>
                if (t != null)
                    qs.addFromClause(t);
                t = null;
                // </bug>

                if (--surrounds < 0) {
                    li.previous();
                    break;
                }
            } else if (!next.toString().equalsIgnoreCase("AS")) {
                // System.out.println("table or alias");

                String schema = null;
                String name = stripQuote(next.toString());

                int i = name.lastIndexOf(SQLFormatter.DOT);
                if (i > 0) {
                    schema = name.substring(0, i);
                    name = name.substring(i + 1);
                }

                if (t == null)
                    t = new QueryTokens.Table(schema, name);
                else
                    t.setAlias(next.toString());
            }
        }
    }

    private static void doParseGroupBy(ListIterator li, QuerySpecification qs) {
        while (li.hasNext()) {
            Object next = li.next();
            if (isReservedWord(next.toString()) || next.toString().equals(";")) {
                li.previous();
                break;
            } else if (next instanceof String) {
                qs.addGroupByClause(new QueryTokens.Group(next.toString()));
            }
        }
    }

    private static void doParseOrderBy(ListIterator li, QueryModel qm) {
        QueryTokens.Sort token = null;
        while (li.hasNext()) {
            Object next = li.next();
            if (next.toString().equals(",") || next.toString().equals(";")) {
                qm.addOrderByClause(token);
                token = null;
            } else {
                if (token == null)
                    token = new QueryTokens.Sort(new QueryTokens.DefaultExpression(next.toString()));
                else if (next.toString().equalsIgnoreCase("ASC"))
                    token.setType(QueryTokens.Sort.ASCENDING);
                else if (next.toString().equalsIgnoreCase("DESC"))
                    token.setType(QueryTokens.Sort.DESCENDING);
            }
        }
    }

    private static QueryTokens.Condition[] doParseConditions(ListIterator li) throws IOException {
        ArrayList tokens = new ArrayList();
        QueryTokens.Condition token = null;
        QueryTokens._Expression expr = null;

        for (int surrounds = 0; li.hasNext();) {
            Object next = li.next();

            if (next.toString().equals("("))
                surrounds++;
            if (next.toString().equals(")"))
                surrounds--;

            if (next.toString().equalsIgnoreCase("EXISTS") || next.toString().equalsIgnoreCase("NOT EXISTS")) {
                SubQuery sub = new SubQuery();
                doParseQuery(li, sub);

                token.setLeft(null);
                token.setOperator(next.toString());
                token.setRight(sub);

                tokens.add(token);

                token = null;
                expr = null;
            } else if (isClauseWord(next.toString())) {
                li.previous();
                if (next.toString().equalsIgnoreCase(_ReservedWords.SELECT)) {
                    expr = new SubQuery();
                    doParseQuery(li, (SubQuery) expr);
                } else {
                    if (token != null) {
                        token.setRight(expr);
                        tokens.add(token);
                    }
                    break;
                }
            } else if (isOperator(next.toString())) {
                if (token == null)
                    token = new QueryTokens.Condition();

                token.setLeft(expr);
                token.setOperator(next.toString().toUpperCase());

                expr = null;
            } else if (next.toString().equalsIgnoreCase(_ReservedWords.AND)
                    || next.toString().equalsIgnoreCase(_ReservedWords.OR) || next.toString().equals(";")
                    || surrounds < 0) {
                if (token != null) {
                    token.setRight(expr);
                    tokens.add(token);
                }

                token = new QueryTokens.Condition();
                token.setAppend(next.toString());

                expr = null;
            } else {
                String value = expr == null ? new String() : expr.toString();

                if (value.length() > 0 && next instanceof String) {
                    char last = value.charAt(value.length() - 1);
                    if (Character.isLetter(last))
                        value = value + SQLFormatter.SPACE;
                }
                value = value + next.toString();
                expr = new QueryTokens.DefaultExpression(value);
            }

            if (surrounds < 0) {
                li.previous();
                break;
            }
        }

        return (QueryTokens.Condition[]) tokens.toArray(new QueryTokens.Condition[tokens.size()]);
    }

    private static void doEnsureReferences(QuerySpecification qs) throws IOException {
        Hashtable tables = new Hashtable();

        QueryTokens._TableReference[] fromClause = qs.getFromClause();
        for (int i = 0; i < fromClause.length; i++) {
            QueryTokens._TableReference token = fromClause[i];
            if (token instanceof QueryTokens.Join) {
                tables.put(stripQuote(((QueryTokens.Join) token).getPrimary().getTable().getReference()),
                        ((QueryTokens.Join) token).getPrimary().getTable());
                tables.put(stripQuote(((QueryTokens.Join) token).getForeign().getTable().getReference()),
                        ((QueryTokens.Join) token).getForeign().getTable());
            } else {
                tables.put(stripQuote(((QueryTokens.Table) token).getReference()), token);
            }
        }

        QueryTokens._Expression[] selectList = qs.getSelectList();
        for (int i = 0; i < selectList.length; i++) {
            String e = selectList[i].toString();
            qs.removeSelectList(selectList[i]);

            StreamTokenizer stream = createTokenizer(new StringReader(e));
            for (ArrayList al = new ArrayList(); true;) {
                stream.nextToken();
                if (stream.ttype == StreamTokenizer.TT_EOF) {
                    ListIterator li = al.listIterator();

                    String ref = li.next().toString();
                    String alias = null;

                    while (li.hasNext()) {
                        String next = li.next().toString();

                        if (next.toString().equals(String.valueOf(SQLFormatter.DOT))
                                || ref.endsWith(String.valueOf(SQLFormatter.DOT)))
                            ref = ref + next;
                        else
                            alias = next;
                    }

                    ref = stripQuote(ref);
                    int dot = ref.lastIndexOf(SQLFormatter.DOT);
                    if (dot != -1) {
                        String owner = ref.substring(0, dot);
                        String cname = ref.substring(dot + 1);

                        if (tables.containsKey(owner)) {
                            selectList[i] = new QueryTokens.Column((QueryTokens.Table) tables.get(owner), cname);
                            if (alias != null)
                                ((QueryTokens.Column) selectList[i]).setAlias(alias);
                        }
                    }
                    break;
                } else {
                    if (stream.sval == null && (char) stream.ttype != SQLFormatter.DOT)
                        break;
                    al.add(stream.sval == null ? String.valueOf(SQLFormatter.DOT) : stream.sval);
                }
            }

            qs.addSelectList(selectList[i]);
        }
    }

    private static boolean isOperator(String s) {
        return isOperatorSimbol(s) || s.equalsIgnoreCase("IS") || s.equalsIgnoreCase("IS NOT")
                || s.equalsIgnoreCase("IN") || s.equalsIgnoreCase("NOT IN") || s.equalsIgnoreCase("LIKE")
                || s.equalsIgnoreCase("NOT LIKE") || s.equalsIgnoreCase("EXISTS") || s.equalsIgnoreCase("NOT EXISTS");
    }

    private static boolean isOperatorSimbol(String s) {
        return s.equals("<") || s.equals(">") || s.equals("=") || s.equals("<=") || s.equals(">=") || s.equals("<>");
    }

    private static boolean isReservedWord(String s) {
        return isClauseWord(s) || isJoinWord(s) || s.equals(_ReservedWords.ON) || s.equals(_ReservedWords.AND)
                || s.equals(_ReservedWords.OR);
    }

    private static boolean isJoinWord(String s) {
        return s.equalsIgnoreCase(_ReservedWords.INNER_JOIN) || s.equalsIgnoreCase(_ReservedWords.FULL_OUTER_JOIN)
                || s.equalsIgnoreCase(_ReservedWords.LEFT_OUTER_JOIN)
                || s.equalsIgnoreCase(_ReservedWords.RIGHT_OUTER_JOIN);
    }

    private static boolean isClauseWord(String s) {
        return s.equalsIgnoreCase(_ReservedWords.SELECT) || s.equalsIgnoreCase(_ReservedWords.FROM)
                || s.equalsIgnoreCase(_ReservedWords.WHERE) || s.equalsIgnoreCase(_ReservedWords.GROUP_BY)
                || s.equalsIgnoreCase(_ReservedWords.HAVING) || s.equalsIgnoreCase(_ReservedWords.UNION)
                || s.equalsIgnoreCase(_ReservedWords.ORDER_BY);
    }

    private static void doAdjustSequence(ArrayList al) {
        for (int i = 0; i < al.size(); i++) {
            if (al.get(i).toString().equalsIgnoreCase(_ReservedWords.SELECT)
                    || al.get(i).toString().equalsIgnoreCase(_ReservedWords.FROM)
                    || al.get(i).toString().equalsIgnoreCase(_ReservedWords.HAVING)) {
                al.set(i, al.get(i).toString().toUpperCase());
            } else if (al.get(i).toString().equalsIgnoreCase("BY")) {
                al.set(i - 1, al.get(i - 1).toString().toUpperCase() + SQLFormatter.SPACE + "BY");
                al.remove(i--);
            } else if (al.get(i).toString().equalsIgnoreCase("JOIN")) {
                if (al.get(i - 1).toString().equalsIgnoreCase("INNER")) {
                    al.set(i - 1, al.get(i - 1).toString().toUpperCase() + SQLFormatter.SPACE + "JOIN");
                    al.remove(i--);
                } else if (al.get(i - 1).toString().equalsIgnoreCase("OUTER")) {
                    al.set(i - 2, al.get(i - 2).toString().toUpperCase() + SQLFormatter.SPACE + "OUTER"
                            + SQLFormatter.SPACE + "JOIN");
                    al.remove(i--);
                    al.remove(i--);
                }
            } else if (al.get(i).toString().equalsIgnoreCase("NOT")) {
                if (al.get(i - 1).toString().equalsIgnoreCase("IS")) {
                    al.set(i - 1, "IS NOT");
                    al.remove(i--);
                } else if (al.get(i + 1).toString().equalsIgnoreCase("IN")
                        || al.get(i + 1).toString().equalsIgnoreCase("LIKE")
                        || al.get(i + 1).toString().equalsIgnoreCase("EXISTS")) {
                    al.set(i, "NOT" + SQLFormatter.SPACE + al.get(i + 1).toString().toUpperCase());
                    al.remove(i + 1);
                }
            } else if (al.get(i).toString().equals("=")) {
                if (al.get(i - 1).toString().equals("<") || al.get(i - 1).toString().equals(">")) {
                    al.set(i - 1, al.get(i - 1).toString() + "=");
                    al.remove(i--);
                }
            } else if (al.get(i).toString().equals(">") && al.get(i - 1).toString().equals("<")) {
                al.set(i - 1, "<>");
                al.remove(i--);
            } else if (al.get(i).toString().equals(".")) {
                al.set(i, al.get(i - 1).toString() + SQLFormatter.DOT + al.get(i + 1).toString());
                al.remove(i - 1);
                al.remove(i--);
            }
        }
    }

    private static ArrayList doTokenize(Reader r) throws IOException {
        ArrayList al = new ArrayList();
        StreamTokenizer stream = createTokenizer(r);

        while (stream.ttype != StreamTokenizer.TT_EOF) {
            stream.nextToken();
            if (stream.ttype == StreamTokenizer.TT_WORD) {
                al.add(stream.sval);
            } else if (stream.ttype == StreamTokenizer.TT_NUMBER) {
                Double dval = new Double(stream.nval);
                al.add(dval.doubleValue() == dval.intValue() ? new Integer((int) stream.nval) : (Number) dval);
            } else if (stream.ttype != StreamTokenizer.TT_EOF) {
                if (stream.sval == null) {
                    al.add(new Character((char) stream.ttype));
                } else {
                    al.add((char) stream.ttype + stream.sval + (char) stream.ttype);
                }
            }
        }

        if (al.size() > 0 && !al.get(al.size() - 1).toString().equals(";"))
            al.add(new Character(';'));
        return al;
    }

    private static String stripQuote(String s) {
        if (s.startsWith(QueryBuilder.identifierQuoteString))
            s = s.substring(1);
        if (s.endsWith(QueryBuilder.identifierQuoteString))
            s = s.substring(0, s.length() - 1);

        for (int i = s.indexOf(QueryBuilder.identifierQuoteString); i != -1; i = s
                .indexOf(QueryBuilder.identifierQuoteString)) {
            String l = s.substring(0, i);
            String r = s.substring(i + 1);

            s = l + r;
        }
        return s;
    }

    private static StreamTokenizer createTokenizer(Reader r) {
        StreamTokenizer stream = new StreamTokenizer(r);
        stream.ordinaryChar('.');
        stream.wordChars('_', '_');

        if (!QueryBuilder.identifierQuoteString.equals("\"")) {
            stream.quoteChar(QueryBuilder.identifierQuoteString.charAt(0));

            // for(int i=0; i<QueryBuilder.identifierQuoteString.length(); i++)
            // {
            // char wc = QueryBuilder.identifierQuoteString.charAt(i);
            // stream.wordChars(wc,wc);
            // }
        }

        stream.slashSlashComments(true);
        stream.slashStarComments(true);

        return stream;
    }
    /*
     * private static void print(ArrayList al) { print(al.toArray()); }
     * 
     * private static void print(Object[] o) { for(int i=0; i<o.length; i++)
     * System.out.println(o[i].getClass().getName() + "[ " + o[i].toString() + "
     * ]");
     * 
     * System.out.println("----------------------------------------------------------------------"); }
     * 
     * public static void main(String[] args) { QueryBuilder.useAlwaysQuote =
     * false;
     * 
     * try { // String fname = "c:\\temp\\test.sql"; // QueryModel qm =
     * toQueryModel(new java.io.FileReader(fname)); //
     * QueryBuilder.identifierQuoteString = new String("`"); // String sql =
     * "SELECT `nome tabella`.`primo campo`, `nome tabella`.`secondo campo` FROM
     * `nome tabella`"; // String sql = "SELECT \"nome tabella\".\"primo
     * campo\", \"nome tabella\".\"secondo campo\" FROM \"nome tabella\"";
     * String sql = "SELECT aa.idSoggetto AS
     * aa_idSoggetto,bb.idCliente,bb.cCognome,bb.cNome FROM aa aa INNER JOIN bb
     * bb ON aa.idSoggetto = bb.idSoggetto WHERE (aa.cPartitaIva = '' OR
     * aa.cPartitaIva IS null) AND (aa.cCodiceFiscale = '' OR aa.cCodiceFiscale
     * IS null) AND bb.idCliente NOT IN (select idcliente from cc)"; QueryModel
     * qm = toQueryModel(sql);
     * 
     * System.out.println(qm.toString(true)); } catch(Exception e) {
     * e.printStackTrace(); } }
     */
}