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
package de.erdesignerng.visual;

import de.erdesignerng.model.*;
import de.erdesignerng.visual.editor.BaseEditor;
import de.erdesignerng.visual.editor.comment.CommentEditor;
import de.erdesignerng.visual.editor.relation.RelationEditor;
import de.erdesignerng.visual.editor.subjectarea.SubjectAreaEditor;
import de.erdesignerng.visual.editor.table.TableEditor;
import de.erdesignerng.visual.editor.view.ViewEditor;
import javax.swing.JComponent;

public class EditorFactory {

    private EditorFactory() {
    }

    public static BaseEditor createEditorFor(ModelItem aItem, JComponent aParent) {
        if (aItem instanceof Table) {
            Table theTable = (Table) aItem;
            TableEditor theEditor = new TableEditor(theTable.getOwner(), aParent);
            theEditor.initializeFor(theTable);
            return theEditor;
        }

        if (aItem instanceof View) {
            View theTable = (View) aItem;
            ViewEditor theEditor = new ViewEditor(theTable.getOwner(), aParent);
            theEditor.initializeFor(theTable);
            return theEditor;
        }

        if (aItem instanceof SubjectArea) {
            SubjectArea theSubjectArea = (SubjectArea) aItem;
            SubjectAreaEditor theEditor = new SubjectAreaEditor(aParent);
            theEditor.initializeFor(theSubjectArea);
            return theEditor;
        }

        if (aItem instanceof Comment) {
            Comment theComment = (Comment) aItem;
            CommentEditor theEditor = new CommentEditor(theComment.getOwner(), aParent);
            theEditor.initializeFor(theComment);
            return theEditor;
        }
        if (aItem instanceof Relation) {
            Relation theRelation = (Relation) aItem;

            RelationEditor theEditor = new RelationEditor(theRelation.getOwner(), aParent);
            theEditor.initializeFor(theRelation);

            return theEditor;
        }
        throw new IllegalArgumentException("Cannot create editor for " + aItem);
    }
}
