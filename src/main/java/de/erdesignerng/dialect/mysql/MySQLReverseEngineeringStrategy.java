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
package de.erdesignerng.dialect.mysql;

import de.erdesignerng.dialect.JDBCReverseEngineeringStrategy;
import de.erdesignerng.model.Table;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-02-01 21:05:35 $
 */
public class MySQLReverseEngineeringStrategy extends JDBCReverseEngineeringStrategy<MySQLDialect> {

    public MySQLReverseEngineeringStrategy(MySQLDialect aDialect) {
        super(aDialect);
    }
    
    @Override
    protected String convertIndexNameFor(Table aTable, String aIndexName) {
        if ("PRIMARY".equals(aIndexName)) {
            return "PK_" + aTable.getName();
        }
        return super.convertIndexNameFor(aTable, aIndexName);
    }
}