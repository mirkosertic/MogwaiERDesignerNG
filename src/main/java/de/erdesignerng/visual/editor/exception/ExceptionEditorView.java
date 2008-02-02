package de.erdesignerng.visual.editor.exception;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mogwai.common.client.looks.components.DefaultButton;
import de.mogwai.common.client.looks.components.DefaultTextArea;

/**
 * Visual class exceptioneditorview.
 * 
 * Created with Mogwai FormMaker 0.6.
 */
public class ExceptionEditorView extends JPanel {

    private JPanel buttonPanel;

    private DefaultButton closeButton;

    private DefaultTextArea exceptionText;

    /**
     * Constructor.
     */
    public ExceptionEditorView() {
        initialize();
        
        exceptionText.setEditable(false);
    }

    /**
     * Initialize method.
     */
    private void initialize() {

        String rowDef = "2dlu,fill:250dlu,10dlu,p,2dlu";
        String colDef = "2dlu,250dlu,2dlu";

        FormLayout layout = new FormLayout(colDef, rowDef);
        setLayout(layout);

        CellConstraints cons = new CellConstraints();

        this.add(getButtonPanel(), cons.xywh(2, 4, 1, 1));
        this.add(new JScrollPane(getExceptionText()), cons.xywh(2, 2, 1, 1));

        buildGroups();
    }

    /**
     * Getter method for component buttonpanel.
     * 
     * @return the initialized component
     */
    public JPanel getButtonPanel() {

        if (buttonPanel == null) {
            buttonPanel = new JPanel();

            String rowDef = "p";
            String colDef = "fill:60dlu,2dlu:grow,fill:60dlu,2dlu,fill:60dlu";

            FormLayout layout = new FormLayout(colDef, rowDef);
            buttonPanel.setLayout(layout);

            CellConstraints cons = new CellConstraints();

            buttonPanel.add(getCloseButton(), cons.xywh(1, 1, 1, 1));
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
     * Getter method for component exceptiontext.
     * 
     * @return the initialized component
     */
    public DefaultTextArea getExceptionText() {

        if (exceptionText == null) {
            exceptionText = new DefaultTextArea();
            exceptionText.setName("exceptiontext");
        }

        return exceptionText;
    }

    /**
     * Initialize method.
     */
    private void buildGroups() {

    }
}