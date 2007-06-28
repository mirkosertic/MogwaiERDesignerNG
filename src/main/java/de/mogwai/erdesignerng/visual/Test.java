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

public class Test {
	public static void main(String[] args) {
		GraphModel model = new DefaultGraphModel();
		GraphLayoutCache view = new GraphLayoutCache(model,
				new DefaultCellViewFactory());
		view.setFactory(new CellViewFactory());
		
		JGraph graph = new JGraph(model, view);
		DefaultGraphCell[] cells = new DefaultGraphCell[3];
		
		cells[0] = new TableCell("Lala");
		
		cells[1] = new TableCell("Lulu");

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