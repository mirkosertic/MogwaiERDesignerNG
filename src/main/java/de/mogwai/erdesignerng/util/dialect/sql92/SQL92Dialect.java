package de.mogwai.erdesignerng.util.dialect.sql92;

import de.mogwai.erdesignerng.model.Attribute;
import de.mogwai.erdesignerng.model.Domain;
import de.mogwai.erdesignerng.model.Index;
import de.mogwai.erdesignerng.model.Relation;
import de.mogwai.erdesignerng.model.Table;
import de.mogwai.erdesignerng.util.dialect.Dialect;

/**
 * Dialect for SQL92. 
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2007-06-27 18:29:00 $
 */
public abstract class SQL92Dialect extends Dialect {

	@Override
	public String createAddAttributeSQL(Attribute aAttribute) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createAddIndexSQL(Index aAttribute) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createAlterAttributeSQL(Table aTable, String attributeName, Domain aDomain, boolean aNullable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createDropAttributeSQL(Attribute aAttribute) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createDropAttributeSQL(Table aTable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createDropIndexSQL(Index aIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createDropRelationSQL(Relation aRelation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createDropTableSQL(Table aTable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createAddRelationSQL(Relation aRelation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createRenameAttributeSQL(Table aTable, Attribute aAttribute, String aNewName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createRenameIndexSQL(Table aTable, Index index, String aNewName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createRenameRelationSQL(Relation aRelation, String aNewName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createRenameTableSQL(Table aTable, String aNewName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createAddTableSQL(Table aTable) {
		// TODO Auto-generated method stub
		return null;
	}
}
