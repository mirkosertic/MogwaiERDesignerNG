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
import de.erdesignerng.ERDesignerBundle;
import de.mogwai.common.client.looks.components.DefaultButton;
import de.mogwai.common.client.looks.components.DefaultComboBox;
import de.mogwai.common.client.looks.components.DefaultLabel;
import de.mogwai.common.client.looks.components.DefaultList;
import de.mogwai.common.client.looks.components.DefaultSeparator;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 */
public class ReverseEngineerView extends JPanel {

    private JPanel schemaGrid;

    private DefaultList schemaList;

    private DefaultButton schemaRefreshButton;

    private DefaultButton startButton;

    private DefaultButton cancelButton;

    private JPanel engineeringOptions;

    private DefaultLabel component9;

    private DefaultComboBox naming;

    /**
     * Constructor.
     */
    public ReverseEngineerView() {
        initialize();
    }

    /**
     * Initialize method.
     */
    private void initialize() {

        String rowDef = "2dlu,p,2dlu,p,2dlu,p,2dlu,p,20dlu,p,2dlu";
        String colDef = "2dlu,fill:60dlu:grow,fill:60dlu:grow,fill:60dlu:grow,2dlu";

        FormLayout layout = new FormLayout(colDef, rowDef);
        setLayout(layout);

        CellConstraints cons = new CellConstraints();

        add(new DefaultSeparator(ERDesignerBundle.SCHEMAOPTIONS), cons.xywh(2, 2, 3, 1));
        add(getSchemaGrid(), cons.xywh(2, 4, 3, 1));
        add(new DefaultSeparator(ERDesignerBundle.ENGINEERINGOPTIONS), cons.xywh(2, 6, 3, 1));
        add(getStartButton(), cons.xywh(2, 10, 1, 1));
        add(getCancelButton(), cons.xywh(4, 10, 1, 1));
        add(getEngineeringOptions(), cons.xywh(2, 8, 3, 1));
    }

    /**
     * Getter method for component schema grid.
     *
     * @return the initialized component
     */
    public JPanel getSchemaGrid() {

        if (schemaGrid == null) {
            schemaGrid = new JPanel();

            String rowDef = "2dlu,80dlu,2dlu,p,2dlu";
            String colDef = "40dlu:grow,2dlu,fill:70dlu";

            FormLayout layout = new FormLayout(colDef, rowDef);
            schemaGrid.setLayout(layout);

            CellConstraints cons = new CellConstraints();

            schemaGrid.add(getSchemaList().getScrollPane(), cons.xywh(1, 2, 3, 1));
            schemaGrid.add(getRefreshButton(), cons.xywh(3, 4, 1, 1));
            schemaGrid.setName("schemagrid");
        }

        return schemaGrid;
    }

    /**
     * Getter method for component schemaList.
     *
     * @return the initialized component
     */
    public DefaultList getSchemaList() {

        if (schemaList == null) {
            schemaList = new DefaultList();
            schemaList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        }

        return schemaList;
    }

    /**
     * Getter method for component refresh button.
     *
     * @return the initialized component
     */
    public JButton getRefreshButton() {

        if (schemaRefreshButton == null) {
            schemaRefreshButton = new DefaultButton();
        }

        return schemaRefreshButton;
    }

    /**
     * Getter method for component start button.
     *
     * @return the initialized component
     */
    public JButton getStartButton() {

        if (startButton == null) {
            startButton = new DefaultButton();
        }

        return startButton;
    }

    /**
     * Getter method for component cancel button.
     *
     * @return the initialized component
     */
    public JButton getCancelButton() {

        if (cancelButton == null) {
            cancelButton = new DefaultButton();
        }

        return cancelButton;
    }

    /**
     * Getter method for component engineeringoptions.
     *
     * @return the initialized component
     */
    public JPanel getEngineeringOptions() {

        if (engineeringOptions == null) {
            engineeringOptions = new JPanel();

            String rowDef = "2dlu,p,2dlu,p,2dlu,p,2dlu";
            String colDef = "80dlu,2dlu,40dlu:grow";

            FormLayout layout = new FormLayout(colDef, rowDef);
            engineeringOptions.setLayout(layout);

            CellConstraints cons = new CellConstraints();

            engineeringOptions.add(getComponent9(), cons.xywh(1, 2, 1, 1));
            engineeringOptions.add(getNaming(), cons.xywh(3, 2, 1, 1));
            engineeringOptions.setName("engineeringoptions");
        }

        return engineeringOptions;
    }

    /**
     * Getter method for component Component_9.
     *
     * @return the initialized component
     */
    public JLabel getComponent9() {

        if (component9 == null) {
            component9 = new DefaultLabel(ERDesignerBundle.TABLEGENERATION);
        }

        return component9;
    }

    /**
     * Getter method for component Naming.
     *
     * @return the initialized component
     */
    public JComboBox getNaming() {

        if (naming == null) {
            naming = new DefaultComboBox();
        }

        return naming;
    }
}
