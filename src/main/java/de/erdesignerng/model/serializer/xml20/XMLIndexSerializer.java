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
package de.erdesignerng.model.serializer.xml20;

import de.erdesignerng.model.Index;
import de.erdesignerng.model.IndexExpression;
import de.erdesignerng.model.IndexType;
import de.erdesignerng.model.Table;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XMLIndexSerializer extends de.erdesignerng.model.serializer.xml10.XMLIndexSerializer {

	@Override
	public void serialize(final Index aIndex, final Document aDocument, final Element aRootElement) {
		final Element theIndexElement = addElement(aDocument, aRootElement, INDEX);

		theIndexElement.setAttribute(INDEXTYPE, aIndex.getIndexType().toString());

		serializeProperties(aDocument, theIndexElement, aIndex);

		// Attribute
		for (final IndexExpression theExpression : aIndex.getExpressions()) {
			final Element theAttributeElement = addElement(aDocument, theIndexElement, INDEXATTRIBUTE);
			theAttributeElement.setAttribute(ID, theExpression.getSystemId());
			if (!StringUtils.isEmpty(theExpression.getExpression())) {
				theAttributeElement.setAttribute(ATTRIBUTEEXPRESSION, theExpression.getExpression());
			} else {
				theAttributeElement.setAttribute(ATTRIBUTEREFID, theExpression.getAttributeRef().getSystemId());
			}
		}

	}

	@Override
	public void deserialize(final Table aTable, final Element aElement) {
		// Parse the indexes
		final NodeList theIndexes = aElement.getElementsByTagName(INDEX);
		for (int j = 0; j < theIndexes.getLength(); j++) {

			final Element theIndexElement = (Element) theIndexes.item(j);
			final Index theIndex = new Index();
			theIndex.setOwner(aTable);
			deserializeProperties(theIndexElement, theIndex);

			theIndex.setIndexType(IndexType.fromString(theIndexElement.getAttribute(INDEXTYPE)));

			final NodeList theAttributes = theIndexElement.getElementsByTagName(INDEXATTRIBUTE);
			for (int k = 0; k < theAttributes.getLength(); k++) {

				final Element theAttributeElement = (Element) theAttributes.item(k);

				final IndexExpression theExpression = new IndexExpression();
				theExpression.setSystemId(theAttributeElement.getAttribute(ID));
				final String theAttributeRefId = theAttributeElement.getAttribute(ATTRIBUTEREFID);
				if (!StringUtils.isEmpty(theAttributeRefId)) {
					theExpression.setAttributeRef(aTable.getAttributes().findBySystemId(theAttributeRefId));
				} else {
					theExpression.setExpression(theAttributeElement.getAttribute(ATTRIBUTEEXPRESSION));
				}
				theIndex.getExpressions().add(theExpression);
			}

			aTable.getIndexes().add(theIndex);

		}
	}
}
