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
package de.erdesignerng.visual.jgraph;

import de.erdesignerng.model.*;
import de.erdesignerng.visual.EditorFactory;
import de.erdesignerng.visual.editor.BaseEditor;
import de.erdesignerng.visual.jgraph.cells.*;
import java.awt.Component;
import java.util.EventObject;
import javax.swing.JComponent;
import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphCellEditor;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:30 $
 */
public class CellEditorFactory extends DefaultGraphCellEditor {

    protected BaseEditor createEditDialogForValue(final JComponent aParent, final Object aValue) {

        if (aValue instanceof TableCell) {
            final TableCell theCell = (TableCell) aValue;

            final Table theTable = (Table) theCell.getUserObject();
            return EditorFactory.createEditorFor(theTable, aParent);
        }

        if (aValue instanceof ViewCell) {
            final ViewCell theCell = (ViewCell) aValue;

            final View theTable = (View) theCell.getUserObject();
            return EditorFactory.createEditorFor(theTable, aParent);
        }

        if (aValue instanceof SubjectAreaCell) {
            final SubjectAreaCell theCell = (SubjectAreaCell) aValue;

            final SubjectArea theSubjectArea = (SubjectArea) theCell.getUserObject();
            return EditorFactory.createEditorFor(theSubjectArea, aParent);
        }

        if (aValue instanceof CommentCell) {
            final CommentCell theCell = (CommentCell) aValue;

            final Comment theComment = (Comment) theCell.getUserObject();
            return EditorFactory.createEditorFor(theComment, aParent);
        }

        if (aValue instanceof RelationEdge) {
            final RelationEdge theCell = (RelationEdge) aValue;

            final Relation theRelation = (Relation) theCell.getUserObject();

            return EditorFactory.createEditorFor(theRelation, aParent);
        }

        throw new IllegalArgumentException("Cannot create editor for " + aValue);
    }

    @Override
    public Component getGraphCellEditorComponent(final JGraph aGraph, final Object aValue, final boolean arg2) {

        final BaseEditor theEditor = createEditDialogForValue(aGraph, aValue);
        theEditor.validate();

        return theEditor;
    }

    @Override
    public boolean isCellEditable(final EventObject aEvent) {
        return true;
    }
}
