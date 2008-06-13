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
package de.erdesignerng.visual.cells.views;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.io.Serializable;

import javax.swing.JTextArea;

import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.GraphCellEditor;
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;

import de.erdesignerng.model.Comment;
import de.erdesignerng.visual.cells.CommentCell;
import de.erdesignerng.visual.editor.CellEditorFactory;

/**
 * View for comment cells.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-06-13 16:48:58 $
 */
public class CommentCellView extends VertexView {

    private static MyRenderer renderer = new MyRenderer();

    public CommentCellView(CommentCell aCell) {
        super(aCell);
    }

    @Override
    public CellViewRenderer getRenderer() {
        return renderer;
    }

    public static class MyRenderer extends VertexRenderer implements CellViewRenderer, Serializable {

        private JTextArea textarea = new JTextArea();
        
        private boolean selected;
        
        public MyRenderer() {
            textarea.setFont(textarea.getFont().deriveFont(Font.BOLD));
            textarea.setOpaque(false);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public Dimension getPreferredSize() {
            Dimension theSize = textarea.getPreferredSize();
            
            return new Dimension(theSize.width + 20, theSize.height + 20);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void paint(Graphics aGraphics) {
            Dimension theSize = getSize();
            
            aGraphics.setColor(selected ? Color.blue : Color.gray);
            aGraphics.drawRoundRect(0, 0, theSize.width - 1, theSize.height - 1, 10, 10);
            
            textarea.setSize(new Dimension(theSize.width - 20, theSize.height - 20));
            aGraphics.translate(10, 10);
            textarea.paint(aGraphics);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Component getRendererComponent(JGraph aGraph, CellView aView, boolean aSelected, boolean aHasFocus,
                boolean aPreview) {

            CommentCellView theView = (CommentCellView) aView;
            Comment theComment = (Comment) ((CommentCell) theView.getCell()).getUserObject();
            textarea.setText(theComment.getComment());
            selected = aSelected;
            
            return this;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraphCellEditor getEditor() {
        return new CellEditorFactory();
    }
}
