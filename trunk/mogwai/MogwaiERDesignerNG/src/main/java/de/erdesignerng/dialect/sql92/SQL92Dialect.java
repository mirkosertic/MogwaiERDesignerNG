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
package de.erdesignerng.dialect.sql92;

import de.erdesignerng.dialect.Dialect;
import de.erdesignerng.model.Attribute;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-17 19:34:29 $
 */
public abstract class SQL92Dialect extends Dialect {

    protected SQL92Dialect(String aDatatypeKonfigFilename) {
        super(aDatatypeKonfigFilename);
    }

    protected void addAttributeDefinition(StringBuffer aBuffer, Attribute aAttribute) {

        aBuffer.append(aAttribute.getName() + " " + aAttribute.getDomain().getDatatype());
        if (!aAttribute.isNullable()) {
            aBuffer.append(" NOT NULL");
        }

    }
}