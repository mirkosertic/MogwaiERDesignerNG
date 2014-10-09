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

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.model.CascadeType;
import de.erdesignerng.util.ApplicationPreferences;
import de.mogwai.common.client.binding.BindingInfo;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.*;
import de.mogwai.common.i18n.ResourceHelper;
import de.mogwai.common.i18n.ResourceHelperProvider;

import javax.swing.*;

public class PreferencesPanel extends DefaultPanel implements
        ResourceHelperProvider {

    private final DefaultSpinner gridSize = new DefaultSpinner();

    private final DefaultTextField automaticRelationAttributePattern = new DefaultTextField();

    private final DefaultComboBox onDeleteDefault = new DefaultComboBox();

    private final DefaultComboBox onUpdateDefault = new DefaultComboBox();

    private final DefaultSpinner gridWidth = new DefaultSpinner();

    private final DefaultSpinner xmlIndentation = new DefaultSpinner();

    private BindingInfo<ApplicationPreferences> bindinginfo;

    public PreferencesPanel() {
        initialize();
    }

    private void initialize() {

        String theColDef = "2dlu,p,2dlu,p:grow,2dlu,20dlu,2";
        String theRowDef = "2dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,50dlu";

        FormLayout theLayout = new FormLayout(theColDef, theRowDef);
        setLayout(theLayout);

        CellConstraints cons = new CellConstraints();

        add(new DefaultLabel(ERDesignerBundle.EDITORGRIDSIZE), cons.xy(2, 2));
        add(gridSize, cons.xywh(4, 2, 3, 1));

        add(
                new DefaultLabel(
                        ERDesignerBundle.AUTOMATICRELATIONATTRIBUTEPATTERN),
                cons.xy(2, 4));
        add(automaticRelationAttributePattern, cons.xywh(4, 4, 3, 1));

        DefaultComboBoxModel theDefaultOnUpdateModel = new DefaultComboBoxModel();
        DefaultComboBoxModel theDefaultOnDeleteModel = new DefaultComboBoxModel();
        for (CascadeType theType : CascadeType.values()) {
            theDefaultOnUpdateModel.addElement(theType);
            theDefaultOnDeleteModel.addElement(theType);
        }

        add(new DefaultLabel(ERDesignerBundle.DEFAULTFORONDELETE), cons
                .xy(2, 6));
        add(onDeleteDefault, cons.xywh(4, 6, 3, 1));
        onDeleteDefault.setModel(theDefaultOnDeleteModel);

        add(new DefaultLabel(ERDesignerBundle.DEFAULTFORONUPDATE), cons
                .xy(2, 8));
        add(onUpdateDefault, cons.xywh(4, 8, 3, 1));
        onUpdateDefault.setModel(theDefaultOnUpdateModel);

        add(new DefaultLabel(ERDesignerBundle.GRIDSIZEAFTERREVERSEENGINEERING),
                cons.xy(2, 10));
        add(gridWidth, cons.xywh(4, 10, 3, 1));

        add(new DefaultLabel(ERDesignerBundle.XMLINDENTATION), cons.xy(2, 12));
        add(xmlIndentation, cons.xywh(4, 12, 3, 1));

        UIInitializer.getInstance().initialize(this);

        bindinginfo = new BindingInfo<>();
        bindinginfo.addBinding("gridSize", gridSize, true);
        bindinginfo.addBinding("automaticRelationAttributePattern",
                automaticRelationAttributePattern, true);
        bindinginfo.addBinding("onUpdateDefault", onUpdateDefault, true);
        bindinginfo.addBinding("onDeleteDefault", onDeleteDefault, true);
        bindinginfo.addBinding("gridWidthAfterReverseEngineering", gridWidth,
                true);
        bindinginfo.addBinding("xmlIndentation", xmlIndentation, true);

        bindinginfo.configure();
    }

    /**
     * Initialize the view with values from the preferences.
     */
    public void initValues() {
        bindinginfo.setDefaultModel(ApplicationPreferences.getInstance());
        bindinginfo.model2view();
    }

    /**
     * Apply the current view values to the preferences after validation. if
     * validation fails, no changes are made.
     *
     * @return true if validation is ok, else false
     */
    public boolean applyValues() {

        if (bindinginfo.validate().isEmpty()) {
            bindinginfo.view2model();
            return true;
        }
        return false;
    }

    @Override
    public ResourceHelper getResourceHelper() {
        return ResourceHelper.getResourceHelper(ERDesignerBundle.BUNDLE_NAME);
    }
}