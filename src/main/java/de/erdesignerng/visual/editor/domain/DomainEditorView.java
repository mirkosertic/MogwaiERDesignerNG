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
package de.erdesignerng.visual.editor.domain;

import javax.swing.JFrame;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.erdesignerng.ERDesignerBundle;
import de.mogwai.common.client.looks.components.DefaultButton;
import de.mogwai.common.client.looks.components.DefaultComboBox;
import de.mogwai.common.client.looks.components.DefaultLabel;
import de.mogwai.common.client.looks.components.DefaultList;
import de.mogwai.common.client.looks.components.DefaultPanel;
import de.mogwai.common.client.looks.components.DefaultScrollPane;
import de.mogwai.common.client.looks.components.DefaultSpinner;
import de.mogwai.common.client.looks.components.DefaultTabbedPane;
import de.mogwai.common.client.looks.components.DefaultTabbedPaneTab;
import de.mogwai.common.client.looks.components.DefaultTextField;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 */
public class DomainEditorView extends DefaultPanel {

    private DefaultTabbedPane mainTabbedPane;

    private DefaultTabbedPaneTab attributesTab;

    private DefaultList domainList;

    private DefaultButton newButton;

    private DefaultButton deleteButton;

    private DefaultTabbedPane component15;

    private DefaultTabbedPaneTab attributesGeneralTab;

    private DefaultLabel component20;

    private DefaultTextField domainName;

    private DefaultLabel component42;

    private DefaultButton updateDomainButton;

    private DefaultLabel label1;

    private DefaultButton okButton;

    private DefaultButton cancelButton;
    
    private DefaultComboBox dataType = new DefaultComboBox();
    
    private DefaultSpinner sizeSpinner = new DefaultSpinner();
    
    private DefaultSpinner fractionSpinner = new DefaultSpinner();
    
    private DefaultSpinner scaleSpinner = new DefaultSpinner();
    
    /**
     * Constructor.
     */
    public DomainEditorView() {
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

        this.add(getMainTabbedPane(), cons.xywh(2, 4, 6, 2));
        this.add(getOkButton(), cons.xywh(5, 8, 1, 1));
        this.add(getCancelButton(), cons.xywh(7, 8, 1, 1));

        buildGroups();
    }

    /**
     * Getter method for component MainTabbedPane.
     * 
     * @return the initialized component
     */
    public DefaultTabbedPane getMainTabbedPane() {

        if (mainTabbedPane == null) {
            mainTabbedPane = new DefaultTabbedPane();
            mainTabbedPane.addTab(null, getDomainsTab());
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
    public DefaultTabbedPaneTab getDomainsTab() {

        if (attributesTab == null) {
            attributesTab = new DefaultTabbedPaneTab(mainTabbedPane, ERDesignerBundle.DOMAINS);

            String rowDef = "2dlu,p,2dlu,p,165dlu:grow,p,2dlu,p,2dlu";
            String colDef = "2dlu,50dlu:grow,2dlu,50dlu:grow,2dlu,50dlu:grow,11dlu:grow,2dlu,11dlu:grow,2dlu,80dlu:grow,2dlu,70dlu:grow,2dlu";

            FormLayout layout = new FormLayout(colDef, rowDef);
            attributesTab.setLayout(layout);

            CellConstraints cons = new CellConstraints();

            // this.m_attributestab.add(this.getUpButton(), cons.xywh(7, 2, 1,
            // 1));
            // this.m_attributestab.add(this.getDownButton(), cons
            // .xywh(9, 2, 1, 1));
            attributesTab.add(new DefaultScrollPane(getDomainList()), cons.xywh(2, 4, 8, 3));
            attributesTab.add(getNewButton(), cons.xywh(2, 8, 1, 1));
            attributesTab.add(getDeleteButton(), cons.xywh(6, 8, 4, 1));
            attributesTab.add(getComponent15(), cons.xywh(11, 2, 3, 5));
            attributesTab.add(getUpdateDomainButton(), cons.xywh(13, 8, 1, 1));
            attributesTab.setName("AttributesTab");
        }

        return attributesTab;
    }

    /**
     * Getter method for component AttributeList.
     * 
     * @return the initialized component
     */
    public DefaultList getDomainList() {

        if (domainList == null) {
            domainList = new DefaultList();
        }

        return domainList;
    }

    /**
     * Getter method for component NewButton.
     * 
     * @return the initialized component
     */
    public javax.swing.JButton getNewButton() {

        if (newButton == null) {
            newButton = new DefaultButton(ERDesignerBundle.NEW);
        }

        return newButton;
    }

    /**
     * Getter method for component DeleteButton.
     * 
     * @return the initialized component
     */
    public javax.swing.JButton getDeleteButton() {

        if (deleteButton == null) {
            deleteButton = new DefaultButton(ERDesignerBundle.DELETE);
        }

        return deleteButton;
    }

    /**
     * Getter method for component Component_15.
     * 
     * @return the initialized component
     */
    public DefaultTabbedPane getComponent15() {

        if (component15 == null) {
            component15 = new DefaultTabbedPane();
            component15.addTab(null, getDomainsGeneralTab());
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
    public DefaultTabbedPaneTab getDomainsGeneralTab() {

        if (attributesGeneralTab == null) {
            attributesGeneralTab = new DefaultTabbedPaneTab(component15, ERDesignerBundle.GENERAL);

            String rowDef = "2dlu,p,2dlu,p,10dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu";
            String colDef = "2dlu,left:40dlu,2dlu,60dlu:grow,2dlu";

            FormLayout layout = new FormLayout(colDef, rowDef);
            attributesGeneralTab.setLayout(layout);

            CellConstraints cons = new CellConstraints();

            attributesGeneralTab.add(getComponent20(), cons.xywh(2, 2, 1, 1));
            attributesGeneralTab.add(getDomainName(), cons.xywh(4, 2, 1, 1));
            attributesGeneralTab.add(new DefaultLabel(ERDesignerBundle.DATATYPE), cons.xywh(2, 4, 1, 1));
            attributesGeneralTab.add(getDataType(), cons.xywh(4, 4, 1, 1));
            attributesGeneralTab.add(new DefaultLabel(ERDesignerBundle.SIZE), cons.xywh(2, 6, 1, 1));
            attributesGeneralTab.add(getSizeSpinner(), cons.xywh(4, 6, 1, 1));
            attributesGeneralTab.add(new DefaultLabel(ERDesignerBundle.FRACTION), cons.xywh(2, 8, 1, 1));
            attributesGeneralTab.add(getFractionSpinner(), cons.xywh(4, 8, 1, 1));
            attributesGeneralTab.add(new DefaultLabel(ERDesignerBundle.SCALE), cons.xywh(2, 10, 1, 1));
            attributesGeneralTab.add(getScaleSpinner(), cons.xywh(4, 10, 1, 1));
            
            attributesGeneralTab.setName("AttributesGeneralTab");
        }

        return attributesGeneralTab;
    }

    /**
     * Getter method for component Component_20.
     * 
     * @return the initialized component
     */
    public javax.swing.JLabel getComponent20() {

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
    public DefaultTextField getDomainName() {

        if (domainName == null) {
            domainName = new DefaultTextField();
            domainName.setName("AttributeName");
        }

        return domainName;
    }

    /**
     * Getter method for component Component_42.
     * 
     * @return the initialized component
     */
    public javax.swing.JLabel getComponent42() {

        if (component42 == null) {
            component42 = new DefaultLabel(ERDesignerBundle.DEFAULT);
        }

        return component42;
    }

    /**
     * Getter method for component UpdateAttributeButton.
     * 
     * @return the initialized component
     */
    public javax.swing.JButton getUpdateDomainButton() {

        if (updateDomainButton == null) {
            updateDomainButton = new DefaultButton(ERDesignerBundle.UPDATE);
        }

        return updateDomainButton;
    }

    /**
     * Getter method for component Label1.
     * 
     * @return the initialized component
     */
    public DefaultLabel getLabel1() {

        if (label1 == null) {
            label1 = new DefaultLabel(ERDesignerBundle.NAME);
        }

        return label1;
    }

    /**
     * Getter method for component OkButton.
     * 
     * @return the initialized component
     */
    public javax.swing.JButton getOkButton() {

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
    public javax.swing.JButton getCancelButton() {

        if (cancelButton == null) {
            cancelButton = new DefaultButton(ERDesignerBundle.CANCEL);
        }

        return cancelButton;
    }

    /**
     * Initialize method.
     */
    private void buildGroups() {

    }

    /**
     * @return the dataType
     */
    public DefaultComboBox getDataType() {
        return dataType;
    }

    /**
     * @return the precisionSpinner
     */
    public DefaultSpinner getFractionSpinner() {
        return fractionSpinner;
    }

    /**
     * @return the scaleSpinner
     */
    public DefaultSpinner getScaleSpinner() {
        return scaleSpinner;
    }

    /**
     * @return the sizeSpinner
     */
    public DefaultSpinner getSizeSpinner() {
        return sizeSpinner;
    }
    
    public static void main(String[] args) {
        JFrame theFrame = new JFrame();
        theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        theFrame.setContentPane(new DomainEditorView());
        theFrame.pack();
        theFrame.setVisible(true);
    }
}