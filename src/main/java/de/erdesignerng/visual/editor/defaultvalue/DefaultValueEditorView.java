package de.erdesignerng.visual.editor.defaultvalue;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.erdesignerng.ERDesignerBundle;
import de.mogwai.common.client.looks.components.DefaultButton;
import de.mogwai.common.client.looks.components.DefaultLabel;
import de.mogwai.common.client.looks.components.DefaultTabbedPane;
import de.mogwai.common.client.looks.components.DefaultTabbedPaneTab;
import de.mogwai.common.client.looks.components.DefaultTextField;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-02-01 17:20:29 $
 */
public class DefaultValueEditorView extends JPanel {

    private javax.swing.JList defaultValueList;

    private DefaultButton newButton;

    private DefaultButton deleteButton;

    private DefaultTabbedPane detailTabbedPane;

    private DefaultTabbedPaneTab component6;

    private DefaultLabel component9;

    private DefaultLabel component10;

    private DefaultTextField defaultValueName;

    private DefaultTextField declaration;

    private javax.swing.JButton updateButton;

    private javax.swing.JButton okButton;

    private javax.swing.JButton cancelButton;

    /**
     * Constructor.
     */
    public DefaultValueEditorView() {
        this.initialize();
    }

    /**
     * Initialize method.
     */
    private void initialize() {

        String rowDef = "2dlu,p,140dlu,2dlu,p,20dlu,p,2dlu";
        String colDef = "2dlu,50dlu:grow,2dlu,50dlu:grow,2dlu,50dlu:grow,2dlu,80dlu:grow,2dlu,60dlu:grow,2dlu";

        FormLayout layout = new FormLayout(colDef, rowDef);
        this.setLayout(layout);

        CellConstraints cons = new CellConstraints();

        this.add(new JScrollPane(this.getDefaultValueList()), cons.xywh(2, 2, 5, 2));
        this.add(this.getNewButton(), cons.xywh(2, 5, 1, 1));
        this.add(this.getDeleteButton(), cons.xywh(6, 5, 1, 1));
        this.add(this.getDetailTabbedPane(), cons.xywh(8, 2, 3, 4));
        this.add(this.getOkButton(), cons.xywh(8, 7, 1, 1));
        this.add(this.getCancelButton(), cons.xywh(10, 7, 1, 1));

        this.buildGroups();
    }

    /**
     * Initialize method.
     */
    private void buildGroups() {

    }

    /**
     * Getter method for component DefaultValueList.
     * 
     * @return the initialized component
     */
    public javax.swing.JList getDefaultValueList() {

        if (defaultValueList == null) {
            defaultValueList = new javax.swing.JList();
            defaultValueList.setName("DefaultValueList");
        }

        return defaultValueList;
    }

    /**
     * Getter method for component NewButton.
     * 
     * @return the initialized component
     */
    public DefaultButton getNewButton() {

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
    public DefaultButton getDeleteButton() {

        if (deleteButton == null) {
            deleteButton = new DefaultButton(ERDesignerBundle.DELETE);
        }

        return deleteButton;
    }

    /**
     * Getter method for component DetailTabbedPane.
     * 
     * @return the initialized component
     */
    public javax.swing.JTabbedPane getDetailTabbedPane() {

        if (detailTabbedPane == null) {
            detailTabbedPane = new DefaultTabbedPane();
            detailTabbedPane.addTab(null, this.getDetailTab());
            detailTabbedPane.setSelectedIndex(0);
        }

        return detailTabbedPane;
    }

    /**
     * Getter method for component Component_6.
     * 
     * @return the initialized component
     */
    public JPanel getDetailTab() {

        if (component6 == null) {
            component6 = new DefaultTabbedPaneTab(getDetailTabbedPane(), ERDesignerBundle.DEFAULTVALUEPROPERTIES);

            String rowDef = "2dlu,p,2dlu,p,20dlu,p,2dlu";
            String colDef = "2dlu,left:60dlu,2dlu,60dlu:grow,2dlu";

            FormLayout layout = new FormLayout(colDef, rowDef);
            component6.setLayout(layout);

            CellConstraints cons = new CellConstraints();

            component6.add(this.getComponent9(), cons.xywh(2, 2, 1, 1));
            component6.add(this.getComponent10(), cons.xywh(2, 4, 1, 1));
            component6.add(this.getDefaultValueName(), cons.xywh(4, 2, 1, 1));
            component6.add(this.getDeclaration(), cons.xywh(4, 4, 1, 1));
            component6.add(this.getUpdateButton(), cons.xywh(4, 6, 1, 1));
            component6.setName("Component_6");
        }

        return component6;
    }

    /**
     * Getter method for component Component_9.
     * 
     * @return the initialized component
     */
    public javax.swing.JLabel getComponent9() {

        if (component9 == null) {
            component9 = new DefaultLabel(ERDesignerBundle.NAME);
        }

        return component9;
    }

    /**
     * Getter method for component Component_10.
     * 
     * @return the initialized component
     */
    public javax.swing.JLabel getComponent10() {

        if (component10 == null) {
            component10 = new DefaultLabel(ERDesignerBundle.DECLRATATION);
        }

        return component10;
    }

    /**
     * Getter method for component DefaultValueName.
     * 
     * @return the initialized component
     */
    public DefaultTextField getDefaultValueName() {

        if (defaultValueName == null) {
            defaultValueName = new DefaultTextField();
            defaultValueName.setName("DefaultValueName");
        }

        return defaultValueName;
    }

    /**
     * Getter method for component Declaration.
     * 
     * @return the initialized component
     */
    public DefaultTextField getDeclaration() {

        if (declaration == null) {
            declaration = new DefaultTextField();
            declaration.setName("Declaration");
        }

        return declaration;
    }

    /**
     * Getter method for component UpdateButton.
     * 
     * @return the initialized component
     */
    public javax.swing.JButton getUpdateButton() {

        if (updateButton == null) {
            updateButton = new javax.swing.JButton();
        }

        return updateButton;
    }

    /**
     * Getter method for component OkButton.
     * 
     * @return the initialized component
     */
    public javax.swing.JButton getOkButton() {

        if (okButton == null) {
            okButton = new javax.swing.JButton();
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
            cancelButton = new javax.swing.JButton();
        }

        return cancelButton;
    }
}
