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
package de.erdesignerng.test.comparator;

import java.io.IOException;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.erdesignerng.model.Model;
import de.erdesignerng.model.ModelIOUtilities;
import de.erdesignerng.model.utils.ModelComparator;
import de.erdesignerng.model.utils.ModelCompareResult;
import de.erdesignerng.test.BaseERDesignerTestCaseImpl;

public class ModelComparatorTest extends BaseERDesignerTestCaseImpl {

	protected void compare(DefaultMutableTreeNode aNode1, DefaultMutableTreeNode aNode2, boolean aCompareUserObjects) {
		if (aCompareUserObjects) {
			assertTrue("UserObjekt equals for " + aNode1, aNode1.getUserObject().toString().equals(
					aNode2.getUserObject().toString()));
		}
		assertTrue("Same child count for " + aNode1, aNode1.getChildCount() == aNode2.getChildCount());
		for (int i = 0; i < aNode1.getChildCount(); i++) {
			compare((DefaultMutableTreeNode) aNode1.getChildAt(i), (DefaultMutableTreeNode) aNode2.getChildAt(i), true);
		}
	}

	public void testModelComparator() throws SAXException, IOException, ParserConfigurationException {
		Model theModel = ModelIOUtilities.getInstance().deserializeModelFromXML(
				getClass().getResourceAsStream("examplemodel.mxm"));

		ModelComparator theComparator = new ModelComparator();
		ModelCompareResult theResult = theComparator.compareModels(theModel, theModel);

		DefaultMutableTreeNode theDbRootNode = theResult.getDbRootNode();
		DefaultMutableTreeNode theModelRootNode = theResult.getModelRootNode();

		compare(theDbRootNode, theModelRootNode, false);
	}

}
