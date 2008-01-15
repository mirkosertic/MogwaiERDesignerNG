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
package de.erdesignerng.visual.editor;

import java.awt.Component;

import javax.swing.JOptionPane;

import de.erdesignerng.ERDesignerBundle;
import de.mogwai.common.client.looks.components.DefaultDialog;

/**
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-15 19:22:44 $
 */
public abstract class BaseEditor extends DefaultDialog implements DialogConstants {

    private int modalResult;

    private javax.swing.JPanel jContentPane = null;

    private Component parent;

    /**
     * Initialize.
     * 
     * @param parent
     *            the parent Frame
     */
    public BaseEditor(Component aParent, String aTitle) {
        super(aParent, ERDesignerBundle.BUNDLE_NAME, aTitle);
        initialize();
        parent = aParent;
    }

    /**
     * This method initializes this.
     */
    private void initialize() {
        setSize(300, 200);
        setContentPane(getJContentPane());
        setResizable(false);
        setModal(true);
    }

    /**
     * This method initializes jContentPane.
     * 
     * @return javax.swing.JPanel
     */
    private javax.swing.JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new javax.swing.JPanel();
            jContentPane.setLayout(new java.awt.BorderLayout());
        }
        return jContentPane;
    }

    /**
     * Set the dialogs modal result and hide it.
     * 
     * @param aModalResult
     *            the modal result.
     */
    public void setModalResult(int aModalResult) {
        modalResult = aModalResult;
        super.setVisible(false);
    }

    public int showModal() {
        modalResult = DialogConstants.MODAL_RESULT_CANCEL;
        setVisible(true);
        return modalResult;
    }

    protected void displayErrorMessage(String aMessage) {

        String theErrorText = getResourceHelper().getText(ERDesignerBundle.ERROR);
        JOptionPane.showMessageDialog(this, aMessage, theErrorText, JOptionPane.ERROR_MESSAGE);
    }

    protected void displayInfoMessage(String aMessage) {

        String theInfoText = getResourceHelper().getText(ERDesignerBundle.INFORMATION);
        JOptionPane.showMessageDialog(this, aMessage, theInfoText, JOptionPane.INFORMATION_MESSAGE);
    }

    protected boolean displayQuestionMessage(String aMessageKey) {
        String theQuestionText = getResourceHelper().getText(ERDesignerBundle.QUESTION);
        String theMessage = getResourceHelper().getText(aMessageKey);
        return JOptionPane.showConfirmDialog(this, theMessage, theQuestionText, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;

    }

    public abstract void applyValues() throws Exception;

    protected void commandCancel() {

        setModalResult(DialogConstants.MODAL_RESULT_CANCEL);
    }

}
