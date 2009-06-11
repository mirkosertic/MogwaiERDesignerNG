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

import de.erdesignerng.exception.CannotDeleteException;
import de.erdesignerng.exception.ElementAlreadyExistsException;
import de.erdesignerng.exception.ElementInvalidNameException;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:29 $
 */
public class View extends OwnedModelItem<Model> implements OwnedModelItemVerifier {
    
    private String sql;
    
    private ViewAttributeList attributes = new ViewAttributeList();
    
    private String schema;

    /**
     * {@inheritDoc}
     */
    public void checkNameAlreadyExists(ModelItem aSender, String aName) throws ElementAlreadyExistsException {
    }

    /**
     * {@inheritDoc}
     */
    public void delete(ModelItem aSender) throws CannotDeleteException {
    }

    /**
     * {@inheritDoc}
     */
    public String checkName(String aName) throws ElementInvalidNameException {
        Model theOwner = getOwner();
        if (theOwner != null) {
            return theOwner.checkName(aName);
        }

        return aName;
    }

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
     * Setzt den Wert des Attributs <code>attributes</code>.
     * 
     * @param attributes Wert für das Attribut attributes.
     */
    public void setAttributes(ViewAttributeList attributes) {
        this.attributes = attributes;
    }

    /**
     * Test if the view was modified(compare it with another view). 
     * 
     * @param aView the view to test against with
     * @return true if it was modified, else false
     */
    public boolean isModified(View aView) {
        if (!sql.equals(aView.getSql())) {
            return true;
        }
        return false;
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
}