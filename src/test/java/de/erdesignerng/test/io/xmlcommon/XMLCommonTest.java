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
package de.erdesignerng.test.io.xmlcommon;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.erdesignerng.model.ModelIOUtilities;
import de.erdesignerng.test.BaseERDesignerTestCaseImpl;

public class XMLCommonTest extends BaseERDesignerTestCaseImpl {

    public void testLoadModelVersion10() throws SAXException, IOException, ParserConfigurationException {
        ModelIOUtilities.getInstance().deserializeModelFromXML(getClass().getResourceAsStream("examplemodel10.mxm"));
    }

    public void testLoadModelVersion20() throws SAXException, IOException, ParserConfigurationException {
        ModelIOUtilities.getInstance().deserializeModelFromXML(getClass().getResourceAsStream("examplemodel20.mxm"));
    }
}
