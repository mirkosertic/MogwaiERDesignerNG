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
package de.erdesignerng.model.serializer;

import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.erdesignerng.model.DefaultValue;
import de.erdesignerng.model.Domain;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.Table;

public class ModelSerializer extends Serializer {
    
    protected static final String MODEL = "Model";

    protected static final String VERSION = "version";

    protected static final String CONFIGURATION = "Configuration";

    protected static final String DIALECT = "dialect";

    protected static final String DEFAULTVALUES = "Defaultvalues";

    protected static final String DOMAINS = "Domains";

    protected static final String TABLES = "Tables";

    protected static final String RELATIONS = "Relations";

    public void serialize(Model aModel, Document aDocument) {
        
        Element theRootElement = addElement(aDocument, aDocument, MODEL);
        theRootElement.setAttribute(VERSION, "1.0");

        Element theConfigurationElement = addElement(aDocument, theRootElement, CONFIGURATION);

        Element theDialectElement = addElement(aDocument, theConfigurationElement, PROPERTY);
        theDialectElement.setAttribute(NAME, DIALECT);
        theDialectElement.setAttribute(VALUE, aModel.getDialect().getUniqueName());

        Map<String, String> theProperties = aModel.getProperties().getProperties();
        for (String theKey : theProperties.keySet()) {
            String theValue = theProperties.get(theKey);

            Element thePropertyElement = addElement(aDocument, theConfigurationElement, PROPERTY);
            thePropertyElement.setAttribute(NAME, theKey);
            thePropertyElement.setAttribute(VALUE, theValue);
        }

        // Default values
        Element theDefaultValuesElement = addElement(aDocument, theRootElement, DEFAULTVALUES);
        for (DefaultValue theDefaultValue : aModel.getDefaultValues()) {
            DefaultValue.SERIALIZER.serialize(theDefaultValue, aDocument, theDefaultValuesElement);
        }

        // Domains serialisieren
        Element theDomainsElement = addElement(aDocument, theRootElement, DOMAINS);
        for (Domain theDomain : aModel.getDomains()) {
            Domain.SERIALIZER.serialize(theDomain, aDocument, theDomainsElement);
        }

        Element theTablesElement = addElement(aDocument, theRootElement, TABLES);
        for (Table theTable : aModel.getTables()) {
            Table.SERIALIZER.serialize(theTable, aDocument, theTablesElement);
        }

        Element theRelationsElement = addElement(aDocument, theRootElement, RELATIONS);
        for (Relation theRelation : aModel.getRelations()) {
            Relation.SERIALIZER.serialize(theRelation, aDocument, theRelationsElement);
        }
        
    }
}
