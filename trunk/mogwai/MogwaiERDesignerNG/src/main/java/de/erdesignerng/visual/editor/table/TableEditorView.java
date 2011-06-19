package de.erdesignerng.visual.editor.table;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.model.IndexExpression;
import de.mogwai.common.client.looks.components.*;

import javax.swing.*;
import java.awt.*;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 */
public class TableEditorView extends DefaultPanel {

	private DefaultLabel component1;

	private DefaultTextField entityName;

	private DefaultTabbedPane mainTabbedPane;

	private DefaultTabbedPaneTab attributesTab;

	private DefaultList attributeList;

	private DefaultButton newButton;

	private DefaultButton deleteButton;

	private DefaultTabbedPane component15;

	private DefaultTabbedPaneTab attributesGeneralTab;

	private DefaultLabel component20;

	private DefaultTextField attributeName;

	private DefaultCheckBox nullable;

	private DefaultLabel component42;

	private DefaultTabbedPaneTab attributeCommentsTab;

	private DefaultTextArea attributeComments;

	private DefaultButton updateAttributeButton;

	private DefaultTabbedPaneTab indexesTab;

	private DefaultList indexList;

	private DefaultButton newIndexButton;

	private DefaultButton deleteIndexButton;

	private DefaultTabbedPane indexTabbedPane;

	private DefaultTabbedPaneTab indexGeneralTab;

	private DefaultLabel label1;

	private DefaultTextField indexName;

	private DefaultTextField extra;

	private DefaultRadioButton uniqueIndex;

	private DefaultRadioButton notUniqueIndex;

	private DefaultRadioButton primaryKeyIndex;

	private DefaultList<IndexExpression> indexAttributesList;

	private DefaultButton updateIndexButton;

	private DefaultTabbedPaneTab tableCommentsTab;

	private DefaultTextArea tableComment;

	private DefaultButton okButton;

	private DefaultButton cancelButton;

	private final DefaultComboBox dataType = new DefaultComboBox();

	private final DefaultSpinner sizeSpinner = new DefaultSpinner();

	private final DefaultSpinner fractionSpinner = new DefaultSpinner();

	private final DefaultSpinner scaleSpinner = new DefaultSpinner();

	private DefaultTextField defaultValue = new DefaultTextField();

	private final DefaultComboBox indexAttribute = new DefaultComboBox();

	private final DefaultTextField indexExpression = new DefaultTextField();

	private final DefaultRadioButton addIndexAttribute = new DefaultRadioButton(ERDesignerBundle.ATTRIBUTE);

	private final DefaultRadioButton addIndexExpression = new DefaultRadioButton(ERDesignerBundle.EXPRESSION);

	private DefaultButton addExpressionToIndexButton = new DefaultButton(ERDesignerBundle.NEWONLYICON);

	private DefaultButton addAttributeToIndexButton = new DefaultButton(ERDesignerBundle.NEWONLYICON);

	private final DefaultButton removeFromIndexButton = new DefaultButton(ERDesignerBundle.DELETEONLYICON);

	private DefaultTabbedPaneTab tablePropertiesTab;

	private DefaultTabbedPaneTab indexPropertiesTab;

	public TableEditorView() {
		initialize();
	}

	private void initialize() {

		String rowDef = "2dlu,p,2dlu,p,fill:220dlu,p,20dlu,p,2dlu";
		String colDef = "2dlu,left:45dlu,2dlu,fill:140dlu:grow,fill:60dlu,2dlu,fill:60dlu,2dlu";

		FormLayout layout = new FormLayout(colDef, rowDef);
		setLayout(layout);

		CellConstraints cons = new CellConstraints();

		this.add(getComponent1(), cons.xywh(2, 2, 1, 1));
		this.add(getEntityName(), cons.xywh(4, 2, 4, 1));
		this.add(getMainTabbedPane(), cons.xywh(2, 4, 6, 2));
		this.add(getOkButton(), cons.xywh(5, 8, 1, 1));
		this.add(getCancelButton(), cons.xywh(7, 8, 1, 1));

		buildGroups();

		getAddIndexAttribute().setSelected(true);
		getRemoveFromIndexButton().setEnabled(false);
	}

	public JLabel getComponent1() {

		if (component1 == null) {
			component1 = new DefaultLabel(ERDesignerBundle.ENTITYNAME);
		}

		return component1;
	}

	public DefaultTextField getEntityName() {

		if (entityName == null) {
			entityName = new DefaultTextField();
			entityName.setName("Entity_name");
		}

		return entityName;
	}

	public DefaultTabbedPane getMainTabbedPane() {

		if (mainTabbedPane == null) {
			mainTabbedPane = new DefaultTabbedPane();
			mainTabbedPane.addTab(null, getAttributesTab());
			mainTabbedPane.addTab(null, getIndexesTab());
			mainTabbedPane.addTab(null, getTableCommentsTab());
			mainTabbedPane.addTab(null, getTablePropertiesTab());
			mainTabbedPane.setName("MainTabbedPane");
			mainTabbedPane.setSelectedIndex(0);
		}

		return mainTabbedPane;
	}

	public DefaultTabbedPaneTab getAttributesTab() {

		if (attributesTab == null) {
			attributesTab = new DefaultTabbedPaneTab(mainTabbedPane, ERDesignerBundle.ATTRIBUTES);

			String rowDef = "2dlu,p,2dlu,p,165dlu:grow,p,2dlu,p,2dlu";
			String colDef = "2dlu,50dlu:grow,2dlu,50dlu:grow,2dlu,50dlu:grow,11dlu:grow,2dlu,11dlu:grow,2dlu,80dlu:grow,2dlu,70dlu:grow,2dlu";

			FormLayout layout = new FormLayout(colDef, rowDef);
			attributesTab.setLayout(layout);

			CellConstraints cons = new CellConstraints();

			// this.m_attributestab.add(this.getUpButton(), cons.xywh(7, 2, 1,
			// 1));
			// this.m_attributestab.add(this.getDownButton(), cons
			// .xywh(9, 2, 1, 1));
			attributesTab.add(new DefaultScrollPane(getAttributeList()), cons.xywh(2, 4, 8, 3));
			attributesTab.add(getNewButton(), cons.xywh(2, 8, 1, 1));
			attributesTab.add(getDeleteButton(), cons.xywh(6, 8, 4, 1));
			attributesTab.add(getComponent15(), cons.xywh(11, 2, 3, 5));
			attributesTab.add(getUpdateAttributeButton(), cons.xywh(13, 8, 1, 1));
			attributesTab.setName("AttributesTab");
		}

		return attributesTab;
	}

	public DefaultList getAttributeList() {

		if (attributeList == null) {
			attributeList = new DefaultList();
			attributeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}

		return attributeList;
	}

	public JButton getNewButton() {

		if (newButton == null) {
			newButton = new DefaultButton(ERDesignerBundle.NEW);
		}

		return newButton;
	}

	public JButton getDeleteButton() {

		if (deleteButton == null) {
			deleteButton = new DefaultButton(ERDesignerBundle.DELETE);
		}

		return deleteButton;
	}

	public DefaultTabbedPane getComponent15() {

		if (component15 == null) {
			component15 = new DefaultTabbedPane();
			component15.addTab(null, getAttributesGeneralTab());
			component15.addTab(null, getAttributeCommentTab());
			component15.setName("Component_15");
			component15.setSelectedIndex(0);
		}

		return component15;
	}

	public DefaultTabbedPaneTab getAttributesGeneralTab() {

		if (attributesGeneralTab == null) {
			attributesGeneralTab = new DefaultTabbedPaneTab(component15, ERDesignerBundle.GENERAL);

			String rowDef = "2dlu,p,2dlu,p,10dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu";
			String colDef = "2dlu,left:40dlu,2dlu,60dlu:grow,2dlu";

			FormLayout layout = new FormLayout(colDef, rowDef);
			attributesGeneralTab.setLayout(layout);

			CellConstraints cons = new CellConstraints();

			attributesGeneralTab.add(getComponent20(), cons.xywh(2, 2, 1, 1));
			attributesGeneralTab.add(getAttributeName(), cons.xywh(4, 2, 1, 1));
			attributesGeneralTab.add(new DefaultLabel(ERDesignerBundle.DATATYPE), cons.xywh(2, 4, 1, 1));
			attributesGeneralTab.add(getDataType(), cons.xywh(4, 4, 1, 1));
			attributesGeneralTab.add(new DefaultLabel(ERDesignerBundle.SIZE), cons.xywh(2, 6, 1, 1));
			attributesGeneralTab.add(getSizeSpinner(), cons.xywh(4, 6, 1, 1));
			attributesGeneralTab.add(new DefaultLabel(ERDesignerBundle.FRACTION), cons.xywh(2, 8, 1, 1));
			attributesGeneralTab.add(getFractionSpinner(), cons.xywh(4, 8, 1, 1));
			attributesGeneralTab.add(new DefaultLabel(ERDesignerBundle.SCALE), cons.xywh(2, 10, 1, 1));
			attributesGeneralTab.add(getScaleSpinner(), cons.xywh(4, 10, 1, 1));

			attributesGeneralTab.add(getNullable(), cons.xywh(4, 12, 1, 1));

			attributesGeneralTab.add(new DefaultLabel(ERDesignerBundle.DEFAULT), cons.xywh(2, 14, 1, 1));
			attributesGeneralTab.add(getDefault(), cons.xywh(4, 14, 1, 1));
			attributesGeneralTab.add(new DefaultLabel(ERDesignerBundle.EXTRA), cons.xywh(2, 16, 1, 1));
			attributesGeneralTab.add(getExtra(), cons.xywh(4, 16, 1, 1));

			attributesGeneralTab.setName("AttributesGeneralTab");
		}

		return attributesGeneralTab;
	}

	public JLabel getComponent20() {

		if (component20 == null) {
			component20 = new DefaultLabel(ERDesignerBundle.NAME);
		}

		return component20;
	}

	public DefaultTextField getAttributeName() {

		if (attributeName == null) {
			attributeName = new DefaultTextField();
			attributeName.setName("AttributeName");
		}

		return attributeName;
	}

	public JCheckBox getNullable() {

		if (nullable == null) {
			nullable = new DefaultCheckBox(ERDesignerBundle.NULLABLE);
		}

		return nullable;
	}

	public DefaultTextField getDefault() {

		if (defaultValue == null) {
			defaultValue = new DefaultTextField();
		}

		return defaultValue;
	}

	public DefaultTextField getExtra() {

		if (extra == null) {
			extra = new DefaultTextField();
		}

		return extra;
	}

	public DefaultTabbedPaneTab getAttributeCommentTab() {

		if (attributeCommentsTab == null) {
			attributeCommentsTab = new DefaultTabbedPaneTab(component15, ERDesignerBundle.COMMENTS);

			String rowDef = "2dlu,p,160dlu:grow,p,2dlu";
			String colDef = "2dlu,left:40dlu,2dlu,60dlu:grow,2dlu";

			FormLayout layout = new FormLayout(colDef, rowDef);
			attributeCommentsTab.setLayout(layout);

			CellConstraints cons = new CellConstraints();

			attributeCommentsTab.add(new DefaultScrollPane(getAttributeComment()), cons.xywh(2, 2, 3, 3));
			attributeCommentsTab.setName("AttributeCommentTab");
			attributeCommentsTab.setVisible(false);
		}

		return attributeCommentsTab;
	}

	public JTextArea getAttributeComment() {

		if (attributeComments == null) {
			attributeComments = new DefaultTextArea();
		}

		return attributeComments;
	}

	public JButton getUpdateAttributeButton() {

		if (updateAttributeButton == null) {
			updateAttributeButton = new DefaultButton(ERDesignerBundle.UPDATE);
		}

		return updateAttributeButton;
	}

	public DefaultTabbedPaneTab getIndexesTab() {

		if (indexesTab == null) {
			indexesTab = new DefaultTabbedPaneTab(mainTabbedPane, ERDesignerBundle.INDEXES);

			String rowDef = "2dlu,p,165dlu:grow,p,2dlu,p,2dlu";
			String colDef = "2dlu,50dlu:grow,2dlu,50dlu:grow,2dlu,50dlu:grow,11dlu:grow,2dlu,11dlu:grow,2dlu,80dlu:grow,2dlu,70dlu:grow,2dlu";

			FormLayout layout = new FormLayout(colDef, rowDef);
			indexesTab.setLayout(layout);

			CellConstraints cons = new CellConstraints();

			indexesTab.add(new DefaultScrollPane(getIndexList()), cons.xywh(2, 2, 8, 3));
			indexesTab.add(getNewIndexButton(), cons.xywh(2, 6, 1, 1));
			indexesTab.add(getDeleteIndexButton(), cons.xywh(6, 6, 4, 1));
			indexesTab.add(getIndexTabbedPane(), cons.xywh(11, 2, 3, 3));
			indexesTab.add(getUpdateIndexButton(), cons.xywh(13, 6, 1, 1));
			indexesTab.setName("IndexesTab");
			indexesTab.setVisible(false);
		}

		return indexesTab;
	}

	public DefaultList getIndexList() {

		if (indexList == null) {
			indexList = new DefaultList();
			indexList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}

		return indexList;
	}

	public JButton getNewIndexButton() {

		if (newIndexButton == null) {
			newIndexButton = new DefaultButton(ERDesignerBundle.NEW);
		}

		return newIndexButton;
	}

	public JButton getDeleteIndexButton() {

		if (deleteIndexButton == null) {
			deleteIndexButton = new DefaultButton(ERDesignerBundle.DELETE);
		}

		return deleteIndexButton;
	}

	public DefaultTabbedPane getIndexTabbedPane() {

		if (indexTabbedPane == null) {
			indexTabbedPane = new DefaultTabbedPane();
			indexTabbedPane.addTab(null, getIndexGeneralTab());
			indexTabbedPane.addTab(null, getIndexPropertiesTab());
			indexTabbedPane.setName("IndexTabbedPane");
			indexTabbedPane.setSelectedIndex(0);
		}

		return indexTabbedPane;
	}

	public DefaultTabbedPaneTab getIndexGeneralTab() {

		if (indexGeneralTab == null) {
			indexGeneralTab = new DefaultTabbedPaneTab(indexTabbedPane, ERDesignerBundle.GENERAL);

			String colDef = "2dlu,left:40dlu,2dlu,60dlu:grow,2dlu,20dlu,2dlu";
			String rowDef = "2dlu,p,2dlu,fill:20dlu:grow,2dlu,p,2dlu,p,2dlu,p,4dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu";

			FormLayout layout = new FormLayout(colDef, rowDef);
			indexGeneralTab.setLayout(layout);

			CellConstraints cons = new CellConstraints();

			indexGeneralTab.add(getLabel1(), cons.xywh(2, 2, 1, 1));
			indexGeneralTab.add(getIndexName(), cons.xywh(4, 2, 3, 1));

			indexGeneralTab.add(new DefaultScrollPane(getIndexFieldList()), cons.xywh(2, 4, 5, 1));

			indexGeneralTab.add(getRemoveFromIndexButton(), cons.xy(6, 6));

			indexGeneralTab.add(getAddIndexAttribute(), cons.xy(2, 8));
			indexGeneralTab.add(getIndexAttribute(), cons.xy(4, 8));
			indexGeneralTab.add(getAddAttributeToIndexButton(), cons.xy(6, 8));

			indexGeneralTab.add(getAddIndexExpression(), cons.xy(2, 10));
			indexGeneralTab.add(getIndexExpression(), cons.xy(4, 10));
			indexGeneralTab.add(getAddExpressionToIndexButton(), cons.xy(6, 10));

			indexGeneralTab.add(getPrimaryIndex(), cons.xywh(4, 12, 3, 1));
			indexGeneralTab.add(getUniqueIndex(), cons.xywh(4, 14, 3, 1));
			indexGeneralTab.add(getNotUniqueIndex(), cons.xywh(4, 16, 3, 1));
			indexGeneralTab.setName("IndexGeneralTab");
		}

		return indexGeneralTab;
	}

	public DefaultLabel getLabel1() {

		if (label1 == null) {
			label1 = new DefaultLabel(ERDesignerBundle.NAME);
		}

		return label1;
	}

	public DefaultTextField getIndexName() {

		if (indexName == null) {
			indexName = new DefaultTextField();
			indexName.setName("IndexName");
		}

		return indexName;
	}

	public DefaultRadioButton getUniqueIndex() {

		if (uniqueIndex == null) {
			uniqueIndex = new DefaultRadioButton(ERDesignerBundle.INDEXISUNIQUE);
		}

		return uniqueIndex;
	}

	public DefaultRadioButton getPrimaryIndex() {

		if (primaryKeyIndex == null) {
			primaryKeyIndex = new DefaultRadioButton(ERDesignerBundle.INDEXISPRIMARY);
		}

		return primaryKeyIndex;
	}

	public DefaultRadioButton getNotUniqueIndex() {

		if (notUniqueIndex == null) {
			notUniqueIndex = new DefaultRadioButton(ERDesignerBundle.INDEXISNOTUNIQUE);
		}

		return notUniqueIndex;
	}

	public DefaultList<IndexExpression> getIndexFieldList() {

		if (indexAttributesList == null) {
			indexAttributesList = new DefaultList<IndexExpression>();
			indexAttributesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}

		return indexAttributesList;
	}

	public DefaultButton getUpdateIndexButton() {

		if (updateIndexButton == null) {
			updateIndexButton = new DefaultButton(ERDesignerBundle.UPDATE);
		}

		return updateIndexButton;
	}

	public DefaultTabbedPaneTab getTableCommentsTab() {

		if (tableCommentsTab == null) {
			tableCommentsTab = new DefaultTabbedPaneTab(mainTabbedPane, ERDesignerBundle.COMMENTS);

			String rowDef = "2dlu,p,100dlu:grow,p,2dlu";
			String colDef = "2dlu,40dlu:grow,2dlu";

			FormLayout layout = new FormLayout(colDef, rowDef);
			tableCommentsTab.setLayout(layout);

			CellConstraints cons = new CellConstraints();

			tableCommentsTab.add(new DefaultScrollPane(getEntityComment()), cons.xywh(2, 2, 1, 3));
			tableCommentsTab.setName("MainCommentsTab");
			tableCommentsTab.setVisible(false);
		}

		return tableCommentsTab;
	}

	public DefaultTextArea getEntityComment() {

		if (tableComment == null) {
			tableComment = new DefaultTextArea();
			tableComment.setName("EntityComment");
		}

		return tableComment;
	}

	public JButton getOkButton() {

		if (okButton == null) {
			okButton = new DefaultButton(ERDesignerBundle.OK);
		}

		return okButton;
	}

	public JButton getCancelButton() {

		if (cancelButton == null) {
			cancelButton = new DefaultButton(ERDesignerBundle.CANCEL);
		}

		return cancelButton;
	}

	/**
	 * Initialize method.
	 */
	private void buildGroups() {

		ButtonGroup theGroup = new ButtonGroup();
		theGroup.add(getPrimaryIndex());
		theGroup.add(getUniqueIndex());
		theGroup.add(getNotUniqueIndex());

		ButtonGroup theGroup2 = new ButtonGroup();
		theGroup2.add(getAddIndexAttribute());
		theGroup2.add(getAddIndexExpression());
	}

	public DefaultComboBox getDataType() {
		return dataType;
	}

	public DefaultSpinner getFractionSpinner() {
		return fractionSpinner;
	}

	public DefaultSpinner getScaleSpinner() {
		return scaleSpinner;
	}

	public DefaultSpinner getSizeSpinner() {
		return sizeSpinner;
	}

	public DefaultComboBox getIndexAttribute() {
		return indexAttribute;
	}

	public DefaultRadioButton getAddIndexAttribute() {
		return addIndexAttribute;
	}

	public DefaultRadioButton getAddIndexExpression() {
		return addIndexExpression;
	}

	public DefaultButton getAddExpressionToIndexButton() {
		return addExpressionToIndexButton;
	}

	public DefaultButton getAddAttributeToIndexButton() {
		return addAttributeToIndexButton;
	}

	public DefaultTextField getIndexExpression() {
		return indexExpression;
	}

	public DefaultButton getRemoveFromIndexButton() {
		return removeFromIndexButton;
	}

	public DefaultTabbedPaneTab getTablePropertiesTab() {
		if (tablePropertiesTab == null) {
			tablePropertiesTab = new DefaultTabbedPaneTab(mainTabbedPane, ERDesignerBundle.PROPERTIES);
			tablePropertiesTab.setLayout(new BorderLayout());
		}
		return tablePropertiesTab;
	}

	public DefaultTabbedPaneTab getIndexPropertiesTab() {
		if (indexPropertiesTab == null) {
			indexPropertiesTab = new DefaultTabbedPaneTab(indexTabbedPane, ERDesignerBundle.PROPERTIES);
			indexPropertiesTab.setLayout(new BorderLayout());
		}
		return indexPropertiesTab;
	}

	public void disableTablePropertiesTab() {
		getMainTabbedPane().removeTabAt(3);
	}

	public void disableIndexPropertiesTab() {
		if (getIndexTabbedPane().getTabCount() > 1) {
			getIndexTabbedPane().removeTabAt(1);
		}
	}

	public void enableIndexPropertiesTab() {
		getIndexTabbedPane().addTab(null, getIndexPropertiesTab());
	}
}