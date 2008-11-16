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
package de.erdesignerng.visual.editor.classpath;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mogwai.common.client.looks.components.DefaultButton;
import de.mogwai.common.client.looks.components.DefaultList;

public class ClasspathEditorView extends JPanel {

    private DefaultList classpath = new DefaultList();

    private DefaultButton addButton = new DefaultButton();

    private DefaultButton removeButton = new DefaultButton();

    private DefaultButton okButton = new DefaultButton();

    private DefaultButton cancelButton = new DefaultButton();

    public ClasspathEditorView() {
        initialize();
    }

    private void initialize() {

        String theColDef = "2dlu,fill:150dlu:grow,2dlu,p,2dlu";
        String theRowDef = "2dlu,fill:150dlu,p,2dlu,p,10dlu,p,2dlu";

        FormLayout theLayout = new FormLayout(theColDef, theRowDef);
        setLayout(theLayout);

        CellConstraints cons = new CellConstraints();

        add(new JScrollPane(classpath), cons.xywh(2, 2, 1, 4));
        add(addButton, cons.xy(4, 3));
        add(removeButton, cons.xy(4, 5));

        JPanel thePanel = new JPanel();

        theColDef = "60dlu,fill:2dlu:grow,60dlu";
        theRowDef = "p";

        theLayout = new FormLayout(theColDef, theRowDef);
        thePanel.setLayout(theLayout);

        thePanel.add(okButton, cons.xy(1, 1));
        okButton.setText("Ok");
        thePanel.add(cancelButton, cons.xy(3, 1));
        cancelButton.setText("Cancel");

        add(thePanel, cons.xyw(2, 7, 3));

        classpath.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }

    public JButton getAddButton() {
        return addButton;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    public JList getClasspath() {
        return classpath;
    }

    public JButton getOkButton() {
        return okButton;
    }

    public JButton getRemoveButton() {
        return removeButton;
    }
}
