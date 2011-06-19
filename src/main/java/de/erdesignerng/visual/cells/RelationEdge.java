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

/**
 * changelog:
 *
 * 2008-11-13:
 *   Besides "textoffset" and "points" the storage of "labelposition" became
 *   necessary due to a change of the usage of the "textoffset" value in the
 *   jgraph-library. The problem occurred because of a bugfix introduced in
 *   JGraph v5.12.1.0 on 08-MAY-2008. See http://www.jgraph.com/jgraphlog.html
 *   or http://www.jgraph.com/tracker/bug.php?op=show&bugid=67 for more details.
 */
package de.erdesignerng.visual.cells;

import de.erdesignerng.model.Relation;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.GraphConstants;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008/06/13 16:48:59 $
 */
public class RelationEdge extends DefaultEdge implements ModelCell<Relation> {

	public RelationEdge(Relation aRelation, TableCell aImporting, TableCell aExporting) {

		super(aRelation);

		GraphConstants.setLineStyle(getAttributes(), GraphConstants.STYLE_ORTHOGONAL);
		GraphConstants.setConnectable(getAttributes(), false);
		GraphConstants.setDisconnectable(getAttributes(), false);
		GraphConstants.setLineBegin(getAttributes(), GraphConstants.ARROW_DIAMOND);
		GraphConstants.setLineEnd(getAttributes(), GraphConstants.ARROW_LINE);
		GraphConstants.setBendable(getAttributes(), true);

		setSource(aImporting.getChildAt(0));
		setTarget(aExporting.getChildAt(0));
	}

	@Override
	public void transferAttributesToProperties(Map aAttributes) {
		Relation theRelation = (Relation) getUserObject();
		String theLocation;

		// PROPERTY_TEXT_OFFSET
		Point2D theOffset = GraphConstants.getOffset(aAttributes);
		if (theOffset != null) {
			theLocation = ((int) theOffset.getX()) + ":" + ((int) theOffset.getY());
			theRelation.getProperties().setProperty(Relation.PROPERTY_TEXT_OFFSET, theLocation);
		}

		// PROPERTY_LABEL_POSITION
		Point2D theLabelPosition = GraphConstants.getLabelPosition(aAttributes);
		if (theLabelPosition != null) {
			theLocation = ((int) theLabelPosition.getX()) + ":" + ((int) theLabelPosition.getY());
			theRelation.getProperties().setProperty(Relation.PROPERTY_LABEL_POSITION, theLocation);
		}

		// PROPERTY_POINTS
		List<Point2D> thePoints = GraphConstants.getPoints(aAttributes);
		if (thePoints != null) {
			StringBuilder theBuffer = new StringBuilder();

			for (Point2D thePoint : thePoints) {
				if (theBuffer.length() > 0) {
					theBuffer.append(",");
				}

				theBuffer.append((int) thePoint.getX());
				theBuffer.append(":");
				theBuffer.append((int) thePoint.getY());
			}

			String thePointBuffer = theBuffer.toString();
			theRelation.getProperties().setProperty(Relation.PROPERTY_POINTS, thePointBuffer);
		}
	}

	@Override
	public void transferPropertiesToAttributes(Relation aRelation) {
		Point2D thePoint;

		// PROPERTY_TEXT_OFFSET
		// skip processing of the offset-property, instead remove it from
		// relation to be compatible to earlier versions
		thePoint = TransferHelper.createPoint2DFromString(aRelation.getProperties().getProperty(
				Relation.PROPERTY_TEXT_OFFSET));
		if (thePoint != null) {
			GraphConstants.setOffset(getAttributes(), thePoint);
		}

		// PROPERTY_LABEL_POSITION)
		// instead of storing the offset, store the location of the label now
		thePoint = TransferHelper.createPoint2DFromString(aRelation.getProperties().getProperty(
				Relation.PROPERTY_LABEL_POSITION));
		if (thePoint != null) {
			GraphConstants.setLabelPosition(getAttributes(), thePoint);
		}

		// PROPERTY_POINTS
		String thePoints = aRelation.getProperties().getProperty(Relation.PROPERTY_POINTS);
		if (thePoints != null) {
			List<Point2D> thePointList = new ArrayList<Point2D>();

			for (StringTokenizer theSt = new StringTokenizer(thePoints, ","); theSt.hasMoreTokens();) {
				thePoint = TransferHelper.createPoint2DFromString(theSt.nextToken());
				thePointList.add(thePoint);
			}

			GraphConstants.setPoints(getAttributes(), thePointList);
		}
	}
}