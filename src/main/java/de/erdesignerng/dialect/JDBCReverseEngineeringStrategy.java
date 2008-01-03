package de.erdesignerng.dialect;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.erdesignerng.exception.ReverseEngineeringException;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.CascadeType;
import de.erdesignerng.model.DefaultValue;
import de.erdesignerng.model.Domain;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.Table;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-03 13:11:06 $
 */
public abstract class JDBCReverseEngineeringStrategy {

	private Dialect dialect;

	protected JDBCReverseEngineeringStrategy(Dialect aDialect) {
		dialect = aDialect;
	}

	/**
	 * Reverse engineer a domain from a column definition.
	 * 
	 * @param aModel
	 * @param aColumnName
	 * @param aTypeName
	 * @param aSize
	 * @param aDecimalDigits
	 * @return
	 */
	protected Domain createDomainFor(Model aModel, String aColumnName,
			String aTypeName, String aSize, String aDecimalDigits) {

		StringBuffer theTypeDefinition = new StringBuffer(aTypeName);
		if (((aSize != null)) && (aTypeName.indexOf(")") < 0)) {
			theTypeDefinition.append("(");
			theTypeDefinition.append(aSize);
			if (aDecimalDigits != null) {
				theTypeDefinition.append(",");
				theTypeDefinition.append(aDecimalDigits);
			}
			theTypeDefinition.append(")");
		}

		String theDataType = theTypeDefinition.toString();
		Domain theDomain = aModel.getDomains().findByDataType(theDataType);
		if (theDomain != null) {

			if (theDomain.getName().equals(aColumnName)) {
				return theDomain;
			}

			for (int i = 0; i < 10000; i++) {
				String theName = aColumnName;
				if (i > 0) {
					theName = theName + "_" + i;
				}

				theDomain = aModel.getDomains().findByName(theName);
				if (theDomain != null) {
					if (theDomain.getName().equals(aColumnName)) {
						return theDomain;
					}
				}

				if (!aModel.getDomains().elementExists(theName,
						dialect.isCaseSensitive())) {

					theDomain = new Domain();
					theDomain.setName(theName);
					theDomain.setDatatype(theDataType);

					aModel.getDomains().add(theDomain);

					return theDomain;
				}
			}

		} else {
			theDomain = new Domain();
			theDomain.setName(aColumnName);
			theDomain.setDatatype(theDataType);

			aModel.getDomains().add(theDomain);
		}

		return theDomain;
	}

	/**
	 * Reverse engineer an existing table.
	 * 
	 * @param aModel
	 * @param aSchemaName
	 * @param aTableName
	 * @param aConnection
	 * @throws SQLException
	 * @throws ReverseEngineeringException
	 */
	protected void reverseEngineerTable(Model aModel, String aSchemaName,
			String aTableName, Connection aConnection) throws SQLException,
			ReverseEngineeringException {

		DatabaseMetaData theMetaData = aConnection.getMetaData();

		ResultSet theTablesResultSet = theMetaData.getTables(null, aSchemaName,
				aTableName, new String[] { "TABLE" });
		while (theTablesResultSet.next()) {

			String theTableRemarks = theTablesResultSet.getString("REMARKS");

			Table theTable = new Table();
			theTable.setName(dialect.getCastType().cast(aTableName));

			if ((theTableRemarks != null) && (!"".equals(theTableRemarks))) {
				theTable.getProperties().setProperty(Table.PROPERTY_REMARKS,
						theTableRemarks);
			}

			// Reverse engineer attributes
			ResultSet theColumnsResultSet = theMetaData.getColumns(null,
					aSchemaName, aTableName, null);
			while (theColumnsResultSet.next()) {

				String theColumnName = theColumnsResultSet
						.getString("COLUMN_NAME");
				String theTypeName = theColumnsResultSet.getString("TYPE_NAME");
				String theSize = theColumnsResultSet.getString("COLUMN_SIZE");
				String theDecimalDigits = theColumnsResultSet
						.getString("DECIMAL_DIGITS");
				String theNullable = theColumnsResultSet.getString("NULLABLE");
				String theDefaultValue = theColumnsResultSet
						.getString("COLUMN_DEF");
				String theColumnRemarks = theColumnsResultSet
						.getString("REMARKS");

				Attribute theAttribute = new Attribute();
				theAttribute.setName(dialect.getCastType().cast(theColumnName));
				if ((theColumnRemarks != null)
						&& (!"".equals(theColumnRemarks))) {
					theAttribute.getProperties().setProperty(
							Attribute.PROPERTY_REMARKS, theColumnRemarks);
				}

				Domain theDomain = createDomainFor(aModel, theColumnName,
						theTypeName, theSize, theDecimalDigits);

				DefaultValue theDefault = createDefaultValueFor(aModel,
						theColumnName, theDefaultValue);

				theAttribute.setDefinition(theDomain, "1".equals(theNullable),
						theDefault);

				theTable.getAttributes().add(theAttribute);
			}
			theColumnsResultSet.close();

			// Reverse engineer primary keys
			ResultSet thePrimaryKeyResultSet = theMetaData.getPrimaryKeys(null,
					aSchemaName, aTableName);
			while (thePrimaryKeyResultSet.next()) {

				String theColumnName = thePrimaryKeyResultSet
						.getString("COLUMN_NAME");

				Attribute theIndexAttribute = theTable.getAttributes()
						.findByName(dialect.getCastType().cast(theColumnName));
				if (theIndexAttribute == null) {
					throw new ReverseEngineeringException(
							"Cannot find attribute " + theColumnName
									+ " in table " + theTable.getName());
				}

				theIndexAttribute.setPrimaryKey(true);

			}
			thePrimaryKeyResultSet.close();

			// We are done here
			aModel.getTables().add(theTable);

		}
		theTablesResultSet.close();
	}

	protected DefaultValue createDefaultValueFor(Model aModel,
			String aColumnName, String aDefaultValue) {
		return null;
	}

	/**
	 * Reverse engineer relations.
	 * 
	 * @param aModel
	 * @param aSchemaName
	 * @param aConnection
	 * @throws SQLException
	 * @throws ReverseEngineeringException
	 */
	protected void reverseEngineerRelations(Model aModel, String aSchemaName,
			Connection aConnection) throws SQLException,
			ReverseEngineeringException {

		DatabaseMetaData theMetaData = aConnection.getMetaData();

		for (Table theTable : aModel.getTables()) {

			// Foreign keys
			Relation theRelation = null;
			ResultSet theForeignKeys = theMetaData.getImportedKeys(null,
					aSchemaName, theTable.getName());
			while (theForeignKeys.next()) {
				String theFKName = theForeignKeys.getString("FK_NAME");
				if ((theRelation == null)
						|| (!theFKName.equals(theRelation.getName()))) {

					String thePKTableName = theForeignKeys
							.getString("PKTABLE_NAME");
					String theUpdateRule = theForeignKeys
							.getString("UPDATE_RULE");
					String theDeleteRule = theForeignKeys
							.getString("DELETE_RULE");

					Table theExportingTable = aModel.getTables().findByName(
							dialect.getCastType().cast(thePKTableName));
					if (theExportingTable == null) {
						throw new ReverseEngineeringException(
								"Cannot find table " + thePKTableName
										+ " in model");
					}

					theRelation = new Relation();
					theRelation.setName(dialect.getCastType().cast(theFKName));
					theRelation.setExportingTable(theExportingTable);
					theRelation.setImportingTable(theTable);

					if (theUpdateRule != null) {
						int theType = Integer
								.parseInt(theUpdateRule.toString());
						switch (theType) {
							case DatabaseMetaData.importedKeyNoAction: {
								theRelation.setOnUpdate(CascadeType.NOTHING);
								break;
							}
							case DatabaseMetaData.importedKeySetNull: {
								theRelation.setOnUpdate(CascadeType.SET_NULL);
								break;
							}
							case DatabaseMetaData.importedKeyCascade: {
								theRelation.setOnUpdate(CascadeType.CASCADE);
								break;
							}
							default: {
								theRelation.setOnUpdate(CascadeType.CASCADE);
							}
						}
					} else {
						theRelation.setOnUpdate(CascadeType.NOTHING);
					}

					if (theDeleteRule != null) {
						int theType = Integer
								.parseInt(theDeleteRule.toString());
						switch (theType) {
							case DatabaseMetaData.importedKeyNoAction: {
								theRelation.setOnDelete(CascadeType.NOTHING);
								break;
							}
							case DatabaseMetaData.importedKeySetNull: {
								theRelation.setOnDelete(CascadeType.SET_NULL);
								break;
							}
							case DatabaseMetaData.importedKeyCascade: {
								theRelation.setOnDelete(CascadeType.CASCADE);
								break;
							}
							default: {
								theRelation.setOnDelete(CascadeType.CASCADE);
							}
						}
					} else {
						theRelation.setOnDelete(CascadeType.NOTHING);
					}

					aModel.getRelations().add(theRelation);
				}

				String thePKColumnName = theForeignKeys
						.getString("PKCOLUMN_NAME");
				String theFKColumnName = theForeignKeys
						.getString("FKCOLUMN_NAME");

				Attribute theExportingAttribute = theRelation
						.getExportingTable().getAttributes().findByName(
								dialect.getCastType().cast(thePKColumnName));
				if (theExportingAttribute == null) {
					throw new ReverseEngineeringException("Cannot find column "
							+ thePKColumnName + " in table "
							+ theRelation.getExportingTable().getName());
				}

				Attribute theImportingAttribute = theRelation
						.getImportingTable().getAttributes().findByName(
								dialect.getCastType().cast(theFKColumnName));
				if (theImportingAttribute == null) {
					throw new ReverseEngineeringException("Cannot find column "
							+ theFKColumnName + " in table "
							+ theRelation.getImportingTable().getName());
				}

				theRelation.getMapping().put(theImportingAttribute,
						theExportingAttribute);
			}
			theForeignKeys.close();
		}
	}

	protected String[] getReverseEngineeringTableTypes() {
		return new String[] { "TABLE" };
	}

	/**
	 * Reverse engineer the existing tables in a schema.
	 * 
	 * @param aModel
	 * @param aSchemaName
	 * @param aConnection
	 * @throws SQLException
	 * @throws ReverseEngineeringException
	 */
	protected void reverseEnginnerTables(Model aModel, String aSchemaName,
			Connection aConnection) throws SQLException,
			ReverseEngineeringException {

		DatabaseMetaData theMetaData = aConnection.getMetaData();

		// Reverse engineer tables
		ResultSet theTablesResultSet = theMetaData.getTables(null, aSchemaName,
				null, getReverseEngineeringTableTypes());
		while (theTablesResultSet.next()) {

			String theTableType = theTablesResultSet.getString("TABLE_TYPE");
			String theSchema = theTablesResultSet.getString("TABLE_SCHEM");

			String theTableName = theTablesResultSet.getString("TABLE_NAME");

			// Make sure that tables are not reverse engineered twice!
			if (!aModel.getTables().elementExists(theTableName,
					dialect.isCaseSensitive())) {
				reverseEngineerTable(aModel, theSchema, theTableName,
						aConnection);
			}
		}
		theTablesResultSet.close();

		// Reverse engineer also relations
		reverseEngineerRelations(aModel, aSchemaName, aConnection);
	}

	/**
	 * Reverse engineer the existing schemas.
	 * 
	 * @param aModel
	 * @param aConnection
	 * @throws SQLException
	 * @throws ReverseEngineeringException
	 */
	protected void reverseEngineerSchemas(Model aModel, String aSchemaName,
			Connection aConnection) throws SQLException,
			ReverseEngineeringException {

		DatabaseMetaData theMetaData = aConnection.getMetaData();
		ResultSet theSchemaResultSet = theMetaData.getSchemas();

		while (theSchemaResultSet.next()) {

			String theSchemaName = theSchemaResultSet.getString("TABLE_SCHEM");

			if (aSchemaName.equals(theSchemaName)) {
				reverseEnginnerTables(aModel, theSchemaName, aConnection);
			}
		}
		theSchemaResultSet.close();

	}

	/**
	 * Create a database model from a jdbc connection.
	 * 
	 * @param aConnection
	 *            the connection
	 * @param aSchemaName
	 * @return the new database model
	 * @throws SQLException
	 * @throws ReverseEngineeringException
	 */
	public Model createModelFromConnection(Connection aConnection,
			String aSchemaName) throws SQLException,
			ReverseEngineeringException {

		Model theNewModel = new Model();
		theNewModel.setDialect(dialect);

		reverseEngineerSchemas(theNewModel, aSchemaName, aConnection);

		return theNewModel;
	}
}
