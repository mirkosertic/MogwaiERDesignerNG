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

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphCell;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;

import de.erdesignerng.model.Model;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.Table;
import de.erdesignerng.visual.cells.RelationEdge;
import de.erdesignerng.visual.cells.TableCell;
import de.erdesignerng.visual.cells.views.RelationEdgeView;
import de.erdesignerng.visual.tools.BaseTool;

/**
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-08 22:13:39 $
 */
public class ERDesignerGraph extends JGraph {

	private Model model;
	
	private boolean domainDisplayMode;

	public ERDesignerGraph(Model aDBModel, GraphModel aModel, GraphLayoutCache aLayoutCache) {
		super(aModel, aLayoutCache);
		// setPortsVisible(true);
		model = aDBModel;
	}
	
	/**
	 * @return the model
	 */
	public Model getDBModel() {
		return model;
	}

	public void setTool(BaseTool aTool) {
		setMarqueeHandler(aTool);
	}

	public void commandDeleteCell(GraphCell aCell) {
		
		GraphModel theModel = getModel();
		
		if (aCell instanceof RelationEdge) {
			RelationEdge theEdge = (RelationEdge)aCell;
			
			theModel.remove(new Object[] {theEdge});

			getDBModel().removeRelation((Relation)theEdge.getUserObject());
			
		}
		if (aCell instanceof TableCell) {
			TableCell theCell = (TableCell) aCell;
			Table theTable = (Table) theCell.getUserObject();
			
			List theObjectsToRemove = new ArrayList();
			theObjectsToRemove.add(theCell);
			
			getDBModel().removeTable(theTable);
			
			CellView[] theViews = getGraphLayoutCache().getAllViews();
			for(CellView theView : theViews) {
				if (theView instanceof RelationEdgeView) {
					RelationEdgeView theRelationView = (RelationEdgeView)theView;
					RelationEdge theEdge = (RelationEdge) theRelationView.getCell();
					TableCell theSource = (TableCell) ((DefaultPort) theEdge.getSource()).getParent();
					TableCell theDestination = (TableCell) ((DefaultPort) theEdge.getTarget()).getParent();
					
					if (theTable.equals(theSource.getUserObject())) {
						getDBModel().removeRelation((Relation) theEdge.getUserObject());
						theObjectsToRemove.add(theEdge);
					} else {
						if (theTable.equals(theDestination.getUserObject())) {
							getDBModel().removeRelation((Relation) theEdge.getUserObject());
							theObjectsToRemove.add(theEdge);
						}
					}
				}
			}
			
			theModel.remove(theObjectsToRemove.toArray());
		}

	}

	public void commandNewTable(Point2D aPoint) {
	}

	/**
	 * @return the domainDisplayMode
	 */
	public boolean isDomainDisplayMode() {
		return domainDisplayMode;
	}

	/**
	 * @param domainDisplayMode the domainDisplayMode to set
	 */
	public void setDomainDisplayMode(boolean domainDisplayMode) {
		this.domainDisplayMode = domainDisplayMode;
	}
}
