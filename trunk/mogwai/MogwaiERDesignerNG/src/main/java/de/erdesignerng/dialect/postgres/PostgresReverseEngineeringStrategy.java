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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.dialect.JDBCReverseEngineeringStrategy;
import de.erdesignerng.dialect.ReverseEngineeringNotifier;
import de.erdesignerng.dialect.ReverseEngineeringOptions;
import de.erdesignerng.dialect.SchemaEntry;
import de.erdesignerng.dialect.TableEntry;
import de.erdesignerng.exception.ReverseEngineeringException;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.CustomType;
import de.erdesignerng.model.Domain;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.View;
import de.erdesignerng.modificationtracker.VetoException;
import java.sql.Types;
import org.apache.commons.lang.StringUtils;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:30 $
 */
public class PostgresReverseEngineeringStrategy extends JDBCReverseEngineeringStrategy<PostgresDialect> {

    public PostgresReverseEngineeringStrategy(PostgresDialect aDialect) {
        super(aDialect);
    }

    @Override
    public List<SchemaEntry> getSchemaEntries(Connection aConnection) throws SQLException {

        List<SchemaEntry> theList = new ArrayList<SchemaEntry>();

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
    protected void reverseEngineerAttribute(Model aModel, Attribute aAttribute, ReverseEngineeringOptions aOptions,
            ReverseEngineeringNotifier aNotifier, TableEntry aTableEntry, Connection aConnection) throws SQLException {

        if ((aAttribute.getDatatype().getName().equalsIgnoreCase("varchar"))
                || (aAttribute.getDatatype().getName().equalsIgnoreCase("character varying"))) {
            // PostgreSQL liefert Integer.MAX_VALUE (2147483647), wenn VARCHAR
            // ohne Parameter definiert wurde, obwohl 1073741823 korrekt
            // wäre
            if (new Integer(Integer.MAX_VALUE).equals(aAttribute.getSize())) {
                aAttribute.setSize(null);
            }
        }
    }

    // Bug Fixing 2895202 [ERDesignerNG] RevEng PostgreSQL domains shows
    // VARCHAR(0)
    @Override
    protected void reverseEngineerDomain(Model aModel, Domain aDomain, ReverseEngineeringOptions aOptions,
            ReverseEngineeringNotifier aNotifier, Connection aConnection) throws SQLException {

        if ((aDomain.getConcreteType().getName().equalsIgnoreCase("varchar"))
                || (aDomain.getConcreteType().getName().equalsIgnoreCase("character varying"))) {
            // PostgreSQL liefert 0, wenn VARCHAR ohne Parameter definiert wurde
            if (new Integer(0).equals(aDomain.getSize())) {
                aDomain.setSize(null);
            }
        }
    }

    @Override
    protected void reverseEngineerCustomTypes(Model aModel, ReverseEngineeringOptions aOptions, ReverseEngineeringNotifier aNotifier, Connection aConnection) throws SQLException, ReverseEngineeringException {
        //This query is reverse engineered from the original
        //aConnection.getMetaData().getUDTs() method which unfortunately
        //supports only the typtypes 'c' and 'd'
        //It is extended to the typtype 'e' and the column BASE_TYPE_NAME which
        //contains the original PostgreSQL basic batatype.
        String theQuery = "SELECT NULL AS type_cat, n.nspname AS type_schem, t.typname AS type_name, NULL AS class_name, CASE WHEN t.typtype = 'c' THEN 2002 WHEN t.typtype = 'e' THEN 2003 ELSE 2001 END AS data_type, pg_catalog.Obj_description(t.oid,'pg_type') AS remarks, CASE WHEN t.typtype = 'd' THEN (SELECT CASE WHEN bt.typname = 'numeric' THEN 2 WHEN bt.typname = 'bpchar' THEN 1 WHEN bt.typname = 'timetz' THEN 92 WHEN bt.typname = 'char' THEN 1 WHEN bt.typname = '_timestamp' THEN 2003 WHEN bt.typname = '_oid' THEN 2003 WHEN bt.typname = '_float8' THEN 2003 WHEN bt.typname = '_name' THEN 2003 WHEN bt.typname = 'float8' THEN 8 WHEN bt.typname = '_bool' THEN 2003 WHEN bt.typname = '_float4' THEN 2003 WHEN bt.typname = 'date' THEN 91 WHEN bt.typname = '_bytea' THEN 2003 WHEN bt.typname = 'float4' THEN 7 WHEN bt.typname = 'timestamptz' THEN 93 WHEN bt.typname = 'timestamp' THEN 93 WHEN bt.typname = '_money' THEN 2003 WHEN bt.typname = '_timestamptz' THEN 2003 WHEN bt.typname = 'time' THEN 92 WHEN bt.typname = '_text' THEN 2003 WHEN bt.typname = '_int8' THEN 2003 WHEN bt.typname = '_numeric' THEN 2003 WHEN bt.typname = 'oid' THEN -5 WHEN bt.typname = 'name' THEN 12 WHEN bt.typname = 'money' THEN 8 WHEN bt.typname = '_date' THEN 2003 WHEN bt.typname = '_int4' THEN 2003 WHEN bt.typname = '_time' THEN 2003 WHEN bt.typname = '_uuid' THEN 2003 WHEN bt.typname = 'varchar' THEN 12 WHEN bt.typname = '_int2' THEN 2003 WHEN bt.typname = 'text' THEN 12 WHEN bt.typname = 'int8' THEN -5 WHEN bt.typname = 'int4' THEN 4 WHEN bt.typname = 'xml' THEN 2009 WHEN bt.typname = '_char' THEN 2003 WHEN bt.typname = 'int2' THEN 5 WHEN bt.typname = '_timetz' THEN 2003 WHEN bt.typname = 'bit' THEN -7 WHEN bt.typname = 'bytea' THEN -2 WHEN bt.typname = '_bit' THEN 2003 WHEN bt.typname = '_varchar' THEN 2003 WHEN bt.typname = '_xml' THEN 2003 WHEN bt.typname = 'uuid' THEN 1111 WHEN bt.typname = 'bool' THEN -7 WHEN bt.typname = '_bpchar' THEN 2003 ELSE 1111 END) ELSE NULL END AS base_type, bt.typname AS base_type_name FROM pg_catalog.pg_type t LEFT JOIN pg_catalog.pg_type bt on bt.oid = t.typbasetype, pg_catalog.pg_namespace n WHERE t.typnamespace = n.oid AND n.nspname != 'pg_catalog' AND n.nspname != 'pg_toast' AND t.typtype IN ('c','d', 'e') AND n.nspname = ? ORDER BY data_type, type_schem, type_name";

        PreparedStatement theStatement = aConnection.prepareStatement(theQuery);
        for (SchemaEntry theEntry : aOptions.getSchemaEntries()) {
            theStatement.setString(1, theEntry.getSchemaName());
            ResultSet theResult = null;
            try {
                theResult = theStatement.executeQuery();
                while (theResult.next()) {
                    String theSchemaName = theResult.getString("TYPE_SCHEM");
                    String theCustomTypeName = theResult.getString("TYPE_NAME");
                    int theJdbcType = theResult.getInt("DATA_TYPE");
                    String theRemarks = theResult.getString("REMARKS");
                    String theBaseType = theResult.getString("BASE_TYPE_NAME");

                    aNotifier.notifyMessage(ERDesignerBundle.ENGINEERINGCUSTOMTYPE, theCustomTypeName);

                    CustomType theCustomType = aModel.getCustomTypes().findByName(theCustomTypeName);
                    if (theCustomType != null) {
                        throw new ReverseEngineeringException("Duplicate custom datatype found : " + theCustomTypeName);
                    }

                    theCustomType = new CustomType(theSchemaName, theJdbcType);
                    theCustomType.setName(theCustomTypeName);

                    if (!StringUtils.isEmpty(theRemarks)) {
                        theCustomType.setComment(theRemarks);
                    }

                    switch(theJdbcType) {
                        case Types.DISTINCT: //2001
                            //enhanced basic datatypes; the basic datatype can
                            //be found in theBaseType
                            theCustomType.setConcreteType(aModel.getDialect().getDataTypes().findByName(theBaseType));
                            break;
                    }

                    try {
                        aModel.addCustomType(theCustomType);
                    } catch (VetoException e) {
                        throw new ReverseEngineeringException(e.getMessage(), e);
                    }

                    reverseEngineerCustomType(aModel, theCustomType, aOptions, aNotifier, aConnection);
                }
            } finally {
                if (theResult != null) {
                    theResult.close();
                }
                theStatement.close();
            }
        }
    }

    @Override
    protected void reverseEngineerCustomType(Model aModel, CustomType aCustomType, ReverseEngineeringOptions aOptions, ReverseEngineeringNotifier aNotifier, Connection aConnection) throws SQLException {
        String theSchema = aCustomType.getSchema();
        ResultSet theResult = null;

        switch (aCustomType.getJDBCType()[0]) {
            case Types.STRUCT: //2002
                //complex datatypes
                //TODO [dr_death] reverse engineer complex types
                break;

            case Types.ARRAY: //2003
                //typical enums
                String theQuery = "SELECT e.enumlabel FROM pg_catalog.pg_type t, pg_catalog.pg_enum e, pg_catalog.pg_namespace n WHERE t.typnamespace = n.oid AND e.enumtypid = t.oid AND t.typname = ?" + ((theSchema != null)?" AND n.nspname = ?":"");

                PreparedStatement theStatement = aConnection.prepareStatement(theQuery);
                theStatement.setString(1, aCustomType.getName());
                if (theSchema != null) {
                    theStatement.setString(2, theSchema);
                }

                try {
                    ArrayList<String> theLabelList = new ArrayList<String>();
                    theResult = theStatement.executeQuery();
                    while (theResult.next()) {
                        String theLabel = theResult.getString("ENUMLABEL");
                        theLabelList.add(theLabel);
                    }

                    aCustomType.setLabelList(theLabelList);

                } finally {
                    if (theResult != null) {
                        theResult.close();
                    }
                    theStatement.close();
                }

                break;
        }
    }

    @Override
    protected String reverseEngineerViewSQL(TableEntry aViewEntry, Connection aConnection, View aView)
            throws SQLException, ReverseEngineeringException {
        PreparedStatement theStatement = aConnection
                .prepareStatement("SELECT * FROM information_schema.views WHERE table_name = ?");
        theStatement.setString(1, aViewEntry.getTableName());
        ResultSet theResult = null;
        try {
            theResult = theStatement.executeQuery();
            while (theResult.next()) {
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
}