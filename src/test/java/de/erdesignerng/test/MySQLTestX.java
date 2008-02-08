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
package de.erdesignerng.test;

import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Types;

import de.erdesignerng.dialect.mysql.MySQLDialect;
import de.erdesignerng.exception.ElementAlreadyExistsException;
import de.erdesignerng.exception.ElementInvalidNameException;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Index;
import de.erdesignerng.model.IndexType;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Table;
import de.erdesignerng.modificationtracker.VetoException;

/**
 * Test for MySQL dialect.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-02-08 20:38:54 $
 */
public class MySQLTestX extends BaseUseCases {

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        model = new Model();
        model.setDialect(new MySQLDialect());

        model.setModificationTracker(new MyTracker(model));

        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://localhost/test", "test", "test");
    }
    
    @Override
    public void setTextAttribute(Attribute aAttribute) {
        aAttribute.setDatatype(model.getDialect().getDataTypeByName("VARCHAR"));
        aAttribute.setSize(20);
    }  
    
    public void setInt(Attribute aAttribute) {
        aAttribute.setDatatype(model.getDialect().getDataTypeByName("INTEGER"));
    }      

    public void setLong(Attribute aAttribute) {
        aAttribute.setDatatype(model.getDialect().getDataTypeByName("BIGINT"));
    }      

    /**
     * Test extraction of datatypes.
     * @throws Exception is thrown in case of an error
     */
    public void testExtractDataTypes() throws Exception {
        DatabaseMetaData theMeta = connection.getMetaData();
        ResultSet theTypes = theMeta.getTypeInfo();
        while (theTypes.next()) {
            String theTypeName = theTypes.getString("TYPE_NAME");
            int theType = theTypes.getInt("DATA_TYPE");
            String theParams = theTypes.getString("CREATE_PARAMS");
            if (theParams == null) {
                theParams = "";
            }
            if (!(Types.OTHER == theType) && !(Types.ARRAY == theType)) {
                System.out.println("registerType(new MySQLDataType(\"" + theTypeName + "\",\"" + theParams + "\","
                        + getTypeName(theType) + "));");
            }
        }
    }
    
    public void testCreateAutoIncrementTable() throws ElementAlreadyExistsException, ElementInvalidNameException, VetoException {

        Table theTempTable = new Table();
        theTempTable.setName("test700");
        for (int i = 0; i < 5; i++) {
            Attribute theAttribute = new Attribute();
            setInt(theAttribute);
            theAttribute.setName("COLUMN_" + i);
            theAttribute.setNullable(false);
            theTempTable.addAttribute(model, theAttribute);
        }

        Index theIndex = new Index();
        theIndex.setName("TESTINDEX");
        theIndex.setIndexType(IndexType.PRIMARYKEY);
        Attribute theAttribute = theTempTable.getAttributes().get(0);
        theAttribute.setExtra("AUTO_INCREMENT PRIMARY KEY");
        theIndex.getAttributes().add(theAttribute);
        theTempTable.addIndex(model, theIndex);

        model.addTable(theTempTable);
        model.removeTable(theTempTable);
    }
    
    public void testCreateAutoIncrementTableDropPK() throws ElementAlreadyExistsException, ElementInvalidNameException, VetoException {

        Table theTempTable = new Table();
        theTempTable.setName("test701");
        for (int i = 0; i < 5; i++) {
            Attribute theAttribute = new Attribute();
            setInt(theAttribute);
            theAttribute.setName("COLUMN_" + i);
            theAttribute.setNullable(false);
            theTempTable.addAttribute(model, theAttribute);
        }

        Index theIndex = new Index();
        theIndex.setName("TESTINDEX");
        theIndex.setIndexType(IndexType.PRIMARYKEY);
        Attribute theAttribute = theTempTable.getAttributes().get(0);
        theAttribute.setExtra("AUTO_INCREMENT PRIMARY KEY");
        theIndex.getAttributes().add(theAttribute);
        theTempTable.addIndex(model, theIndex);

        model.addTable(theTempTable);
        
        model.removeIndex(theTempTable, theIndex);
        
        model.removeTable(theTempTable);
    }  
    
    public void testChangeAutoIncrementTable() throws Exception {

        Table theTempTable = new Table();
        theTempTable.setName("test702");
        for (int i = 0; i < 5; i++) {
            Attribute theAttribute = new Attribute();
            setInt(theAttribute);
            theAttribute.setName("COLUMN_" + i);
            theAttribute.setNullable(false);
            theTempTable.addAttribute(model, theAttribute);
        }

        Index theIndex = new Index();
        theIndex.setName("TESTINDEX");
        theIndex.setIndexType(IndexType.PRIMARYKEY);
        
        Attribute theAttribute = theTempTable.getAttributes().get(0);
        theAttribute.setExtra("AUTO_INCREMENT PRIMARY KEY");
        theIndex.getAttributes().add(theAttribute);
        theTempTable.addIndex(model, theIndex);

        model.addTable(theTempTable);
        
        Attribute theClone = theAttribute.clone();
        setLong(theClone);
        model.changeAttribute(theAttribute, theClone);
        
        model.removeTable(theTempTable);
    }    
}
