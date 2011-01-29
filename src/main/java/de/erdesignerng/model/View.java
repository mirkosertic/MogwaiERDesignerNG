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

import org.apache.commons.lang.StringUtils;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:29 $
 */
public class View extends OwnedModelItem<Model> {

    private String sql;

    private ViewAttributeList attributes = new ViewAttributeList();

    private String schema;

    /**
     * Gibt den Wert des Attributs <code>sql</code> zurück.
     *
     * @return Wert des Attributs sql.
     */
    public String getSql() {
        return sql;
    }

    /**
     * Setzt den Wert des Attributs <code>sql</code>.
     *
     * @param sql Wert für das Attribut sql.
     */
    public void setSql(String sql) {
        this.sql = sql;
    }

    /**
     * Gibt den Wert des Attributs <code>attributes</code> zurück.
     *
     * @return Wert des Attributs attributes.
     */
    public ViewAttributeList getAttributes() {
        return attributes;
    }

    /**
     * Test if the view was modified(compare it with another view).
     *
     * @param aView the view to test against with
     * @return true if it was modified, else false
     */
    public boolean isModified(View aView) {
        return !StringUtils.equals(sql, aView.getSql());
    }

    /**
     * @return the schema
     */
    public String getSchema() {
        return schema;
    }

    /**
     * @param schema the schema to set
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    @Override
    public String getUniqueName() {
        if (!StringUtils.isEmpty(schema)) {
            return schema + "." + getName();
        }
        return super.getUniqueName();
    }
}