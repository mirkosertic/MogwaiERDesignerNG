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
package de.erdesignerng.model.check;

import de.erdesignerng.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to check a database model with a defined rule base.
 */
public class ModelChecker {

    private List<ModelCheck> checks = new ArrayList();
    private List<ModelError> errors = new ArrayList<ModelError>();

    public ModelChecker() {
        checks.add(new TableWithPrimaryKeryCheck());
        checks.add(new ForeignKeyWithoutIndexCheck());
    }

    public void check(Model aModel) {
        for (ModelCheck theCheck : checks) {
            theCheck.check(aModel, this);
        }
    }

    public void addError(ModelError anError) {
        errors.add(anError);
    }

    public List<ModelError> getErrors() {
        return errors;
    }
}
