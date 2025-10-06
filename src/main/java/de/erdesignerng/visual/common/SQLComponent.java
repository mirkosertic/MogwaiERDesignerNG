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
package de.erdesignerng.visual.common;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.dialect.Dialect;
import de.erdesignerng.dialect.SQLGenerator;
import de.erdesignerng.dialect.Statement;
import de.erdesignerng.dialect.StatementList;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.CustomType;
import de.erdesignerng.model.Domain;
import de.erdesignerng.model.Index;
import de.erdesignerng.model.IndexType;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.ModelItem;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.View;
import de.erdesignerng.visual.components.DefaultEditorPane;
import de.erdesignerng.visual.components.SQLEditorKit;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.DefaultPanel;
import de.mogwai.common.i18n.ResourceHelper;
import de.mogwai.common.i18n.ResourceHelperProvider;
import org.apache.commons.lang.ArrayUtils;

import javax.swing.text.EditorKit;
import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;

public class SQLComponent extends DefaultPanel implements
		ResourceHelperProvider {

	private DefaultEditorPane sql;

	private static SQLComponent DEFAULT;

	public static SQLComponent initializeComponent() {
		if (DEFAULT == null) {
			DEFAULT = new SQLComponent();
		}
		return DEFAULT;
	}

	public static SQLComponent getDefault() {
		initializeComponent();
		return DEFAULT;
	}

	private SQLComponent() {
		initialize();
	}

	private void initialize() {

		setLayout(new BorderLayout());
		sql = new DefaultEditorPane();
		sql.setEditable(false);

		final EditorKit editorKit = new SQLEditorKit();

		sql.setEditorKitForContentType("text/sql", editorKit);
		sql.setContentType("text/sql");

		add(sql.getScrollPane(), BorderLayout.CENTER);

		UIInitializer.getInstance().initialize(this);
	}

	/**
	 * Reset the SQL display.
	 */
	public void resetDisplay() {
		sql.setText("");
	}

	/**
	 * Display the CREATE SQL Statements for a given set of model items.
	 *
	 * @param aModelItems a set of model items
	 */
	public void displaySQLFor(final ModelItem[] aModelItems) {
		resetDisplay();

		final Model theModel = ERDesignerComponent.getDefault().getModel();
		final Dialect theDialect = theModel.getDialect();
		if (theDialect != null && !ArrayUtils.isEmpty(aModelItems)) {
			final StatementList theStatementList = new StatementList();
			final SQLGenerator theGenerator = theDialect.createSQLGenerator();
			for (final ModelItem aItem : aModelItems) {
				if (aItem instanceof Table) {
					final Table theTable = (Table) aItem;
					theStatementList.addAll(theGenerator
							.createAddTableStatement(theTable));
					for (final Relation theRelation : theModel.getRelations()
							.getForeignKeysFor(theTable)) {
						theStatementList.addAll(theGenerator
								.createAddRelationStatement(theRelation));

					}
				}
				if (aItem instanceof View) {
					theStatementList.addAll(theGenerator
							.createAddViewStatement((View) aItem));
				}
				if (aItem instanceof Relation) {
					theStatementList.addAll(theGenerator
							.createAddRelationStatement((Relation) aItem));
				}
				if (aItem instanceof Attribute) {
					final Attribute theAttribute = (Attribute) aItem;

					final ModelItem theOwner = theAttribute.getOwner();
					if (theOwner instanceof Table) {
						theStatementList.addAll(theGenerator.createAddAttributeToTableStatement((Table) theOwner, theAttribute));
					}
				}
				if (aItem instanceof Index) {
					final Index theIndex = (Index) aItem;
					if (theIndex.getIndexType() == IndexType.PRIMARYKEY) {
						theStatementList.addAll(theGenerator
								.createAddPrimaryKeyToTable(
										theIndex.getOwner(), theIndex));
					} else {
						theStatementList.addAll(theGenerator
								.createAddIndexToTableStatement(theIndex
										.getOwner(), theIndex));
					}
				}
				if (aItem instanceof CustomType) {
					final CustomType theCustomType = (CustomType) aItem;
					theStatementList.addAll(theGenerator
							.createAddCustomTypeStatement(theCustomType));
				}
				if (aItem instanceof Domain) {
					final Domain theDomain = (Domain) aItem;
					theStatementList.addAll(theGenerator
							.createAddDomainStatement(theDomain));
				}
			}

			if (!theStatementList.isEmpty()) {
				final StringWriter theWriter = new StringWriter();
				final PrintWriter thePW = new PrintWriter(theWriter);
				for (final Statement theStatement : theStatementList) {
					thePW.print(theStatement.getSql());
					thePW
							.println(theGenerator
									.createScriptStatementSeparator());
				}
				thePW.flush();
				thePW.close();
				sql.setText(theWriter.toString());
			}
		} else {
			if (theDialect == null) {
				sql.setText(getResourceHelper().getText(
						ERDesignerBundle.PLEASEDEFINEADATABASECONNECTIONFIRST));
			}
		}
	}

	@Override
	public ResourceHelper getResourceHelper() {
		return ResourceHelper.getResourceHelper(ERDesignerBundle.BUNDLE_NAME);
	}
}