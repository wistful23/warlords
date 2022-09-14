package com.def.warlords.control.form;

import com.def.warlords.control.common.StrategicMapComponent;
import com.def.warlords.game.Game;
import com.def.warlords.game.Player;
import com.def.warlords.game.model.ArmyGroup;
import com.def.warlords.game.model.City;
import com.def.warlords.game.model.EmpireType;
import com.def.warlords.graphics.Palette;
import com.def.warlords.gui.Label;
import com.def.warlords.util.Util;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wistful23
 * @version 1.23
 */
public class StatReportForm extends EmptyForm {

    private static final int SHORT_BAR_LENGTH = 3;
    private static final int MAX_BAR_LENGTH = 500;

    private final StatReportType type;
    private final Game game;

    public StatReportForm(FormController controller, StatReportType type, Game game) {
        super(controller);
        this.type = type;
        this.game = game;
    }

    @Override
    void init() {
        if (type == StatReportType.ARMIES) {
            add(new ArmiesMap());
        } else if (type == StatReportType.PRODUCTION) {
            add(new ProductionMap());
        }
        add(new Label(550, 356, type.getName()));
    }

    public void paint(java.awt.Graphics g) {
        super.paint(g);
        // Baseline.
        g.setColor(Palette.BLACK);
        g.fillRect(16, 334, 2, 64);
        // Divider.
        g.setColor(Palette.BLACK);
        g.fillRect(533, 334, 1, 64);
        g.setColor(Palette.GRAY_LIGHT);
        g.fillRect(535, 334, 1, 64);
        for (int index = 0; index < game.getPlayerCount(); ++index) {
            final int y = 336 + index * 8;
            final Player player = game.getPlayer(index);
            // NOTE: W displays a unit length bar for the gold report when the player is dead.
            final int report = Util.truncate(type.getReport(player), MAX_BAR_LENGTH);
            // Baseline.
            g.setColor(player.getEmpireType() == EmpireType.LORD_BANE ? Palette.RED : Palette.BLACK);
            g.fillRect(18, y, 1, 6);
            // Shadow.
            if (report > SHORT_BAR_LENGTH) {
                g.fillRect(19, y + 4, report - SHORT_BAR_LENGTH, 2);
            }
            // Bar.
            g.setColor(player.getEmpireType().getColor());
            g.fillRect(19, y, report, 4);
        }
    }

    private class ArmiesMap extends StrategicMapComponent {

        private final List<ArmyGroup> groups = new ArrayList<>();

        private ArmiesMap() {
            for (final Player player : game.getPlayers()) {
                groups.addAll(player.getGroups());
            }
            groups.sort(null);
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            for (final ArmyGroup group : groups) {
                drawArmyTag(g, group.getPosX(), group.getPosY(), group.getEmpire().getType());
            }
        }
    }

    private class ProductionMap extends StrategicMapComponent {

        private final List<City> cities = game.getCurrentPlayer().getCities();
        private final Map<City, Integer> targetColors = new HashMap<>();

        private ProductionMap() {
            cities.sort(null);
            int currentTargetColor = 0;
            for (final City city : cities) {
                final City targetCity = city.getTargetCity();
                if (targetCity != null && !targetColors.containsKey(targetCity)) {
                    targetColors.put(targetCity, currentTargetColor++);
                }
            }
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            // NOTE: W has buggy logic for tagging city deliveries.
            for (final City city : cities) {
                final City targetCity = city.getTargetCity();
                if (targetCity != null) {
                    Util.assertTrue(targetColors.containsKey(targetCity));
                    drawCityFrame(g, city.getPosX(), city.getPosY(), CityFrameColor.YELLOW);
                    drawDeliveryCityTag(g, city.getPosX(), city.getPosY(),
                            targetColors.get(targetCity), DeliveryDirection.SOURCE);
                } else if (city.getSourceCityCount() > 0) {
                    Util.assertTrue(targetColors.containsKey(city));
                    drawCityFrame(g, city.getPosX(), city.getPosY(),
                            city.isProducing() ? CityFrameColor.WHITE : CityFrameColor.RED);
                    drawDeliveryCityTag(g, city.getPosX(), city.getPosY(),
                            targetColors.get(city), DeliveryDirection.TARGET);
                } else {
                    drawCityTag(g, city.getPosX(), city.getPosY(),
                            city.isProducing() ? EmpireType.SIRIANS : EmpireType.ORCS_OF_KOR);
                }
            }
        }
    }
}
