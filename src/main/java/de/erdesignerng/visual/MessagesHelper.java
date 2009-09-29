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
package de.erdesignerng.visual;

import java.awt.Component;

import javax.swing.JOptionPane;

import de.erdesignerng.ERDesignerBundle;
import de.mogwai.common.i18n.ResourceHelper;

public final class MessagesHelper {

    private MessagesHelper() {
    }

    public static ResourceHelper getResourceHelper() {
        return ResourceHelper.getResourceHelper(ERDesignerBundle.BUNDLE_NAME);
    }

    public static void displayErrorMessage(Component aParent, String aMessage) {

        String theErrorText = getResourceHelper().getText(ERDesignerBundle.ERROR);
        JOptionPane.showMessageDialog(aParent, aMessage, theErrorText, JOptionPane.ERROR_MESSAGE);
    }

    public static String askForInput(Component aParent, String aMessage, String aInitialValue) {

        String theText = getResourceHelper().getText(aMessage);
        return JOptionPane.showInputDialog(aParent, theText, aInitialValue);
    }

    public static void displayInfoMessage(Component aParent, String aMessage) {

        String theInfoText = getResourceHelper().getText(ERDesignerBundle.INFORMATION);
        JOptionPane.showMessageDialog(aParent, aMessage, theInfoText, JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean displayQuestionMessage(Component aParent, String aMessageKey, Object... aReplacementValues) {
        String theQuestionText = getResourceHelper().getText(ERDesignerBundle.QUESTION);
        String theMessage = getResourceHelper().getFormattedText(aMessageKey, aReplacementValues);
        return JOptionPane.showConfirmDialog(aParent, theMessage, theQuestionText, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;

    }
}
