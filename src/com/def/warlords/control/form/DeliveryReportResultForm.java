package com.def.warlords.control.form;

import com.def.warlords.control.common.Sprites;
import com.def.warlords.game.model.City;
import com.def.warlords.gui.ImageButton;
import com.def.warlords.gui.Label;

import static com.def.warlords.control.common.Dimensions.SCREEN_WIDTH;

/**
 * @author wistful23
 * @version 1.23
 */
public class DeliveryReportResultForm extends ResultForm<ReportResult> {

    private final City city;

    public DeliveryReportResultForm(FormController controller, City city) {
        super(controller);
        this.city = city;
    }

    @Override
    void init() {
        add(new Label(0, 344, SCREEN_WIDTH, Label.Alignment.CENTER,
                city.getDeliveredArmy().getName() + " reaches " + city.getName()));
        add(new ImageButton(39, 366, Sprites.BUTTON_OK, source -> setResult(ReportResult.YES)));
        add(new ImageButton(520, 366, Sprites.BUTTON_END_REPORT, source -> setResult(ReportResult.END_REPORT)));
    }
}
