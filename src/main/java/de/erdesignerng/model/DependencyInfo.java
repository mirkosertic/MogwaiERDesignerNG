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
import java.util.List;
import java.util.Map;

public class DependencyInfo {

    private Map<ModelItem, List<Dependency>> dependencies = new HashMap<ModelItem, List<Dependency>>();

    public void addDependencyFor(ModelItem aItem, Dependency aDependency) {
        register(aItem).add(aDependency);
    }

    public List<Dependency> register(ModelItem aItem) {
        List<Dependency> theList = dependencies.get(aItem);
        if (theList == null) {
            theList = new ArrayList<Dependency>();
            dependencies.put(aItem, theList);
        }
        return theList;
    }

    public List<ModelItem> getItemsWithoutDependencies() {
        List<ModelItem> theResult = new ArrayList<ModelItem>();
        for (Map.Entry<ModelItem, List<Dependency>> theEntry : dependencies.entrySet()) {
            if (theEntry.getValue().size() == 0) {
                theResult.add(theEntry.getKey());
            }
        }
        return theResult;
    }

    public List<ModelItem> getRootTables() {
        List<ModelItem> theRootTables = new ArrayList<ModelItem>();
        for (Map.Entry<ModelItem, List<Dependency>> theEntry : dependencies.entrySet()) {
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