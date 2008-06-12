package de.erdesignerng.visual.editor.comment;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.erdesignerng.ERDesignerBundle;
import de.mogwai.common.client.looks.components.DefaultButton;
import de.mogwai.common.client.looks.components.DefaultFrame;
import de.mogwai.common.client.looks.components.DefaultLabel;
import de.mogwai.common.client.looks.components.DefaultSeparator;
import de.mogwai.common.client.looks.components.DefaultTextArea;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-06-12 20:14:59 $
 */
public class CommentEditorView extends JPanel {

    private DefaultLabel component1;

    private DefaultTextArea comment;

    private DefaultButton okButton;

    private DefaultButton cancelButton;
    
    private JPanel component8;    

    /**
     * Constructor.
     */
    public CommentEditorView() {
        initialize();
    }

    /**
     * Initialize method.
     */
    private void initialize() {

        String rowDef = "2dlu,p,2dlu,fill:160dlu,20dlu,p,2dlu";
        String colDef = "2dlu,60dlu,2dlu,fill:250dlu:grow,2dlu";

        FormLayout layout = new FormLayout(colDef, rowDef);
        setLayout(layout);

        CellConstraints cons = new CellConstraints();

        add(new DefaultSeparator(ERDesignerBundle.COMMENTPROPERTIES), cons.xywh(2, 2, 3, 1));
        add(getComponent1(), cons.xywh(2, 4, 1, 1));
        add(getComment().getScrollPane(), cons.xywh(4, 4, 1, 1));

        add(getComponent8(), cons.xywh(2, 6, 3, 1));
        
        buildGroups();
    }

    /**
     * Getter method for component Component_1.
     * 
     * @return the initialized component
     */
    public javax.swing.JLabel getComponent1() {

        if (component1 == null) {
            component1 = new DefaultLabel(ERDesignerBundle.COMMENTS);
        }

        return component1;
    }

    /**
     * Getter method for component Relationname.
     * 
     * @return the initialized component
     */
    public DefaultTextArea getComment() {

        if (comment == null) {
            comment = new DefaultTextArea();
        }

        return comment;
    }

    /**
     * Getter method for component OKButton.
     * 
     * @return the initialized component
     */
    public DefaultButton getOKButton() {

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
    public DefaultButton getCancelButton() {

        if (cancelButton == null) {
            cancelButton = new DefaultButton(ERDesignerBundle.CANCEL);
        }

        return cancelButton;
    }
    
    /**
     * Getter method for component Component_8.
     * 
     * @return the initialized component
     */
    public JPanel getComponent8() {

        if (component8 == null) {
            component8 = new JPanel();

            String rowDef = "p";
            String colDef = "60dlu,2dlu:grow,60dlu";

            FormLayout layout = new FormLayout(colDef, rowDef);
            component8.setLayout(layout);

            CellConstraints cons = new CellConstraints();

            component8.add(getOKButton(), cons.xywh(1, 1, 1, 1));
            component8.add(getCancelButton(), cons.xywh(3, 1, 1, 1));
            component8.setName("Component_8");
        }

        return component8;
    }    

    /**
     * Initialize method.
     */
    private void buildGroups() {
    }
    
    public static void main(String[] args) {
        
        DefaultFrame theFrame = new DefaultFrame(ERDesignerBundle.BUNDLE_NAME);
        theFrame.setContentPane(new CommentEditorView());
        theFrame.pack();
        theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        theFrame.setVisible(true);
    }

}
