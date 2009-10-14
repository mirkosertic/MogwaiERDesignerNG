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
package de.erdesignerng.visual.common;

import de.erdesignerng.dialect.SQLGenerator;
import de.erdesignerng.dialect.StatementList;
import de.erdesignerng.model.ModelBasedConnectionProvider;
import de.erdesignerng.modificationtracker.VetoException;
import de.erdesignerng.visual.editor.sql.SQLEditor;

public class GenerateSQLCommand extends UICommand {

    public GenerateSQLCommand(ERDesignerComponent component) {
        super(component);
    }

    @Override
    void execute() {
        if (!component.checkForValidConnection()) {
            return;
        }

        try {
            SQLGenerator theGenerator = component.model.getDialect().createSQLGenerator();
            StatementList theStatements = theGenerator.createCreateAllObjects(component.model);
            SQLEditor theEditor = new SQLEditor(component.scrollPane,
                    new ModelBasedConnectionProvider(component.model), theStatements, component.currentEditingFile,
                    "schema.sql", component.preferences, component.worldConnector);
            theEditor.showModal();
        } catch (VetoException e) {
            component.worldConnector.notifyAboutException(e);
        }
    }
}