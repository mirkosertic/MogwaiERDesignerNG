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
package de.erdesignerng.visual.editor.modelcheck;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.check.ModelChecker;
import de.erdesignerng.model.check.ModelError;
import de.erdesignerng.model.check.QuickFix;
import de.erdesignerng.util.ApplicationPreferences;
import de.erdesignerng.visual.common.OutlineComponent;
import de.erdesignerng.visual.editor.BaseEditor;
import de.erdesignerng.visual.editor.DialogConstants;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.action.ActionEventProcessor;
import de.mogwai.common.client.looks.components.action.DefaultAction;
import de.mogwai.common.client.looks.components.list.DefaultListModel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Editor for model checks.
 *
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:29 $
 */
public class ModelCheckEditor extends BaseEditor {

    private final DefaultAction closeAction = new DefaultAction(
            new ActionEventProcessor() {

                @Override
                public void processActionEvent(ActionEvent e) {
                    commandClose();
                }
            }, this, ERDesignerBundle.CLOSE);

    private final DefaultAction quickFixAction = new DefaultAction(
            new ActionEventProcessor() {

                @Override
                public void processActionEvent(ActionEvent e) {
                    commandApplyQuickFix();
                }
            }, this, ERDesignerBundle.APPLYQUICKFIX);


    private ModelCheckEditorView view = new ModelCheckEditorView();

    private Model model;

    public ModelCheckEditor(Component aParent, Model aModel) {
        super(aParent, ERDesignerBundle.MODELCHECKRESULT);

        model = aModel;

        initialize();

        view.getCloseButton().setAction(closeAction);
        view.getQuickFixButton().setAction(quickFixAction);

        quickFixAction.setEnabled(false);

        ModelChecker theChecker = new ModelChecker();
        theChecker.check(model);

        DefaultListModel theModel = view.getErrorList().getModel();
        for (ModelError theError : theChecker.getErrors()) {
            theModel.add(theError);
        }
        view.getErrorList().setCellRenderer(new ErrorRenderer());
        view.getErrorList().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        view.getErrorList().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                Object[] theSelection = view.getErrorList().getSelectedValues();
                quickFixAction
                        .setEnabled(theSelection != null && theSelection.length > 0);
            }
        });

    }

    private void initialize() {

        setContentPane(view);
        setResizable(true);

        pack();

        setMinimumSize(getSize());
        ApplicationPreferences.getInstance().setWindowSize(
                getClass().getSimpleName(), this);

        UIInitializer.getInstance().initialize(this);
    }

    @Override
    public void applyValues() throws Exception {
    }

    private void commandClose() {
        ApplicationPreferences.getInstance().updateWindowSize(
                getClass().getSimpleName(), this);
        setModalResult(DialogConstants.MODAL_RESULT_OK);
    }

    private void commandApplyQuickFix() {
        for (Object theEntry : view.getErrorList().getSelectedValues()) {
            ModelError theError = (ModelError) theEntry;
            QuickFix theFix = theError.getQuickFix();
            if (theFix != null) {
                try {
                    Object[] theAffectedObjects = theFix.applyTo(model);
                    for (Object theAffectedObject : theAffectedObjects) {
                        OutlineComponent.getDefault().refresh(model, theAffectedObject);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    theError.clearQuickFix();
                }
            }
        }
        view.getErrorList().clearSelection();
        view.getErrorList().invalidate();
        view.getErrorList().repaint();
    }
}