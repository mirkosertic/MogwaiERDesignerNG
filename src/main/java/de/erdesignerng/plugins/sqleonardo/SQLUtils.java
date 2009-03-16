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
package de.erdesignerng.plugins.sqleonardo;

import nickyb.sqleonardo.querybuilder.QueryModel;
import nickyb.sqleonardo.querybuilder.syntax.QueryExpression;
import nickyb.sqleonardo.querybuilder.syntax.QuerySpecification;
import nickyb.sqleonardo.querybuilder.syntax.SQLParser;
import nickyb.sqleonardo.querybuilder.syntax.QueryTokens.Column;
import nickyb.sqleonardo.querybuilder.syntax.QueryTokens.DefaultExpression;
import nickyb.sqleonardo.querybuilder.syntax.QueryTokens._Expression;

import org.apache.commons.lang.StringUtils;

import de.erdesignerng.model.View;
import de.erdesignerng.model.ViewAttribute;
import de.erdesignerng.model.ViewAttributeList;

/**
 * Common SQL Utilities. 
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 */
public final class SQLUtils {
    
    private SQLUtils() {
    }
    
    public static void updateViewAttributesFromSQL(View aView, String aStatement) throws Exception {
        
        QueryModel theModel = SQLParser.toQueryModel(aStatement);

        updateViewAttributesFromQueryModel(aView, theModel);
    }
    
    public static void updateViewAttributesFromQueryModel(View aView, QueryModel aQueryModel) throws Exception {
        
        ViewAttributeList theList = aView.getAttributes();
        theList.clear();
        
        QueryExpression theExpression = aQueryModel.getQueryExpression();
        QuerySpecification theSpec = theExpression.getQuerySpecification();
        for (_Expression theSelectExpression : theSpec.getSelectList()) {
            ViewAttribute theAttribute = new ViewAttribute();
            if (theSelectExpression instanceof Column) {
                Column theColumn = (Column) theSelectExpression;
                String theAlias = theColumn.getAlias();
                if (StringUtils.isEmpty(theAlias)) {
                    theAttribute.setName(theColumn.getName());
                } else {
                    theAttribute.setName(theAlias);
                }
            }
            if (theSelectExpression instanceof DefaultExpression) {
                DefaultExpression theDefaultExpression = (DefaultExpression) theSelectExpression;
                theAttribute.setName(theDefaultExpression.getAlias());
            }

            if (StringUtils.isEmpty(theAttribute.getName())) {
                throw new Exception("Not every expression/column has an alias");
            }

            if (theList.elementExists(theAttribute.getName(), false)) {
                throw new Exception("Duplicate name is used : " + theAttribute.getName());
            }
            theList.add(theAttribute);
        }
    }    
}
