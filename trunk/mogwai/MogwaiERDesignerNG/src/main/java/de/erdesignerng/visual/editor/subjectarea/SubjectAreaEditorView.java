package de.erdesignerng.visual.editor.subjectarea;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import de.erdesignerng.ERDesignerBundle;
import de.mogwai.common.client.looks.components.DefaultButton;
import de.mogwai.common.client.looks.components.DefaultLabel;
import de.mogwai.common.client.looks.components.DefaultSeparator;
import de.mogwai.common.client.looks.components.DefaultTextField;
import de.mogwai.common.i18n.ResourceHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-06-13 16:48:59 $
 */
public class SubjectAreaEditorView extends JPanel {

	private DefaultLabel component1;

	private DefaultTextField subjectAreaName;

	private JPanel component8;

	private JPanel colorPanel;

	private DefaultButton okButton;

	private DefaultButton cancelButton;

	public class MyMouseListener extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			Color theColor = JColorChooser.showDialog(SubjectAreaEditorView.this, ResourceHelper.getResourceHelper(
					ERDesignerBundle.BUNDLE_NAME).getText(ERDesignerBundle.COLOR), colorPanel.getBackground());
			if (theColor != null) {
				colorPanel.setBackground(theColor);
			}
		}

	}

	/**
	 * Constructor.
	 */
	public SubjectAreaEditorView() {
		initialize();
	}

	/**
	 * Initialize method.
	 */
	private void initialize() {

		String rowDef = "2dlu,p,2dlu,p,2dlu,fill:p,20dlu,p,2dlu";
		String colDef = "2dlu,60dlu,2dlu,fill:150dlu:grow,2dlu";

		FormLayout layout = new FormLayout(colDef, rowDef);
		setLayout(layout);

		CellConstraints cons = new CellConstraints();

		add(new DefaultSeparator(ERDesignerBundle.SUBJECTAREAPROPERTIES), cons.xywh(2, 2, 3, 1));
		add(getComponent1(), cons.xywh(2, 4, 1, 1));
		add(getSubjectAreaName(), cons.xywh(4, 4, 1, 1));
		add(new DefaultLabel(ERDesignerBundle.COLOR), cons.xywh(2, 6, 1, 1));
		add(getColorPanel(), cons.xywh(4, 6, 1, 1));

		add(getComponent8(), cons.xywh(2, 8, 3, 1));

		buildGroups();
	}

	/**
	 * Getter method for component Component_1.
	 *
	 * @return the initialized component
	 */
	public JLabel getComponent1() {

		if (component1 == null) {
			component1 = new DefaultLabel(ERDesignerBundle.NAME);
		}

		return component1;
	}

	/**
	 * Getter method for component Relationname.
	 *
	 * @return the initialized component
	 */
	public DefaultTextField getSubjectAreaName() {

		if (subjectAreaName == null) {
			subjectAreaName = new DefaultTextField();
		}

		return subjectAreaName;
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

	/**
	 * @return the colorPanel
	 */
	public JPanel getColorPanel() {
		if (colorPanel == null) {
			colorPanel = new JPanel();
			colorPanel.addMouseListener(new MyMouseListener());
		}
		return colorPanel;
	}

	/**
	 * Initialize method.
	 */
	private void buildGroups() {
	}
}
