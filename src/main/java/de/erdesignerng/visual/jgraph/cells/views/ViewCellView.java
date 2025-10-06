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

import de.erdesignerng.model.ModelItem;
import de.erdesignerng.model.View;
import de.erdesignerng.model.ViewAttribute;
import de.erdesignerng.visual.DisplayOrder;
import de.erdesignerng.visual.jgraph.CellEditorFactory;
import de.erdesignerng.visual.jgraph.ERDesignerGraph;
import de.erdesignerng.visual.jgraph.cells.ViewCell;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ReverseComparator;
import org.apache.commons.lang.StringUtils;
import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.GraphCellEditor;
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 */
public class ViewCellView extends VertexView {

	private static final Comparator NAME_COMPARATOR = new BeanComparator("name", String.CASE_INSENSITIVE_ORDER);

	private static final Comparator REVERSE_NAME_COMPARATOR = new ReverseComparator(NAME_COMPARATOR);

	private static final MyRenderer RENDERER = new MyRenderer();

	public ViewCellView(final ViewCell aCell) {
		super(aCell);
	}

	@Override
	public CellViewRenderer getRenderer() {
		return RENDERER;
	}

	public static class MyRenderer extends VertexRenderer implements CellViewRenderer, Serializable {

		private View view;

		private boolean selected;

		private boolean includeComments;

		private DisplayOrder displayOrder;

		public MyRenderer() {
			setBackground(Color.white);
		}

		private void fillRect(final Graphics aGraphics, final int aX1, final int aY1, final int aWidth, final int aHeight) {

			aGraphics.fillRect(aX1, aY1, aWidth, aHeight);
		}

		private void drawRect(final Graphics aGraphics, final int aY1, final int aWidth, final int aHeight) {

			aGraphics.drawRect(0, aY1, aWidth, aHeight);
		}

		protected String getConvertedName(final ModelItem aItem) {
			String theText = aItem.getUniqueName();
			if (includeComments) {
				if (StringUtils.isNotEmpty(aItem.getComment())) {
					theText += " (" + aItem.getComment() + ")";
				}
			}
			return theText;
		}

		@Override
		public void paint(final Graphics aGraphics) {

			final Dimension theSize = getSize();
			final int theWidth = theSize.width;
			final int theHeight = theSize.height;

			aGraphics.setFont(getFont());
			aGraphics.setColor(getBackground());

			final FontMetrics theMetrics = aGraphics.getFontMetrics();

			aGraphics.setColor(Color.black);
			String theString = getConvertedName(view);

			aGraphics.drawString(theString, 0, theMetrics.getAscent());

			int theYOffset = theMetrics.getHeight();

			aGraphics.setColor(selected ? Color.blue : Color.black);

			fillRect(aGraphics, 5, theYOffset + 5, theWidth - 5, theHeight - theYOffset - 5);

			aGraphics.setColor(new Color(212, 255, 255));

			fillRect(aGraphics, 0, theYOffset, theWidth - 5, theHeight - theYOffset - 6);

			aGraphics.setColor(selected ? Color.blue : Color.black);

			drawRect(aGraphics, theYOffset, theWidth - 5, theHeight - theYOffset - 6);

			final int theTextXOffset = 15;

			final List<ViewAttribute> theTempList = new ArrayList<>();
			theTempList.addAll(view.getAttributes());

			switch (displayOrder) {
				case NATURAL:
					break;
				case ASCENDING:
					theTempList.sort(NAME_COMPARATOR);
					break;
				case DESCENDING:
					theTempList.sort(REVERSE_NAME_COMPARATOR);
					break;
				default:
					throw new IllegalStateException("Unknown display order");
			}

			final List<ViewAttribute> theAllAttributes = new ArrayList<>();
			theAllAttributes.addAll(theTempList);

			// Only do the following if there are any not primary key
			// attributes
			if (!theAllAttributes.isEmpty()) {

				// Draw the attributes
				for (final ViewAttribute theAttribute : theAllAttributes) {

					theString = getConvertedName(theAttribute);

					aGraphics.setColor(Color.black);

					aGraphics.drawString(theString, theTextXOffset, theYOffset + theMetrics.getAscent());
					theYOffset += theMetrics.getHeight();
				}
			}
		}

		@Override
		public Dimension getPreferredSize() {

			int theMaxX = 150;
			int theMaxY = 8;

			final FontMetrics theMetrics = getFontMetrics(getFont());

			int theYOffset = theMetrics.getHeight();
			final int theXTextOffset = 30;

			final String theString = getConvertedName(view);

			int theLength = theMetrics.stringWidth(theString);
			if (theLength > theMaxX) {
				theMaxX = theLength + 5;
			}

			final List<ViewAttribute> theAllAttributes = new ArrayList<>();
			theAllAttributes.addAll(view.getAttributes());

			for (final ViewAttribute theAttribute : theAllAttributes) {

				final String theText = getConvertedName(theAttribute);

				theLength = theMetrics.stringWidth(theText);
				if (theLength + theXTextOffset > theMaxX) {
					theMaxX = theLength + theXTextOffset;
				}

				theYOffset += theMetrics.getHeight();
			}

			theYOffset += 8;
			theMaxX += 8;

			final Insets theInsets = getInsets();
			theMaxX += theInsets.left + theInsets.right;
			theMaxY += theInsets.top + theInsets.bottom;

			if (theYOffset > theMaxY) {
				theMaxY = theYOffset;
			}

			return new Dimension(theMaxX, theMaxY);
		}

		@Override
		public Component getRendererComponent(final JGraph aGraph, final CellView aView, final boolean aSelected, final boolean aHasFocus,
                                              final boolean aPreview) {

			final ViewCellView theView = (ViewCellView) aView;
			view = (View) ((ViewCell) theView.getCell()).getUserObject();
			selected = aSelected;

			final ERDesignerGraph theGraph = (ERDesignerGraph) aGraph;
			displayOrder = theGraph.getDisplayOrder();
			includeComments = theGraph.isDisplayComments();

			return this;
		}

		public JComponent getRendererComponent(final View aView) {
			view = aView;
			selected = false;

			displayOrder = DisplayOrder.NATURAL;
			includeComments = false;

			return this;
		}
	}

	@Override
	public GraphCellEditor getEditor() {
		return new CellEditorFactory();
	}
}