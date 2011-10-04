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
package de.erdesignerng.model.check;

import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Index;
import de.erdesignerng.model.IndexExpression;
import de.erdesignerng.model.IndexExpressionList;
import de.erdesignerng.model.IndexType;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Table;

/**
 * Check for spatial indexes without spatial datatype).
 */
public class SpatialIndexWithoutSpatialType implements ModelCheck {

    @Override
    public void check(Model aModel, ModelChecker aChecker) {
        for (Table theEntity : aModel.getTables()) {
            Index theIndex = theEntity.getPrimarykey();
            if (theIndex != null) {
                if (theIndex.getIndexType() == IndexType.SPATIAL) {
                    boolean hasSpatialType = false;
                    IndexExpressionList theExpressions = theIndex.getExpressions();
                    for (IndexExpression theExpression : theExpressions) {
                        Attribute<Table> theAttribute = theExpression.getAttributeRef();
                        if (theAttribute != null) {
                            if (theAttribute.getDatatype().isSpatial()) {
                                hasSpatialType = true;
                            }
                        }
                    }
                    if (!hasSpatialType) {
                        aChecker.addError(new ModelError("Spatial Index " + theEntity.getName() + "." + theIndex.getName() + " does not point to a spacial attribute"));
                    }
                }
            }
        }
    }
}
