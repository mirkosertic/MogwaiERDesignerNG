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
package de.erdesignerng.visual.editor.relation;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.dialect.RelationProperties;
import de.erdesignerng.model.*;
import de.erdesignerng.visual.editor.BaseEditor;
import de.erdesignerng.visual.scaffolding.ScaffoldingUtils;
import de.erdesignerng.visual.scaffolding.ScaffoldingWrapper;
import de.mogwai.common.client.binding.BindingInfo;
import de.mogwai.common.client.binding.adapter.RadioButtonAdapter;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.DefaultPanel;

import java.awt.*;
import java.util.Map;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 */
public class RelationEditor extends BaseEditor {

    private final Model model;

    private final BindingInfo<Relation> bindingInfo = new BindingInfo<>();

    private RelationEditorView editingView;

    private RelationProperties relationProperties;

    private ScaffoldingWrapper relationPropertiesWrapper;


    /**
     * Create a relation editor.
     *
     * @param aModel  the model
     * @param aParent the parent container
     */
    public RelationEditor(Model aModel, Component aParent) {
        super(aParent, ERDesignerBundle.RELATIONEDITOR);

        initialize();

        model = aModel;

        bindingInfo.addBinding("name", editingView.getRelationName(), true);
        bindingInfo.addBinding("mapping", new RelationAttributesPropertyAdapter(editingView.getAttributeMappingTable(), getResourceHelper()));

        RadioButtonAdapter theOnDeleteAdapter = new RadioButtonAdapter();
        theOnDeleteAdapter.addMapping(CascadeType.NOTHING, editingView.getOnDeleteCascadeNothing());
        theOnDeleteAdapter.addMapping(CascadeType.CASCADE, editingView.getOnDeleteCascade());
        theOnDeleteAdapter.addMapping(CascadeType.SETNULL, editingView.getOnDeleteSetNull());
        theOnDeleteAdapter.addMapping(CascadeType.RESTRICT, editingView.getOnDeleteRestrict());
        bindingInfo.addBinding("onDelete", theOnDeleteAdapter);

        RadioButtonAdapter theOnUpdateAdapter = new RadioButtonAdapter();
        theOnUpdateAdapter.addMapping(CascadeType.NOTHING, editingView.getOnUpdateCascadeNothing());
        theOnUpdateAdapter.addMapping(CascadeType.CASCADE, editingView.getOnUpdateCascade());
        theOnUpdateAdapter.addMapping(CascadeType.SETNULL, editingView.getOnUpdateSetNull());
        theOnUpdateAdapter.addMapping(CascadeType.RESTRICT, editingView.getOnUpdateRestrict());
        bindingInfo.addBinding("onUpdate", theOnUpdateAdapter);

        bindingInfo.configure();
    }

    /**
     * This method initializes this.
     */
    private void initialize() {

        editingView = new RelationEditorView();
        editingView.getOKButton().setAction(okAction);
        editingView.getCancelButton().setAction(cancelAction);

        setContentPane(editingView);
        setResizable(false);

        pack();

        UIInitializer.getInstance().initialize(this);
    }

    public void initializeFor(Relation aRelation) {
        bindingInfo.setDefaultModel(aRelation);
        bindingInfo.model2view();

        relationProperties = model.getDialect().createRelationPropertiesFor(aRelation);
        DefaultPanel theProperties = editingView.getPropertiesPanel();
        relationPropertiesWrapper = ScaffoldingUtils.createScaffoldingPanelFor(model, relationProperties);

        theProperties.setLayout(new BorderLayout());
        theProperties.add(relationPropertiesWrapper.getComponent(), BorderLayout.CENTER);

        UIInitializer.getInstance().initialize(theProperties);

        pack();
    }

    @Override
    protected void commandOk() {
        if (bindingInfo.validate().isEmpty()) {
            setModalResult(MODAL_RESULT_OK);
        }
    }

    @Override
    public void applyValues() throws Exception {
        Relation theRelation = bindingInfo.getDefaultModel();

        relationPropertiesWrapper.save();
        relationProperties.copyTo(theRelation);

        if (!model.getRelations().contains(theRelation)) {

            bindingInfo.view2model();

            // Try to detect if there were foreign key suggestions used
            for (Map.Entry<IndexExpression, Attribute<Table>> theEntry : theRelation.getMapping().entrySet()) {
                Attribute<Table> theAttribute = theEntry.getValue();
                if (theAttribute.getOwner() == null) {
                    // A suggested foreign key was used, so add it to the table
                    model.addAttributeToTable(theRelation.getImportingTable(), theAttribute);
                }
            }

            model.addRelation(theRelation);

        } else {

            Relation theTempRelation = theRelation.clone();
            bindingInfo.setDefaultModel(theTempRelation);
            bindingInfo.view2model();

            if (theRelation.isModified(theTempRelation, false)) {
                model.changeRelation(theRelation, theTempRelation);
            }
        }
    }
}