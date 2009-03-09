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

import java.awt.Color;

/**
 * A subject area.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:29 $
 */
public class SubjectArea extends ModelItem {
    
    private Color color;
    
    private TableList tables = new TableList();
    
    private ViewList views = new ViewList();
    
    private CommentList comments = new CommentList();
    
    public SubjectArea() {
        setName("Subject Area");
        setColor(Color.lightGray);
    }

    /**
     * @return the color
     */
    public Color getColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * @return the tables
     */
    public TableList getTables() {
        return tables;
    }

    /**
     * @param tables the tables to set
     */
    public void setTables(TableList tables) {
        this.tables = tables;
    }

    /**
     * Gibt den Wert des Attributs <code>comments</code> zurück.
     * 
     * @return Wert des Attributs comments.
     */
    public CommentList getComments() {
        return comments;
    }

    /**
     * Setzt den Wert des Attributs <code>comments</code>.
     * 
     * @param comments Wert für das Attribut comments.
     */
    public void setComments(CommentList comments) {
        this.comments = comments;
    }
    
    /**
     * Test, if this subject area contains any item. 
     * 
     * @return true if it is empty, else false
     */
    public boolean isEmpty() {
        return (comments.size() == 0) && (tables.size() == 0) && (views.size() == 0);
    }

    /**
     * Gibt den Wert des Attributs <code>views</code> zurück.
     * 
     * @return Wert des Attributs views.
     */
    public ViewList getViews() {
        return views;
    }

    /**
     * Setzt den Wert des Attributs <code>views</code>.
     * 
     * @param views Wert für das Attribut views.
     */
    public void setViews(ViewList views) {
        this.views = views;
    }
}