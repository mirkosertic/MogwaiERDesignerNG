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

import de.mogwai.erdesignerng.dialect.Dialect;
import de.mogwai.erdesignerng.dialect.mysql.MySQLDialect;
import de.mogwai.erdesignerng.exception.CannotDeleteException;
import de.mogwai.erdesignerng.exception.ElementAlreadyExistsException;
import de.mogwai.erdesignerng.exception.ElementInvalidNameException;

/**
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2007-07-08 10:29:41 $
 */
public class Model implements OwnedModelItemVerifier {

	private TableList tables = new TableList();

	private DomainList domains = new DomainList();

	private DefaultValueList defaultValues = new DefaultValueList();

	private RelationList relations = new RelationList();

	private ModelHistory history = new ModelHistory(this);

	private Dialect dialect = new MySQLDialect();

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

		ModelUtilities.checkNameAndExistance(tables, aTable, dialect);

		for (Attribute theAttribute : aTable.getAttributes()) {
			theAttribute.setName(dialect.checkName(theAttribute.getName()));
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

		ModelUtilities.checkNameAndExistance(domains, aDomain, dialect);

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

		ModelUtilities.checkNameAndExistance(relations, aRelation, dialect);

		history.createAddRelationCommand(aRelation);

		aRelation.setOwner(this);
		relations.add(aRelation);
	}

	/**
	 * Add a default value to the database model.
	 * 
	 * @param aDefaultValue
	 *            the table
	 * @throws ElementAlreadyExistsException
	 * @throws ElementInvalidNameException
	 */
	public void addDefaultValue(DefaultValue aDefaultValue)
			throws ElementAlreadyExistsException, ElementInvalidNameException {

		ModelUtilities.checkNameAndExistance(defaultValues, aDefaultValue,
				dialect);

		aDefaultValue.setOwner(this);
		defaultValues.add(aDefaultValue);
	}

	public void checkNameAlreadyExists(ModelItem aSender, String aName)
			throws ElementAlreadyExistsException {
		if (aSender instanceof Table) {
			ModelUtilities.checkExistance(tables, aName, dialect);
		}
		if (aSender instanceof Domain) {
			ModelUtilities.checkExistance(domains, aName, dialect);
		}
	}

	public ModelHistory getModelHistory() {
		return history;
	}

	public void setModelHistory(ModelHistory aModelHistory) {
		history = aModelHistory;
	}

	public Dialect getDialect() {
		return dialect;
	}

	public void setDialect(Dialect modelProperties) {
		this.dialect = modelProperties;
	}

	public boolean isUsedByRelations(Attribute aAttribute) {
		return relations.isAttributeInUse(aAttribute);
	}

	public void delete(ModelItem aSender) throws CannotDeleteException {
		if (aSender instanceof Table) {

			Table theTable = (Table) aSender;

			if (relations.isTableInUse(theTable)) {
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

		if (aSender instanceof Relation) {

			Relation theRelation = (Relation) aSender;

			relations.remove(theRelation);

			return;
		}

		throw new UnsupportedOperationException("Unknown element " + aSender);
	}

	public String checkName(String aName) throws ElementInvalidNameException {
		return dialect.checkName(aName);
	}

	public DomainList getDomains() {
		return domains;
	}

	public void setDomains(DomainList aDomains) {
		domains = aDomains;
	}

	public RelationList getRelations() {
		return relations;
	}

	public void setRelations(RelationList aRelations) {
		relations = aRelations;
	}

	public TableList getTables() {
		return tables;
	}

	public void setTables(TableList aTables) {
		tables = aTables;
	}

	public DefaultValueList getDefaultValues() {
		return defaultValues;
	}

	public void setDefaultValues(DefaultValueList aDefaultValues) {
		defaultValues = aDefaultValues;
	}
}
