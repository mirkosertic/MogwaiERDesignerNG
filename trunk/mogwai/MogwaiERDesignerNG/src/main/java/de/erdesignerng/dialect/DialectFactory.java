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
package de.erdesignerng.dialect;

import de.erdesignerng.dialect.db2.DB2Dialect;
import de.erdesignerng.dialect.h2.H2Dialect;
import de.erdesignerng.dialect.hsqldb.HSQLDBDialect;
import de.erdesignerng.dialect.msaccess.MSAccessDialect;
import de.erdesignerng.dialect.mssql.MSSQLDialect;
import de.erdesignerng.dialect.mysql.MySQLDialect;
import de.erdesignerng.dialect.mysql.MySQLInnoDBDialect;
import de.erdesignerng.dialect.oracle.OracleDialect;
import de.erdesignerng.dialect.postgres.PostgresDialect;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang.SystemUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-11-15 14:21:15 $
 */
public final class DialectFactory {

    private static DialectFactory me;

    private final Map<String, Dialect> knownDialects = new HashMap<>();

    private DialectFactory() {
    }

    public static synchronized DialectFactory getInstance() {
        if (me == null) {
            me = new DialectFactory();
            me.registerDialect(new DB2Dialect());
            me.registerDialect(new MSSQLDialect());
            me.registerDialect(new MySQLDialect());
            me.registerDialect(new MySQLInnoDBDialect());
            me.registerDialect(new OracleDialect());
            me.registerDialect(new PostgresDialect());
            me.registerDialect(new H2Dialect());
            me.registerDialect(new HSQLDBDialect());

            // provide MSAccessDialect only on Windows-Systems due to the
            // requirement of the JET/ACE-Engine
            if (SystemUtils.IS_OS_WINDOWS) {
                me.registerDialect(new MSAccessDialect());
            }
        }

        return me;
    }

    private void registerDialect(Dialect aDialect) {
        knownDialects.put(aDialect.getUniqueName(), aDialect);
    }

    public Dialect getDialect(String aUniqueName) {
        return knownDialects.get(aUniqueName);
    }

    public List<Dialect> getSupportedDialects() {
        List<Dialect> theDialects = new ArrayList<>();
        theDialects.addAll(knownDialects.values());
        Collections.sort(theDialects, new BeanComparator("uniqueName", String.CASE_INSENSITIVE_ORDER));
        return theDialects;
    }
}
