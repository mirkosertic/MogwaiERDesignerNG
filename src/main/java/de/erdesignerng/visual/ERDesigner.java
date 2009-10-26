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
package de.erdesignerng.visual;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.UnsupportedLookAndFeelException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import de.erdesignerng.dialect.DataTypeIO;
import de.erdesignerng.exception.ElementAlreadyExistsException;
import de.erdesignerng.exception.ElementInvalidNameException;
import de.erdesignerng.util.ApplicationPreferences;
import de.mogwai.common.client.looks.components.DefaultSplashScreen;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-11-17 20:53:55 $
 */
public final class ERDesigner {

    private ERDesigner() {
    }

    public static void main(String[] args) throws ElementAlreadyExistsException, ElementInvalidNameException,
            ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException,
            TransformerException, IOException, ParserConfigurationException, SAXException, InterruptedException,
            InvocationTargetException {
        
        // Disable D3D rendering pipeline
        System.setProperty("sun.java2d.d3d", "false");

        DefaultSplashScreen theScreen = new DefaultSplashScreen("/de/erdesignerng/splashscreen.jpg");
        theScreen.setVisible(true);

        ApplicationPreferences thePreferences = ApplicationPreferences.getInstance();
        DataTypeIO.getInstance().loadUserTypes(thePreferences);

        ERDesignerMainFrame frame = new ERDesignerMainFrame(thePreferences);
        frame.setModel(frame.createNewModel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            // Just wait here :-)
        }

        theScreen.setVisible(false);
        frame.setVisible(true);
    }
}