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
package de.erdesignerng.test.sql;

import de.erdesignerng.dialect.ReverseEngineeringNotifier;
import de.erdesignerng.model.Model;
import de.erdesignerng.modificationtracker.HistoryModificationTracker;
import de.erdesignerng.test.BaseERDesignerTestCaseImpl;
import de.erdesignerng.visual.common.ERDesignerWorldConnector;
import de.mogwai.common.client.looks.components.DefaultToolbar;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringTokenizer;

public abstract class AbstractReverseEngineeringTestImpl extends
		BaseERDesignerTestCaseImpl {

	public static class EmptyWorldConnector implements ERDesignerWorldConnector {

		@Override
		public Model createNewModel() {
			Model theNewModel = new Model();
			theNewModel.setModificationTracker(new HistoryModificationTracker(
					theNewModel));
			return theNewModel;
		}

		@Override
		public void exitApplication() {
		}

		@Override
		public DefaultToolbar getToolBar() {
			return null;
		}

		@Override
		public void initTitle(String file) {
		}

		@Override
		public void initTitle() {
		}

		@Override
		public void initializeLoadedModel(Model model) {
			model.setModificationTracker(new HistoryModificationTracker(model));
		}

		@Override
		public void notifyAboutException(Exception exception) {
			throw new RuntimeException(exception);

		}

		@Override
		public void setStatusText(String theMessage) {
		}

		@Override
		public boolean supportsClasspathEditor() {
			return false;
		}

		@Override
		public boolean supportsConnectionEditor() {
			return false;
		}

		@Override
		public boolean supportsExitApplication() {
			return false;
		}

		@Override
		public boolean supportsPreferences() {
			return false;
		}

		@Override
		public boolean supportsRepositories() {
			return false;
		}

		@Override
		public boolean supportsHelp() {
			return false;
		}

		@Override
		public boolean supportsReporting() {
			return false;
		}
	}

	public static class EmptyReverseEngineeringNotifier implements
			ReverseEngineeringNotifier {

		@Override
		public void notifyMessage(String resourceKey, String... values) {
			if (values != null) {
				for (String aValue : values) {
					System.out.println(aValue);
				}
			}
		}
	}

	protected void loadSQL(Connection aConnection, String aResource)
			throws IOException, SQLException {
		BufferedReader theReader = new BufferedReader(new InputStreamReader(
				getClass().getResourceAsStream(aResource)));
		Statement theStatement = aConnection.createStatement();
		while (theReader.ready()) {
			String theLine = theReader.readLine();
			if (StringUtils.isNotEmpty(theLine)) {
				theLine = theLine.trim();
			}
			if (StringUtils.isNotEmpty(theLine)) {
				System.out.println(theLine);
				theStatement.execute(theLine);
			}
		}
		theStatement.close();

		theReader.close();
	}

	protected void loadSingleSQL(Connection aConnection, String aResource)
			throws IOException, SQLException {

		String theSQL = readResourceFile(aResource);

		Statement theStatement = aConnection.createStatement();
		StringTokenizer theST = new StringTokenizer(theSQL, ";");
		while (theST.hasMoreTokens()) {
			String theSingleSQL = theST.nextToken();
			if (StringUtils.isNotEmpty(theSingleSQL)) {
				theStatement.execute(theSingleSQL);
			}
		}
		theStatement.close();
	}

	public String getDBServerName() {
		String theName = System.getProperty("mogwai.test.db.server.name");
		if (StringUtils.isEmpty(theName)) {
			theName = "127.0.0.1";
		}
		return theName;
	}

}
