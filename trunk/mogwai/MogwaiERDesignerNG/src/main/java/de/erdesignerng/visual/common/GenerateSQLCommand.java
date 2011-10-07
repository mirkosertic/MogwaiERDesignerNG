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
import de.erdesignerng.model.Model;
import de.erdesignerng.model.ModelBasedConnectionProvider;
import de.erdesignerng.visual.UsageDataCollector;
import de.erdesignerng.visual.editor.sql.SQLEditor;

public class GenerateSQLCommand extends UICommand {

    public GenerateSQLCommand() {
    }

    @Override
    public void execute() {

        UsageDataCollector.getInstance().addExecutedUsecase(UsageDataCollector.Usecase.GENERATE_COMPLETE_SQL);

        ERDesignerComponent component = ERDesignerComponent.getDefault();

        if (!component.checkForValidConnection()) {
            return;
        }

        Model theModel = component.getModel();

        SQLGenerator theGenerator = theModel.getDialect().createSQLGenerator();
        StatementList theStatements = theGenerator
                .createCreateAllObjects(theModel);
        SQLEditor theEditor = new SQLEditor(getDetailComponent(),
                new ModelBasedConnectionProvider(theModel), theStatements,
                component.currentEditingFile, "schema.sql", getWorldConnector());
        theEditor.showModal();
    }
}