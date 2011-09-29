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
package de.erdesignerng.visual.editor.preferences;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.visual.common.ERDesignerComponent;
import de.erdesignerng.visual.editor.BaseEditor;
import de.erdesignerng.visual.editor.DialogConstants;
import de.mogwai.common.client.looks.UIInitializer;

import javax.swing.*;

/**
 * Editor for the database connection.
 *
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 */
public class PreferencesEditor extends BaseEditor {

    private final PreferencesEditorView view;

    private final ERDesignerComponent component;

    public PreferencesEditor(JComponent aParent, ERDesignerComponent aComponent) {
        super(aParent, ERDesignerBundle.PREFERENCES);

        view = new PreferencesEditorView();

        initialize();

        component = aComponent;
    }

    private void initialize() {

        view.getOkButton().setAction(okAction);
        view.getCancelButton().setAction(cancelAction);

        setContentPane(view);
        setResizable(false);

        UIInitializer.getInstance().initialize(this);

        pack();
    }

    @Override
    public void applyValues() throws Exception {
    }

    @Override
    protected void commandOk() {
        if (view.getPreferences().applyValues()) {
            setModalResult(DialogConstants.MODAL_RESULT_OK);
            component.refreshPreferences();
        }
    }
}