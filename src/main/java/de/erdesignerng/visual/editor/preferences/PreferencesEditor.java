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
import de.erdesignerng.util.ApplicationPreferences;
import de.erdesignerng.visual.ERDesignerGraph;
import de.erdesignerng.visual.common.ERDesignerComponent;
import de.erdesignerng.visual.editor.BaseEditor;
import de.erdesignerng.visual.editor.DialogConstants;
import de.mogwai.common.client.looks.UIInitializer;

/**
 * Editor for the database connection.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 */
public class PreferencesEditor extends BaseEditor {

    private PreferencesEditorView view;

    private ApplicationPreferences preferences;

    private ERDesignerComponent component;

    public PreferencesEditor(ERDesignerGraph aParent, ApplicationPreferences aPreferences,
            ERDesignerComponent aComponent) {
        super(aParent, ERDesignerBundle.PREFERENCES);

        view = new PreferencesEditorView(aPreferences);

        initialize();

        preferences = aPreferences;
        component = aComponent;
    }

    private void initialize() {

        view.getOkButton().setAction(okAction);
        view.getCancelButton().setAction(cancelAction);

        setContentPane(view);
        setResizable(false);

        pack();

        UIInitializer.getInstance().initialize(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applyValues() throws Exception {
    }

    @Override
    protected void commandOk() {
        if (view.getPreferences().applyValues(preferences)) {
            setModalResult(DialogConstants.MODAL_RESULT_OK);
            component.refreshPreferences(preferences);
        }
    }
}