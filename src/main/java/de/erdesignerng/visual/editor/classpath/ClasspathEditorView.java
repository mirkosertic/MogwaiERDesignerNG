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

        String theColDef = "2dlu,150dlu,2dlu,p,2";
        String theRowDef = "2dlu,150dlu,p,2dlu,p,10dlu,p,2dlu";

        FormLayout theLayout = new FormLayout(theColDef, theRowDef);
        setLayout(theLayout);

        CellConstraints cons = new CellConstraints();

        add(new JScrollPane(classpath), cons.xywh(2, 2, 1, 4));
        add(addButton, cons.xy(4, 3));
        add(removeButton, cons.xy(4, 5));

        JPanel thePanel = new JPanel();

        theColDef = "fill:2dlu:grow,50dlu,2dlu,50dlu,2dlu";
        theRowDef = "p";

        theLayout = new FormLayout(theColDef, theRowDef);
        thePanel.setLayout(theLayout);

        thePanel.add(okButton, cons.xy(2, 1));
        okButton.setText("Ok");
        thePanel.add(cancelButton, cons.xy(4, 1));
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
