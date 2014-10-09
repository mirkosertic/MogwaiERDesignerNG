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
package de.erdesignerng.visual.editor.domain;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.dialect.DataType;
import de.erdesignerng.model.Domain;
import de.erdesignerng.visual.editor.CheckboxCellRenderer;
import de.erdesignerng.visual.editor.ModelItemDefaultCellRenderer;
import de.erdesignerng.visual.editor.TableHelper;
import de.mogwai.common.client.looks.components.DefaultButton;
import de.mogwai.common.client.looks.components.DefaultComboBox;
import de.mogwai.common.client.looks.components.DefaultPanel;
import de.mogwai.common.client.looks.components.DefaultTable;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 */
public class DomainEditorView extends DefaultPanel {

	private DefaultButton newButton;

	private DefaultButton deleteButton;

	private JPanel component15;

	private DefaultButton okButton;

	private DefaultButton cancelButton;

	private final DefaultComboBoxModel dataTypesModel = new DefaultComboBoxModel();

	private final DefaultTable domainTable = new DefaultTable() {
		@Override
		public void removeEditor() {
			super.removeEditor();

			Domain theAttribute = domainTableModel.getRow(getSelectedRow());
			domainEditorRemoved(theAttribute);

			invalidate();
			repaint();

			TableHelper.processEditorRemovel(this);
		}
	};

	private final DomainTableModel domainTableModel = new DomainTableModel();

	public DomainEditorView() {
		initialize();
	}

	public DomainTableModel getDomainTableModel() {
		return domainTableModel;
	}

	public DefaultTable getDomainTable() {
		return domainTable;
	}

	protected void domainEditorRemoved(Domain aDomain) {
	}

	/**
	 * Initialize method.
	 */
	private void initialize() {

		String rowDef = "2dlu,p,2dlu,p,fill:220dlu,p,20dlu,p,2dlu";
		String colDef = "2dlu,left:45dlu,2dlu,fill:140dlu:grow,fill:60dlu,2dlu,fill:60dlu,2dlu";

		domainTable.setCellSelectionEnabled(true);

		FormLayout layout = new FormLayout(colDef, rowDef);
		setLayout(layout);

		CellConstraints cons = new CellConstraints();

		add(getComponent15(), cons.xywh(2, 4, 6, 2));
		add(getOkButton(), cons.xywh(5, 8, 1, 1));
		add(getCancelButton(), cons.xywh(7, 8, 1, 1));

	}

	/**
	 * Getter method for component NewButton.
	 *
	 * @return the initialized component
	 */
	public JButton getNewButton() {

		if (newButton == null) {
			newButton = new DefaultButton(ERDesignerBundle.NEW);
		}

		return newButton;
	}

	/**
	 * Getter method for component DeleteButton.
	 *
	 * @return the initialized component
	 */
	public JButton getDeleteButton() {

		if (deleteButton == null) {
			deleteButton = new DefaultButton(ERDesignerBundle.DELETE);
		}

		return deleteButton;
	}

	/**
	 * Getter method for component Component_15.
	 *
	 * @return the initialized component
	 */
	public JPanel getComponent15() {

		if (component15 == null) {
			component15 = new JPanel();

			FormLayout theLayout = new FormLayout("fill:10dlu:grow,2dlu,60dlu,2dlu,60dlu", "fill:10dlu:grow,2dlu,p");
			component15.setLayout(theLayout);

			CellConstraints cons = new CellConstraints();
			domainTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
			domainTable.setModel(domainTableModel);
			domainTable.getColumnModel().getColumn(0).setPreferredWidth(200);
			domainTable.getColumnModel().getColumn(1).setPreferredWidth(100);
			domainTable.getColumnModel().getColumn(2).setPreferredWidth(60);
			domainTable.getColumnModel().getColumn(3).setPreferredWidth(60);
			domainTable.getColumnModel().getColumn(4).setPreferredWidth(60);
			domainTable.getColumnModel().getColumn(5).setPreferredWidth(50);
			domainTable.getColumnModel().getColumn(6).setPreferredWidth(300);
			domainTable.getTableHeader().setResizingAllowed(true);
			domainTable.getTableHeader().setReorderingAllowed(false);
			domainTable.setAutoResizeMode(DefaultTable.AUTO_RESIZE_OFF);
			domainTable.setRowHeight(22);

			DefaultComboBox theBox = new DefaultComboBox();
			theBox.setBorder(BorderFactory.createEmptyBorder());
			theBox.setModel(dataTypesModel);
			domainTable.setDefaultEditor(DataType.class, new DefaultCellEditor(theBox));
			domainTable.setDefaultRenderer(DataType.class, ModelItemDefaultCellRenderer.getInstance());
			domainTable.setDefaultRenderer(String.class, ModelItemDefaultCellRenderer.getInstance());
			domainTable.setDefaultRenderer(Integer.class, ModelItemDefaultCellRenderer.getInstance());
			domainTable.setDefaultRenderer(Boolean.class, CheckboxCellRenderer.getInstance());

			component15.add(domainTable.getScrollPane(), cons.xywh(1, 1, 5, 1));
			component15.add(getNewButton(), cons.xy(3, 3));
			component15.add(getDeleteButton(), cons.xy(5, 3));
		}

		return component15;
	}

	/**
	 * Getter method for component OkButton.
	 *
	 * @return the initialized component
	 */
	public JButton getOkButton() {

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
	public JButton getCancelButton() {

		if (cancelButton == null) {
			cancelButton = new DefaultButton(ERDesignerBundle.CANCEL);
		}

		return cancelButton;
	}

	public DefaultComboBoxModel getDataTypesModel() {
		return dataTypesModel;
	}
}