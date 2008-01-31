package de.erdesignerng.visual.editor.domain;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.model.Domain;
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
 * @version $Date: 2008-01-31 20:08:52 $
 */
public class DomainEditorView extends DefaultPanel {

    private DefaultList<Domain> domainList = new DefaultList<Domain>();

    private DefaultButton newButton;

    private DefaultButton deleteButton;

    private DefaultTabbedPane detailTabbedPane;

    private DefaultTabbedPaneTab domainPropertiesTab;

    private DefaultLabel nameLabel = new DefaultLabel(ERDesignerBundle.NAME);

    private DefaultLabel datatypeLabel = new DefaultLabel(ERDesignerBundle.DATATYPE);

    private DefaultLabel sizeLabel = new DefaultLabel(ERDesignerBundle.SIZE);

    private DefaultLabel precisionLabel = new DefaultLabel(ERDesignerBundle.PRECISION);
    
    private DefaultLabel scaleLabel = new DefaultLabel(ERDesignerBundle.SCALE);

    private DefaultTextField domainname = new DefaultTextField();
    
    private DefaultComboBox dataType = new DefaultComboBox();
    
    private DefaultSpinner sizeSpinner = new DefaultSpinner();
    
    private DefaultSpinner precisionSpinner = new DefaultSpinner();
    
    private DefaultSpinner scaleSpinner = new DefaultSpinner();

    private DefaultButton updatebutton;

    private DefaultButton okbutton;

    private DefaultButton cancelbutton;

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

        String rowDef = "2dlu,p,140dlu,2dlu,p,20dlu,p,2dlu";
        String colDef = "2dlu,50dlu:grow,2dlu,50dlu:grow,2dlu,50dlu:grow,2dlu,80dlu:grow,2dlu,60dlu:grow,2dlu";

        FormLayout layout = new FormLayout(colDef, rowDef);
        setLayout(layout);

        CellConstraints cons = new CellConstraints();

        add(new DefaultScrollPane(getDomainList()), cons.xywh(2, 2, 5, 2));
        add(getNewButton(), cons.xywh(2, 5, 1, 1));
        add(getDeleteButton(), cons.xywh(6, 5, 1, 1));
        add(getDetailTabbedPane(), cons.xywh(8, 2, 3, 4));
        add(getOkButton(), cons.xywh(8, 7, 1, 1));
        add(getCancelButton(), cons.xywh(10, 7, 1, 1));

        buildGroups();
    }

    /**
     * Initialize method.
     */
    private void buildGroups() {

    }

    /**
     * Getter method for component DomainList.
     * 
     * @return the initialized component
     */
    public DefaultList<Domain> getDomainList() {

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
     * Getter method for component DetailTabbedPane.
     * 
     * @return the initialized component
     */
    public javax.swing.JTabbedPane getDetailTabbedPane() {

        if (detailTabbedPane == null) {
            detailTabbedPane = new DefaultTabbedPane();
            detailTabbedPane.addTab(null, this.getDomainPropertiesTab());
            detailTabbedPane.setSelectedIndex(0);
        }

        return detailTabbedPane;
    }

    /**
     * Getter method for component Component_6.
     * 
     * @return the initialized component
     */
    public DefaultTabbedPaneTab getDomainPropertiesTab() {

        if (domainPropertiesTab == null) {
            domainPropertiesTab = new DefaultTabbedPaneTab(detailTabbedPane, ERDesignerBundle.DOMAINPROPERTIES);

            String rowDef = "2dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,8dlu,p,2dlu";
            String colDef = "2dlu,left:60dlu,2dlu,60dlu:grow,2dlu";

            FormLayout layout = new FormLayout(colDef, rowDef);
            domainPropertiesTab.setLayout(layout);

            CellConstraints cons = new CellConstraints();

            domainPropertiesTab.add(getNameLabel(), cons.xywh(2, 2, 1, 1));
            domainPropertiesTab.add(getDomainName(), cons.xywh(4, 2, 1, 1));
            
            domainPropertiesTab.add(getDatatypeLabel(), cons.xywh(2, 4, 1, 1));
            domainPropertiesTab.add(getDataType(), cons.xywh(4, 4, 1, 1));
            
            domainPropertiesTab.add(getSizeLabel(), cons.xywh(2, 6, 1, 1));
            domainPropertiesTab.add(getSizeSpinner(), cons.xywh(4, 6, 1, 1));
            
            domainPropertiesTab.add(getPrecisionLabel(), cons.xywh(2, 8, 1, 1));
            domainPropertiesTab.add(getPrecisionSpinner(), cons.xywh(4, 8, 1, 1));
            
            domainPropertiesTab.add(getScaleLabel(), cons.xywh(2, 10, 1, 1));
            domainPropertiesTab.add(getScaleSpinner(), cons.xywh(4, 10, 1, 1));
            
            domainPropertiesTab.add(getUpdateButton(), cons.xywh(4, 12, 1, 1));
        }

        return domainPropertiesTab;
    }

    /**
     * Getter method for component Component_9.
     * 
     * @return the initialized component
     */
    public DefaultLabel getNameLabel() {

        return nameLabel;
    }

    /**
     * Getter method for component Component_10.
     * 
     * @return the initialized component
     */
    public DefaultLabel getDatatypeLabel() {

        return datatypeLabel;
    }

    /**
     * Getter method for component DomainName.
     * 
     * @return the initialized component
     */
    public DefaultTextField getDomainName() {

        return domainname;
    }

    /**
     * Getter method for component UpdateButton.
     * 
     * @return the initialized component
     */
    public javax.swing.JButton getUpdateButton() {

        if (updatebutton == null) {
            updatebutton = new DefaultButton(ERDesignerBundle.UPDATE);
        }

        return updatebutton;
    }

    /**
     * Getter method for component OkButton.
     * 
     * @return the initialized component
     */
    public javax.swing.JButton getOkButton() {

        if (okbutton == null) {
            okbutton = new DefaultButton(ERDesignerBundle.OK);
        }

        return okbutton;
    }

    /**
     * Getter method for component CancelButton.
     * 
     * @return the initialized component
     */
    public javax.swing.JButton getCancelButton() {

        if (cancelbutton == null) {
            cancelbutton = new DefaultButton(ERDesignerBundle.CANCEL);
        }

        return cancelbutton;
    }

    /**
     * @return the precisionLabel
     */
    public DefaultLabel getPrecisionLabel() {
        return precisionLabel;
    }

    /**
     * @return the precisionSpinner
     */
    public DefaultSpinner getPrecisionSpinner() {
        return precisionSpinner;
    }

    /**
     * @return the scaleLabel
     */
    public DefaultLabel getScaleLabel() {
        return scaleLabel;
    }

    /**
     * @return the scaleSpinner
     */
    public DefaultSpinner getScaleSpinner() {
        return scaleSpinner;
    }

    /**
     * @return the sizeLabel
     */
    public DefaultLabel getSizeLabel() {
        return sizeLabel;
    }

    /**
     * @return the sizeSpinner
     */
    public DefaultSpinner getSizeSpinner() {
        return sizeSpinner;
    }

    /**
     * @return the dataType
     */
    public DefaultComboBox getDataType() {
        return dataType;
    }
}
