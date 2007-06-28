package de.mogwai.erdesignerng.visual;

import java.awt.Component;
import java.io.Serializable;

import javax.swing.JLabel;

import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.VertexView;

public class TableCellView extends VertexView {
	
	protected static MyRenderer renderer = new MyRenderer();
	
	public TableCellView(TableCell aCell) {
		super(aCell);
	}

	public CellViewRenderer getRenderer() {
		return renderer;
	}

	public static class MyRenderer extends JLabel implements CellViewRenderer,
			Serializable {
		
		public Component getRendererComponent(JGraph graph, CellView view,
				boolean sel, boolean focus, boolean preview) {
			
			TableCellView theView = (TableCellView) view;
			setText(""+((TableCell)theView.getCell()).getUserObject());
			return this;
		}
	}
}