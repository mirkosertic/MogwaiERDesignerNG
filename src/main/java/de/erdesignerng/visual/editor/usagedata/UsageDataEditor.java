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
package de.erdesignerng.visual.editor.usagedata;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.util.ApplicationPreferences;
import de.erdesignerng.visual.UsageDataCollector;
import de.erdesignerng.visual.editor.BaseEditor;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.action.ActionEventProcessor;
import de.mogwai.common.client.looks.components.action.DefaultAction;
import de.mogwai.common.i18n.ResourceHelper;
import java.awt.Component;
import java.awt.event.ActionEvent;

/**
 * Editor to show usage data.
 */
public class UsageDataEditor extends BaseEditor {

    private UsageDataEditorView editorView;

    protected final DefaultAction iWantAction = new DefaultAction(new ActionEventProcessor() {

        @Override
        public void processActionEvent(ActionEvent e) {
            commandOk();
        }
    }, this, ERDesignerBundle.YESIWANT);

    protected final DefaultAction noThanksAction = new DefaultAction(new ActionEventProcessor() {

        @Override
        public void processActionEvent(ActionEvent e) {
            commandCancel();
        }
    }, this, ERDesignerBundle.NOTHANKS);

    protected final DefaultAction noThanksDontAskAgainAction = new DefaultAction(new ActionEventProcessor() {

        @Override
        public void processActionEvent(ActionEvent e) {
            ApplicationPreferences.getInstance().setUsageDataCollector(false);
            commandCancel();
        }
    }, this, ERDesignerBundle.NOTHANKS);


    public UsageDataEditor(Component aParent) {
        super(aParent, ERDesignerBundle.USAGEDATACOLLECTOR);

        initialize();
    }

    private void initialize() {

        editorView = new UsageDataEditorView();
        editorView.getOKButton().setAction(iWantAction);
        editorView.getCancelButton().setAction(noThanksAction);
        editorView.getDontAskAgain().setAction(noThanksDontAskAgainAction);
        setContentPane(editorView);

        StringBuilder theInfo = new StringBuilder("<html>");
        theInfo.append("<b>");
        theInfo.append(ResourceHelper.getResourceHelper(ERDesignerBundle.BUNDLE_NAME).getText(ERDesignerBundle.USAGEDATACOLLECTOR_INFO));
        theInfo.append("</b>");

        UsageDataCollector.getInstance().getHTMLSummary(theInfo);

        theInfo.append("</html>");

        editorView.getTextArea().setText(theInfo.toString());

        UIInitializer.getInstance().initialize(this);

        pack();
    }

    @Override
    public void applyValues() throws Exception {
        UsageDataCollector.getInstance().flush();
    }
}