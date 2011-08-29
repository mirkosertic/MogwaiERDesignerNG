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
package de.erdesignerng.visual.editor.relation;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import de.erdesignerng.ERDesignerBundle;
import de.mogwai.common.client.looks.components.DefaultButton;
import de.mogwai.common.client.looks.components.DefaultLabel;
import de.mogwai.common.client.looks.components.DefaultPanel;
import de.mogwai.common.client.looks.components.DefaultRadioButton;
import de.mogwai.common.client.looks.components.DefaultSeparator;
import de.mogwai.common.client.looks.components.DefaultTable;
import de.mogwai.common.client.looks.components.DefaultTextField;

import javax.swing.*;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-06-13 16:48:59 $
 */
public class RelationEditorView extends JPanel {

    private DefaultLabel component1;

    private DefaultTextField relationName;

    private DefaultTable attributeMappingTable;

    private JPanel onDeleteContainer;

    private DefaultRadioButton onDeleteNothing;

    private DefaultRadioButton onDeleteCascade;

    private DefaultRadioButton onDeleteSetNull;

    private DefaultRadioButton onDeleteRestrict;

    private JPanel onUpdateContainer;

    private DefaultRadioButton onUpdateNothing;

    private DefaultRadioButton onUpdateCascade;

    private DefaultRadioButton onUpdateSetNull;

    private DefaultRadioButton onUpdateRestrict;

    private JPanel component8;

    private DefaultButton okButton;

    private DefaultButton cancelButton;

    private final DefaultPanel propertiesPanel = new DefaultPanel();

    public RelationEditorView() {
        initialize();
    }

    /**
     * Initialize method.
     */
    private void initialize() {

        String rowDef = "2dlu,p,8dlu,p,8dlu,p,2dlu,fill:100dlu,8dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,20dlu,p,2dlu";
        String colDef = "2dlu,60dlu,2dlu,fill:150dlu:grow,2dlu";

        FormLayout layout = new FormLayout(colDef, rowDef);
        setLayout(layout);

        CellConstraints cons = new CellConstraints();

        add(getComponent1(), cons.xywh(2, 4, 1, 1));
        add(new DefaultSeparator(ERDesignerBundle.RELATIONPROPERTIES), cons.xywh(2, 2, 3, 1));
        add(getRelationName(), cons.xywh(4, 4, 1, 1));
        add(new DefaultSeparator(ERDesignerBundle.ATTRIBUTEMAPPING), cons.xywh(2, 6, 3, 1));
        add(new JScrollPane(getAttributeMappingTable()), cons.xywh(2, 8, 3, 1));
        add(new DefaultSeparator(ERDesignerBundle.ONDELETEHANDLING), cons.xywh(2, 10, 3, 1));
        add(getOnDeleteContainer(), cons.xywh(2, 12, 3, 1));
        add(new DefaultSeparator(ERDesignerBundle.ONUPDATEHANDLING), cons.xywh(2, 14, 3, 1));
        add(getOnUpdateContainer(), cons.xywh(2, 18, 3, 1));

        add(getPropertiesPanel(), cons.xywh(2, 20, 3, 1));

        add(getComponent8(), cons.xywh(2, 22, 3, 1));

        buildGroups();
    }

    public JLabel getComponent1() {

        if (component1 == null) {
            component1 = new DefaultLabel(ERDesignerBundle.RELATIONNAME);
        }

        return component1;
    }

    public DefaultTextField getRelationName() {

        if (relationName == null) {
            relationName = new DefaultTextField();
        }

        return relationName;
    }

    public DefaultTable getAttributeMappingTable() {

        if (attributeMappingTable == null) {
            attributeMappingTable = new DefaultTable();
            attributeMappingTable.setName("Component_5");
        }

        return attributeMappingTable;
    }

    public JPanel getOnDeleteContainer() {

        if (onDeleteContainer == null) {
            onDeleteContainer = new JPanel();

            String rowDef = "p,2dlu";
            String colDef = "p,p,p,p";

            FormLayout layout = new FormLayout(colDef, rowDef);
            onDeleteContainer.setLayout(layout);

            CellConstraints cons = new CellConstraints();

            onDeleteContainer.add(getOnDeleteCascadeNothing(), cons.xywh(1, 1, 1, 1));
            onDeleteContainer.add(getOnDeleteCascade(), cons.xywh(2, 1, 1, 1));
            onDeleteContainer.add(getOnDeleteRestrict(), cons.xywh(3, 1, 1, 1));
            onDeleteContainer.add(getOnDeleteSetNull(), cons.xywh(4, 1, 1, 1));
            onDeleteContainer.setName("Component_7");
        }

        return onDeleteContainer;
    }

    public JPanel getOnUpdateContainer() {

        if (onUpdateContainer == null) {
            onUpdateContainer = new JPanel();

            String rowDef = "p,2dlu";
            String colDef = "p,p,p,p";

            FormLayout layout = new FormLayout(colDef, rowDef);
            onUpdateContainer.setLayout(layout);

            CellConstraints cons = new CellConstraints();

            onUpdateContainer.add(getOnUpdateCascadeNothing(), cons.xywh(1, 1, 1, 1));
            onUpdateContainer.add(getOnUpdateCascade(), cons.xywh(2, 1, 1, 1));
            onUpdateContainer.add(getOnUpdateRestrict(), cons.xywh(3, 1, 1, 1));
            onUpdateContainer.add(getOnUpdateSetNull(), cons.xywh(4, 1, 1, 1));
            onUpdateContainer.setName("Component_7");
        }

        return onUpdateContainer;
    }

    public JRadioButton getOnDeleteCascadeNothing() {

        if (onDeleteNothing == null) {
            onDeleteNothing = new DefaultRadioButton(ERDesignerBundle.DATABASEDEFAULT);
        }

        return onDeleteNothing;
    }

    public JRadioButton getOnDeleteCascade() {

        if (onDeleteCascade == null) {
            onDeleteCascade = new DefaultRadioButton(ERDesignerBundle.CASCADE);
        }

        return onDeleteCascade;
    }

    public JRadioButton getOnDeleteRestrict() {

        if (onDeleteRestrict == null) {
            onDeleteRestrict = new DefaultRadioButton(ERDesignerBundle.RESTRICT);
        }

        return onDeleteRestrict;
    }

    public JRadioButton getOnDeleteSetNull() {

        if (onDeleteSetNull == null) {
            onDeleteSetNull = new DefaultRadioButton(ERDesignerBundle.SETNULL);
        }

        return onDeleteSetNull;
    }

    public JRadioButton getOnUpdateCascadeNothing() {

        if (onUpdateNothing == null) {
            onUpdateNothing = new DefaultRadioButton(ERDesignerBundle.DATABASEDEFAULT);
        }

        return onUpdateNothing;
    }

    public JRadioButton getOnUpdateCascade() {

        if (onUpdateCascade == null) {
            onUpdateCascade = new DefaultRadioButton(ERDesignerBundle.CASCADE);
        }

        return onUpdateCascade;
    }

    public JRadioButton getOnUpdateRestrict() {

        if (onUpdateRestrict == null) {
            onUpdateRestrict = new DefaultRadioButton(ERDesignerBundle.RESTRICT);
        }

        return onUpdateRestrict;
    }

    public JRadioButton getOnUpdateSetNull() {

        if (onUpdateSetNull == null) {
            onUpdateSetNull = new DefaultRadioButton(ERDesignerBundle.SETNULL);
        }

        return onUpdateSetNull;
    }

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

    public DefaultButton getOKButton() {

        if (okButton == null) {
            okButton = new DefaultButton(ERDesignerBundle.OK);
        }

        return okButton;
    }

    public DefaultButton getCancelButton() {

        if (cancelButton == null) {
            cancelButton = new DefaultButton(ERDesignerBundle.CANCEL);
        }

        return cancelButton;
    }

    /**
     * Initialize method.
     */
    private void buildGroups() {

        ButtonGroup theGroup1 = new ButtonGroup();
        theGroup1.add(getOnDeleteCascadeNothing());
        theGroup1.add(getOnDeleteCascade());
        theGroup1.add(getOnDeleteSetNull());
        theGroup1.add(getOnDeleteRestrict());

        ButtonGroup theGroup2 = new ButtonGroup();
        theGroup2.add(getOnUpdateCascadeNothing());
        theGroup2.add(getOnUpdateCascade());
        theGroup2.add(getOnUpdateSetNull());
        theGroup2.add(getOnUpdateRestrict());
    }

    public DefaultPanel getPropertiesPanel() {
        return propertiesPanel;
    }
}