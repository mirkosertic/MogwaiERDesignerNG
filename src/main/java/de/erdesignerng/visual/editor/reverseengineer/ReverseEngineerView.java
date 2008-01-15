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

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.erdesignerng.ERDesignerBundle;
import de.mogwai.common.client.looks.components.DefaultButton;
import de.mogwai.common.client.looks.components.DefaultComboBox;
import de.mogwai.common.client.looks.components.DefaultLabel;
import de.mogwai.common.client.looks.components.DefaultList;
import de.mogwai.common.client.looks.components.DefaultSeparator;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-15 19:22:44 $
 */
public class ReverseEngineerView extends JPanel {

    private JPanel m_schemagrid;

    private DefaultList m_schemalist;

    private DefaultButton m_refreshbutton;

    private DefaultButton m_startbutton;

    private DefaultButton m_cancelbutton;

    private JPanel m_engineeringoptions;

    private DefaultLabel m_component_9;

    private DefaultLabel m_component_10;

    private DefaultLabel m_component_11;

    private DefaultComboBox m_naming;

    private DefaultComboBox m_domaingeneration;

    private DefaultComboBox m_defaultvaluegeneration;

    /**
     * Constructor.
     */
    public ReverseEngineerView() {
        this.initialize();
    }

    /**
     * Initialize method.
     */
    private void initialize() {

        String rowDef = "2dlu,p,2dlu,p,2dlu,p,2dlu,p,20dlu,p,2dlu";
        String colDef = "2dlu,left:100dlu,2dlu,right:100dlu,2dlu";

        FormLayout layout = new FormLayout(colDef, rowDef);
        this.setLayout(layout);

        CellConstraints cons = new CellConstraints();

        this.add(new DefaultSeparator(ERDesignerBundle.SCHEMAOPTIONS), cons.xywh(2, 2, 3, 1));
        this.add(this.getschemagrid(), cons.xywh(2, 4, 3, 1));
        this.add(new DefaultSeparator(ERDesignerBundle.ENGINEERINGOPTIONS), cons.xywh(2, 6, 3, 1));
        this.add(this.getstartbutton(), cons.xywh(2, 10, 1, 1));
        this.add(this.getcancelbutton(), cons.xywh(4, 10, 1, 1));
        this.add(this.getengineeringoptions(), cons.xywh(2, 8, 3, 1));

        this.buildGroups();
    }

    /**
     * Getter method for component schemagrid.
     * 
     * @return the initialized component
     */
    public JPanel getschemagrid() {

        if (m_schemagrid == null) {
            m_schemagrid = new JPanel();

            String rowDef = "2dlu,80dlu,2dlu,p,2dlu";
            String colDef = "40dlu:grow,2dlu,50dlu";

            FormLayout layout = new FormLayout(colDef, rowDef);
            m_schemagrid.setLayout(layout);

            CellConstraints cons = new CellConstraints();

            m_schemagrid.add(new JScrollPane(this.getschemaList()), cons.xywh(1, 2, 3, 1));
            m_schemagrid.add(this.getrefreshbutton(), cons.xywh(3, 4, 1, 1));
            m_schemagrid.setName("schemagrid");
            m_schemagrid.setToolTipText("1 , 3");
        }

        return m_schemagrid;
    }

    /**
     * Getter method for component schemaList.
     * 
     * @return the initialized component
     */
    public DefaultList getschemaList() {

        if (m_schemalist == null) {
            m_schemalist = new DefaultList();
        }

        return m_schemalist;
    }

    /**
     * Getter method for component refreshbutton.
     * 
     * @return the initialized component
     */
    public javax.swing.JButton getrefreshbutton() {

        if (m_refreshbutton == null) {
            m_refreshbutton = new DefaultButton();
        }

        return m_refreshbutton;
    }

    /**
     * Getter method for component startbutton.
     * 
     * @return the initialized component
     */
    public javax.swing.JButton getstartbutton() {

        if (m_startbutton == null) {
            m_startbutton = new DefaultButton();
        }

        return m_startbutton;
    }

    /**
     * Getter method for component cancelbutton.
     * 
     * @return the initialized component
     */
    public javax.swing.JButton getcancelbutton() {

        if (m_cancelbutton == null) {
            m_cancelbutton = new DefaultButton();
        }

        return m_cancelbutton;
    }

    /**
     * Getter method for component engineeringoptions.
     * 
     * @return the initialized component
     */
    public JPanel getengineeringoptions() {

        if (m_engineeringoptions == null) {
            m_engineeringoptions = new JPanel();

            String rowDef = "2dlu,p,2dlu,p,2dlu,p,2dlu";
            String colDef = "80dlu,2dlu,40dlu:grow";

            FormLayout layout = new FormLayout(colDef, rowDef);
            m_engineeringoptions.setLayout(layout);

            CellConstraints cons = new CellConstraints();

            m_engineeringoptions.add(this.getComponent_9(), cons.xywh(1, 2, 1, 1));
            m_engineeringoptions.add(this.getComponent_10(), cons.xywh(1, 4, 1, 1));
            m_engineeringoptions.add(this.getComponent_11(), cons.xywh(1, 6, 1, 1));
            m_engineeringoptions.add(this.getNaming(), cons.xywh(3, 2, 1, 1));
            m_engineeringoptions.add(this.getDomaingeneration(), cons.xywh(3, 4, 1, 1));
            m_engineeringoptions.add(this.getDefaultvaluegeneration(), cons.xywh(3, 6, 1, 1));
            m_engineeringoptions.setName("engineeringoptions");
            m_engineeringoptions.setToolTipText("3 , 7");
        }

        return m_engineeringoptions;
    }

    /**
     * Getter method for component Component_9.
     * 
     * @return the initialized component
     */
    public javax.swing.JLabel getComponent_9() {

        if (m_component_9 == null) {
            m_component_9 = new DefaultLabel(ERDesignerBundle.TABLEGENERATION);
        }

        return m_component_9;
    }

    /**
     * Getter method for component Component_10.
     * 
     * @return the initialized component
     */
    public javax.swing.JLabel getComponent_10() {

        if (m_component_10 == null) {
            m_component_10 = new DefaultLabel(ERDesignerBundle.DOMAINGENERATION);
        }

        return m_component_10;
    }

    /**
     * Getter method for component Component_11.
     * 
     * @return the initialized component
     */
    public javax.swing.JLabel getComponent_11() {

        if (m_component_11 == null) {
            m_component_11 = new DefaultLabel(ERDesignerBundle.DEFAULTVALUEENERATION);
        }

        return m_component_11;
    }

    /**
     * Getter method for component Naming.
     * 
     * @return the initialized component
     */
    public javax.swing.JComboBox getNaming() {

        if (m_naming == null) {
            m_naming = new DefaultComboBox();
        }

        return m_naming;
    }

    /**
     * Getter method for component Domaingeneration.
     * 
     * @return the initialized component
     */
    public javax.swing.JComboBox getDomaingeneration() {

        if (m_domaingeneration == null) {
            m_domaingeneration = new DefaultComboBox();
        }

        return m_domaingeneration;
    }

    /**
     * Getter method for component Defaultvaluegeneration.
     * 
     * @return the initialized component
     */
    public javax.swing.JComboBox getDefaultvaluegeneration() {

        if (m_defaultvaluegeneration == null) {
            m_defaultvaluegeneration = new DefaultComboBox();
        }

        return m_defaultvaluegeneration;
    }

    /**
     * Initialize method.
     */
    private void buildGroups() {

    }
}
