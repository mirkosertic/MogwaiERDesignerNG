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
package de.erdesignerng.test.dialect;

import java.sql.Types;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import de.erdesignerng.dialect.DataType;
import de.erdesignerng.dialect.Dialect;
import de.erdesignerng.dialect.DialectFactory;
import de.erdesignerng.test.BaseERDesignerTestCaseImpl;

public class DialectTest extends BaseERDesignerTestCaseImpl {

    public void testDialectConvertable() {
        DialectFactory theFactory = DialectFactory.getInstance();
        List<Dialect> theDialects = theFactory.getSupportedDialects();
        for (Dialect theDialect : theDialects) {
            for (Dialect theOtherDialect : theDialects) {
                if (!theOtherDialect.getUniqueName().equals(theDialect.getUniqueName())) {
                    for (DataType theDataType : theDialect.getDataTypes()) {
                        if (!ArrayUtils.contains(theDataType.getJDBCType(), Types.OTHER)) {
                            DataType theOtherDataType = theOtherDialect.findClosestMatchingTypeFor(theDataType);
                            if (theOtherDataType == null) {
                                throw new RuntimeException(theDialect + " No Matching type for " + theDataType + " in "
                                        + theOtherDialect + " JDBC-Type : "
                                        + ArrayUtils.toString(theDataType.getJDBCType()));
                            }
                        }
                    }
                }
            }
        }
    }
}
