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

import de.erdesignerng.model.*;
import de.erdesignerng.visual.DisplayLevel;
import de.erdesignerng.visual.DisplayOrder;
import de.erdesignerng.visual.IconFactory;
import de.erdesignerng.visual.jgraph.CellEditorFactory;
import de.erdesignerng.visual.jgraph.ERDesignerGraph;
import de.erdesignerng.visual.jgraph.cells.TableCell;
import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ReverseComparator;
import org.apache.commons.lang.StringUtils;
import org.jgraph.JGraph;
import org.jgraph.graph.*;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:30 $
 */
public class TableCellView extends VertexView {

	private static final Comparator NAME_COMPARATOR = new BeanComparator("name", String.CASE_INSENSITIVE_ORDER);

	private static final Comparator REVERSE_NAME_COMPARATOR = new ReverseComparator(NAME_COMPARATOR);

	private static final MyRenderer RENDERER = new MyRenderer();

	public TableCellView(TableCell aCell) {
		super(aCell);
	}

	@Override
	public CellViewRenderer getRenderer() {
		return RENDERER;
	}

	public static class MyRenderer extends VertexRenderer implements CellViewRenderer, Serializable {

		private Table table;

		private boolean selected;

		private boolean includeComments;

		private DisplayLevel displayLevel;

		private DisplayOrder displayOrder;

		private static final ImageIcon key = IconFactory.getKeyIcon();

		public MyRenderer() {
			setBackground(Color.white);
		}

		private void fillRect(Graphics aGraphics, int aX1, int aY1, int aWidth, int aHeight) {
			aGraphics.fillRect(aX1, aY1, aWidth, aHeight);
		}

		private void drawRect(Graphics aGraphics, int aY1, int aWidth, int aHeight) {
			aGraphics.drawRect(0, aY1, aWidth, aHeight);
		}

		protected String getConvertedName(ModelItem aItem) {
			String theText = aItem.getUniqueName();
			if (includeComments) {
				if (StringUtils.isNotEmpty(aItem.getComment())) {
					theText += " (" + aItem.getComment() + ")";
				}
			}
			return theText;
		}

		@Override
		public void paint(Graphics aGraphics) {

			Dimension theSize = getSize();
			int theWidth = theSize.width;
			int theHeight = theSize.height;

			aGraphics.setFont(getFont());
			aGraphics.setColor(getBackground());
			// aGraphics.fillRect(0, 0, theWidth, theHeight);

			FontMetrics theMetrics = aGraphics.getFontMetrics();

			aGraphics.setColor(Color.black);
			String theString = getConvertedName(table);

			aGraphics.drawString(theString, 0, theMetrics.getAscent());

			int theYOffset = theMetrics.getHeight();

			aGraphics.setColor(selected ? Color.blue : Color.black);

			fillRect(aGraphics, 5, theYOffset + 5, theWidth - 5, theHeight - theYOffset - 5);

			aGraphics.setColor(new Color(255, 255, 212));

			fillRect(aGraphics, 0, theYOffset, theWidth - 5, theHeight - theYOffset - 6);

			aGraphics.setColor(selected ? Color.blue : Color.black);

			drawRect(aGraphics, theYOffset, theWidth - 5, theHeight - theYOffset - 6);

			int theTextXOffset = 15;

			List<Attribute<Table>> theTempList = new ArrayList<>();
			theTempList.addAll(table.getAttributes());

			switch (displayOrder) {
				case NATURAL:
					break;
				case ASCENDING:
					Collections.sort(theTempList, NAME_COMPARATOR);
					break;
				case DESCENDING:
					Collections.sort(theTempList, REVERSE_NAME_COMPARATOR);
					break;
				default:
					throw new IllegalStateException("Unknown display order");
			}

			List<Attribute<Table>> theAllAttributes = new ArrayList<>();
			theAllAttributes.addAll(theTempList);

			boolean hasPrimaryKey = false;

			// Draw the attributes
			for (Attribute<Table> theAttribute : theTempList) {

				if (theAttribute.isPrimaryKey()) {

					hasPrimaryKey = true;

					theAllAttributes.remove(theAttribute);

					aGraphics.setColor(Color.red);

					theString = getConvertedName(theAttribute);

					theString += " : ";

					theString += theAttribute.getLogicalDeclaration();

					if (theAttribute.isForeignKey()) {
						theString += " (FK)";
					}

					aGraphics.drawString(theString, theTextXOffset, theYOffset + theMetrics.getAscent());
					key.paintIcon(this, aGraphics, 5, theYOffset + 4);
					theYOffset += theMetrics.getHeight();
				}
			}

			if (DisplayLevel.ALL == displayLevel || (DisplayLevel.PRIMARYKEYSANDFOREIGNKEYS == displayLevel)) {

				// Only do the following if there are any not primary key
				// attributes
				if (theAllAttributes.size() > 0) {

					// This line is only necessary in case that there are PK
					// attributes
					if (hasPrimaryKey) {

						// Draw the border line
						aGraphics.setColor(Color.black);
						aGraphics.drawLine(0, theYOffset, theWidth - 5, theYOffset);
					}

					// Draw the attributes
					for (Attribute<Table> theAttribute : theAllAttributes) {

						if (DisplayLevel.ALL == displayLevel || (theAttribute.isForeignKey())) {
							boolean isFK = theAttribute.isForeignKey();

							theString = getConvertedName(theAttribute);

							theString += " : ";

							theString += theAttribute.getLogicalDeclaration();

							if (isFK) {
								theString += " (FK)";
							}

							aGraphics.setColor(isFK ? Color.red : Color.black);

							aGraphics.drawString(theString, theTextXOffset, theYOffset + theMetrics.getAscent());
							theYOffset += theMetrics.getHeight();
						}
					}
				}
			}

			if (table.getIndexes().size() > 0 && DisplayLevel.ALL == displayLevel) {
				boolean lineDrawn = false;
				for (Index theIndex : table.getIndexes()) {
					if (theIndex.getIndexType() != IndexType.PRIMARYKEY) {
						if (!lineDrawn) {
							aGraphics.setColor(Color.black);
							aGraphics.drawLine(0, theYOffset, theWidth - 5, theYOffset);
							lineDrawn = true;
						}
						String theName = getConvertedName(theIndex);

						aGraphics.setColor(Color.black);
						aGraphics.drawString(theName, theTextXOffset, theYOffset + theMetrics.getAscent());
						aGraphics.setColor(Color.black);
						theYOffset += theMetrics.getHeight();

						for (IndexExpression theExpression : theIndex.getExpressions()) {
							String theExpressionText = theExpression.toString();
							aGraphics.drawString(theExpressionText, theTextXOffset + 5, theYOffset + theMetrics.getAscent());

							theYOffset += theMetrics.getHeight();
						}
					}
				}
			}
		}

		@Override
		public Dimension getPreferredSize() {

			int theMaxX = 150;
			int theMaxY = 8;

			FontMetrics theMetrics = getFontMetrics(getFont());

			int theYOffset = theMetrics.getHeight();
			int theXTextOffset = 30;

			String theString = getConvertedName(table);

			int theLength = theMetrics.stringWidth(theString);
			if (theLength > theMaxX) {
				theMaxX = theLength + 5;
			}

			List<Attribute<Table>> theAllAttributes = new ArrayList<>();
			theAllAttributes.addAll(table.getAttributes());

			for (Attribute<Table> theAttribute : table.getAttributes()) {

				if (theAttribute.isPrimaryKey()) {

					theAllAttributes.remove(theAttribute);

					String theText = getConvertedName(theAttribute);
					theText += " : ";

					theText += theAttribute.getLogicalDeclaration();

					// Assume the text is a foreign key...
					theText += " (FK)";

					theLength = theMetrics.stringWidth(theText);
					if (theLength + theXTextOffset > theMaxX) {
						theMaxX = theLength + theXTextOffset;
					}

					theYOffset += theMetrics.getHeight();
				}
			}

			if (DisplayLevel.ALL == displayLevel || (DisplayLevel.PRIMARYKEYSANDFOREIGNKEYS == displayLevel)) {
				for (Attribute<Table> theAttribute : theAllAttributes) {

					if (DisplayLevel.ALL == displayLevel || (theAttribute.isForeignKey())) {

						String theText = getConvertedName(theAttribute);
						theText += " : ";

						theText += theAttribute.getLogicalDeclaration();

						theText += " (FK)";

						theLength = theMetrics.stringWidth(theText);
						if (theLength + theXTextOffset > theMaxX) {
							theMaxX = theLength + theXTextOffset;
						}

						theYOffset += theMetrics.getHeight();
					}
				}
			}

			if (table.getIndexes().size() > 0 && DisplayLevel.ALL == displayLevel) {
				for (Index theIndex : table.getIndexes()) {
					if (theIndex.getIndexType() != IndexType.PRIMARYKEY) {
						String theName = getConvertedName(theIndex);

						theLength = theMetrics.stringWidth(theName);
						if (theLength + theXTextOffset > theMaxX) {
							theMaxX = theLength + theXTextOffset;
						}

						theYOffset += theMetrics.getHeight();

						for (IndexExpression theExpression : theIndex.getExpressions()) {
							String theExpressionText = theExpression.toString();

							theLength = theMetrics.stringWidth(theExpressionText);
							if (theLength + theXTextOffset + 5 > theMaxX) {
								theMaxX = theLength + theXTextOffset + 5;
							}

							theYOffset += theMetrics.getHeight();
						}
					}
				}
			}

			theYOffset += 8;
			theMaxX += 8;

			Insets theInsets = getInsets();
			theMaxX += theInsets.left + theInsets.right;
			theMaxY += theInsets.top + theInsets.bottom;

			if (theYOffset > theMaxY) {
				theMaxY = theYOffset;
			}

			return new Dimension(theMaxX, theMaxY);
		}

		@Override
		public Component getRendererComponent(JGraph aGraph, CellView aView, boolean aSelected, boolean aHasFocus,
											  boolean aPreview) {

			TableCellView theView = (TableCellView) aView;
			table = (Table) ((TableCell) theView.getCell()).getUserObject();
			selected = aSelected;

			ERDesignerGraph theGraph = (ERDesignerGraph) aGraph;
			includeComments = theGraph.isDisplayComments();
			displayLevel = theGraph.getDisplayLevel();
			displayOrder = theGraph.getDisplayOrder();

			return this;
		}

		public JComponent getRendererComponent(Table aTable) {

			table = aTable;
			selected = false;

			includeComments = false;
			displayLevel = DisplayLevel.ALL;
			displayOrder = DisplayOrder.NATURAL;

			return this;
		}
	}

	@Override
	public GraphCellEditor getEditor() {
		return new CellEditorFactory();
	}
}