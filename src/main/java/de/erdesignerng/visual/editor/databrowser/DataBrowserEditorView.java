package de.erdesignerng.visual.editor.databrowser;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.visual.components.SQLEditorKit;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.DefaultButton;
import de.mogwai.common.client.looks.components.DefaultPanel;
import de.mogwai.common.client.looks.components.DefaultTable;
import de.mogwai.common.client.looks.components.DefaultTextPane;
import org.apache.commons.lang.ArrayUtils;

import javax.swing.*;
import javax.swing.text.EditorKit;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Visual class ModelCheckEditorView.
 * <p/>
 * Created with Mogwai FormMaker 0.6.
 */
public class DataBrowserEditorView extends JPanel {

	private DefaultTextPane sql;

	private DefaultTable data;

	private JPanel buttonPanel;

	private DefaultButton closeButton;

	private DefaultButton queryButton;

	private DefaultPanel breadCrumb;

	public DataBrowserEditorView() {
		initialize();
	}

	/**
	 * Initialize method.
	 */
	private void initialize() {

		String rowDef = "2dlu,p,2dlu,fill:100dlu,2dlu,fill:200dlu:grow,10dlu,p,2dlu";
		String colDef = "2dlu,fill:300dlu:grow,2dlu";

		FormLayout layout = new FormLayout(colDef, rowDef);
		setLayout(layout);

		CellConstraints cons = new CellConstraints();

		add(getBreadCrumb(), cons.xywh(2, 2, 1, 1));
		add(getSql().getScrollPane(), cons.xywh(2, 4, 1, 1));
		add(getData().getScrollPane(), cons.xywh(2, 6, 1, 1));
		add(getButtonPanel(), cons.xywh(2, 8, 1, 1));
	}

	public JPanel getButtonPanel() {

		if (buttonPanel == null) {
			buttonPanel = new JPanel();

			String rowDef = "10dlu,p";
			String colDef = "fill:80dlu,2dlu:grow,fill:80dlu";

			FormLayout layout = new FormLayout(colDef, rowDef);
			buttonPanel.setLayout(layout);

			CellConstraints cons = new CellConstraints();

			buttonPanel.add(getCloseButton(), cons.xywh(3, 2, 1, 1));
			buttonPanel.add(getQueryButton(), cons.xywh(1, 2, 1, 1));
			buttonPanel.setName("buttonpanel");
		}

		return buttonPanel;
	}

	public DefaultButton getCloseButton() {

		if (closeButton == null) {
			closeButton = new DefaultButton(ERDesignerBundle.CLOSE);
		}

		return closeButton;
	}

	public DefaultButton getQueryButton() {

		if (queryButton == null) {
			queryButton = new DefaultButton(ERDesignerBundle.QUERY);
		}

		return queryButton;
	}

	public DefaultTextPane getSql() {

		if (sql == null) {
			sql = new DefaultTextPane();

			EditorKit editorKit = new SQLEditorKit();
			sql.setEditorKitForContentType("text/sql", editorKit);
			sql.setContentType("text/sql");
		}

		return sql;
	}

	public DefaultTable getData() {

		if (data == null) {
			data = new DefaultTable();
		}

		return data;
	}

	public DefaultPanel getBreadCrumb() {
		if (breadCrumb == null) {
			breadCrumb = new DefaultPanel();
			breadCrumb.setLayout(new FlowLayout(FlowLayout.LEFT));
		}
		return breadCrumb;
	}

	public JButton addBreadCrumb(String aDescription,
								 ActionListener aActionListener) {
		final JButton theButton = new JButton();
		theButton.setText(aDescription);
		theButton.addActionListener(aActionListener);
		theButton.addActionListener(e -> {
            int p = ArrayUtils.indexOf(breadCrumb.getComponents(),
                    theButton);
            while (breadCrumb.getComponentCount() > p + 1) {
                breadCrumb.remove(breadCrumb.getComponentCount() - 1);
            }
            breadCrumb.invalidate();
            breadCrumb.repaint();
        });
		breadCrumb.add(theButton);

		UIInitializer.getInstance().initialize(theButton);

		return theButton;
	}
}
