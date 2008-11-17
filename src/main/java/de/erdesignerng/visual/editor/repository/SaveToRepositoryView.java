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
package de.erdesignerng.visual.editor.repository;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.erdesignerng.ERDesignerBundle;
import de.mogwai.common.client.looks.components.DefaultButton;
import de.mogwai.common.client.looks.components.DefaultComboBox;
import de.mogwai.common.client.looks.components.DefaultLabel;
import de.mogwai.common.client.looks.components.DefaultRadioButton;
import de.mogwai.common.client.looks.components.DefaultTextField;

/**
 * View for the save to dictionary dialog.
 *  
 * @author msertic
 */
public class SaveToRepositoryView extends JPanel {

    private DefaultRadioButton newEntryButton = new DefaultRadioButton(ERDesignerBundle.CREATENEWENTRY);

    private DefaultTextField newNameField = new DefaultTextField();

    private DefaultRadioButton existingEntryButton = new DefaultRadioButton(ERDesignerBundle.OVERWRITEEXISTINGENTRY);

    private DefaultComboBox existingNameBox = new DefaultComboBox();
    
    private DefaultTextField existingNameField = new DefaultTextField();

    private DefaultButton okButton = new DefaultButton();

    private DefaultButton cancelButton = new DefaultButton();

    public SaveToRepositoryView() {
        initialize();
    }

    private void initialize() {

        String theColDef = "2dlu,10dlu,2dlu,50dlu,2dlu,fill:100dlu:grow,2dlu";
        String theRowDef = "2dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,50dlu,2dlu,p,2dlu";

        FormLayout theLayout = new FormLayout(theColDef, theRowDef);
        setLayout(theLayout);

        CellConstraints cons = new CellConstraints();

        add(newEntryButton, cons.xywh(2, 2, 5, 1));
        add(new DefaultLabel(ERDesignerBundle.NAME), cons.xy(4, 4));
        add(newNameField, cons.xy(6, 4));

        add(existingEntryButton, cons.xywh(2, 6, 5, 1));
        add(new DefaultLabel(ERDesignerBundle.NAME), cons.xy(4, 8));
        add(existingNameBox, cons.xy(6, 8));

        add(new DefaultLabel(ERDesignerBundle.NEWNAME), cons.xy(4, 10));
        add(existingNameField, cons.xy(6, 10));

        JPanel thePanel = new JPanel();

        theColDef = "60dlu,fill:2dlu:grow,60dlu";
        theRowDef = "p";

        theLayout = new FormLayout(theColDef, theRowDef);
        thePanel.setLayout(theLayout);

        thePanel.add(okButton, cons.xy(1, 1));
        okButton.setText("Ok");
        thePanel.add(cancelButton, cons.xy(3, 1));
        cancelButton.setText("Cancel");

        add(thePanel, cons.xyw(2, 13, 5));
        
        existingNameBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                commandChangeRepositoryEntry();
            }});
    }
    
    /**
     * Is invoked when the repository entry was changed.
     */
    public void commandChangeRepositoryEntry() {
    }

    /**
     * @return the newEntryButton
     */
    public DefaultRadioButton getNewEntryButton() {
        return newEntryButton;
    }

    /**
     * @return the newNameField
     */
    public DefaultTextField getNewNameField() {
        return newNameField;
    }

    /**
     * @return the existingEntryButton
     */
    public DefaultRadioButton getExistingEntryButton() {
        return existingEntryButton;
    }

    /**
     * @return the existingNameBox
     */
    public DefaultComboBox getExistingNameBox() {
        return existingNameBox;
    }

    /**
     * @return the okButton
     */
    public DefaultButton getOkButton() {
        return okButton;
    }

    /**
     * @return the cancelButton
     */
    public DefaultButton getCancelButton() {
        return cancelButton;
    }

    /**
     * @return the existingNameField
     */
    public DefaultTextField getExistingNameField() {
        return existingNameField;
    }
}