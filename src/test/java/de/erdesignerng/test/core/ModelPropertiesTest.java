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
package de.erdesignerng.test.core;

import de.erdesignerng.model.ModelProperties;
import junit.framework.TestCase;

public class ModelPropertiesTest extends TestCase {

    public void testNotModified() {
        ModelProperties theProperties = new ModelProperties();
        theProperties.setProperty("K1", "V1");
        theProperties.setProperty("K2", "V2");
        assert (!theProperties.isModified(theProperties));
    }

    public void testNotModified1() {
        ModelProperties theProperties = new ModelProperties();
        theProperties.setProperty("K1", null);
        theProperties.setProperty("K2", "V2");
        assert (!theProperties.isModified(theProperties));
    }

    public void testModified1() {
        ModelProperties theProperties = new ModelProperties();
        theProperties.setProperty("K1", "V1");
        theProperties.setProperty("K2", "V2");

        ModelProperties theProperties2 = new ModelProperties();
        theProperties2.setProperty("K1", "V1");
        theProperties2.setProperty("K2", "V2");
        theProperties2.setProperty("K3", "V3");

        assert (theProperties.isModified(theProperties2));
    }

    public void testModified2() {
        ModelProperties theProperties = new ModelProperties();
        theProperties.setProperty("K1", "V1");
        theProperties.setProperty("K2", "V2");
        theProperties.setProperty("K3", "V3");

        ModelProperties theProperties2 = new ModelProperties();
        theProperties2.setProperty("K1", "V1");
        theProperties2.setProperty("K2", "V2");

        assert (theProperties.isModified(theProperties2));
    }

    public void testModified3() {
        ModelProperties theProperties = new ModelProperties();
        theProperties.setProperty("K1", null);
        theProperties.setProperty("K2", "V2");

        ModelProperties theProperties2 = new ModelProperties();
        theProperties2.setProperty("K1", "V1");
        theProperties2.setProperty("K2", "V2");

        assert (theProperties.isModified(theProperties2));
    }

    public void testModified4() {
        ModelProperties theProperties = new ModelProperties();
        theProperties.setProperty("K1", "V1");
        theProperties.setProperty("K2", "V2");

        ModelProperties theProperties2 = new ModelProperties();
        theProperties2.setProperty("K1", null);
        theProperties2.setProperty("K2", "V2");

        assert (theProperties.isModified(theProperties2));
    }

}
