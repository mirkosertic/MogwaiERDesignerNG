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
package de.erdesignerng.visual.editor.sql;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.dialect.Statement;
import de.erdesignerng.dialect.StatementList;
import de.erdesignerng.model.Model;
import de.erdesignerng.visual.editor.BaseEditor;
import de.erdesignerng.visual.editor.DialogConstants;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.DefaultTextArea;
import de.mogwai.common.client.looks.components.list.DefaultListModel;

/**
 * Editor for the class path entries.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-02-01 17:20:30 $
 */
public class SQLEditor extends BaseEditor {

    private final class StatementRenderer implements ListCellRenderer {

        private DefaultTextArea label = new DefaultTextArea();

        public Component getListCellRendererComponent(JList aList, Object aValue, int aIndex, boolean isSelected,
                boolean cellHasFocus) {
            Statement theStatement = (Statement) aValue;
            label.setText(theStatement.getSql());
            return label;
        }
    }

    private SQLEditorView view = new SQLEditorView();

    public SQLEditor(Component aParent, Model aModel, StatementList aStatements) {
        super(aParent, ERDesignerBundle.CLASSPATHCONFIGURATION);

        initialize();

        view.getSqlList().setCellRenderer(new StatementRenderer());

        DefaultListModel theModel = (DefaultListModel) view.getSqlList().getModel();
        for (Statement theStatement : aStatements) {
            theModel.add(theStatement);
        }
    }

    private void initialize() {

        setContentPane(view);
        setResizable(false);
        pack();

        UIInitializer.getInstance().initialize(this);
    }

    @Override
    public void applyValues() throws Exception {
    }

    private void commandClose() {
        setModalResult(DialogConstants.MODAL_RESULT_OK);
    }

}
