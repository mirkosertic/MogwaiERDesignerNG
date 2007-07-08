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
package de.mogwai.erdesignerng.visual;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import de.mogwai.erdesignerng.dialect.mysql.MySQLDialect;
import de.mogwai.erdesignerng.exception.ElementAlreadyExistsException;
import de.mogwai.erdesignerng.exception.ElementInvalidNameException;
import de.mogwai.erdesignerng.model.Attribute;
import de.mogwai.erdesignerng.model.Domain;
import de.mogwai.erdesignerng.model.Model;
import de.mogwai.erdesignerng.model.Relation;
import de.mogwai.erdesignerng.model.Table;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2007-07-08 10:29:47 $
 */
public final class ERDesigner {

	private ERDesigner() {
	}

	public static void main(String[] args)
			throws ElementAlreadyExistsException, ElementInvalidNameException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, UnsupportedLookAndFeelException {

		Model theModel = new Model();
		theModel.setDialect(new MySQLDialect());

		Domain theDomain = new Domain();
		theDomain.setName("DOMAIN1");
		theModel.addDomain(theDomain);

		Table theTable1 = new Table();
		theTable1.setName("TABLE1");

		for (int i = 0; i < 5; i++) {
			Attribute theAttribute = new Attribute();
			theAttribute.setName("a1_" + i);
			theAttribute.setDefinition(theDomain, true, null);

			theTable1.addAttribute(theModel, theAttribute);
		}

		theModel.addTable(theTable1);

		Table theTable2 = new Table();
		theTable2.setName("TABLE2");

		for (int i = 0; i < 5; i++) {
			Attribute theAttribute = new Attribute();
			theAttribute.setName("a1_" + i);
			theAttribute.setDefinition(theDomain, true, null);

			theTable2.addAttribute(theModel, theAttribute);
		}

		theModel.addTable(theTable2);

		Relation theRelation = new Relation();
		theRelation.setName("REL_1");
		theRelation.setExportingTable(theTable1);
		theRelation.setImportingTable(theTable2);

		theModel.addRelation(theRelation);

		UIManager
				.setLookAndFeel("com.incors.plaf.kunststoff.KunststoffLookAndFeel");

		ERDesignerMainFrame frame = new ERDesignerMainFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setModel(theModel);
		frame.setExtendedState(ERDesignerMainFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);
	}
}
