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
package de.erdesignerng.visual.editor.reverseengineer;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import de.mogwai.common.client.looks.components.DefaultButton;
import de.mogwai.common.client.looks.components.DefaultPanel;
import de.mogwai.common.client.looks.components.DefaultTree;

import javax.swing.JPanel;

/**
 * Visual class TablesSelectEditorView.
 * <p/>
 * Created with Mogwai FormMaker 0.7.
 */
public class TablesSelectEditorView extends JPanel {

    private JPanel contentPanel;

    private DefaultTree tableList;

    private DefaultButton okButton;

    private DefaultButton cancelButton;

    private final DefaultButton selectAll = new DefaultButton();

    private final DefaultButton deselectAll = new DefaultButton();

    private final DefaultButton invertSelection = new DefaultButton();

    /**
     * Constructor.
     */
    public TablesSelectEditorView() {
        initialize();
    }

    /**
     * Initialize method.
     */
    private void initialize() {

        String rowDef = "8dlu,fill:250dlu,8dlu,p,2dlu";
        String colDef = "2dlu,fill:60dlu,2dlu:grow,fill:60dlu,2dlu";

        FormLayout layout = new FormLayout(colDef, rowDef);
        setLayout(layout);

        CellConstraints cons = new CellConstraints();

        add(getContentPanel(), cons.xywh(2, 2, 3, 1));
        add(getOkButton(), cons.xywh(2, 4, 1, 1));
        add(getCancelButton(), cons.xywh(4, 4, 1, 1));

        buildGroups();
    }

    /**
     * Getter method for component ContentPanel.
     *
     * @return the initialized component
     */
    public JPanel getContentPanel() {

        if (contentPanel == null) {
            contentPanel = new JPanel();

            String rowDef = "2dlu,fill:200dlu:grow,2dlu,p,2dlu";
            String colDef = "fill:180dlu:grow";

            FormLayout layout = new FormLayout(colDef, rowDef);
            contentPanel.setLayout(layout);

            CellConstraints cons = new CellConstraints();

            contentPanel.add(getTableList().getScrollPane(), cons.xywh(1, 2, 1,
                    1));

            DefaultPanel thePanel = new DefaultPanel();
            thePanel.setLayout(new FormLayout(
                    "fill:60dlu:grow,2dlu,fill:60dlu:grow", "p,2dlu,p"));
            thePanel.add(selectAll, cons.xy(1, 1));
            thePanel.add(deselectAll, cons.xy(3, 1));
            thePanel.add(invertSelection, cons.xy(1, 3));

            contentPanel.add(thePanel, cons.xywh(1, 4, 1, 1));

        }

        return contentPanel;
    }

    /**
     * Getter method for component DatabaseView.
     *
     * @return the initialized component
     */
    public DefaultTree getTableList() {

        if (tableList == null) {
            tableList = new DefaultTree();
            tableList.setCellRenderer(CheckboxTreeCellRenderer.getInstance());
            tableList.setCellEditor(new CheckboxTreeCellEditor(tableList));
            tableList.setRootVisible(false);
            tableList.setEditable(true);
        }

        return tableList;
    }

    /**
     * Getter method for component OkButton.
     *
     * @return the initialized component
     */
    public DefaultButton getOkButton() {

        if (okButton == null) {
            okButton = new DefaultButton();
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
            cancelButton = new DefaultButton();
        }

        return cancelButton;
    }

    /**
     * Initialize method.
     */
    private void buildGroups() {

    }

    public DefaultButton getSelectAll() {
        return selectAll;
    }

    public DefaultButton getDeselectAll() {
        return deselectAll;
    }

    public DefaultButton getInvertSelection() {
        return invertSelection;
    }
}
