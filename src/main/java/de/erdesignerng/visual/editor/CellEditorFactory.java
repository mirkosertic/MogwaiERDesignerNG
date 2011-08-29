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
package de.erdesignerng.visual.editor;

import de.erdesignerng.model.Comment;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.SubjectArea;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.View;
import de.erdesignerng.visual.cells.CommentCell;
import de.erdesignerng.visual.cells.RelationEdge;
import de.erdesignerng.visual.cells.SubjectAreaCell;
import de.erdesignerng.visual.cells.TableCell;
import de.erdesignerng.visual.cells.ViewCell;
import de.erdesignerng.visual.editor.comment.CommentEditor;
import de.erdesignerng.visual.editor.relation.RelationEditor;
import de.erdesignerng.visual.editor.subjectarea.SubjectAreaEditor;
import de.erdesignerng.visual.editor.table.TableEditor;
import de.erdesignerng.visual.editor.view.ViewEditor;
import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphCellEditor;

import javax.swing.*;
import java.awt.*;
import java.util.EventObject;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:30 $
 */
public class CellEditorFactory extends DefaultGraphCellEditor {

    protected BaseEditor createEditDialogForValue(JComponent aParent, Object aValue) {

        if (aValue instanceof TableCell) {
            TableCell theCell = (TableCell) aValue;

            Table theTable = (Table) theCell.getUserObject();
            TableEditor theEditor = new TableEditor(theTable.getOwner(), aParent);
            theEditor.initializeFor(theTable);
            return theEditor;
        }

        if (aValue instanceof ViewCell) {
            ViewCell theCell = (ViewCell) aValue;

            View theTable = (View) theCell.getUserObject();
            ViewEditor theEditor = new ViewEditor(theTable.getOwner(), aParent);
            theEditor.initializeFor(theTable);
            return theEditor;
        }

        if (aValue instanceof SubjectAreaCell) {
            SubjectAreaCell theCell = (SubjectAreaCell) aValue;

            SubjectArea theSubjectArea = (SubjectArea) theCell.getUserObject();
            SubjectAreaEditor theEditor = new SubjectAreaEditor(aParent);
            theEditor.initializeFor(theSubjectArea);
            return theEditor;
        }

        if (aValue instanceof CommentCell) {
            CommentCell theCell = (CommentCell) aValue;

            Comment theComment = (Comment) theCell.getUserObject();
            CommentEditor theEditor = new CommentEditor(theComment.getOwner(), aParent);
            theEditor.initializeFor(theComment);
            return theEditor;
        }

        if (aValue instanceof RelationEdge) {
            RelationEdge theCell = (RelationEdge) aValue;

            Relation theRelation = (Relation) theCell.getUserObject();

            RelationEditor theEditor = new RelationEditor(theRelation.getOwner(), aParent);
            theEditor.initializeFor(theRelation);

            return theEditor;
        }

        throw new IllegalArgumentException("Cannot create editor for " + aValue.getClass());
    }

    @Override
    public Component getGraphCellEditorComponent(JGraph aGraph, Object aValue, boolean arg2) {

        BaseEditor theEditor = createEditDialogForValue(aGraph, aValue);
        theEditor.validate();

        return theEditor;
    }

    @Override
    public boolean isCellEditable(EventObject aEvent) {
        return true;
    }
}
