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

import java.sql.Connection;

import de.erdesignerng.dialect.ConnectionProvider;
import de.erdesignerng.util.ApplicationPreferences;

/**
 * Implementation of a connection provider. 
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:29 $
 */
public class ModelBasedConnectionProvider implements ConnectionProvider {
    
    private Model model;
    
    public ModelBasedConnectionProvider(Model aModel) {
        model = aModel;
    }

    public Connection createConnection(ApplicationPreferences aPreferences) throws Exception {
        return model.createConnection(aPreferences);
    }

    public String createScriptStatementSeparator() {
        return model.getDialect().createSQLGenerator().createScriptStatementSeparator();
    }

    public boolean generatesManagedConnection() {
        return model.getDialect().generatesManagedConnection();
    }
}