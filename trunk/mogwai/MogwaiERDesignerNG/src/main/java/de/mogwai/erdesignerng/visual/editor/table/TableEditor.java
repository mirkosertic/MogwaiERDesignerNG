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
package de.mogwai.erdesignerng.visual.editor.table;

import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.event.ChangeEvent;

import de.mogwai.erdesignerng.model.Model;
import de.mogwai.erdesignerng.model.Table;
import de.mogwai.erdesignerng.visual.editor.BaseEditor;
import de.mogwai.erdesignerng.visual.editor.DialogConstants;

/**
 * @author Mirko Sertic
 */
public class TableEditor extends BaseEditor {

	private Model model;

	private Table currentEditingTable;

	private TableEditorView view = new TableEditorView();

	/**
	 * @param parent
	 */
	public TableEditor(JFrame aParent) {
		super(aParent);
		initialize();
	}

	/**
	 * Update the domain list.
	 * 
	 * The displayed model for domains is updated from the current domain list.
	 */
	private void updateDomains() {
	}

	public void applyChanges(Table aTable) {
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {

		view = new TableEditorView();
		view.getOkButton().addActionListener(
				new java.awt.event.ActionListener() {

					public void actionPerformed(java.awt.event.ActionEvent e) {
						setModalResult(DialogConstants.MODAL_RESULT_OK);
					}
				});
		view.getCancelButton().addActionListener(
				new java.awt.event.ActionListener() {

					public void actionPerformed(java.awt.event.ActionEvent e) {
						setModalResult(MODAL_RESULT_CANCEL);
					}
				});
		view.getMainTabbedPane().addChangeListener(
				new javax.swing.event.ChangeListener() {

					public void stateChanged(javax.swing.event.ChangeEvent e) {
						commandTabStateChange(e);
					}
				});
		view.getAttributeList().addListSelectionListener(
				new javax.swing.event.ListSelectionListener() {

					public void valueChanged(
							javax.swing.event.ListSelectionEvent e) {
						commandAttributeListValueChanged(e);
					}
				});
		view.getUpButton().addActionListener(
				new java.awt.event.ActionListener() {

					public void actionPerformed(java.awt.event.ActionEvent e) {
						commandMoveAttributeUp(e);
					}
				});
		view.getDownButton().addActionListener(
				new java.awt.event.ActionListener() {

					public void actionPerformed(java.awt.event.ActionEvent e) {
						commandMoveAttributeDown(e);
					}
				});
		view.getNewButton().addActionListener(
				new java.awt.event.ActionListener() {

					public void actionPerformed(java.awt.event.ActionEvent e) {
						commandNewAttribute(e);
					}
				});
		view.getRenameButton().addActionListener(
				new java.awt.event.ActionListener() {

					public void actionPerformed(java.awt.event.ActionEvent e) {
						commandRenameAttribute(e);
					}
				});
		view.getDeleteButton().addActionListener(
				new java.awt.event.ActionListener() {

					public void actionPerformed(java.awt.event.ActionEvent e) {
						commandDeleteAttribute(e);
					}
				});
		view.getUpdateAttributeButton().addActionListener(
				new java.awt.event.ActionListener() {

					public void actionPerformed(java.awt.event.ActionEvent e) {
						commandUpdateAttribute(e);
					}
				});
		view.getPrimaryKey().addItemListener(new java.awt.event.ItemListener() {

			public void itemStateChanged(java.awt.event.ItemEvent e) {
				commandPrimaryKeyItemStateChanged(e);
			}
		});
		view.getUpdateIndexButton().addActionListener(
				new java.awt.event.ActionListener() {

					public void actionPerformed(java.awt.event.ActionEvent e) {
						commandUpdateIndex();
					}
				});
		view.getNewIndexButton().addActionListener(
				new java.awt.event.ActionListener() {

					public void actionPerformed(java.awt.event.ActionEvent e) {
						commandNewIndex();
					}
				});
		view.getRenameIndexButton().addActionListener(
				new java.awt.event.ActionListener() {

					public void actionPerformed(java.awt.event.ActionEvent e) {
						commandRenameIndex();
					}
				});
		view.getDeleteIndexButton().addActionListener(
				new java.awt.event.ActionListener() {

					public void actionPerformed(java.awt.event.ActionEvent e) {
						commandDeleteIndex();
					}
				});
		view.getIndexList().addListSelectionListener(
				new javax.swing.event.ListSelectionListener() {

					public void valueChanged(
							javax.swing.event.ListSelectionEvent e) {
						updateEditFields();
					}
				});
		view.getDomainDictionary().addActionListener(
				new java.awt.event.ActionListener() {

					public void actionPerformed(java.awt.event.ActionEvent e) {
						commandStartDomainEditor(e);
					}
				});

		setContentPane(view);

		setTitle("Entity editor");
		pack();

		view.getUpdateIndexButton().setEnabled(false);

	}

	private void commandTabStateChange(ChangeEvent e) {
	}

	private void commandUpdateAttribute(java.awt.event.ActionEvent evt) {
	}

	/**
	 * Select a domain in the domain list by it's name.
	 * 
	 * @param name
	 *            the domain name to be selected
	 */
	private void selectDomainByName(String name) {
	}

	private void selectDefaultValueByName(String name) {
	}

	private void updateEditFields() {
	}

	private void commandAttributeListValueChanged(
			javax.swing.event.ListSelectionEvent evt) {// GEN-FIRST:event_m_AttributeListValueChanged
	}

	private void commandRenameAttribute(java.awt.event.ActionEvent evt) {
	}

	private void commandDeleteAttribute(java.awt.event.ActionEvent evt) {
	}

	private void commandNewAttribute(java.awt.event.ActionEvent evt) {
	}

	private void commandPrimaryKeyActionPerformed(java.awt.event.ActionEvent evt) {
	}

	private void commandRequiredActionPerformed(java.awt.event.ActionEvent evt) {
	}

	private void commandMoveAttributeDown(java.awt.event.ActionEvent evt) {
	}

	private void commandMoveAttributeUp(java.awt.event.ActionEvent evt) {
	}

	private void commandPrimaryKeyItemStateChanged(java.awt.event.ItemEvent evt) {
	}

	private void commandDeleteIndex() {
	}

	private void commandRenameIndex() {
	}

	private void commandNewIndex() {
	}

	private void commandUpdateIndex() {
	}

	/**
	 * Start the domain editing.
	 * 
	 * @param e
	 *            the event that causes the editing
	 */
	private void commandStartDomainEditor(ActionEvent e) {
	}
}
