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
package de.erdesignerng.visual.editor.preferences;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.erdesignerng.ERDesignerBundle;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.DefaultButton;
import de.mogwai.common.client.looks.components.DefaultLabel;
import de.mogwai.common.client.looks.components.DefaultPanel;
import de.mogwai.common.client.looks.components.DefaultTextField;
import de.mogwai.common.client.looks.components.action.DefaultAction;
import de.mogwai.common.i18n.ResourceHelper;
import de.mogwai.common.i18n.ResourceHelperProvider;

public class PreferencesPanel extends DefaultPanel implements ResourceHelperProvider {

    private DefaultButton searchDotButton = new DefaultButton();

    private DefaultTextField dotPath = new DefaultTextField();

    private DefaultAction okAction = new DefaultAction(ERDesignerBundle.BUNDLE_NAME, ERDesignerBundle.ADDFOLDER);

    public PreferencesPanel() {
        initialize();
    }

    private void initialize() {

        String theColDef = "2dlu,p,2dlu,p:grow,2dlu,20dlu,2";
        String theRowDef = "2dlu,p,50dlu";

        searchDotButton.setAction(okAction);
        okAction.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                commandSelectDOTDir();
            }
        });

        FormLayout theLayout = new FormLayout(theColDef, theRowDef);
        setLayout(theLayout);

        CellConstraints cons = new CellConstraints();

        add(new DefaultLabel(ERDesignerBundle.DOTPATH), cons.xy(2, 2));
        add(dotPath, cons.xy(4, 2));
        add(searchDotButton, cons.xy(6, 2));

        UIInitializer.getInstance().initialize(this);
    }

    public ResourceHelper getResourceHelper() {
        return ResourceHelper.getResourceHelper(ERDesignerBundle.BUNDLE_NAME);
    }

    private void commandSelectDOTDir() {
        
        JFileChooser theChooser = new JFileChooser();
        String theDotDir = dotPath.getText();
        if (theDotDir != null) {
            theChooser.setCurrentDirectory(new File(theDotDir));
        }
        if (theChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File theFile = theChooser.getSelectedFile();
            
            dotPath.setText(theFile.toString());
        }        
    }

    /**
     * @return the dotPath
     */
    public DefaultTextField getDotPath() {
        return dotPath;
    }

    /**
     * @param dotPath
     *            the dotPath to set
     */
    public void setDotPath(DefaultTextField dotPath) {
        this.dotPath = dotPath;
    }
}
