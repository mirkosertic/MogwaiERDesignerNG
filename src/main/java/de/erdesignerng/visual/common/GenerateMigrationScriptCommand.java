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

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.dialect.Dialect;
import de.erdesignerng.dialect.DialectFactory;
import de.erdesignerng.dialect.GenericConnectionProvider;
import de.erdesignerng.model.serializer.repository.DictionaryModelSerializer;
import de.erdesignerng.model.serializer.repository.entities.RepositoryEntity;
import de.erdesignerng.util.ApplicationPreferences;
import de.erdesignerng.util.ConnectionDescriptor;
import de.erdesignerng.visual.MessagesHelper;
import de.erdesignerng.visual.editor.repository.MigrationScriptEditor;

import java.sql.Connection;
import java.sql.SQLException;

public class GenerateMigrationScriptCommand extends UICommand {

	public GenerateMigrationScriptCommand(ERDesignerComponent component) {
		super(component);
	}

	@Override
	public void execute() {
		ConnectionDescriptor theRepositoryConnection = ApplicationPreferences
				.getInstance().getRepositoryConnection();
		if (theRepositoryConnection == null) {
			MessagesHelper.displayErrorMessage(getDetailComponent(), component
					.getResourceHelper().getText(
							ERDesignerBundle.ERRORINREPOSITORYCONNECTION));
			return;
		}
		Connection theConnection = null;
		Dialect theDialect = DialectFactory.getInstance().getDialect(
				theRepositoryConnection.getDialect());
		try {

			theConnection = theDialect.createConnection(ApplicationPreferences
					.getInstance().createDriverClassLoader(),
					theRepositoryConnection.getDriver(),
					theRepositoryConnection.getUrl(), theRepositoryConnection
							.getUsername(), theRepositoryConnection
							.getPassword(), false);

			RepositoryEntity theEntity = DictionaryModelSerializer.SERIALIZER
					.getRepositoryEntity(theDialect.getHibernateDialectClass(),
							theConnection, component.currentRepositoryEntry);

			MigrationScriptEditor theEditor = new MigrationScriptEditor(
					getDetailComponent(), theEntity,
					new GenericConnectionProvider(theConnection, theDialect
							.createSQLGenerator()
							.createScriptStatementSeparator()),
					getWorldConnector());

			theEditor.showModal();

		} catch (Exception e) {
			getWorldConnector().notifyAboutException(e);
		} finally {
			if (theConnection != null
					&& !theDialect.generatesManagedConnection()) {
				try {
					theConnection.close();
				} catch (SQLException e) {
					// Do nothing here
				}
			}
		}

	}
}
