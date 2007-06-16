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

import java.util.Map;

import de.mogwai.erdesignerng.exception.CannotDeleteException;
import de.mogwai.erdesignerng.exception.ElementAlreadyExistsException;
import de.mogwai.erdesignerng.exception.ElementInvalidNameException;
import de.mogwai.erdesignerng.util.dialect.Dialect;

/**
 * A database model.
 * 
 * @author Mirko Sertic <mail@mirkosertic.de>
 */
public class Model implements OwnedModelItemVerifier {

	private TableList tables = new TableList();

	private DomainList domains = new DomainList();

	private RelationList relations = new RelationList();

	private ModelHistory history;

	private Dialect modelProperties;

	/**
	 * Add a table to the database model.
	 * 
	 * @param aTable
	 *            the table
	 * @throws ElementAlreadyExistsException
	 * @throws ElementInvalidNameException
	 */
	public void addTable(Table aTable) throws ElementAlreadyExistsException,
			ElementInvalidNameException {

		ModelUtilities.checkNameAndExistance(tables, aTable, modelProperties);

		for (Attribute theAttribute : aTable.getAttributes()) {
			theAttribute.setName(modelProperties.checkName(theAttribute
					.getName()));
		}

		aTable.setOwner(this);
		tables.add(aTable);
	}

	/**
	 * Add a domain to the database model.
	 * 
	 * @param aDomain
	 *            the table
	 * @throws ElementAlreadyExistsException
	 * @throws ElementInvalidNameException
	 */
	public void addDomain(Domain aDomain) throws ElementAlreadyExistsException,
			ElementInvalidNameException {

		ModelUtilities.checkNameAndExistance(domains, aDomain, modelProperties);

		aDomain.setOwner(this);
		domains.add(aDomain);
	}

	/**
	 * Add a relation to the database model.
	 * 
	 * @param aRelation
	 *            the table
	 * @throws ElementAlreadyExistsException
	 * @throws ElementInvalidNameException
	 */
	public void addRelation(Relation aRelation)
			throws ElementAlreadyExistsException, ElementInvalidNameException {

		ModelUtilities.checkNameAndExistance(relations, aRelation,
				modelProperties);

		aRelation.setOwner(this);
		relations.add(aRelation);
	}

	public void checkNameAlreadyExists(ModelItem aSender, String aName)
			throws ElementAlreadyExistsException {
		if (aSender instanceof Table) {
			ModelUtilities.checkExistance(tables, aName, modelProperties);
		}
		if (aSender instanceof Domain) {
			ModelUtilities.checkExistance(domains, aName, modelProperties);
		}
	}

	public ModelHistory getModelHistory() {
		return history;
	}

	public void setModelHistory(ModelHistory aModelHistory) {
		history = aModelHistory;
	}

	public Dialect getModelProperties() {
		return modelProperties;
	}

	public void setModelProperties(Dialect modelProperties) {
		this.modelProperties = modelProperties;
	}

	public boolean isUsedByRelations(Attribute aAttribute) {
		for (Relation theRelation : relations) {
			Map theMap = theRelation.getMapping();
			if (theMap.containsKey(aAttribute)) {
				return true;
			}
			if (theMap.containsValue(aAttribute)) {
				return true;
			}
		}
		return false;
	}

	public boolean isUsedByRelations(Table aTable) {
		for (Relation theRelation : relations) {
			if (theRelation.getStart().equals(aTable)) {
				return true;
			}
			if (theRelation.getEnd().equals(aTable)) {
				return true;
			}
		}
		return false;
	}

	public void delete(ModelItem aSender) throws CannotDeleteException {
		if (aSender instanceof Table) {

			Table theTable = (Table) aSender;

			if (isUsedByRelations(theTable)) {
				throw new CannotDeleteException("Table is used by relations!");
			}

			tables.remove(theTable);

			return;
		}
		if (aSender instanceof Domain) {

			Domain theDomain = (Domain) aSender;

			domains.remove(theDomain);

			return;
		}

		throw new UnsupportedOperationException("Unknown element " + aSender);
	}

	public String checkName(String aName) throws ElementInvalidNameException {
		return modelProperties.checkName(aName);
	}
}
