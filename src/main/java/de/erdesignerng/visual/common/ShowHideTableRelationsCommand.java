package de.erdesignerng.visual.common;

import de.erdesignerng.model.Table;
import de.erdesignerng.visual.UsageDataCollector;

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

        UsageDataCollector.getInstance().addExecutedUsecase(UsageDataCollector.Usecase.SHOW_HIDE_RELATONS_FOR_TABLE);

        ERDesignerComponent.getDefault().commandShowOrHideRelationsFor(table, show);
    }
}
