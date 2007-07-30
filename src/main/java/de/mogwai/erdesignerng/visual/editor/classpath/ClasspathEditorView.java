package de.mogwai.erdesignerng.visual.editor.classpath;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mogwai.erdesignerng.io.GenericFileFilter;
import de.mogwai.erdesignerng.visual.IconFactory;

public class ClasspathEditorView extends JPanel {

	private File lastDir;
	
	private JList classpath = new JList();

	private JButton addButton = new JButton(IconFactory.getFolderAddIcon());

	private JButton removeButton = new JButton(IconFactory
			.getFolderRemoveIcon());

	private JButton okButton = new JButton(IconFactory.getSaveIcon());

	private JButton cancelButton = new JButton(IconFactory.getCancelIcon());

	public ClasspathEditorView() {
		initialize();
	}

	private void initialize() {

		String theColDef = "2dlu,150dlu,2dlu,p,2";
		String theRowDef = "2dlu,150dlu,p,2dlu,p,10dlu,p,2dlu";

		FormLayout theLayout = new FormLayout(theColDef, theRowDef);
		setLayout(theLayout);

		CellConstraints cons = new CellConstraints();

		add(new JScrollPane(classpath), cons.xywh(2, 2, 1, 4));
		add(addButton, cons.xy(4, 3));
		add(removeButton, cons.xy(4, 5));

		JPanel thePanel = new JPanel();

		theColDef = "fill:2dlu:grow,50dlu,2dlu,50dlu,2dlu";
		theRowDef = "p";

		theLayout = new FormLayout(theColDef, theRowDef);
		thePanel.setLayout(theLayout);

		thePanel.add(okButton, cons.xy(2, 1));
		okButton.setText("Ok");
		okButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				handleOk();
			}

		});
		thePanel.add(cancelButton, cons.xy(4, 1));
		cancelButton.setText("Cancel");
		cancelButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				handleCancel();
			}
		});

		add(thePanel, cons.xyw(2, 7, 3));

		addButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				folderAdd();
			}

		});

		removeButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				folderRemove();
			}

		});

		classpath.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	}

	protected void handleOk() {

	}

	protected void handleCancel() {

	}

	protected void folderAdd() {
		
		DefaultListModel theListModel = (DefaultListModel) classpath.getModel();

		JFileChooser theChooser = new JFileChooser();
		if (lastDir != null) {
			theChooser.setCurrentDirectory(lastDir);
		}
		theChooser.setMultiSelectionEnabled(true);
		theChooser.setFileFilter(new GenericFileFilter(".jar","Java archive"));
		if (theChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File[] theFiles = theChooser.getSelectedFiles();

			for (File theFile : theFiles) {
				if (!theListModel.contains(theFile)) {
					theListModel.addElement(theFile);
				}
			}
			
			lastDir = theChooser.getCurrentDirectory();
		}
	}

	protected void folderRemove() {

		DefaultListModel theListModel = (DefaultListModel) classpath.getModel();
		
		Object[] theValues = classpath.getSelectedValues();
		for (Object theValue : theValues) {
			theListModel.removeElement(theValue);
		}
	}

	public JButton getAddButton() {
		return addButton;
	}

	public void setAddButton(JButton addButton) {
		this.addButton = addButton;
	}

	public JButton getCancelButton() {
		return cancelButton;
	}

	public void setCancelButton(JButton cancelButton) {
		this.cancelButton = cancelButton;
	}

	public JList getClasspath() {
		return classpath;
	}

	public void setClasspath(JList classpath) {
		this.classpath = classpath;
	}

	public JButton getOkButton() {
		return okButton;
	}

	public void setOkButton(JButton okButton) {
		this.okButton = okButton;
	}

	public JButton getRemoveButton() {
		return removeButton;
	}

	public void setRemoveButton(JButton removeButton) {
		this.removeButton = removeButton;
	}

}
