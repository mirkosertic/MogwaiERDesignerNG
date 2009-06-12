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
package de.erdesignerng.test.sql.postgres;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.hsqldb.lib.StringUtil;

import de.erdesignerng.dialect.Dialect;
import de.erdesignerng.dialect.ReverseEngineeringNotifier;
import de.erdesignerng.dialect.ReverseEngineeringOptions;
import de.erdesignerng.dialect.ReverseEngineeringStrategy;
import de.erdesignerng.dialect.TableNamingEnum;
import de.erdesignerng.dialect.h2.H2Dialect;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Index;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.View;
import de.erdesignerng.modificationtracker.HistoryModificationTracker;
import de.erdesignerng.test.BaseERDesignerTestCaseImpl;
import de.erdesignerng.visual.common.ERDesignerWorldConnector;
import de.mogwai.common.client.looks.components.DefaultToolbar;

/**
 * Test for XML based model io.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-11-16 17:48:26 $
 */
public class ReverseEngineeringTest extends BaseERDesignerTestCaseImpl {

    public void testReverseEngineerPostgreSQL() throws Exception {

        Class.forName("org.postgresql.Driver").newInstance();
        Connection theConnection = null;
        try {
            theConnection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mogwai", "mogwai", "mogwai");
            
            // 
            Statement theStatement = theConnection.createStatement();
            try {
                theStatement.execute("DROP SCHEMA schemaa CASCADE");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                theStatement.execute("DROP SCHEMA schemab CASCADE");
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            BufferedReader theReader = new BufferedReader(new InputStreamReader(getClass()
                    .getResourceAsStream("db.sql")));
            while (theReader.ready()) {
                String theLine = theReader.readLine();
                if (!StringUtil.isEmpty(theLine)) {
                    theLine = theLine.trim();
                }
                if (!StringUtil.isEmpty(theLine)) {
                    System.out.println(theLine);
                    theStatement.execute(theLine);
                }
            }
            theStatement.close();

            theReader.close();

            Dialect theDialect = new H2Dialect();
            ReverseEngineeringStrategy<H2Dialect> theST = theDialect.getReverseEngineeringStrategy();

            Model theModel = new Model();
            theModel.setDialect(theDialect);
            theModel.setModificationTracker(new HistoryModificationTracker(theModel));

            ReverseEngineeringOptions theOptions = new ReverseEngineeringOptions();
            theOptions.setTableNaming(TableNamingEnum.INCLUDE_SCHEMA);
            theOptions.getTableEntries().addAll(
                    theST.getTablesForSchemas(theConnection, theST.getSchemaEntries(theConnection)));

            theST.updateModelFromConnection(theModel, new ERDesignerWorldConnector() {

                @Override
                public Model createNewModel() {
                    Model theNewModel = new Model();
                    theNewModel.setModificationTracker(new HistoryModificationTracker(theNewModel));
                    return theNewModel;
                }

                @Override
                public void exitApplication() {
                }

                @Override
                public DefaultToolbar getToolBar() {
                    return null;
                }

                @Override
                public void initTitle(String file) {
                }

                @Override
                public void initTitle() {
                }

                @Override
                public void initializeLoadedModel(Model model) {
                    model.setModificationTracker(new HistoryModificationTracker(model));
                }

                @Override
                public void notifyAboutException(Exception exception) {
                    throw new RuntimeException(exception);

                }

                @Override
                public void setStatusText(String theMessage) {
                }

                @Override
                public boolean supportsClasspathEditor() {
                    return false;
                }

                @Override
                public boolean supportsConnectionEditor() {
                    return false;
                }

                @Override
                public boolean supportsExitApplication() {
                    return false;
                }

                @Override
                public boolean supportsPreferences() {
                    return false;
                }

                @Override
                public boolean supportsRepositories() {
                    return false;
                }
            }, theConnection, theOptions, new ReverseEngineeringNotifier() {

                @Override
                public void notifyMessage(String resourceKey, String... values) {
                    if (values != null) {
                        for (String aValue : values) {
                            System.out.println(aValue);
                        }
                    }
                }
            });

            // Implement Unit Tests here
            Table theTable = theModel.getTables().findByNameAndSchema("TABLE1", "schemaa");
            assertTrue(theTable != null);
            assertTrue("Tablecomment".equals(theTable.getComment()));
            Attribute theAttribute = theTable.getAttributes().findByName("TB1_1");
            assertTrue(theAttribute != null);
            assertTrue(theAttribute.isNullable() == false);
            assertTrue(theAttribute.getDatatype().getName().equals("varchar"));
            assertTrue(theAttribute.getSize() == 20);
            assertTrue("Columncomment".equals(theAttribute.getComment()));
            theAttribute = theTable.getAttributes().findByName("TB1_2");
            assertTrue(theAttribute != null);
            assertTrue(theAttribute.isNullable());
            assertTrue(theAttribute.getDatatype().getName().equals("varchar"));
            assertTrue(theAttribute.getSize() == 100);
            theAttribute = theTable.getAttributes().findByName("TB1_3");
            assertTrue(theAttribute != null);
            assertTrue(theAttribute.isNullable() == false);
            assertTrue(theAttribute.getDatatype().getName().equals("numeric"));
            assertTrue(theAttribute.getSize() == 20);
            assertTrue(theAttribute.getFraction() == 5);

            Index thePK = theTable.getPrimarykey();
            assertTrue("PK1".equals(thePK.getName()));
            assertTrue(thePK != null);
            assertTrue(thePK.getExpressions().findByAttributeName("TB1_1") != null);

            theTable = theModel.getTables().findByNameAndSchema("TABLE1", "schemab");
            assertTrue(theTable != null);
            theAttribute = theTable.getAttributes().findByName("TB2_1");
            assertTrue(theAttribute != null);
            theAttribute = theTable.getAttributes().findByName("TB2_2");
            assertTrue(theAttribute != null);
            theAttribute = theTable.getAttributes().findByName("TB2_3");
            assertTrue(theAttribute != null);
            
            View theView = theModel.getViews().findByNameAndSchema("VIEW1","schemab");
            assertTrue(theView != null);

            theView = theModel.getViews().findByNameAndSchema("VIEW1","schemaa");
            assertTrue(theView == null);

        } finally {
            if (theConnection != null) {
                theConnection.close();
            }
        }
    }
}