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
package de.erdesignerng.visual.scaffolding;

import de.erdesignerng.generator.GeneratorUtils;
import de.erdesignerng.util.XMLUtils;
import org.metawidget.inspector.iface.Inspector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.StringWriter;
import java.sql.ResultSetMetaData;

/**
 * Inspector for ResultsetMetaData.
 */
public class ResultSetMetaDataInspector implements Inspector {

    private String inspectionResult;

    public ResultSetMetaDataInspector(ResultSetMetaData aMetaData) {

        try {
            XMLUtils theUtils = XMLUtils.getInstance();

            Document theDocument = theUtils.newDocument();
            Element theRoot = theDocument.createElement("inspection-result");
            theDocument.appendChild(theRoot);

            Element theEntity = theDocument.createElement("entity");
            theEntity.setAttribute("type","java.util.Map");

            for (int i=1;i<=aMetaData.getColumnCount();i++) {
                String theColumnName = aMetaData.getColumnName(i);

                Element theProperty = theDocument.createElement("property");
                theProperty.setAttribute("name", theColumnName);

                boolean nullable = true;

                if (aMetaData.isNullable(i) == ResultSetMetaData.columnNoNulls) {
                    theProperty.setAttribute("required", "true");
                    nullable = false;
                }

                String theJavaType = GeneratorUtils.findClosestJavaTypeFor(aMetaData.getColumnType(i), nullable);
                if (!theJavaType.contains(".")) {
                    theJavaType = "java.lang." + theJavaType;
                }
                theJavaType = String.class.getName();
                theProperty.setAttribute("type", theJavaType);

                theEntity.appendChild(theProperty);
            }

            theRoot.appendChild(theEntity);

            StringWriter theWriter = new StringWriter();
            theUtils.transform(theDocument, theWriter);

            inspectionResult = theWriter.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String inspect(Object aObject, String s, String[] strings) {
        System.out.println(inspectionResult);
        return inspectionResult;
    }
}