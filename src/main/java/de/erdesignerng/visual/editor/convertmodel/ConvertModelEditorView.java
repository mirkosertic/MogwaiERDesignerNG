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
package de.erdesignerng.visual.editor.convertmodel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.erdesignerng.ERDesignerBundle;
import de.mogwai.common.client.looks.components.DefaultButton;
import de.mogwai.common.client.looks.components.DefaultComboBox;
import de.mogwai.common.client.looks.components.DefaultLabel;
import de.mogwai.common.client.looks.components.DefaultSeparator;
import de.mogwai.common.client.looks.components.DefaultTable;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-06-13 16:48:59 $
 */
public class ConvertModelEditorView extends JPanel {

	private DefaultLabel component1;

	private DefaultComboBox targetDialect;

	private DefaultTable mappingTable;

	private JPanel component8;

	private DefaultButton okButton;

	private DefaultButton cancelButton;

	/**
	 * Constructor.
	 */
	public ConvertModelEditorView() {
		initialize();
	}

	/**
	 * Initialize method.
	 */
	private void initialize() {

		String rowDef = "2dlu,p,8dlu,p,8dlu,p,2dlu,fill:300dlu,8dlu,p,2dlu";
		String colDef = "2dlu,60dlu,2dlu,fill:250dlu:grow,2dlu";

		FormLayout layout = new FormLayout(colDef, rowDef);
		setLayout(layout);

		CellConstraints cons = new CellConstraints();

		add(getComponent1(), cons.xywh(2, 4, 1, 1));
		add(new DefaultSeparator(ERDesignerBundle.TARGETDIALECT), cons.xywh(2, 2, 3, 1));
		add(getTargetDialect(), cons.xywh(4, 4, 1, 1));
		add(new DefaultSeparator(ERDesignerBundle.DATATYPEMAPPING), cons.xywh(2, 6, 3, 1));
		add(new JScrollPane(getMappingTable()), cons.xywh(2, 8, 3, 1));

		add(getComponent8(), cons.xywh(2, 10, 3, 1));
	}

	/**
	 * Getter method for component Component_1.
	 * 
	 * @return the initialized component
	 */
	public JLabel getComponent1() {

		if (component1 == null) {
			component1 = new DefaultLabel(ERDesignerBundle.DIALECT);
		}

		return component1;
	}

	/**
	 * Getter method for component TargetDataType.
	 * 
	 * @return the initialized component
	 */
	public DefaultComboBox getTargetDialect() {

		if (targetDialect == null) {
			targetDialect = new DefaultComboBox();
		}

		return targetDialect;
	}

	/**
	 * Getter method for component Component_5.
	 * 
	 * @return the initialized component
	 */
	public DefaultTable getMappingTable() {

		if (mappingTable == null) {
			mappingTable = new DefaultTable();
			mappingTable.setName("Component_5");
		}

		return mappingTable;
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
}
