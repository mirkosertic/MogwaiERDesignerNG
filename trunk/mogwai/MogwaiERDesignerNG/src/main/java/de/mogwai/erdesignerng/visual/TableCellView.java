package de.mogwai.erdesignerng.visual;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.io.Serializable;

import javax.swing.JPanel;

import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.VertexView;

import de.mogwai.erdesignerng.model.Attribute;
import de.mogwai.erdesignerng.model.AttributeList;
import de.mogwai.erdesignerng.model.Table;

public class TableCellView extends VertexView {
	
	protected static MyRenderer renderer = new MyRenderer();
	
	public TableCellView(TableCell aCell) {
		super(aCell);
	}

	public CellViewRenderer getRenderer() {
		return renderer;
	}

	public static class MyRenderer extends JPanel implements CellViewRenderer,
			Serializable {
		
		private String name;
		private AttributeList attributeList;
		private boolean roundedRect = false;
		private boolean selected;
		
		public MyRenderer() {
			setBackground(Color.white);
		}
		
		private void fillRect(Graphics g, int x1, int y1, int width, int height) {
			if (this.roundedRect)
				g.fillRoundRect(x1, y1, width, height, 10, 10);
			else
				g.fillRect(x1, y1, width, height);
		}

		private void drawRect(Graphics g, int x1, int y1, int width, int height) {
			if (this.roundedRect)
				g.drawRoundRect(x1, y1, width, height, 10, 10);
			else
				g.drawRect(x1, y1, width, height);
		}
		
		public void paint(Graphics g) {
			
			Dimension size = getSize();
			int width = size.width;
			int height = size.height;
			
			g.setColor(this.getBackground());
			g.fillRect(0, 0, width, height);

			FontMetrics fm = g.getFontMetrics();

			g.setColor(Color.black);
			g.drawString(name, 0, fm.getAscent());

			int starty = fm.getHeight();

			fillRect(g, 5, starty + 5, width - 5, height - starty - 5);

			g.setColor(Color.white);

			fillRect(g, 0, starty, width - 5, height - starty - 6);

			g.setColor(this.selected ? Color.blue : Color.black);

			this.drawRect(g, 0, starty, width - 5, height - starty - 6);

			int textx = 15;
			int pkcount = 0;

			for (Attribute theAttribute : attributeList) {

				g.setColor(Color.red);

				g.drawString(theAttribute.getName(), textx, starty + fm.getAscent());
				starty += fm.getHeight();
				pkcount++;
			}

			// if (pkcount==0)
			// starty+=8;
			
			boolean first = true;

			// Only do the following if there are any not primary key attributes
			if (attributeList.size() > 0) {

				// This line is only neccesary in case that there are PK
				// attributes
				if (first) {

					first = false;
					
					// Draw the border line
					g.setColor(Color.black);
					g.drawLine(0, starty, width - 5, starty);
				}

				// Draw the attributes
				for (Attribute theAttribute : attributeList) {

					g.setColor(Color.black);

					g.drawString(theAttribute.getName(), textx, starty + fm.getAscent());
					starty += fm.getHeight();
				}
			}
			
		}
		
		@Override
		public Dimension getPreferredSize() {
			return new Dimension(150,150);
		}

		public Component getRendererComponent(JGraph graph, CellView view,
				boolean sel, boolean focus, boolean preview) {
			
			TableCellView theView = (TableCellView) view;
			Table theTable = (Table) ((TableCell)theView.getCell()).getUserObject();
			
			name = theTable.getName();
			attributeList = theTable.getAttributes();
			
			return this;
		}
	}
}