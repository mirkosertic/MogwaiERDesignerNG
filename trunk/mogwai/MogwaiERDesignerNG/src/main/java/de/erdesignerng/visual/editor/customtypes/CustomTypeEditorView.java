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
package de.erdesignerng.visual.editor.customtypes;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import de.erdesignerng.ERDesignerBundle;
import de.mogwai.common.client.looks.components.DefaultButton;
import de.mogwai.common.client.looks.components.DefaultLabel;
import de.mogwai.common.client.looks.components.DefaultList;
import de.mogwai.common.client.looks.components.DefaultPanel;
import de.mogwai.common.client.looks.components.DefaultScrollPane;
import de.mogwai.common.client.looks.components.DefaultTabbedPane;
import de.mogwai.common.client.looks.components.DefaultTabbedPaneTab;
import de.mogwai.common.client.looks.components.DefaultTextArea;
import de.mogwai.common.client.looks.components.DefaultTextField;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.ListSelectionModel;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 */
public class CustomTypeEditorView extends DefaultPanel {

    private DefaultTabbedPane mainTabbedPane;

    private DefaultTabbedPaneTab attributesTab;

    private DefaultList typesList;

    private DefaultButton newButton;

    private DefaultButton deleteButton;

    private DefaultTabbedPane component15;

    private DefaultTabbedPaneTab attributesGeneralTab;

    private DefaultLabel component20;

    private DefaultTextField typeName;

    private DefaultButton updateDomainButton;

    private DefaultButton okButton;

    private DefaultButton cancelButton;

    private DefaultTextArea typeddl;

    public CustomTypeEditorView() {
        initialize();
    }

    /**
     * Initialize method.
     */
    private void initialize() {

        String rowDef = "2dlu,p,2dlu,p,fill:220dlu,p,20dlu,p,2dlu";
        String colDef = "2dlu,left:45dlu,2dlu,fill:140dlu:grow,fill:60dlu,2dlu,fill:60dlu,2dlu";

        FormLayout layout = new FormLayout(colDef, rowDef);
        setLayout(layout);

        CellConstraints cons = new CellConstraints();

        add(getMainTabbedPane(), cons.xywh(2, 4, 6, 2));
        add(getOkButton(), cons.xywh(5, 8, 1, 1));
        add(getCancelButton(), cons.xywh(7, 8, 1, 1));

    }

    /**
     * Getter method for component MainTabbedPane.
     *
     * @return the initialized component
     */
    public DefaultTabbedPane getMainTabbedPane() {

        if (mainTabbedPane == null) {
            mainTabbedPane = new DefaultTabbedPane();
            mainTabbedPane.addTab(null, getTypesTab());
            mainTabbedPane.setName("MainTabbedPane");
            mainTabbedPane.setSelectedIndex(0);
        }

        return mainTabbedPane;
    }

    /**
     * Getter method for component AttributesTab.
     *
     * @return the initialized component
     */
    public DefaultTabbedPaneTab getTypesTab() {

        if (attributesTab == null) {
            attributesTab = new DefaultTabbedPaneTab(mainTabbedPane,
                    ERDesignerBundle.TYPES);

            String rowDef = "2dlu,p,2dlu,p,165dlu:grow,p,2dlu,p,2dlu";
            String colDef = "2dlu,50dlu:grow,2dlu,50dlu:grow,2dlu,50dlu:grow,11dlu:grow,2dlu,11dlu:grow,2dlu,80dlu:grow,2dlu,70dlu:grow,2dlu";

            FormLayout layout = new FormLayout(colDef, rowDef);
            attributesTab.setLayout(layout);

            CellConstraints cons = new CellConstraints();

            // this.m_attributestab.add(this.getUpButton(), cons.xywh(7, 2, 1,
            // 1));
            // this.m_attributestab.add(this.getDownButton(), cons
            // .xywh(9, 2, 1, 1));
            attributesTab.add(new DefaultScrollPane(getTypesList()), cons.xywh(
                    2, 4, 8, 3));
            attributesTab.add(getNewButton(), cons.xywh(2, 8, 1, 1));
            attributesTab.add(getDeleteButton(), cons.xywh(6, 8, 4, 1));
            attributesTab.add(getComponent15(), cons.xywh(11, 2, 3, 5));
            attributesTab.add(getUpdateTypeButton(), cons.xywh(13, 8, 1, 1));
            attributesTab.setName("AttributesTab");
        }

        return attributesTab;
    }

    public DefaultList getTypesList() {

        if (typesList == null) {
            typesList = new DefaultList();
            typesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }

        return typesList;
    }

    public JButton getNewButton() {

        if (newButton == null) {
            newButton = new DefaultButton(ERDesignerBundle.NEW);
        }

        return newButton;
    }

    public JButton getDeleteButton() {

        if (deleteButton == null) {
            deleteButton = new DefaultButton(ERDesignerBundle.DELETE);
        }

        return deleteButton;
    }

    public DefaultTabbedPane getComponent15() {

        if (component15 == null) {
            component15 = new DefaultTabbedPane();
            component15.addTab(null, getTypesGeneralTab());
            component15.setName("Component_15");
            component15.setSelectedIndex(0);
        }

        return component15;
    }

    /**
     * Getter method for component AttributesGeneralTab.
     *
     * @return the initialized component
     */
    public DefaultTabbedPaneTab getTypesGeneralTab() {

        if (attributesGeneralTab == null) {
            attributesGeneralTab = new DefaultTabbedPaneTab(component15,
                    ERDesignerBundle.GENERAL);

            String rowDef = "2dlu,p,2dlu,p,2dlu,fill:50dlu:grow";
            String colDef = "2dlu,left:40dlu,2dlu,60dlu:grow,2dlu";

            FormLayout layout = new FormLayout(colDef, rowDef);
            attributesGeneralTab.setLayout(layout);

            CellConstraints cons = new CellConstraints();

            attributesGeneralTab.add(getComponent20(), cons.xywh(2, 2, 1, 1));
            attributesGeneralTab.add(getTypeName(), cons.xywh(4, 2, 1, 1));
            attributesGeneralTab.add(
                    new DefaultLabel(ERDesignerBundle.SQL), cons.xywh(2,
                    4, 1, 1));

            attributesGeneralTab.add(getTypeddl().getScrollPane(), cons.xywh(2, 6, 3, 1));

            attributesGeneralTab.setName("AttributesGeneralTab");
        }

        return attributesGeneralTab;
    }

    /**
     * Getter method for component Component_20.
     *
     * @return the initialized component
     */
    public JLabel getComponent20() {

        if (component20 == null) {
            component20 = new DefaultLabel(ERDesignerBundle.NAME);
        }

        return component20;
    }

    /**
     * Getter method for component AttributeName.
     *
     * @return the initialized component
     */
    public DefaultTextField getTypeName() {

        if (typeName == null) {
            typeName = new DefaultTextField();
            typeName.setName("AttributeName");
        }

        return typeName;
    }

    /**
     * Getter method for component UpdateAttributeButton.
     *
     * @return the initialized component
     */
    public JButton getUpdateTypeButton() {

        if (updateDomainButton == null) {
            updateDomainButton = new DefaultButton(ERDesignerBundle.UPDATE);
        }

        return updateDomainButton;
    }

    /**
     * Getter method for component OkButton.
     *
     * @return the initialized component
     */
    public JButton getOkButton() {

        if (okButton == null) {
            okButton = new DefaultButton(ERDesignerBundle.OK);
        }

        return okButton;
    }

    /**
     * Getter method for component CancelButton.
     *
     * @return the initialized component
     */
    public JButton getCancelButton() {

        if (cancelButton == null) {
            cancelButton = new DefaultButton(ERDesignerBundle.CANCEL);
        }

        return cancelButton;
    }

    public DefaultTextArea getTypeddl() {
        if (typeddl == null) {
            typeddl = new DefaultTextArea();
        }
        return typeddl;
    }
}