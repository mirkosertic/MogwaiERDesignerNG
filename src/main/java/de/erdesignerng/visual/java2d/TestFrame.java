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

import de.erdesignerng.model.Model;
import de.erdesignerng.model.ModelIOUtilities;
import de.erdesignerng.model.Table;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TestFrame extends JFrame {

    private Model model;
    private Java2DEditor editor;

    public TestFrame() throws ParserConfigurationException, IOException, SAXException {
        InputStream theStream = new FileInputStream("U:\\Eigene Dateien\\Capitastra_6_5_0.mxm");
        model = ModelIOUtilities.getInstance().deserializeModelFromXML(theStream);
        editor = new Java2DEditor();
        editor.setModel(model);

        setContentPane(editor.getDetailComponent());

        Table theTable = model.getTables().findByName("CAPITIMEPOINT");
        theTable = model.getTables().findByName("GRUNDSTUECK");
        theTable = model.getTables().findByName("DIENSTBARKEIT_M_M_GEBAEUDE");

        editor.setSelectedObject(theTable);
    }

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        TestFrame theFrame = new TestFrame();
        theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        theFrame.setSize(800, 600);
        theFrame.setVisible(true);
    }
}
