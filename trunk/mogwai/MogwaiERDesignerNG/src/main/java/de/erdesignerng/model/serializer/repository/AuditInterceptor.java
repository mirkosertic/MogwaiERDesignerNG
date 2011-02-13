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
package de.erdesignerng.model.serializer.repository;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Interceptor for audit information.
 * 
 * @author mirkosertic
 */
public class AuditInterceptor extends EmptyInterceptor {

	public static final AuditInterceptor INSTANCE = new AuditInterceptor();

	protected String getCurrentUserId() {
		return System.getProperty("user.name");
	}

	@Override
	public boolean onSave(Object aEntity, Serializable aID, Object[] aStates, String[] aPropertyNames, Type[] aTypes) {

		String theCurrentUser = getCurrentUserId();

		if (theCurrentUser != null) {

			for (int i = 0; i < aPropertyNames.length; i++) {

				String thePropertyName = aPropertyNames[i];
				if ("creationDate".equals(thePropertyName)) {
					aStates[i] = new Timestamp(System.currentTimeMillis());
				}
				if ("creationUser".equals(thePropertyName)) {
					aStates[i] = theCurrentUser;
				}

			}

			return true;
		}

		return false;
	}

	@Override
	public boolean onFlushDirty(Object aEntity, Serializable aID, Object[] aCurrentState, Object[] aPreviousState,
			String[] aPropertyNames, Type[] aTypes) {

		String theCurrentUser = getCurrentUserId();

		if (theCurrentUser != null) {

			for (int i = 0; i < aPropertyNames.length; i++) {

				String thePropertyName = aPropertyNames[i];
				if ("lastModificationDate".equals(thePropertyName)) {
					aCurrentState[i] = new Timestamp(System.currentTimeMillis());
				}
				if ("lastModificationUser".equals(thePropertyName)) {
					aCurrentState[i] = theCurrentUser;
				}

			}

			return true;
		}

		return false;
	}
}
