package de.mogwai.erdesignerng.visual;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;

public class TableCell extends DefaultGraphCell {
	
	public TableCell(String aMessage) {
		super(aMessage);
		
		GraphConstants.setBounds(getAttributes(),
				new Rectangle2D.Double(20, 20, 40, 20));
		GraphConstants.setGradientColor(getAttributes(), Color.orange);
		GraphConstants.setOpaque(getAttributes(), true);
		GraphConstants.setSizeable(getAttributes(), false);
		GraphConstants.setEditable(getAttributes(), false);
		DefaultPort port0 = new DefaultPort();
		add(port0);
	}
}
