package de.erdesignerng.visual.editor.databrowser;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import de.erdesignerng.visual.scaffolding.ScaffoldingUtils;
import de.erdesignerng.visual.scaffolding.ScaffoldingWrapper;
import de.mogwai.common.client.looks.components.DefaultButton;
import de.mogwai.common.client.looks.components.DefaultScrollPane;

import javax.swing.*;
import java.sql.SQLException;
import java.util.Map;

/**
 * View for the RowEditor.
 */
public class RowEditorView extends JPanel {

    private final DefaultButton okButton = new DefaultButton();

    private final DefaultButton cancelButton = new DefaultButton();

    private ScaffoldingWrapper content;

    public RowEditorView(PaginationDataModel aModel, int aRow) throws SQLException {
        initialize(aModel, aRow);
    }

    private void initialize(PaginationDataModel aModel, int aRow) throws SQLException {

        String theColDef = "2dlu,fill:250dlu:grow,2dlu";
        String theRowDef = "2dlu,fill:200dlu:grow,10dlu,p,2dlu";

        FormLayout theLayout = new FormLayout(theColDef, theRowDef);
        setLayout(theLayout);

        CellConstraints cons = new CellConstraints();

        Map<String, Object> theRow = aModel.getRowData(aRow);

        content = ScaffoldingUtils.createScaffoldingPanelFor(aModel.getResultSetMetaData(), theRow);
        add(new DefaultScrollPane(content.getComponent()), cons.xy(2, 2));

        JPanel thePanel = new JPanel();

        theColDef = "60dlu,2dlu:grow,60dlu,";
        theRowDef = "p";

        theLayout = new FormLayout(theColDef, theRowDef);
        thePanel.setLayout(theLayout);

        thePanel.add(okButton, cons.xy(1, 1));
        okButton.setText("Ok");

        thePanel.add(cancelButton, cons.xy(3, 1));
        cancelButton.setText("Cancel");

        add(thePanel, cons.xy(2, 4));
    }

    public DefaultButton getOkButton() {
        return okButton;
    }

    public DefaultButton getCancelButton() {
        return cancelButton;
    }
}