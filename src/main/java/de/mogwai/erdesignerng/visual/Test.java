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
package de.mogwai.erdesignerng.visual;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;

import de.mogwai.erdesignerng.exception.ElementAlreadyExistsException;
import de.mogwai.erdesignerng.exception.ElementInvalidNameException;
import de.mogwai.erdesignerng.model.Attribute;
import de.mogwai.erdesignerng.model.Domain;
import de.mogwai.erdesignerng.model.Index;
import de.mogwai.erdesignerng.model.IndexType;
import de.mogwai.erdesignerng.model.Model;
import de.mogwai.erdesignerng.model.Table;
import de.mogwai.erdesignerng.util.dialect.mysql.MySQLDialect;

public class Test {
	public static void main(String[] args) throws ElementAlreadyExistsException, ElementInvalidNameException {
		
		Model theModel = new Model();
		theModel.setDialect(new MySQLDialect());

		Domain theDomain = new Domain();
		theDomain.setName("DOMAIN1");
		theModel.addDomain(theDomain);

		Table theTable1 = new Table();
		theTable1.setName("TABLE1");
		
		Index thePK = new Index();
		thePK.setName("TABLE2_P1");
		thePK.setIndexType(IndexType.PRIMARYKEY);

		for (int i = 0; i < 5; i++) {
			Attribute theAttribute = new Attribute();
			theAttribute.setName("a1_" + i);
			theAttribute.setDefinition(theDomain, true, null);

			theTable1.addAttribute(theModel, theAttribute);
			
			if (i<2) {
				thePK.getAttributes().add(theAttribute);
			}
		}
		
		theTable1.addIndex(theModel, thePK);

		theModel.addTable(theTable1);

		Table theTable2 = new Table();
		theTable2.setName("TABLE2");

		for (int i = 0; i < 5; i++) {
			Attribute theAttribute = new Attribute();
			theAttribute.setName("a1_" + i);
			theAttribute.setDefinition(theDomain, true, null);

			theTable2.addAttribute(theModel, theAttribute);
		}

		theModel.addTable(theTable2);
		
		
		GraphModel theGraphModel = new DefaultGraphModel();
		GraphLayoutCache theView = new GraphLayoutCache(theGraphModel,
				new DefaultCellViewFactory());
		theView.setFactory(new CellViewFactory());
		
		JGraph theGraph = new JGraph(theGraphModel, theView);
		DefaultGraphCell[] theCells = new DefaultGraphCell[3];
		
		theCells[0] = new TableCell(theTable1);
		
		theCells[1] = new TableCell(theTable2);

		theCells[2] = new RelationCell((TableCell)theCells[0],(TableCell)theCells[1]);
		
		theGraph.getGraphLayoutCache().insert(theCells[0]);
		theGraph.getGraphLayoutCache().insert(theCells[1]);
		theGraph.getGraphLayoutCache().insert(theCells[2]);		
		
		JFrame frame = new JFrame();
		frame.getContentPane().add(new JScrollPane(theGraph));
		frame.pack();
		frame.setVisible(true);
	}
}