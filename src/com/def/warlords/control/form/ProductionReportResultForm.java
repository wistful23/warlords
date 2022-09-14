package com.def.warlords.control.form;

import com.def.warlords.game.model.City;
import com.def.warlords.gui.Label;
import com.def.warlords.gui.TextButton;

import static com.def.warlords.control.common.Dimensions.SCREEN_WIDTH;

/**
 * @author wistful23
 * @version 1.23
 */
public class ProductionReportResultForm extends ResultForm<ReportResult> {

    private final City city;

    public ProductionReportResultForm(FormController controller, City city) {
        super(controller);
        this.city = city;
    }

    @Override
    void init() {
        add(new Label(0, 344, SCREEN_WIDTH, Label.Alignment.CENTER,
                city.getProducedArmy().getName() +
                        (city.getTargetCity() == null ? " - Produced!" : " -> " + city.getTargetCity().getName())));
        add(new Label(0, 368, SCREEN_WIDTH, Label.Alignment.CENTER, "Keep producing these?"));
        add(new TextButton(39, 366, " Yes ", source -> setResult(ReportResult.YES)));
        add(new TextButton(105, 366, " No ", source -> setResult(ReportResult.NO)));
        add(new TextButton(520, 366, "End Report", source -> setResult(ReportResult.END_REPORT)));
    }
}
