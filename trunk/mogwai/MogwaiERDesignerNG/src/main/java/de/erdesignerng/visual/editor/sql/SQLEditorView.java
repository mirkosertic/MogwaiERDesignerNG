package de.erdesignerng.visual.editor.sql;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import de.mogwai.common.client.looks.components.DefaultButton;
import de.mogwai.common.client.looks.components.DefaultList;

import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

/**
 * Visual class ModelCheckEditorView.
 * <p/>
 * Created with Mogwai FormMaker 0.6.
 */
public class SQLEditorView extends JPanel {

    private DefaultList sqlList;

    private JPanel buttonPanel;

    private DefaultButton closeButton;

    private DefaultButton deleteButton;

    private DefaultButton executeButton;

    private DefaultButton saveToFileButton;

    /**
     * Constructor.
     */
    public SQLEditorView() {
        initialize();
    }

    /**
     * Initialize method.
     */
    private void initialize() {

        String rowDef = "2dlu,fill:300dlu:grow,10dlu,p,2dlu";
        String colDef = "2dlu,fill:300dlu:grow,2dlu";

        FormLayout layout = new FormLayout(colDef, rowDef);
        setLayout(layout);

        CellConstraints cons = new CellConstraints();

        add(getSqlList().getScrollPane(), cons.xywh(2, 2, 1, 1));
        add(getButtonPanel(), cons.xywh(2, 4, 1, 1));
    }

    /**
     * Getter method for component SQLList.
     *
     * @return the initialized component
     */
    public DefaultList getSqlList() {

        if (sqlList == null) {
            sqlList = new DefaultList();
            sqlList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }

        return sqlList;
    }

    /**
     * Getter method for component buttonpanel.
     *
     * @return the initialized component
     */
    public JPanel getButtonPanel() {

        if (buttonPanel == null) {
            buttonPanel = new JPanel();

            String rowDef = "p,10dlu,p";
            String colDef = "fill:80dlu,2dlu:grow,fill:80dlu,2dlu,fill:80dlu";

            FormLayout layout = new FormLayout(colDef, rowDef);
            buttonPanel.setLayout(layout);

            CellConstraints cons = new CellConstraints();

            buttonPanel.add(getDeleteButton(), cons.xywh(1, 1, 1, 1));
            buttonPanel.add(getExecuteButton(), cons.xywh(3, 1, 1, 1));
            buttonPanel.add(getSaveToFileButton(), cons.xywh(5, 1, 1, 1));
            buttonPanel.add(getCloseButton(), cons.xywh(1, 3, 1, 1));
            buttonPanel.setName("buttonpanel");
        }

        return buttonPanel;
    }

    /**
     * Getter method for component closebutton.
     *
     * @return the initialized component
     */
    public DefaultButton getCloseButton() {

        if (closeButton == null) {
            closeButton = new DefaultButton();
            closeButton.setActionCommand("Close");
            closeButton.setName("closebutton");
            closeButton.setText("Close");
        }

        return closeButton;
    }

    /**
     * Getter method for component executeButton.
     *
     * @return the initialized component
     */
    public DefaultButton getExecuteButton() {

        if (executeButton == null) {
            executeButton = new DefaultButton();
            executeButton.setActionCommand("Execute");
            executeButton.setName("executeButton");
            executeButton.setText("Execute");
        }

        return executeButton;
    }

    /**
     * Getter method for component deleteButton.
     *
     * @return the initialized component
     */
    public DefaultButton getDeleteButton() {

        if (deleteButton == null) {
            deleteButton = new DefaultButton();
            deleteButton.setActionCommand("Execute");
            deleteButton.setName("executeButton");
            deleteButton.setText("Execute");
        }

        return deleteButton;
    }

    /**
     * Getter method for component saveToFileButton.
     *
     * @return the initialized component
     */
    public DefaultButton getSaveToFileButton() {

        if (saveToFileButton == null) {
            saveToFileButton = new DefaultButton();
            saveToFileButton.setActionCommand("saveToFile");
            saveToFileButton.setName("saveToFileButton");
            saveToFileButton.setText("saveToFile");
        }

        return saveToFileButton;
    }
}
