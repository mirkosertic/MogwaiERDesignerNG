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
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.io.Serializable;

import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.GraphCellEditor;
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;

import de.erdesignerng.model.SubjectArea;
import de.erdesignerng.visual.cells.SubjectAreaCell;
import de.erdesignerng.visual.editor.CellEditorFactory;

/**
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-06-13 16:48:58 $
 */
public class SubjectAreaCellView extends VertexView {

	private static final MyRenderer renderer = new MyRenderer();

	public SubjectAreaCellView(SubjectAreaCell aCell) {
		super(aCell);
	}

	@Override
	public CellViewRenderer getRenderer() {
		return renderer;
	}

	public static class MyRenderer extends VertexRenderer implements CellViewRenderer, Serializable {

		private SubjectArea subjectArea;

		private boolean selected;

		public MyRenderer() {
			setBackground(Color.white);
		}

		@Override
		public void paint(Graphics aGraphics) {

			Dimension theSize = getSize();
			int theWidth = theSize.width;
			int theHeight = theSize.height;

			aGraphics.setColor(subjectArea.getColor());
			aGraphics.fillRect(0, 0, theWidth - 1, theHeight - 1);

			aGraphics.setColor(selected ? Color.blue : Color.black);
			aGraphics.drawRect(0, 0, theWidth - 1, theHeight - 1);

			aGraphics.setColor(Color.black);

			FontMetrics theMetrics = aGraphics.getFontMetrics();
			int theYOffset = theMetrics.getHeight();

			aGraphics.drawString(subjectArea.getName(), 5, theYOffset);
		}

		@Override
		public Component getRendererComponent(JGraph aGraph, CellView aView, boolean aSelected, boolean aHasFocus,
				boolean aPreview) {

			SubjectAreaCellView theView = (SubjectAreaCellView) aView;
			subjectArea = (SubjectArea) ((SubjectAreaCell) theView.getCell()).getUserObject();
			selected = aSelected;

			return this;
		}
	}

	@Override
	public GraphCellEditor getEditor() {
		return new CellEditorFactory();
	}
}
