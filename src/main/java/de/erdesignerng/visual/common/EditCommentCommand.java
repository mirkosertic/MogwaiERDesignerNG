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
package de.erdesignerng.visual.common;

import de.erdesignerng.model.Comment;
import de.erdesignerng.visual.editor.comment.CommentEditor;

public class EditCommentCommand extends UICommand {

    private final Comment comment;
    private final GenericModelEditor editor;

    public EditCommentCommand(final GenericModelEditor aEditor, final Comment aComment) {
        comment = aComment;
        editor = aEditor;
    }

    @Override
    public void execute() {
        final CommentEditor theEditor = new CommentEditor(comment.getOwner(), getDetailComponent());
        theEditor.initializeFor(comment);
        if (theEditor.showModal() == CommentEditor.MODAL_RESULT_OK) {
            try {
                theEditor.applyValues();
                editor.repaintGraph();
            } catch (final Exception e) {
                getWorldConnector().notifyAboutException(e);
            }
        }
    }
}
