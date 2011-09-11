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
    private EditorPanel panel;

    public TestFrame() throws ParserConfigurationException, IOException, SAXException {
        InputStream theStream = new FileInputStream("D:\\Temp\\Capitastra_6_5_0.mxm");
        model = ModelIOUtilities.getInstance().deserializeModelFromXML(theStream);

        panel = new EditorPanel() {
            @Override
            public void componentClicked(EditorComponent aComponent) {
                generateGraphFor((Table) aComponent.userObject);
            }
        };
        panel.setBackground(Color.black);
        setContentPane(panel);

        Table theTable = model.getTables().findByName("CAPITIMEPOINT");
        theTable = model.getTables().findByName("GRUNDSTUECK");
        generateGraphFor(theTable);
    }

    public void generateGraphFor(Table aTable) {

        panel.cleanup();

        Map<ModelItem, EditorPanel.EditorComponent> theComponentMap = new HashMap<ModelItem, EditorPanel.EditorComponent>();

        TableComponent theButton = new TableComponent(aTable);
        EditorPanel.EditorComponent theRoot = new EditorPanel.EditorComponent(aTable, 0, 0, theButton, true);
        panel.add(theRoot);

        theComponentMap.put(aTable, theRoot);

        int r1 = 250;

        List<Table> theAlreadyKnown = new ArrayList<Table>();
        theAlreadyKnown.add(aTable);

        // Level 1
        List<Relation> theIncomingRelationsLevel1 = model.getRelations().getForeignKeysFor(aTable);
        //List<Relation> theOutgoingRelationsLevel1 = model.getRelations().getExportedKeysFor(aTable);
        //theIncomingRelationsLevel1.addAll(theOutgoingRelationsLevel1);
        List<Table> theTablesLevel1 = new ArrayList<Table>();
        for (Relation theRelation : theIncomingRelationsLevel1) {
            if (!theTablesLevel1.contains(theRelation.getExportingTable()) && !theAlreadyKnown.contains(theRelation.getExportingTable())) {
                theTablesLevel1.add(theRelation.getExportingTable());
                theAlreadyKnown.add(theRelation.getExportingTable());
            }
            if (!theTablesLevel1.contains(theRelation.getImportingTable()) && !theAlreadyKnown.contains(theRelation.getImportingTable())) {
                theTablesLevel1.add(theRelation.getImportingTable());
                theAlreadyKnown.add(theRelation.getImportingTable());
            }
        }

        if (theTablesLevel1.size() > 0) {
            float theIncrement1 = 360 / theTablesLevel1.size();
            float theAngleLevel1 = 0;

            for (Table theTableLevel1 : theTablesLevel1) {

                TableComponent theButtonLevel1 = new TableComponent(theTableLevel1);
                EditorPanel.EditorComponent theChildLevel1 = new EditorPanel.EditorComponent(theTableLevel1, theAngleLevel1, r1, theButtonLevel1);
                panel.add(theChildLevel1);

                theComponentMap.put(theTableLevel1, theChildLevel1);

                theAngleLevel1 += theIncrement1;
            }
        }

        for (Table theKnownTable : theAlreadyKnown) {
            for (Relation theRelation : model.getRelations().getForeignKeysFor(theKnownTable)) {
                EditorPanel.EditorComponent theFrom = theComponentMap.get(theRelation.getExportingTable());
                EditorPanel.EditorComponent theTo = theComponentMap.get(theRelation.getImportingTable());
                if (theFrom != null && theTo != null && theFrom != theTo && !panel.hasConnection(theFrom, theTo)) {
                    panel.add(new EditorPanel.Connector(theFrom, theTo));
                }
            }
        }

        panel.invalidate();
        panel.repaint();
        panel.explodeAnimation();
    }

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        TestFrame theFrame = new TestFrame();
        theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        theFrame.setSize(800, 600);
        theFrame.setVisible(true);
    }
}
