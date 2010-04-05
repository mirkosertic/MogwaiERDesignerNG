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
package de.erdesignerng.model;

import de.erdesignerng.dialect.DataType;
import java.util.ArrayList;

/**
 * A custom datatype.
 *
 * @author $Author: dr-death $
 * @version $Date: 2010-03-30 20:00:00 $
 */
public class CustomType extends OwnedModelItem<Model> implements ModelItemClonable<CustomType>, DataType {
    //TODO [dr-death] currently CustomTypes only supports types consisting of a
    //String-Enum (CREATE TYPE %1 AS ENUM ('Label1', 'Label2', ...) or
    //enhancing an baic datatype.
    //Complex UDTs are currently not supported.

    private int[] jdbcType;

    private String schema;

    private DataType concreteType;

    private ArrayList<String> labelList = new ArrayList<String>();

    public CustomType(String aSchema, int... aJdbcType) {
        this.schema = aSchema;
        this.jdbcType = aJdbcType;
    }

    public void setConcreteType(DataType concreteType) {
        this.concreteType = concreteType;
    }

    public void setLabelList(ArrayList<String> labelList) {
        this.labelList = labelList;
    }

    public String getSchema() {
        return schema;
    }

    public DataType getConcreteType() {
        return concreteType;
    }

    public ArrayList<String> getLabelList() {
        return labelList;
    }

    @Override
    public CustomType clone() {
        CustomType theCustomType = new CustomType(schema, jdbcType);
        theCustomType.setSystemId(getSystemId());
        theCustomType.setName(getName());
        theCustomType.setLabelList(labelList);
        return theCustomType;
    }

    public void restoreFrom(CustomType aCustomType) throws Exception {
        setSystemId(aCustomType.getSystemId());
        setName(aCustomType.getName());
        setLabelList(aCustomType.getLabelList());
    }

    public boolean isDomain() {
        return false;
    }

    public boolean isCustomType() {
        return true;
    }

    public boolean supportsSize() {
        return false;
    }

    public boolean supportsFraction() {
        return false;
    }

    public boolean supportsScale() {
        return false;
    }

    public boolean supportsExtra() {
        return false;
    }

    public boolean isJDBCStringType() {
        return false; 
    }

    public String createTypeDefinitionFor(Attribute aAttribute) {
        return getName();
    }

    public boolean isIdentity() {
        return false;
    }

    public int[] getJDBCType() {
        return jdbcType;
    }

    public String getDefinition() {
        return "";
    }
}