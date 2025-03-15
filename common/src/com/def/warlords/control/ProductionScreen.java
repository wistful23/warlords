package com.def.warlords.control;

import com.def.warlords.control.common.Sprites;
import com.def.warlords.control.form.ArmyInfoForm;
import com.def.warlords.game.model.ArmyFactory;
import com.def.warlords.game.model.City;
import com.def.warlords.gui.*;

/**
 * @author wistful23
 * @version 1.23
 */
public class ProductionScreen extends Container {

    private final MainController controller;

    private City currentCity;
    private Image locErrorImage;
    private Button locButton;
    private Component currentFactoryComponent;

    public ProductionScreen(MainController controller) {
        super(0, 332, 640, 68);
        this.controller = controller;
    }

    public void init(City city) {
        if (currentCity != null) {
            throw new IllegalStateException("Production Screen is already initialized");
        }
        currentCity = city;
        // Loc error.
        locErrorImage = add(new Image(371, 336, Sprites.LOC_ERROR_NONE));
        // Prod button.
        final Button prodButton = add(new TextButton(438, 336, "Prod", source -> {
            final boolean enoughGold = currentCity.startProducing((ArmyFactory) currentFactoryComponent.getTag());
            controller.deactivateProductionScreen(enoughGold ? null : "Not enough gold!");
        }));
        prodButton.setEnabled(false);
        // Loc button.
        locButton = add(new TextButton(494, 336, "Loc", true,
                source -> controller.getStrategicMap().setMode(StrategicMap.Mode.PRODUCTION_TARGET)));
        locButton.setEnabled(false);
        // Stop button.
        add(new TextButton(542, 336, "Stop", source -> {
            currentCity.stopProducing();
            controller.deactivateProductionScreen(null);
        }));
        // Exit button.
        add(new TextButton(594, 336, "Exit", source -> controller.deactivateProductionScreen(null)));
        // Current factory.
        add(new Label(424, 368, "Current:"));
        if (currentCity.isProducing()) {
            add(new Image(496, 368, Sprites.getArmySprite(currentCity.getCurrentFactory())));
            add(new Label(540, 368,
                    currentCity.getRemainingTime() + "t" + (currentCity.getTargetCity() != null ? " - Loc" : "")));
        } else {
            add(new Label(540, 368, "None"));
        }
        // Factories.
        for (int index = 0; index < currentCity.getFactoryCount(); ++index) {
            final ArmyFactory factory = currentCity.getFactory(index);
            final int x = index / 2;
            final int y = index % 2;
            add(new Label(8 + x * 168, 340 + y * 32, "F" + (index + 5)));
            add(new Label(96 + x * 172, 340 + y * 28, factory.getTime() + "t/" + factory.getCost() + "gp"));
            // NOTE: W doesn't offset the army sprite to the right.
            add(new SelectableImage(48 + x * 168, 334 + y * 32, Sprites.getArmySprite(factory), source -> {
                if (source == currentFactoryComponent) {
                    // BUG: W releases the LOC button when the army info called.
                    new ArmyInfoForm(controller, factory).activate();
                    return;
                }
                prodButton.setEnabled(true);
                locButton.setEnabled(currentCity.getEmpire().getCityCount() > 1 && !factory.getType().isNavy());
                if (locButton.isSelected()) {
                    // BUG: W doesn't update the strategic map.
                    controller.getStrategicMap().setMode(StrategicMap.Mode.PRODUCTION_SOURCE);
                    locButton.release();
                }
                if (currentFactoryComponent != null) {
                    currentFactoryComponent.setSelected(false);
                }
                currentFactoryComponent = source;
                currentFactoryComponent.setSelected(true);
            })).setTag(factory);
        }
    }

    public void reset() {
        clear();
        currentCity = null;
        locButton = null;
        currentFactoryComponent = null;
    }

    public void assignTargetCity(City targetCity) {
        if (targetCity == currentCity) {
            locErrorImage.setSprite(Sprites.LOC_ERROR_ITSELF);
        } else if (targetCity != currentCity.getTargetCity() &&
                targetCity.getSourceCityCount() == City.MAX_SOURCE_CITY_COUNT) {
            // NOTE: W checks gold first.
            // NOTE: W doesn't check if the current city is already a source of the target city.
            locErrorImage.setSprite(Sprites.LOC_ERROR_CAPACITY);
        } else if (!currentCity.startProducing((ArmyFactory) currentFactoryComponent.getTag(), targetCity)) {
            locErrorImage.setSprite(Sprites.LOC_ERROR_GOLD);
        } else {
            controller.deactivateProductionScreen(
                    "Production transferred form " + currentCity.getName() + " to " + targetCity.getName());
        }
    }

    public void notifyCity(City city) {
        final boolean citySelected =
                city != null && city.getEmpire() == controller.getGame().getCurrentPlayer().getEmpire();
        if (currentCity == null) {
            // Maybe assign the current city.
            if (citySelected) {
                controller.activateProductionScreen(city);
            } else {
                // NOTE: W makes a beep.
                // Disable the production mode and reset the sword button.
                controller.deactivateProductionScreen(null);
            }
        } else if (locButton.isSelected() && citySelected) {
            // Maybe assign the target city.
            assignTargetCity(city);
        }
        // Do nothing if the city is already selected.
    }
}
