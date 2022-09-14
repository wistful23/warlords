package com.def.warlords.control.form;

import com.def.warlords.control.common.GameHelper;
import com.def.warlords.control.common.Sprites;
import com.def.warlords.control.common.StrategicMapComponent;
import com.def.warlords.game.model.ArmyDelivery;
import com.def.warlords.game.model.ArmyFactory;
import com.def.warlords.game.model.City;
import com.def.warlords.game.model.EmpireType;
import com.def.warlords.graphics.Sprite;
import com.def.warlords.gui.*;
import com.def.warlords.util.Util;

import java.awt.Graphics;
import java.util.List;

/**
 * @author wistful23
 * @version 1.23
 */
public class ProductionExForm extends Form {

    private City currentCity;

    private final List<City> cities;
    private Sprite locErrorSprite;
    private Button locButton;
    private Component currentFactoryComponent;

    public ProductionExForm(FormController controller, City currentCity) {
        super(controller);
        this.currentCity = currentCity;
        cities = currentCity.getEmpire().getCities();
        cities.sort(null);
    }

    @Override
    void init() {
        // Map.
        add(new ProductionMap());
        // Panel.
        add(new GrayPanel(8, 10, 360, 320));
        // Loc error.
        if (locErrorSprite != null) {
            add(new Image(27, 28, locErrorSprite));
            // NOTE: W doesn't clear the loc error image when the prod button is pressed.
            locErrorSprite = null;
        }
        // City name.
        add(new GreenLabel(63, 34, 134, currentCity.getName()));
        // Current production.
        add(new Image(205, 34, Sprites.ARMY_BACKDROP_GREEN));
        add(new Image(284, 34, Sprites.ARMY_BACKDROP_GREEN));
        add(new Image(320, 34, Sprites.ARMY_BACKDROP_GREEN));
        if (currentCity.isProducing()) {
            add(new Image(205, 34, Sprites.getArmySprite(currentCity.getCurrentFactory())));
            add(new Label(252, 40, currentCity.getRemainingTime() + "t"));
            final City targetCity = currentCity.getTargetCity();
            if (targetCity != null) {
                addDeliveryTimeline(34, currentCity, targetCity);
            }
        }
        add(new TextButton(27, 136, " Next ", source -> {
            currentCity = Util.nextElement(cities, currentCity);
            reset();
        }));
        add(new TextButton(92, 136, " Prev ", source -> {
            currentCity = Util.prevElement(cities, currentCity);
            reset();
        }));
        // Prod button.
        final Button prodButton = add(new TextButton(178, 136, " Prod ", source -> {
            // BUG: W doesn't update the gold field in Info Screen.
            currentCity.startProducing((ArmyFactory) currentFactoryComponent.getTag());
            reset();
        }));
        prodButton.setEnabled(false);
        // Loc button.
        locButton = add(new TextButton(244, 136, " Loc ", true, null));
        locButton.setEnabled(false);
        // Stop button.
        add(new TextButton(298, 136, " Stop ", source -> {
            currentCity.stopProducing();
            reset();
        })).setEnabled(currentCity.isProducing());
        // Factories.
        currentFactoryComponent = null;
        for (int index = 0; index < currentCity.getFactoryCount(); ++index) {
            final int x = 62 + index / 2 * 169;
            final int y = 70 + index % 2 * 32;
            final ArmyFactory factory = currentCity.getFactory(index);
            add(new Label(x - 33, y + 6, "F" + (index + 5)));
            add(new Label(x + 49, y + 6, factory.getTime() + "t/" + factory.getCost() + "gp"));
            add(new SelectableImage(x, y, Sprites.getArmySprite(factory), source -> {
                if (source == currentFactoryComponent) {
                    return;
                }
                final boolean enoughGold = factory.getCost() <= currentCity.getEmpire().getGold();
                // NOTE: W doesn't check the number of cities.
                final boolean canLoc = currentCity.getEmpire().getCityCount() > 1 && !factory.getType().isNavy();
                prodButton.setEnabled(enoughGold);
                locButton.setEnabled(enoughGold && canLoc);
                if (locButton.isSelected()) {
                    // BUG: W doesn't update the strategic map.
                    locButton.release();
                }
                if (currentFactoryComponent != null) {
                    currentFactoryComponent.setSelected(false);
                }
                currentFactoryComponent = source;
                currentFactoryComponent.setSelected(true);
            })).setTag(factory);
        }
        // Source cities.
        for (int index = 0; index < City.MAX_SOURCE_CITY_COUNT; ++index) {
            final int y = 168 + index * 30;
            final City sourceCity = index < currentCity.getSourceCityCount() ? currentCity.getSourceCity(index) : null;
            add(new TextButton(26, y + 2, " ^ ", source -> {
                currentCity = sourceCity;
                reset();
            })).setEnabled(sourceCity != null);
            add(new GreenLabel(63, y + 4, 134, sourceCity != null ? sourceCity.getName() : ""));
            add(new Image(205, y, Sprites.ARMY_BACKDROP_GREEN));
            add(new Image(284, y, Sprites.ARMY_BACKDROP_GREEN));
            add(new Image(320, y, Sprites.ARMY_BACKDROP_GREEN));
            if (sourceCity != null) {
                add(new Image(205, y, Sprites.getArmySprite(sourceCity.getCurrentFactory())));
                add(new Label(252, y + 4, sourceCity.getRemainingTime() + "t"));
                addDeliveryTimeline(y, sourceCity, currentCity);
            }
        }
        // Target city.
        final City targetCity = currentCity.getTargetCity();
        add(new TextButton(26, 290, " -> ", source -> {
            currentCity = targetCity;
            reset();
        })).setEnabled(targetCity != null);
        add(new GreenLabel(63, 292, 134, targetCity != null ? targetCity.getName() : ""));
        // OK.
        add(new TextButton(289, 288, "  OK  ", source -> deactivate()));
    }

    private void addDeliveryTimeline(int y, City sourceCity, City targetCity) {
        final ArmyDelivery[] deliveryTimeline = sourceCity.getEmpire().getDeliveryTimeline(sourceCity, targetCity);
        for (int time = ArmyDelivery.MAX_DELIVERY_TIME - 1; time >= 0; --time) {
            if (deliveryTimeline[time] != null) {
                add(new Image(320 - time * 36, y, Sprites.getArmySprite(deliveryTimeline[time].getArmy())));
            }
        }
    }

    private void assignTargetCity(City targetCity) {
        if (targetCity == currentCity) {
            // NOTE: W checks capacity first.
            locErrorSprite = Sprites.LOC_ERROR_ITSELF;
        } else if (targetCity != currentCity.getTargetCity() &&
                targetCity.getSourceCityCount() == City.MAX_SOURCE_CITY_COUNT) {
            // NOTE: W doesn't check if the current city is already a source of the target city.
            locErrorSprite = Sprites.LOC_ERROR_CAPACITY;
        } else {
            Util.assertTrue(currentCity.startProducing((ArmyFactory) currentFactoryComponent.getTag(), targetCity));
        }
        reset();
    }

    private class ProductionMap extends StrategicMapComponent {

        @Override
        protected void positionSelected(int posX, int posY) {
            final City city = GameHelper.getNearest(cities, posX, posY, true);
            if (city != null) {
                if (locButton.isSelected()) {
                    assignTargetCity(city);
                } else {
                    currentCity = city;
                    reset();
                }
            }
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            if (locButton.isSelected()) {
                // Loc mode.
                final City targetCity = currentCity.getTargetCity();
                for (final City city : cities) {
                    // White.
                    EmpireType tagColor = EmpireType.SIRIANS;
                    if (city == targetCity || (targetCity == null && city == currentCity)) {
                        // Yellow.
                        tagColor = EmpireType.STORM_GIANTS;
                    } else if (city.getSourceCityCount() == City.MAX_SOURCE_CITY_COUNT) {
                        // Red.
                        tagColor = EmpireType.ORCS_OF_KOR;
                    }
                    drawCityTag(g, city.getPosX(), city.getPosY(), tagColor);
                }
            } else {
                // Regular mode.
                for (final City city : cities) {
                    // Frame.
                    if (city.getTargetCity() == currentCity) {
                        // Yellow.
                        drawCityFrame(g, city.getPosX(), city.getPosY(), CityFrameColor.YELLOW);
                    } else if (city.getSourceCityCount() > 0) {
                        // White.
                        drawCityFrame(g, city.getPosX(), city.getPosY(), CityFrameColor.WHITE);
                    }
                    // Tag.
                    EmpireType tagColor = EmpireType.ORCS_OF_KOR;
                    if (city == currentCity) {
                        // White.
                        tagColor = EmpireType.SIRIANS;
                    } else if (city == currentCity.getTargetCity()) {
                        // Yellow.
                        tagColor = EmpireType.STORM_GIANTS;
                    } else if (city.isProducing()) {
                        // Green.
                        tagColor = EmpireType.ELVALLIE;
                    }
                    drawCityTag(g, city.getPosX(), city.getPosY(), tagColor);
                }
            }
        }
    }
}
