package de.mogwai.erdesignerng.visual;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;

import de.mogwai.erdesignerng.model.Table;

public class TableCell extends DefaultGraphCell {
	
	public TableCell(Table aTable) {
		super(aTable);
		
		GraphConstants.setBounds(getAttributes(),
				new Rectangle2D.Double(20, 20, 40, 20));
		GraphConstants.setGradientColor(getAttributes(), Color.orange);
		GraphConstants.setOpaque(getAttributes(), true);
		GraphConstants.setAutoSize(getAttributes(), true);
		GraphConstants.setEditable(getAttributes(), false);
		DefaultPort port0 = new DefaultPort();
		add(port0);
	}
}
