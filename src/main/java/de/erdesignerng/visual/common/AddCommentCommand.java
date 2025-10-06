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
import de.erdesignerng.visual.editor.DialogConstants;
import de.erdesignerng.visual.editor.comment.CommentEditor;

import java.awt.geom.Point2D;

public class AddCommentCommand extends UICommand {

    private final Point2D location;

    public AddCommentCommand(final Point2D aLocation) {
        location = aLocation;
    }

    @Override
    public void execute() {

        final ERDesignerComponent theComponent = ERDesignerComponent.getDefault();

        final Comment theComment = new Comment();
        final CommentEditor theEditor = new CommentEditor(theComponent.getModel(), getDetailComponent());
        theEditor.initializeFor(theComment);
        if (theEditor.showModal() == DialogConstants.MODAL_RESULT_OK) {
            try {

                theEditor.applyValues();

                theComponent.commandCreateComment(theComment, location);

                refreshDisplayAndOutline();

            } catch (final Exception e) {
                getWorldConnector().notifyAboutException(e);
            }
        }
    }
}