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
package de.erdesignerng.test.core;

import junit.framework.TestCase;
import de.erdesignerng.exception.ElementAlreadyExistsException;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Index;
import de.erdesignerng.model.IndexType;
import de.erdesignerng.model.Table;

public class IndexTest extends TestCase {

	public void testModified() throws ElementAlreadyExistsException {
		Attribute<Table> theAttribute = new Attribute<>();
		theAttribute.setName("TEST");

		Index theIndex = new Index();
		theIndex.setName("TESTINDEX");
		theIndex.setIndexType(IndexType.UNIQUE);
		theIndex.getExpressions().addExpressionFor(theAttribute);

		Index theCloneIndex = theIndex.clone();
		assertTrue(!theIndex.isModified(theCloneIndex, true));
		assertTrue(!theIndex.isModified(theCloneIndex, false));
	}
}