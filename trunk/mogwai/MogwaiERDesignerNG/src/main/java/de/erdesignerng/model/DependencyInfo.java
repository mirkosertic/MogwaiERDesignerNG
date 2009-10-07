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
package de.erdesignerng.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class DependencyInfo {

    private Map<Table, List<Dependency>> dependencies = new HashMap<Table, List<Dependency>>();

    public void addDependencyFor(Table aTable, Dependency aDependency) {
        register(aTable).add(aDependency);
    }

    public List<Dependency> register(Table aTable) {
        List<Dependency> theList = dependencies.get(aTable);
        if (theList == null) {
            theList = new ArrayList<Dependency>();
            dependencies.put(aTable, theList);
        }
        return theList;
    }

    public List<Table> getRootTables() {
        List<Table> theRootTables = new ArrayList<Table>();
        for (Map.Entry<Table, List<Dependency>> theEntry : dependencies.entrySet()) {
            boolean dependsOn = false;
            for (Dependency theDependency : theEntry.getValue()) {
                if (theDependency.getType() == Dependency.DependencyType.DEPENDSON) {
                    dependsOn = true;
                }
            }
            if (!dependsOn) {
                theRootTables.add(theEntry.getKey());
            }
        }
        return theRootTables;
    }
}