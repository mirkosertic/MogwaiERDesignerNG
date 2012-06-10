package de.erdesignerng.visual.common;

import de.erdesignerng.model.Table;

/**
 * Command to show or hide all relations of a given table.
 */
public class ShowHideTableRelationsCommand extends UICommand {

    private Table table;
    private boolean show;

    public ShowHideTableRelationsCommand(Table aTable, boolean aShow) {
        table = aTable;
        show = aShow;
    }

    @Override
    public void execute() {

        ERDesignerComponent.getDefault().commandShowOrHideRelationsFor(table, show);
    }
}
