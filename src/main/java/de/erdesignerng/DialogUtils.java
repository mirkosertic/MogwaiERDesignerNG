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
package de.erdesignerng;

import javax.swing.JOptionPane;

import de.mogwai.common.i18n.ResourceHelper;

public final class DialogUtils {

    private static final ResourceHelper HELPER = ResourceHelper.getResourceHelper(ERDesignerBundle.BUNDLE_NAME);

    private DialogUtils() {
    }
    
    public static String promptForPassword() {

        String theMesssage = HELPER.getText(ERDesignerBundle.PLEASEENTERDATABASEPASSWORD);
        String theTitle = HELPER.getText(ERDesignerBundle.CONNECTIONCONFIGURATION);
        return JOptionPane.showInputDialog(null, theMesssage, theTitle, JOptionPane.QUESTION_MESSAGE);
    }
}
