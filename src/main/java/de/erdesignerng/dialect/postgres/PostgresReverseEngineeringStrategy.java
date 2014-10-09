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
package de.erdesignerng.dialect.postgres;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.dialect.DataType;
import de.erdesignerng.dialect.JDBCReverseEngineeringStrategy;
import de.erdesignerng.dialect.ReverseEngineeringNotifier;
import de.erdesignerng.dialect.ReverseEngineeringOptions;
import de.erdesignerng.dialect.SchemaEntry;
import de.erdesignerng.dialect.TableEntry;
import de.erdesignerng.exception.ReverseEngineeringException;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.CustomType;
import de.erdesignerng.model.CustomTypeType;
import de.erdesignerng.model.Domain;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.View;
import de.erdesignerng.modificationtracker.VetoException;
import de.mogwai.common.i18n.ResourceHelper;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:30 $
 */
public class PostgresReverseEngineeringStrategy extends JDBCReverseEngineeringStrategy<PostgresDialect> {

	public PostgresReverseEngineeringStrategy(PostgresDialect aDialect) {
		super(aDialect);
	}

	@Override
	public List<SchemaEntry> getSchemaEntries(Connection aConnection)
			throws SQLException {

		List<SchemaEntry> theList = new ArrayList<>();

		DatabaseMetaData theMetadata = aConnection.getMetaData();
		ResultSet theResult = theMetadata.getSchemas();

		while (theResult.next()) {
			String theSchemaName = theResult.getString("TABLE_SCHEM");
			String theCatalogName = null;

			theList.add(new SchemaEntry(theCatalogName, theSchemaName));
		}

		return theList;
	}

	// Bug Fixing 2876916 [ERDesignerNG] Reverse-Eng. PgSQL VARCHAR max-length
	// wrong
	@Override
	protected void reverseEngineerAttribute(Attribute<Table> aAttribute, TableEntry aTableEntry, Connection aConnection) throws SQLException {
		if (("varchar".equalsIgnoreCase(aAttribute.getDatatype().getName()))
				|| ("character varying".equalsIgnoreCase(aAttribute.getDatatype().getName()))) {
			// PostgreSQL liefert Integer.MAX_VALUE (2147483647), wenn VARCHAR ohne Parameter definiert wurde, obwohl 1073741823 korrekt wäre
			if (new Integer(Integer.MAX_VALUE).equals(aAttribute.getSize())) {
				aAttribute.setSize(null);
			}
		}
	}

	@Override
	protected void reverseEngineerDomain(Model aModel, Domain aDomain, ReverseEngineeringOptions aOptions, ReverseEngineeringNotifier aNotifier, Connection aConnection) {
		// Bug Fixing 2895202 [ERDesignerNG] RevEng PostgreSQL domains shows VARCHAR(0)
		if (("varchar".equalsIgnoreCase(aDomain.getConcreteType().getName()))
				|| ("character varying".equalsIgnoreCase(aDomain.getConcreteType().getName()))) {
			// PostgreSQL liefert 0, wenn VARCHAR ohne Parameter definiert wurde
			if (((Integer) 0).equals(aDomain.getSize())) {
				aDomain.setSize(null);
			}
		}

		//Bug Fixing 3420937 [ERDesignerNG] PostgreSQL: comments of domains not RevEnged
		PreparedStatement theStatement;

		String theQuery = "SELECT t.oid, n.nspname, t.typname, format_type(t.oid, null) AS alias, d.description "
						+ "FROM pg_type t LEFT OUTER JOIN pg_description d ON d.objoid = t.oid LEFT OUTER JOIN pg_catalog.pg_namespace n ON t.typnamespace = n.oid "
						+ "WHERE t.typcategory = 'N' AND n.nspname = ? AND t.typname = ?";

		for (SchemaEntry theEntry : aOptions.getSchemaEntries()) {
			try {
				theStatement = aConnection.prepareStatement(theQuery);
				theStatement.setString(1, theEntry.getSchemaName());
				theStatement.setString(2, aDomain.getName());
				ResultSet theResult = null;

				try {
					theResult = theStatement.executeQuery();
					if (theResult.next()) {
						String theDescription = theResult.getString("description");
						if (!StringUtils.isEmpty(theDescription)) {
							aDomain.setComment(theDescription);
						}
					}
				} finally {
					if (theResult != null) {
						theResult.close();
					}

					theStatement.close();
				}
			} catch(SQLException e) {

			}
		}
	}

	// Bug Fixing 2949508 [ERDesignerNG] Rev Eng not handling UDTs in PostgreSQL
	// Bug Fixing 2952877 [ERDesignerNG] Custom Types
	// Bug Fixing 3056071 [ERDesignerNG] postgres unkown datatype prevent reverse eng.
	// TODO: [dr-death2] reverse engineere details of custom types and create DDL
	@Override
	protected void reverseEngineerCustomTypes(Model aModel, ReverseEngineeringOptions aOptions, ReverseEngineeringNotifier aNotifier, Connection aConnection) throws SQLException, ReverseEngineeringException {
		// TODO: [mirkosertic] implement valid way to retrieve type ddl from db

		SQLException thePreviousException = null;
		SQLException theCurrentException = null;
		PreparedStatement theStatement;

		String theQuery = "SELECT t.oid, t.typcategory, n.nspname, t.typname, format_type(t.oid, null) AS alias, c.relname, t.typrelid, t.typelem, d.description "
				+ "FROM pg_type t LEFT OUTER JOIN pg_type e ON e.oid = t.typelem LEFT OUTER JOIN pg_class c ON c.oid = t.typrelid AND c.relkind <> 'c' LEFT OUTER JOIN pg_description d ON d.objoid = t.oid LEFT OUTER JOIN pg_catalog.pg_namespace n ON t.typnamespace = n.oid LEFT OUTER JOIN pg_type b ON t.typbasetype = b.oid "
				+ "WHERE t.typtype != 'd' AND t.typname NOT LIKE E'\\\\_%' AND n.nspname = ? AND c.oid IS NULL "
				+ "ORDER BY t.typname";

		for (SchemaEntry theEntry : aOptions.getSchemaEntries()) {

			do {
				theStatement = aConnection.prepareStatement(theQuery);
				theStatement.setString(1, theEntry.getSchemaName());
				ResultSet theResult = null;

				try {
					theResult = theStatement.executeQuery();
					thePreviousException = null;
					theCurrentException = null;

					while (theResult.next()) {
						String theTypeType = theResult.getString("typcategory");
						String theSchemaName = theResult.getString("nspname");
						String theTypeName = theResult.getString("typname");
						String theTypeNameAlias = theResult.getString("alias");
						String theDescription = theResult.getString("description");

						aNotifier.notifyMessage(ERDesignerBundle.ENGINEERINGCUSTOMTYPE, theTypeName);

						CustomType theCustomType = aModel.getCustomTypes().findByNameAndSchema(theTypeName, theSchemaName);
						if (theCustomType != null) {
							throw new ReverseEngineeringException("Duplicate custom datatype found: " + theTypeName);
						}

						theCustomType = new CustomType();
						theCustomType.setName(theTypeName);
						theCustomType.setAlias(theTypeNameAlias);
						theCustomType.setSchema(theSchemaName);

						if (!StringUtils.isEmpty(theDescription)) {
							theCustomType.setComment(theDescription);
						}

						if (!StringUtils.isEmpty(theTypeType)) {
//enumeration
							if (theTypeType.equals("E")) {
								theCustomType.setType(CustomTypeType.ENUMERATION);

								String theAttributesQuery = "SELECT enumlabel "
										+ "FROM pg_enum "
										+ "WHERE enumtypid = ?";
								PreparedStatement theAttributesStatement;
								theAttributesStatement = aConnection.prepareStatement(theAttributesQuery);
								theAttributesStatement.setInt(1, theResult.getInt("oid"));
								ResultSet theAttributesResult = null;

								try {
									theAttributesResult = theAttributesStatement.executeQuery();
									while (theAttributesResult.next()) {
										String theAttributeName = null;

										try {
											theAttributeName = theAttributesResult.getString("enumlabel");
										} catch (Exception e) {
										}

										Attribute<CustomType> theAttribute = new Attribute<>();
										theAttribute.setName(theAttributeName);
										theAttribute.setDatatype(null);

										try {
											theCustomType.addAttribute(aModel, theAttribute);
										} catch (Exception e) {
											throw new ReverseEngineeringException(e.getMessage(), e);
										}
									}
								} finally {
									if (theAttributesResult != null) {
										theAttributesResult.close();
									}

									theAttributesStatement.close();
								}
//composite
							} else if (theTypeType.equals("C")) {
								theCustomType.setType(CustomTypeType.COMPOSITE);

								String theAttributesQuery = "SELECT a.attname, format_type(t.oid,NULL) AS typname, a.attndims, a.atttypmod, n.nspname "
										+ "FROM pg_attribute a JOIN pg_type t ON t.oid = a.atttypid JOIN pg_namespace n ON t.typnamespace = n.oid LEFT OUTER JOIN pg_type b ON t.typelem = b.oid "
										+ "WHERE a.attrelid = ? "
										+ "ORDER BY a.attnum, a.attname";
								PreparedStatement theAttributesStatement;
								theAttributesStatement = aConnection.prepareStatement(theAttributesQuery);
								theAttributesStatement.setInt(1, theResult.getInt("typrelid"));
								ResultSet theAttributesResult = null;

								try {
									theAttributesResult = theAttributesStatement.executeQuery();
									while (theAttributesResult.next()) {
										String theAttributeTypeName = null;
										String theAttributeName = null;
										Integer theTypeProperties;
										Integer theSize = null; //in pg called "precision"
										Integer theFraction = null; //in pg called "scale"

										try {
											theAttributeTypeName = theAttributesResult.getString("typname");
										} catch (Exception e) {
										}

										DataType theDataType = aModel.getDialect().getDataTypes().findByName(theAttributeTypeName);
										if (theDataType == null) {
											throw new ReverseEngineeringException("Unknown data type " + theAttributeTypeName + " for CustomType " + theCustomType.getName());
										}

										try {
											theAttributeName = theAttributesResult.getString("attname");
										} catch (Exception e) {
										}

										try {
											theTypeProperties = theAttributesResult.getInt("atttypmod");

											//are data type parameters set?
											if (theTypeProperties > -1) {
												int theTemp = (theTypeProperties % 65536);
												int theSizeTemp = (theTypeProperties / 65536);

												if (theTypeProperties >= 65536) {
													// more than one parameter is set, so is must be "numeric" data type?
													theFraction = theTemp - 4;
													theSize = theSizeTemp;
												} else {
													// varchar data type
													theSize = theTemp - 4;
												}
											}
										} catch (Exception e) {
										}

										Attribute<CustomType> theAttribute = new Attribute<>();
										theAttribute.setName(theAttributeName);
										theAttribute.setDatatype(theDataType);

										if ((theDataType.supportsSize()) && (theSize != null) && (theSize > 0)) {
											theAttribute.setSize(theSize);
										}

										if ((theDataType.supportsFraction()) && (theFraction != null) && (theFraction > 0)) {
											theAttribute.setFraction(theFraction);
										}

										try {
											theCustomType.addAttribute(aModel, theAttribute);
										} catch (Exception e) {
											throw new ReverseEngineeringException(e.getMessage(), e);
										}
									}
								} finally {
									if (theAttributesResult != null) {
										theAttributesResult.close();
									}

									theAttributesStatement.close();
								}
//TODO: implement rev-eng of "external" UDTs 
//							} else if (theType.equals("X")) { // are external types really represented by "X"?
//								theCustomType.setType(CustomTypeType.EXTERNAL);
							} else {
								theCustomType.setType(null);
							}
						}

						try {
							aModel.addCustomType(theCustomType);
						} catch (VetoException e) {
							throw new ReverseEngineeringException(e.getMessage(), e);
						}

						reverseEngineerCustomType(aModel, theCustomType, aOptions, aNotifier, aConnection);
					}
				} catch (SQLException e) {
					// older pg-versions like v8.1.23 do not support typcategories other than
					// COMPOSITE ('C') so the column t.typcategory is not present there
					// a probably missing column will be defined here, explicitly containing 'C'
					if ((e.getClass().getName().equals("org.postgresql.util.PSQLException")) && (thePreviousException == null || !thePreviousException.getMessage().equals(e.getMessage()))) {
						String theMessage = e.getMessage();
						int theFieldNameStart = theMessage.lastIndexOf(".");
						int theFieldNameEnd = theMessage.indexOf(" ", theFieldNameStart);
						int theExpressionStart = theMessage.substring(0, theFieldNameEnd).lastIndexOf(" ");
						String theFieldName = theMessage.substring(theFieldNameStart + 1, theFieldNameEnd);
						String theTarget = theMessage.substring(theExpressionStart + 1, theFieldNameEnd);
						String theReplacement = ((theFieldName.equalsIgnoreCase("typcategory")) ? "'C'" : "NULL") + " AS " + theFieldName;

						theQuery = theQuery.replace(theTarget, theReplacement);

						thePreviousException = theCurrentException;
						theCurrentException = e;
					} else {
						throw new ReverseEngineeringException(ResourceHelper.getResourceHelper(ERDesignerBundle.BUNDLE_NAME).getFormattedText(ERDesignerBundle.ENGINEERINGCUSTOMTYPESNOTSUPPORTED, aConnection.getMetaData().getDatabaseProductVersion()));
					}
				} finally {
					if (theResult != null) {
						theResult.close();
					}

					theStatement.close();
				}
			} while ((theCurrentException != null) && ((thePreviousException == null) || (thePreviousException.getMessage().equalsIgnoreCase(theCurrentException.getMessage()))));
		}
	}

	// Bug Fixing 2949508 [ERDesignerNG] Rev Eng not handling UDTs in PostgreSQL
	// Bug Fixing 2952877 [ERDesignerNG] Custom Types
	@Override
	protected void reverseEngineerCustomType(Model aModel,
											 CustomType aCustomType, ReverseEngineeringOptions aOptions,
											 ReverseEngineeringNotifier aNotifier, Connection aConnection) {
		// TODO [mirko sertic]: Grab custom type ddl from information_schema
	}

	@Override
	protected String reverseEngineerViewSQL(TableEntry aViewEntry,
											Connection aConnection, View aView) throws SQLException {
		PreparedStatement theStatement = aConnection.prepareStatement("SELECT * FROM information_schema.views WHERE table_name = ?");
		theStatement.setString(1, aViewEntry.getTableName());
		ResultSet theResult = null;
		try {
			theResult = theStatement.executeQuery();
			if (theResult.next()) {
				String theViewDefinition = theResult.getString("view_definition");
				theViewDefinition = extractSelectDDLFromViewDefinition(theViewDefinition);
				return theViewDefinition;
			}
			return null;
		} finally {
			if (theResult != null) {
				theResult.close();
			}
			theStatement.close();
		}
	}

	// Bug Fixing 3317547 [ERDesignerNG] Error during RevEnging Postgres-DB ('Cannot find table in model')
	@Override
	protected String getEscapedPattern(DatabaseMetaData aMetaData, String aValue) throws SQLException {
		String thePrefix = aMetaData.getSearchStringEscape();

		//related to a bug in some PostgreSQL JDBC driver versions
		//see: http://archives.postgresql.org/pgsql-bugs/2007-03/msg00035.php
		if (thePrefix.length() > 1) {
			thePrefix = StringEscapeUtils.unescapeJava(thePrefix);
		}

		if (!StringUtils.isEmpty(thePrefix) && !StringUtils.isEmpty(aValue)) {
			aValue = aValue.replace("_", thePrefix + "_");
			aValue = aValue.replace("%", thePrefix + "%");
		}

		return aValue;
	}

}