package de.erdesignerng.visual.common;

import de.erdesignerng.visual.editor.DialogConstants;
import de.erdesignerng.visual.editor.connection.RepositoryConnectionEditor;

public class RepositoryConnectionCommand extends UICommand {

    public RepositoryConnectionCommand(ERDesignerComponent component) {
        super(component);
    }

    @Override
    public void execute() {
        RepositoryConnectionEditor theEditor = new RepositoryConnectionEditor(component.scrollPane, component.preferences);
        if (theEditor.showModal() == DialogConstants.MODAL_RESULT_OK) {
            try {
                theEditor.applyValues();
            } catch (Exception e) {
                component.worldConnector.notifyAboutException(e);
            }
        }
    }
}