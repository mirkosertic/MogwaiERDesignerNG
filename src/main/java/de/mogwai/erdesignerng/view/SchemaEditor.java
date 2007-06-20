package de.mogwai.erdesignerng.view;

import java.util.HashMap;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.mogwai.erdesignerng.exception.ElementAlreadyExistsException;
import de.mogwai.erdesignerng.exception.ElementInvalidNameException;
import de.mogwai.erdesignerng.model.Attribute;
import de.mogwai.erdesignerng.model.Domain;
import de.mogwai.erdesignerng.model.EmptyModelHistory;
import de.mogwai.erdesignerng.model.Model;
import de.mogwai.erdesignerng.model.Relation;
import de.mogwai.erdesignerng.model.Table;
import de.mogwai.erdesignerng.util.dialect.oracle.OracleDialect;

public class SchemaEditor extends LightweightSystem {
	
	protected Model model;
	protected Figure rootFigure = new Figure();
	protected HashMap<String, TableFigure> tableFigureMap = new HashMap<String, TableFigure>();
	
	public SchemaEditor(Canvas aCanvas) {
		super(aCanvas);
		
		rootFigure.setLayoutManager(new XYLayout());
		setContents(rootFigure);
	}
	
	protected void updateChildren() {
		
		// Add every table not added to the model
		for(Table theTable : model.getTables()) {
			if (!tableFigureMap.containsKey(theTable.getSystemId())) {
				TableFigure theFigure = new TableFigure();
				
				tableFigureMap.put(theTable.getSystemId(), theFigure);
				theFigure.setModel(theTable);
				
				rootFigure.add(theFigure);
				
				int x=theTable.getIntProperty(Table.PROPERTY_XLOCATION, 100);
				int y=theTable.getIntProperty(Table.PROPERTY_YLOCATION, 100);
				
				rootFigure.setConstraint(theFigure, new Rectangle(x,y,-1,-1));
				
			} else {
				TableFigure theTableFigure = tableFigureMap.get(theTable.getSystemId());
				theTableFigure.updateChildren();
			}
		}
		
		// Remove tables not in the model but there as figure
		for (String theKey : tableFigureMap.keySet()) {
			if (model.getTables().findTableBySystemId(theKey)==null) {
				rootFigure.remove(tableFigureMap.get(theKey));
			}
		}
	}
	
	public void setModel(Model aModel) {
		model = aModel;
		
		updateChildren();
	}

	public static void main(String args[]) throws ElementAlreadyExistsException, ElementInvalidNameException {
		Display d = new Display();
		final Shell shell = new Shell(d);
		shell.setSize(400, 400);
		shell.setText("UMLClassFigure Test");
		
		Model theModel = new Model();
		theModel.setModelHistory(new EmptyModelHistory());
		theModel.setModelProperties(new OracleDialect());

		Domain theDomain = new Domain();
		theDomain.setName("DOMAIN1");
		theModel.addDomain(theDomain);

		Table theTable1 = new Table();
		theTable1.setName("TABLE1");

		for (int i = 0; i < 5; i++) {
			Attribute theAttribute = new Attribute();
			theAttribute.setName("a1_" + i);
			theAttribute.setDefinition(theDomain, true);

			theTable1.addAttribute(theAttribute);
		}

		theModel.addTable(theTable1);

		Table theTable2 = new Table();
		theTable2.setName("TABLE2");

		for (int i = 0; i < 5; i++) {
			Attribute theAttribute = new Attribute();
			theAttribute.setName("a2_" + i);
			theAttribute.setDefinition(theDomain, true);

			theTable2.addAttribute(theAttribute);
		}

		theModel.addTable(theTable2);

		Relation theRelation = new Relation();
		theRelation.setName("REL1");
		theRelation.setStart(theTable1);
		theRelation.setEnd(theTable2);
		theRelation.getMapping().put(theTable1.getAttributes().get(0),
				theTable2.getAttributes().get(0));

		theModel.addRelation(theRelation);
		
		SchemaEditor theEditor = new SchemaEditor(shell);
		theEditor.setModel(theModel);
		shell.open();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch())
				d.sleep();
	 	}		
	}
}
