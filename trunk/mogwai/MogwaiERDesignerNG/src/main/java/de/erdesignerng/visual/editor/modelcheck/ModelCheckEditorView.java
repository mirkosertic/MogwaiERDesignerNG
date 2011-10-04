package de.erdesignerng.visual.editor.modelcheck;

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
public class ModelCheckEditorView extends JPanel {

    private DefaultList errorList;

    private JPanel buttonPanel;

    private DefaultButton closeButton;

    private DefaultButton quickFixButton;

    /**
     * Constructor.
     */
    public ModelCheckEditorView() {
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

        add(getErrorList().getScrollPane(), cons.xywh(2, 2, 1, 1));
        add(getButtonPanel(), cons.xywh(2, 4, 1, 1));
    }

    /**
     * Getter method for component SQLList.
     *
     * @return the initialized component
     */
    public DefaultList getErrorList() {

        if (errorList == null) {
            errorList = new DefaultList();
            errorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }

        return errorList;
    }

    /**
     * Getter method for component buttonpanel.
     *
     * @return the initialized component
     */
    public JPanel getButtonPanel() {

        if (buttonPanel == null) {
            buttonPanel = new JPanel();

            String rowDef = "10dlu,p";
            String colDef = "fill:80dlu,2dlu:grow,fill:80dlu,2dlu,fill:80dlu";

            FormLayout layout = new FormLayout(colDef, rowDef);
            buttonPanel.setLayout(layout);

            CellConstraints cons = new CellConstraints();

            buttonPanel.add(getQuickFixButton(), cons.xywh(1, 2, 1, 1));
            buttonPanel.add(getCloseButton(), cons.xywh(5, 2, 1, 1));
            buttonPanel.setName("buttonpanel");
        }

        return buttonPanel;
    }

    public DefaultButton getCloseButton() {

        if (closeButton == null) {
            closeButton = new DefaultButton();
            closeButton.setActionCommand("Close");
            closeButton.setName("closebutton");
            closeButton.setText("Close");
        }

        return closeButton;
    }

    public DefaultButton getQuickFixButton() {

        if (quickFixButton == null) {
            quickFixButton = new DefaultButton();
            quickFixButton.setActionCommand("QuickFix");
            quickFixButton.setName("QuickFix");
            quickFixButton.setText("QuickFix");
        }

        return quickFixButton;
    }
}