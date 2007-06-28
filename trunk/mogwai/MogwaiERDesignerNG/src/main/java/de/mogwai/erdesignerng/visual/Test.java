package de.mogwai.erdesignerng.visual;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.jgraph.JGraph;
import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;

import de.mogwai.erdesignerng.exception.ElementAlreadyExistsException;
import de.mogwai.erdesignerng.exception.ElementInvalidNameException;
import de.mogwai.erdesignerng.model.Attribute;
import de.mogwai.erdesignerng.model.Domain;
import de.mogwai.erdesignerng.model.Model;
import de.mogwai.erdesignerng.model.Table;
import de.mogwai.erdesignerng.util.dialect.oracle.OracleDialect;

public class Test {
	public static void main(String[] args) throws ElementAlreadyExistsException, ElementInvalidNameException {
		
		Model theModel = new Model();
		theModel.setDialect(new OracleDialect());

		Domain theDomain = new Domain();
		theDomain.setName("DOMAIN1");
		theModel.addDomain(theDomain);

		Table theTable1 = new Table();
		theTable1.setName("TABLE1");

		for (int i = 0; i < 5; i++) {
			Attribute theAttribute = new Attribute();
			theAttribute.setName("a1_" + i);
			theAttribute.setDefinition(theDomain, true, null);

			theTable1.addAttribute(theAttribute);
		}

		theModel.addTable(theTable1);
		
		
		GraphModel model = new DefaultGraphModel();
		GraphLayoutCache view = new GraphLayoutCache(model,
				new DefaultCellViewFactory());
		view.setFactory(new CellViewFactory());
		
		JGraph graph = new JGraph(model, view);
		DefaultGraphCell[] cells = new DefaultGraphCell[3];
		
		cells[0] = new TableCell(theTable1);
		
		cells[1] = new TableCell(theTable1);

		RelationCell edge = new RelationCell();
		edge.setSource(cells[0].getChildAt(0));
		edge.setTarget(cells[1].getChildAt(0));
		cells[2] = edge;
		
		graph.getGraphLayoutCache().insert(cells[0]);
		graph.getGraphLayoutCache().insert(cells[1]);
		graph.getGraphLayoutCache().insert(cells[2]);		
		//graph.getGraphLayoutCache().insert(cells);
		
		JFrame frame = new JFrame();
		frame.getContentPane().add(new JScrollPane(graph));
		frame.pack();
		frame.setVisible(true);
	}
}