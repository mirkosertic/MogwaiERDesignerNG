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
package de.erdesignerng.plugins.squirrel.action;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.dialect.DataType;
import de.erdesignerng.dialect.Dialect;
import de.erdesignerng.dialect.db2.DB2Dialect;
import de.erdesignerng.dialect.h2.H2Dialect;
import de.erdesignerng.dialect.mssql.MSSQLDialect;
import de.erdesignerng.dialect.mysql.MySQLInnoDBDialect;
import de.erdesignerng.dialect.oracle.OracleDialect;
import de.erdesignerng.dialect.postgres.PostgresDialect;
import de.erdesignerng.plugins.squirrel.SquirrelMogwaiController;
import de.erdesignerng.plugins.squirrel.SquirrelMogwaiPluginDelegate;
import de.erdesignerng.plugins.squirrel.SquirrelMogwaiPluginResources;
import de.mogwai.common.i18n.ResourceHelper;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-02-02 18:49:15 $
 */
public class StartMogwaiAction extends SquirrelAction implements ISessionAction {

	protected ISession session;

	protected final SquirrelMogwaiPluginDelegate plugin;

	public StartMogwaiAction(IApplication aApplication, SquirrelMogwaiPluginResources aResources,
			SquirrelMogwaiPluginDelegate aPlugin) {
		super(aApplication, aResources);
		plugin = aPlugin;
	}

	protected Dialect determineDialect(ISession aSession) {
		if (DialectFactory.isOracle(aSession.getMetaData())) {
			return new OracleDialect();
		}
		if (DialectFactory.isMySQL(aSession.getMetaData())) {
			return new MySQLInnoDBDialect();
		}
		if (DialectFactory.isMySQL5(aSession.getMetaData())) {
			return new MySQLInnoDBDialect();
		}
		if (DialectFactory.isMSSQLServer(aSession.getMetaData())) {
			return new MSSQLDialect();
		}
		if (DialectFactory.isPostgreSQL(aSession.getMetaData())) {
			return new PostgresDialect();
		}
		if (DialectFactory.isDB2(aSession.getMetaData())) {
			return new DB2Dialect();
		}
		if (DialectFactory.isH2(aSession.getMetaData())) {
			return new H2Dialect();
		}
		return null;
	}

	public void actionPerformed(ActionEvent evt) {
		if (session != null) {

			Dialect theDialect = determineDialect(session);
			if (theDialect != null) {

				session.showMessage("Mogwai Dialect : " + theDialect.getClass().getName());
				for (DataType theDataType : theDialect.getDataTypes()) {
					session.showMessage(" Supported datatype : " + theDataType.getName());
				}

				SquirrelMogwaiController theNewController = null;

				SquirrelMogwaiController[] controllers = plugin.getGraphControllers(session);
				if ((controllers == null) || (0 == controllers.length)) {
					theNewController = plugin.createNewGraphControllerForSession(session, theDialect);
					theNewController.startReverseEngineering();
				}
			} else {
				ResourceHelper theHelper = ResourceHelper.getResourceHelper(ERDesignerBundle.BUNDLE_NAME);
				session.showErrorMessage(theHelper.getText(ERDesignerBundle.DIALECTISNOTSUPPORTED));
			}
		}
	}

	public void setSession(ISession aSession) {
		session = aSession;
	}
}