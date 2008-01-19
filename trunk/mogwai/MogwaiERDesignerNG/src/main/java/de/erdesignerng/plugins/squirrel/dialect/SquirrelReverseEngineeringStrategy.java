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
package de.erdesignerng.plugins.squirrel.dialect;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.fw.sql.ForeignKeyColumnInfo;
import net.sourceforge.squirrel_sql.fw.sql.ForeignKeyInfo;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.PrimaryKeyInfo;
import net.sourceforge.squirrel_sql.fw.sql.ProgressCallBack;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.dialect.ReverseEngineeringNotifier;
import de.erdesignerng.dialect.ReverseEngineeringOptions;
import de.erdesignerng.dialect.ReverseEngineeringStrategy;
import de.erdesignerng.dialect.SchemaEntry;
import de.erdesignerng.exception.ReverseEngineeringException;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.DefaultValue;
import de.erdesignerng.model.Domain;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.ModelItem;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.Table;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-19 18:21:02 $
 */
public class SquirrelReverseEngineeringStrategy extends ReverseEngineeringStrategy<SquirrelDialect> {

    private ObjectTreeNode node;

    public SquirrelReverseEngineeringStrategy(SquirrelDialect aDialect, ObjectTreeNode aNode) {
        super(aDialect);
        node = aNode;
    }

    protected void reverseEngineerTable(Model aModel, ITableInfo aInfo, SQLDatabaseMetaData aMeta,
            ReverseEngineeringOptions aOptions, ReverseEngineeringNotifier aNotifier) throws SQLException,
            ReverseEngineeringException {

        aNotifier.notifyMessage(ERDesignerBundle.ENGINEERINGTABLE, aInfo.getSimpleName());

        Table theTable = new Table();
        theTable.setName(dialect.getCastType().cast(aInfo.getSimpleName()));

        String theTableRemarks = aInfo.getRemarks();
        if ((theTableRemarks != null) && (!"".equals(theTableRemarks))) {
            theTable.getProperties().setProperty(ModelItem.PROPERTY_REMARKS, theTableRemarks);
        }

        TableColumnInfo[] theColumns = aMeta.getColumnInfo(aInfo);
        for (TableColumnInfo theColumn : theColumns) {

            Attribute theAttribute = new Attribute();
            theAttribute.setName(dialect.getCastType().cast(theColumn.getColumnName()));

            String theColumnRemarks = theColumn.getRemarks();

            if ((theColumnRemarks != null) && (!"".equals(theColumnRemarks))) {
                theAttribute.getProperties().setProperty(ModelItem.PROPERTY_REMARKS, theColumnRemarks);
            }

            Domain theDomain = new Domain();
            theDomain.setName("DOM");
            // Domain theDomain = createDomainFor(aModel, theColumnName,
            // theTypeName, theSize, theDecimalDigits,
            // aOptions);

            // DefaultValue theDefault = createDefaultValueFor(aModel,
            // theColumn.getColumnName(), theColumn.getDefaultValue());
            DefaultValue theDefault = null;

            theAttribute.setDefinition(theDomain, "1".equals(theColumn.isNullable()), theDefault);

            try {
                theTable.addAttribute(aModel, theAttribute);
            } catch (Exception e) {
                throw new ReverseEngineeringException(e.getMessage());
            }
        }

        PrimaryKeyInfo[] thePrimaryKeys = aMeta.getPrimaryKey(aInfo);
        for (PrimaryKeyInfo thePrimaryKey : thePrimaryKeys) {

            Attribute theAttribute = theTable.getAttributes().findByName(thePrimaryKey.getColumnName());
            theAttribute.setPrimaryKey(true);
        }

        // We are done here
        try {
            aModel.addTable(theTable);
        } catch (Exception e) {
            throw new ReverseEngineeringException(e.getMessage());
        }
    }

    protected void reverseEngineerTables(Model aModel, ReverseEngineeringOptions aOptions,
            ReverseEngineeringNotifier aNotifier) throws SQLException, ReverseEngineeringException {
        SQLDatabaseMetaData theMeta = dialect.getSession().getSQLConnection().getSQLMetaData();
        String[] theTableTypes = theMeta.getTableTypes();
        IDatabaseObjectInfo theObjectInfo = node.getDatabaseObjectInfo();

        ITableInfo[] theInfos = theMeta.getTables(theObjectInfo.getCatalogName(), theObjectInfo.getSchemaName(), null,
                theTableTypes, new ProgressCallBack() {

                    public void currentlyLoading(String aInfo) {
                        System.out.println(aInfo);
                    }

                });

        for (ITableInfo theInfo : theInfos) {
            reverseEngineerTable(aModel, theInfo, theMeta, aOptions, aNotifier);
        }

        reverseEngineerRelations(aModel, theInfos, theMeta, aOptions, aNotifier);
    }

    protected void reverseEngineerRelations(Model aModel, ITableInfo[] aInfos, SQLDatabaseMetaData aMeta,
            ReverseEngineeringOptions aOptions, ReverseEngineeringNotifier aNotifier) throws SQLException,
            ReverseEngineeringException {

        for (ITableInfo theTableInfo : aInfos) {

            aNotifier.notifyMessage(ERDesignerBundle.ENGINEERINGRELATION, theTableInfo.getSimpleName());

            Map<Integer, String> thePKAttributes = new HashMap<Integer, String>();

            PrimaryKeyInfo[] thePrimaryKeys = aMeta.getPrimaryKey(theTableInfo);
            for (PrimaryKeyInfo thePrimaryKey : thePrimaryKeys) {
                thePKAttributes.put((int) thePrimaryKey.getKeySequence(), thePrimaryKey.getColumnName());
            }

            Table theImportingTable = aModel.getTables().findByName(theTableInfo.getSimpleName());

            ForeignKeyInfo[] theFKs = aMeta.getImportedKeysInfo(theTableInfo);
            for (ForeignKeyInfo theInfo : theFKs) {

                Table theExportingTable = aModel.getTables().findByName(theInfo.getPrimaryKeyTableName());
                if (theExportingTable == null) {
                    throw new ReverseEngineeringException("Cannot find table " + theInfo.getPrimaryKeyTableName());
                }

                Relation theRelation = new Relation();
                theRelation.setExportingTable(theExportingTable);
                theRelation.setImportingTable(theImportingTable);
                theRelation.setName(theInfo.getForeignKeyName());
                theRelation.setOnUpdate(getCascadeType(theInfo.getUpdateRule()));
                theRelation.setOnDelete(getCascadeType(theInfo.getDeleteRule()));

                ForeignKeyColumnInfo[] theColumns = theInfo.getForeignKeyColumnInfo();
                for (ForeignKeyColumnInfo theColumnInfo : theColumns) {

                    String thePrimaryKeyColumn = thePKAttributes.get(theColumnInfo.getKeySequence());

                    Attribute thePKAttribute = theExportingTable.getAttributes().findByName(thePrimaryKeyColumn);
                    Attribute theFKAttribute = theImportingTable.getAttributes().findByName(
                            theColumnInfo.getForeignKeyColumnName());

                    if (thePKAttribute == null) {
                        throw new ReverseEngineeringException("Cannot find attribute " + thePrimaryKeyColumn
                                + " in table " + theExportingTable.getName());
                    }

                    if (theFKAttribute == null) {
                        throw new ReverseEngineeringException("Cannot find attribute "
                                + theColumnInfo.getForeignKeyColumnName() + " in table " + theImportingTable.getName());
                    }

                    theRelation.getMapping().put(thePKAttribute, theFKAttribute);
                }

                try {
                    aModel.addRelation(theRelation);
                } catch (Exception e) {
                    throw new ReverseEngineeringException(e.getMessage());
                }
            }
        }
    }

    @Override
    public Model createModelFromConnection(Connection aConnection, ReverseEngineeringOptions aOptions,
            ReverseEngineeringNotifier aNotifier) throws SQLException, ReverseEngineeringException {
        Model theModel = new Model();
        theModel.setDialect(dialect);

        reverseEngineerTables(theModel, aOptions, aNotifier);

        aNotifier.notifyMessage(ERDesignerBundle.ENGINEERINGFINISHED, "");

        return theModel;
    }

    @Override
    public List<SchemaEntry> getSchemaEntries(Connection aConnection) throws SQLException {
        return null;
    }
}