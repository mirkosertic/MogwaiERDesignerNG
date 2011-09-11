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
package de.erdesignerng.visual.java2d;

import de.erdesignerng.model.*;
import java.awt.Color;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public class TestFrame extends JFrame {

    private Model model;

    public TestFrame() throws ParserConfigurationException, IOException, SAXException {
        InputStream theStream = new FileInputStream("D:\\Temp\\Capitastra_6_5_0.mxm");
        model = ModelIOUtilities.getInstance().deserializeModelFromXML(theStream);
    }

    public void generateGraphFor(String aTableName) {

        EditorPanel thePanel = new EditorPanel();
        thePanel.setBackground(Color.black);
        setContentPane(thePanel);

        Map<ModelItem, EditorPanel.EditorComponent> theComponentMap = new HashMap<ModelItem, EditorPanel.EditorComponent>();

        Table theTable = model.getTables().findByName(aTableName);
        TableComponent theButton = new TableComponent(theTable);
        EditorPanel.EditorComponent theRoot = new EditorPanel.EditorComponent(0, 0, theButton, true);
        thePanel.add(theRoot);

        theComponentMap.put(theTable, theRoot);

        int r1 = 250;

        List<Table> theAlreadyKnown = new ArrayList<Table>();
        theAlreadyKnown.add(theTable);

        // Level 1
        List<Relation> theIncomingRelationsLevel1 = model.getRelations().getForeignKeysFor(theTable);
        List<Table> theTablesLevel1 = new ArrayList<Table>();
        for (Relation theRelation : theIncomingRelationsLevel1) {
            if (!theTablesLevel1.contains(theRelation.getExportingTable()) && !theAlreadyKnown.contains(theRelation.getExportingTable())) {
                theTablesLevel1.add(theRelation.getExportingTable());
                theAlreadyKnown.add(theRelation.getExportingTable());
            }
        }
        float theIncrement1 = 360 / theTablesLevel1.size();
        float theAngleLevel1 = 0;

        for (Table theTableLevel1 : theTablesLevel1) {

            TableComponent theButtonLevel1 = new TableComponent(theTableLevel1);
            EditorPanel.EditorComponent theChildLevel1 = new EditorPanel.EditorComponent(theAngleLevel1, r1, theButtonLevel1);
            thePanel.add(theChildLevel1);

            theComponentMap.put(theTableLevel1, theChildLevel1);

            theAngleLevel1 += theIncrement1;
        }

        for (Table theKnownTable : theAlreadyKnown) {
            for (Relation theRelation : model.getRelations().getForeignKeysFor(theKnownTable)) {
                EditorPanel.EditorComponent theFrom = theComponentMap.get(theRelation.getExportingTable());
                EditorPanel.EditorComponent theTo = theComponentMap.get(theRelation.getImportingTable());
                if (theFrom != null && theTo!= null && theFrom != theTo && !thePanel.hasConnection(theFrom, theTo)) {
                    thePanel.add(new EditorPanel.Connector(theFrom, theTo));
                }
            }
        }
    }

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        TestFrame theFrame = new TestFrame();
        theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        theFrame.setSize(800, 600);
        theFrame.generateGraphFor("CAPITIMEPOINT");
        theFrame.setVisible(true);
    }
}
