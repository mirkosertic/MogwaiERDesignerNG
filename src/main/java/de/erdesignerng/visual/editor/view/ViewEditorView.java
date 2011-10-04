package de.erdesignerng.visual.editor.view;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.visual.components.DefaultEditorPane;
import de.erdesignerng.visual.components.SQLEditorKit;
import de.mogwai.common.client.looks.components.DefaultButton;
import de.mogwai.common.client.looks.components.DefaultLabel;
import de.mogwai.common.client.looks.components.DefaultPanel;
import de.mogwai.common.client.looks.components.DefaultScrollPane;
import de.mogwai.common.client.looks.components.DefaultTabbedPane;
import de.mogwai.common.client.looks.components.DefaultTabbedPaneTab;
import de.mogwai.common.client.looks.components.DefaultTextArea;
import de.mogwai.common.client.looks.components.DefaultTextField;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.text.EditorKit;
import java.awt.BorderLayout;

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

    private final DefaultEditorPane sqlText = new DefaultEditorPane();

    private DefaultTabbedPaneTab propertiesPanel;

    public ViewEditorView() {
        initialize();
    }

    /**
     * Initialize method.
     */
    private void initialize() {

        EditorKit editorKit = new SQLEditorKit();
        sqlText.setEditorKitForContentType("text/sql", editorKit);
        sqlText.setContentType("text/sql");

        String rowDef = "2dlu,p,2dlu,p,fill:220dlu,10dlu,p,2dlu";
        String colDef = "2dlu,left:45dlu,2dlu,fill:140dlu:grow,fill:60dlu,2dlu,fill:60dlu,2dlu";

        FormLayout layout = new FormLayout(colDef, rowDef);
        setLayout(layout);

        CellConstraints cons = new CellConstraints();

        add(getComponent1(), cons.xywh(2, 2, 1, 1));
        add(getEntityName(), cons.xywh(4, 2, 4, 1));
        add(getMainTabbedPane(), cons.xywh(2, 4, 6, 2));
        add(getOkButton(), cons.xywh(5, 7, 1, 1));
        add(getCancelButton(), cons.xywh(7, 7, 1, 1));
    }

    public JLabel getComponent1() {

        if (component1 == null) {
            component1 = new DefaultLabel(ERDesignerBundle.ENTITYNAME);
        }

        return component1;
    }

    public DefaultTextField getEntityName() {

        if (entityName == null) {
            entityName = new DefaultTextField();
            entityName.setName("Entity_name");
        }

        return entityName;
    }

    public DefaultTabbedPane getMainTabbedPane() {

        if (mainTabbedPane == null) {
            mainTabbedPane = new DefaultTabbedPane();
            mainTabbedPane.addTab("SQL", sqlText.getScrollPane());
            mainTabbedPane.addTab(null, getTableCommentsTab());
            mainTabbedPane.addTab(null, getPropertiesPanel());
            mainTabbedPane.setName("MainTabbedPane");
            mainTabbedPane.setSelectedIndex(0);
        }

        return mainTabbedPane;
    }

    public DefaultTabbedPaneTab getTableCommentsTab() {

        if (tableCommentsTab == null) {
            tableCommentsTab = new DefaultTabbedPaneTab(mainTabbedPane,
                    ERDesignerBundle.COMMENTS);

            String rowDef = "2dlu,p,100dlu:grow,p,2dlu";
            String colDef = "2dlu,40dlu:grow,2dlu";

            FormLayout layout = new FormLayout(colDef, rowDef);
            tableCommentsTab.setLayout(layout);

            CellConstraints cons = new CellConstraints();

            tableCommentsTab.add(new DefaultScrollPane(getEntityComment()),
                    cons.xywh(2, 2, 1, 3));
            tableCommentsTab.setName("MainCommentsTab");
            tableCommentsTab.setVisible(false);
        }

        return tableCommentsTab;
    }

    public DefaultTextArea getEntityComment() {

        if (tableComment == null) {
            tableComment = new DefaultTextArea();
            tableComment.setName("EntityComment");
        }

        return tableComment;
    }

    public JButton getOkButton() {

        if (okButton == null) {
            okButton = new DefaultButton(ERDesignerBundle.OK);
        }

        return okButton;
    }

    public JButton getCancelButton() {

        if (cancelButton == null) {
            cancelButton = new DefaultButton(ERDesignerBundle.CANCEL);
        }

        return cancelButton;
    }

    public DefaultEditorPane getSqlText() {
        return sqlText;
    }

    public DefaultTabbedPaneTab getPropertiesPanel() {
        if (propertiesPanel == null) {
            propertiesPanel = new DefaultTabbedPaneTab(mainTabbedPane,
                    ERDesignerBundle.PROPERTIES);
            propertiesPanel.setLayout(new BorderLayout());
        }
        return propertiesPanel;
    }

    public void disablePropertiesTab() {
        getMainTabbedPane().removeTabAt(2);
    }

}