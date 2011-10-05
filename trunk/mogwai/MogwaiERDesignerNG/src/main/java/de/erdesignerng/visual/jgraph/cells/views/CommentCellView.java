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
package de.erdesignerng.visual.jgraph.cells.views;

import de.erdesignerng.model.Comment;
import de.erdesignerng.visual.jgraph.CellEditorFactory;
import de.erdesignerng.visual.jgraph.cells.CommentCell;
import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.GraphCellEditor;
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

/**
 * View for comment cells.
 *
 * @author $Author: mirkosertic $
 * @version $Date: 2008-11-02 10:05:43 $
 */
public class CommentCellView extends VertexView {

    private static final MyRenderer RENDERER = new MyRenderer();

    public CommentCellView(CommentCell aCell) {
        super(aCell);
    }

    @Override
    public CellViewRenderer getRenderer() {
        return RENDERER;
    }

    public static class MyRenderer extends VertexRenderer implements CellViewRenderer, Serializable {

        private static final Dimension NULL_DIMENSION = new Dimension(0, 0);

        private final JTextArea textarea = new JTextArea();

        private boolean selected;

        private boolean visible;

        public MyRenderer() {
            textarea.setFont(textarea.getFont().deriveFont(Font.BOLD));
            textarea.setOpaque(false);
        }

        @Override
        public Dimension getPreferredSize() {
            if (visible) {
                Dimension theSize = textarea.getPreferredSize();

                return new Dimension(theSize.width + 20, theSize.height + 20);
            }
            return NULL_DIMENSION;
        }

        @Override
        public void paint(Graphics aGraphics) {

            if (visible) {
                Dimension theSize = getSize();

                aGraphics.setColor(selected ? Color.blue : Color.gray);
                aGraphics.drawRoundRect(0, 0, theSize.width - 1, theSize.height - 1, 10, 10);

                textarea.setSize(new Dimension(theSize.width - 20, theSize.height - 20));
                aGraphics.translate(10, 10);
                textarea.paint(aGraphics);
            }
        }

        @Override
        public Component getRendererComponent(JGraph aGraph, CellView aView, boolean aSelected, boolean aHasFocus,
                                              boolean aPreview) {

            CommentCellView theView = (CommentCellView) aView;
            Comment theComment = (Comment) ((CommentCell) theView.getCell()).getUserObject();
            textarea.setText(theComment.getComment());
            selected = aSelected;
            visible = true;

            return this;
        }
    }

    @Override
    public GraphCellEditor getEditor() {
        return new CellEditorFactory();
    }
}
