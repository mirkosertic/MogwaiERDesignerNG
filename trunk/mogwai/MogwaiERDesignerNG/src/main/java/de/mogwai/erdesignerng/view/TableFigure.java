package de.mogwai.erdesignerng.view;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;

import de.mogwai.erdesignerng.model.Table;

public class TableFigure extends Label {
	
	protected Table model;

	public void updateChildren() {
		setText(model.getName());
	}
	
	public void setModel(Table aModel) {
		model = aModel;
		updateChildren();
	}
}
