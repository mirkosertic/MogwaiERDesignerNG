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
package de.erdesignerng.visual.editor.completecompare;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import de.erdesignerng.ERDesignerBundle;
import de.mogwai.common.client.looks.components.DefaultButton;
import de.mogwai.common.client.looks.components.DefaultSeparator;
import de.mogwai.common.client.looks.components.DefaultTree;

import javax.swing.JPanel;

/**
 * Visual class CompleteCompareEditorView.
 * <p/>
 * Created with Mogwai FormMaker 0.7.
 */
public class CompleteCompareEditorView extends JPanel {

    private JPanel contentPanel;

    private DefaultTree currentModelView;

    private DefaultTree databaseView;

    private DefaultButton okButton;

    /**
     * Constructor.
     */
    public CompleteCompareEditorView() {
        initialize();
    }

    /**
     * Initialize method.
     */
    private void initialize() {

        String rowDef = "8dlu,fill:250dlu:grow,8dlu,p,2dlu";
        String colDef = "2dlu,fill:60dlu,2dlu:grow,fill:60dlu,2dlu";

        FormLayout layout = new FormLayout(colDef, rowDef);
        setLayout(layout);

        CellConstraints cons = new CellConstraints();

        add(getContentPanel(), cons.xywh(2, 2, 3, 1));
        add(getOkButton(), cons.xywh(2, 4, 1, 1));
    }

    /**
     * Getter method for component ContentPanel.
     *
     * @return the initialized component
     */
    public JPanel getContentPanel() {

        if (contentPanel == null) {
            contentPanel = new JPanel();

            String rowDef = "2dlu,p,2dlu,fill:200dlu:grow,2dlu";
            String colDef = "fill:180dlu:grow,2dlu,fill:180dlu:grow";

            FormLayout layout = new FormLayout(colDef, rowDef);
            contentPanel.setLayout(layout);

            CellConstraints cons = new CellConstraints();

            contentPanel.add(new DefaultSeparator(ERDesignerBundle.CURRENTMODEL), cons.xy(1, 2));
            contentPanel.add(new DefaultSeparator(ERDesignerBundle.OTHERMODEL), cons.xywh(3, 2, 1, 1));
            contentPanel.add(getCurrentModelView().getScrollPane(), cons.xy(1, 4));
            contentPanel.add(getDatabaseView().getScrollPane(), cons.xy(3, 4));
        }

        return contentPanel;
    }

    /**
     * Getter method for component CurrentModelView.
     *
     * @return the initialized component
     */
    public DefaultTree getCurrentModelView() {

        if (currentModelView == null) {
            currentModelView = new DefaultTree();
        }

        return currentModelView;
    }

    /**
     * Getter method for component DatabaseView.
     *
     * @return the initialized component
     */
    public DefaultTree getDatabaseView() {

        if (databaseView == null) {
            databaseView = new DefaultTree();
        }

        return databaseView;
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
}
