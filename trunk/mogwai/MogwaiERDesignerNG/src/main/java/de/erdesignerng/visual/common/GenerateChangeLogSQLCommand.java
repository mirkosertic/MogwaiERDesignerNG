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

import de.erdesignerng.dialect.StatementList;
import de.erdesignerng.model.ModelBasedConnectionProvider;
import de.erdesignerng.modificationtracker.HistoryModificationTracker;
import de.erdesignerng.visual.editor.sql.SQLEditor;

public class GenerateChangeLogSQLCommand extends UICommand {

    public GenerateChangeLogSQLCommand(ERDesignerComponent component) {
        super(component);
    }

    protected String generateChangelogSQLFileName() {
        return "changelog.sql";
    }

    @Override
    void execute() {
        if (!component.checkForValidConnection()) {
            return;
        }

        StatementList theStatements = ((HistoryModificationTracker) component.model.getModificationTracker())
                .getStatements();
        SQLEditor theEditor = new SQLEditor(component.scrollPane, new ModelBasedConnectionProvider(component.model),
                theStatements, component.currentEditingFile, generateChangelogSQLFileName(), component.preferences,
                component.worldConnector);
        theEditor.showModal();
    }
}
