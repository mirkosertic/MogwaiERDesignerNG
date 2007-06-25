package de.mogwai.erdesignerng.view;

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
import de.mogwai.erdesignerng.view.editpart.EditPartViewer;
import de.mogwai.erdesignerng.view.editpart.TableEditPart;

public class SchemaEditor extends EditPartViewer<Model> {

	public SchemaEditor(Canvas aCanvas) {
		super(aCanvas);
	}

	public void setModel(Model aModel) {

		super.setModel(aModel);

		// Add every table not added to the model
		for (Table theTable : aModel.getTables()) {

			TableEditPart theEditPart = new TableEditPart();
			theEditPart.setModel(theTable);

			addEditPart(theEditPart);
		}
	}

	public static void main(String[] args)
			throws ElementAlreadyExistsException, ElementInvalidNameException {
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
