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

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.visual.editor.exception.ExceptionEditor;
import de.mogwai.common.client.looks.components.DefaultDialog;
import de.mogwai.common.client.looks.components.action.DefaultAction;

import javax.swing.JPanel;
import javax.swing.KeyStroke;
import java.awt.Component;
import java.awt.event.KeyEvent;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 */
public abstract class BaseEditor extends DefaultDialog implements DialogConstants {

    private int modalResult;

    private JPanel jContentPane;

    protected final DefaultAction okAction = new DefaultAction(e -> commandOk(), this, ERDesignerBundle.OK);

    protected final DefaultAction cancelAction = new DefaultAction(e -> commandCancel(), this, ERDesignerBundle.CANCEL);

    /**
     * Initialize.
     *
     * @param aParent the parent Frame
     * @param aTitle  the title
     */
    public BaseEditor(Component aParent, String aTitle) {
        super(aParent, ERDesignerBundle.BUNDLE_NAME, aTitle);
        initialize();
    }

    /**
     * This method initializes this.
     */
    private void initialize() {
        setSize(300, 200);

        JPanel theContentPane = getJContentPane();

        setContentPane(theContentPane);
        setResizable(false);
        setModal(true);

        cancelAction.putValue(DefaultAction.HOTKEY_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
    }

    /**
     * This method initializes jContentPane.
     *
     * @return JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new javax.swing.JPanel();
            jContentPane.setLayout(new java.awt.BorderLayout());
        }
        return jContentPane;
    }

    /**
     * Set the dialogs modal result and hide it.
     *
     * @param aModalResult the modal result.
     */
    public void setModalResult(int aModalResult) {
        modalResult = aModalResult;
        setVisible(false);
    }

    @Override
    public int showModal() {
        modalResult = DialogConstants.MODAL_RESULT_CANCEL;
        setVisible(true);

        return modalResult;
    }

    public abstract void applyValues() throws Exception;

    /**
     * Is called by the default cancel action.
     */
    protected void commandCancel() {
        setModalResult(DialogConstants.MODAL_RESULT_CANCEL);
    }

    /**
     * is called by the default ok action.
     */
    protected void commandOk() {
        setModalResult(DialogConstants.MODAL_RESULT_OK);
    }

    /**
     * Log a fatal error using the default exception dialogue.
     *
     * @param e the exception to log
     */
    protected void logFatalError(Exception e) {
        ExceptionEditor theEditor = new ExceptionEditor(this, e);
        theEditor.showModal();
    }
}