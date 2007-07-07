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
package de.mogwai.erdesignerng.model;

import de.mogwai.erdesignerng.exception.CannotDeleteException;
import de.mogwai.erdesignerng.exception.ElementAlreadyExistsException;
import de.mogwai.erdesignerng.exception.ElementInvalidNameException;

/**
 * A database table.
 * 
 * @author Mirko Sertic <mail@mirkosertic.de>
 */
public class Table extends OwnedModelItem<Model> implements
		OwnedModelItemVerifier {

	private AttributeList attributes = new AttributeList();

	private IndexList indexes = new IndexList();

	/**
	 * Add an attribute to the table.
	 * 
	 * @param aModel
	 *            the model
	 * @param aAttribute
	 *            the table
	 * @throws ElementAlreadyExistsException
	 * @throws ElementInvalidNameException
	 */
	public void addAttribute(Model aModel, Attribute aAttribute)
			throws ElementAlreadyExistsException, ElementInvalidNameException {

		ModelUtilities.checkNameAndExistance(attributes, aAttribute, aModel
				.getDialect());

		aAttribute.setOwner(this);
		attributes.add(aAttribute);

		if (getOwner() != null) {
			getOwner().getModelHistory().createAddAttributeCommand(aAttribute);
		}
	}

	/**
	 * Add an index to the table.
	 * 
	 * @param Model
	 *            aModel
	 * @param aIndex
	 *            the table
	 * @throws ElementAlreadyExistsException
	 * @throws ElementInvalidNameException
	 */
	public void addIndex(Model aModel, Index aIndex)
			throws ElementAlreadyExistsException, ElementInvalidNameException {

		Model theOwner = getOwner();
		if (theOwner != null) {
			ModelUtilities.checkNameAndExistance(indexes, aIndex, theOwner
					.getDialect());
		}

		aIndex.setOwner(this);

		indexes.add(aIndex);

		if (theOwner != null) {
			theOwner.getModelHistory().createAddIndexCommand(aIndex);
		}
	}

	public void checkNameAlreadyExists(ModelItem aSender, String aName)
			throws ElementAlreadyExistsException {

		Model theOwner = getOwner();

		if (aSender instanceof Attribute) {
			ModelUtilities.checkExistance(attributes, aName, theOwner
					.getDialect());
		}
		if (aSender instanceof Index) {
			ModelUtilities
					.checkExistance(indexes, aName, theOwner.getDialect());
		}

	}

	public void delete(ModelItem aSender) throws CannotDeleteException {

		Model theOwner = getOwner();

		if (aSender instanceof Attribute) {
			if (attributes.size() == 1) {
				throw new CannotDeleteException(
						"Table must have at least one attribute!");
			}

			Attribute theAttribute = (Attribute) aSender;

			if (theOwner.isUsedByRelations(theAttribute)) {
				throw new CannotDeleteException(
						"Attribute in use by relations!");
			}

			attributes.remove(theAttribute);

			return;
		}

		if (aSender instanceof Index) {

			Index theIndex = (Index) aSender;

			indexes.remove(theIndex);

			return;
		}

		throw new UnsupportedOperationException("Unknown element " + aSender);

	}

	@Override
	protected void generateRenameHistoryCommand(String aNewName) {
		Model theOwner = getOwner();
		if (theOwner != null) {
			theOwner.getModelHistory().createRenameTableCommand(this, aNewName);
		}
	}

	public String checkName(String aName) throws ElementInvalidNameException {
		Model theOwner = getOwner();
		if (theOwner != null) {
			return theOwner.checkName(aName);
		}

		return aName;
	}

	@Override
	protected void generateDeleteCommand() {
		Model theOwner = getOwner();
		if (theOwner != null) {
			theOwner.getModelHistory().createDeleteCommand(this);
		}
	}

	public AttributeList getAttributes() {
		return attributes;
	}

	public boolean isForeignKey(Attribute aAttribute) {
		return getOwner().isUsedByRelations(aAttribute);
	}

	public IndexList getIndexes() {
		return indexes;
	}

	public void setIndexes(IndexList aIndexes) {
		indexes = aIndexes;
	}

	public void setAttributes(AttributeList aAttributes) {
		attributes = aAttributes;
	}

	public Index findPrimaryKey() {

		for (Index theIndex : indexes) {
			if (theIndex.getIndexType().equals(IndexType.PRIMARYKEY)) {
				return theIndex;
			}
		}

		return null;
	}
}
