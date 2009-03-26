package de.erdesignerng.visual.editor.view;

import nickyb.sqleonardo.querybuilder.QueryBuilder;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.erdesignerng.ERDesignerBundle;
import de.mogwai.common.client.looks.components.DefaultButton;
import de.mogwai.common.client.looks.components.DefaultLabel;
import de.mogwai.common.client.looks.components.DefaultPanel;
import de.mogwai.common.client.looks.components.DefaultScrollPane;
import de.mogwai.common.client.looks.components.DefaultTabbedPane;
import de.mogwai.common.client.looks.components.DefaultTabbedPaneTab;
import de.mogwai.common.client.looks.components.DefaultTextArea;
import de.mogwai.common.client.looks.components.DefaultTextField;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 */
public class ViewEditorView extends DefaultPanel {

    private DefaultLabel component1;

    private DefaultTextField entityName;

    private DefaultTabbedPane mainTabbedPane;

    private DefaultTabbedPaneTab tableCommentsTab;

    private DefaultTextArea tableComment;

    private DefaultButton okButton;

    private DefaultButton cancelButton;
    
    private QueryBuilder builder = new QueryBuilder();

    /**
     * Constructor.
     */
    public ViewEditorView() {
        initialize();
    }

    /**
     * Initialize method.
     */
    private void initialize() {

        String rowDef = "2dlu,p,2dlu,p,fill:220dlu,10dlu,p,2dlu";
        String colDef = "2dlu,left:45dlu,2dlu,fill:140dlu:grow,fill:60dlu,2dlu,fill:60dlu,2dlu";

        FormLayout layout = new FormLayout(colDef, rowDef);
        setLayout(layout);

        CellConstraints cons = new CellConstraints();

        this.add(getComponent1(), cons.xywh(2, 2, 1, 1));
        this.add(getEntityName(), cons.xywh(4, 2, 4, 1));
        this.add(getMainTabbedPane(), cons.xywh(2, 4, 6, 2));
        this.add(getOkButton(), cons.xywh(5, 7, 1, 1));
        this.add(getCancelButton(), cons.xywh(7, 7, 1, 1));
    }

    /**
     * Getter method for component Component_1.
     * 
     * @return the initialized component
     */
    public javax.swing.JLabel getComponent1() {

        if (component1 == null) {
            component1 = new DefaultLabel(ERDesignerBundle.ENTITYNAME);
        }

        return component1;
    }

    /**
     * Getter method for component Entity_name.
     * 
     * @return the initialized component
     */
    public DefaultTextField getEntityName() {

        if (entityName == null) {
            entityName = new DefaultTextField();
            entityName.setName("Entity_name");
        }

        return entityName;
    }

    /**
     * Getter method for component MainTabbedPane.
     * 
     * @return the initialized component
     */
    public DefaultTabbedPane getMainTabbedPane() {

        if (mainTabbedPane == null) {
            mainTabbedPane = new DefaultTabbedPane();
            mainTabbedPane.addTab("Querydesigner", builder);
            mainTabbedPane.addTab(null, getTableCommentsTab());
            mainTabbedPane.setName("MainTabbedPane");
            mainTabbedPane.setSelectedIndex(0);
        }

        return mainTabbedPane;
    }

    /**
     * Getter method for component MainCommensTab.
     * 
     * @return the initialized component
     */
    public DefaultTabbedPaneTab getTableCommentsTab() {

        if (tableCommentsTab == null) {
            tableCommentsTab = new DefaultTabbedPaneTab(mainTabbedPane, ERDesignerBundle.COMMENTS);

            String rowDef = "2dlu,p,100dlu:grow,p,2dlu";
            String colDef = "2dlu,40dlu:grow,2dlu";

            FormLayout layout = new FormLayout(colDef, rowDef);
            tableCommentsTab.setLayout(layout);

            CellConstraints cons = new CellConstraints();

            tableCommentsTab.add(new DefaultScrollPane(getEntityComment()), cons.xywh(2, 2, 1, 3));
            tableCommentsTab.setName("MainCommensTab");
            tableCommentsTab.setVisible(false);
        }

        return tableCommentsTab;
    }

    /**
     * Getter method for component EntityComment.
     * 
     * @return the initialized component
     */
    public DefaultTextArea getEntityComment() {

        if (tableComment == null) {
            tableComment = new DefaultTextArea();
            tableComment.setName("EntityComment");
        }

        return tableComment;
    }

    /**
     * Getter method for component OkButton.
     * 
     * @return the initialized component
     */
    public javax.swing.JButton getOkButton() {

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
    public javax.swing.JButton getCancelButton() {

        if (cancelButton == null) {
            cancelButton = new DefaultButton(ERDesignerBundle.CANCEL);
        }

        return cancelButton;
    }

    /**
     * Gibt den Wert des Attributs <code>builder</code> zurück.
     * 
     * @return Wert des Attributs builder.
     */
    public QueryBuilder getBuilder() {
        return builder;
    }
}