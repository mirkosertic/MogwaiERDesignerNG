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
package de.erdesignerng.visual.editor.connection;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.dialect.Dialect;
import de.mogwai.common.client.looks.components.*;

import javax.swing.JPanel;

public class RepositoryConnectionEditorView extends DefaultPanel {

    private final DefaultComboBox dialect = new DefaultComboBox();

    private final DefaultTextField driver = new DefaultTextField();

    private final DefaultTextField url = new DefaultTextField();

    private final DefaultTextField user = new DefaultTextField();

    private final DefaultPasswordField password = new DefaultPasswordField();

    private final DefaultButton testButton = new DefaultButton();

    private final DefaultButton okButton = new DefaultButton();

    private final DefaultButton cancelButton = new DefaultButton();

    public RepositoryConnectionEditorView() {
        initialize();
    }

    private void initialize() {

        String theColDef = "2dlu,p,2dlu,fill:150dlu:grow,2";
        String theRowDef = "2dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,10dlu,p,2dlu";

        FormLayout theLayout = new FormLayout(theColDef, theRowDef);
        setLayout(theLayout);

        CellConstraints cons = new CellConstraints();

        add(new DefaultLabel(ERDesignerBundle.DIALECT), cons.xy(2, 2));
        add(dialect, cons.xy(4, 2));

        add(new DefaultLabel(ERDesignerBundle.JDBCDRIVER), cons.xy(2, 4));
        add(driver, cons.xy(4, 4));

        add(new DefaultLabel(ERDesignerBundle.JDBCURL), cons.xy(2, 6));
        add(url, cons.xy(4, 6));

        add(new DefaultLabel(ERDesignerBundle.USER), cons.xy(2, 8));
        add(user, cons.xy(4, 8));

        add(new DefaultLabel(ERDesignerBundle.PASSWORD), cons.xy(2, 10));
        add(password, cons.xy(4, 10));

        JPanel thePanel = new JPanel();

        theColDef = "60dlu,2dlu:grow,60dlu,2dlu,60dlu,2dlu";
        theRowDef = "p";

        theLayout = new FormLayout(theColDef, theRowDef);
        thePanel.setLayout(theLayout);

        thePanel.add(testButton, cons.xy(1, 1));
        testButton.setText("Test");

        thePanel.add(okButton, cons.xy(3, 1));
        okButton.setText("Ok");

        thePanel.add(cancelButton, cons.xy(5, 1));
        cancelButton.setText("Cancel");

        dialect.addActionListener(e -> handleDialectChange((Dialect) dialect.getSelectedItem()));

        add(thePanel, cons.xywh(2, 12, 3, 1));
    }

    public DefaultComboBox getDialect() {
        return dialect;
    }

    public DefaultTextField getDriver() {
        return driver;
    }

    public DefaultPasswordField getPassword() {
        return password;
    }

    public DefaultTextField getUrl() {
        return url;
    }

    public DefaultTextField getUser() {
        return user;
    }

    public void handleDialectChange(Dialect aDialect) {
    }

    /**
     * @return the cancelButton
     */
    public DefaultButton getCancelButton() {
        return cancelButton;
    }

    /**
     * @return the okButton
     */
    public DefaultButton getOkButton() {
        return okButton;
    }

    /**
     * @return the testButton
     */
    public DefaultButton getTestButton() {
        return testButton;
    }
}