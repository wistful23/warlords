package com.def.warlords.control;

import com.def.warlords.control.common.StrategicMapComponent;
import com.def.warlords.game.model.*;
import com.def.warlords.graphics.Cursor;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * @author wistful23
 * @version 1.23
 */
public class StrategicMap extends StrategicMapComponent {

    public enum Mode {
        CITIES,             // Empires of cities. Default mode.
        RUINS,              // Status of ruins.
        ARMIES,             // Armies of the current player.
        HEROES,             // Heroes of the current player.
        HERO_OFFER,         // Hero offer in the source city.
        PRODUCTION_SOURCE,  // Delivery state of the selected city.
        PRODUCTION_TARGET,  // Delivery options for the selected city.
        PRODUCTION_REPORT,  // Production report of the source city.
        DELIVERY_REPORT     // Delivery report of the source city.
    }

    private final MainController controller;

    private Mode mode = Mode.CITIES;
    private City sourceCity, targetCity;

    StrategicMap(MainController controller) {
        this.controller = controller;
    }

    void setMode(Mode mode) {
        this.mode = mode;
    }

    void setSourceCity(City sourceCity) {
        this.sourceCity = sourceCity;
    }

    void setTargetCity(City targetCity) {
        this.targetCity = targetCity;
    }

    @Override
    protected void positionSelected(int posX, int posY) {
        controller.getPlayingMap().setPos(posX, posY);
    }

    @Override
    protected void positionDragged(int posX, int posY) {
        controller.getPlayingMap().setPos(posX, posY);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        switch (mode) {
            case CITIES:
                for (final City city : controller.getGame().getKingdom().getCities()) {
                    final Empire empire = city.getEmpire();
                    if (empire == null) {
                        // Razed city.
                        drawCityTag(g, city.getPosX(), city.getPosY(), EmpireType.NEUTRAL);
                    } else if (empire.getType() != EmpireType.NEUTRAL) {
                        drawCityTag(g, city.getPosX(), city.getPosY(), empire.getType());
                    }
                }
                break;
            case RUINS:
                for (final Building building : controller.getGame().getKingdom().getBuildings()) {
                    // White / Red - Unexplored / Explored.
                    EmpireType tagColor = building.isExplored() ? EmpireType.ORCS_OF_KOR : EmpireType.SIRIANS;
                    if (building.isTemple()) {
                        tagColor = EmpireType.ELVALLIE;
                    }
                    if (building.isLibrary()) {
                        tagColor = EmpireType.LORD_BANE;
                    }
                    drawCityTag(g, building.getPosX(), building.getPosY(), tagColor);
                }
                // Don't display the viewing window.
                return;
            case ARMIES:
                final List<ArmyGroup> groups = controller.getGame().getCurrentPlayer().getGroups();
                groups.sort(null);
                for (final ArmyGroup group : groups) {
                    // NOTE: W displays red tag shadows for LORD_BANE.
                    drawArmyTag(g, group.getPosX(), group.getPosY(),
                            group.isActive() ? EmpireType.ORCS_OF_KOR : EmpireType.SIRIANS);
                }
                // Don't display the viewing window.
                return;
            case HEROES:
                final List<Hero> heroes = controller.getGame().getCurrentPlayer().getHeroes();
                heroes.sort(null);
                for (final Hero hero : heroes) {
                    // NOTE: W displays red tag shadows for LORD_BANE.
                    drawArmyTag(g, hero.getPosX(), hero.getPosY(), EmpireType.SIRIANS);
                }
                // Don't display the viewing window.
                return;
            case HERO_OFFER:
                // White - Hero city.
                drawCityTag(g, sourceCity.getPosX(), sourceCity.getPosY(), EmpireType.SIRIANS);
                // Don't display the viewing window.
                return;
            case PRODUCTION_SOURCE:
            case PRODUCTION_REPORT:
                if (sourceCity.getTargetCity() != null) {
                    // Yellow - Source city.
                    drawCityTag(g, sourceCity.getPosX(), sourceCity.getPosY(), EmpireType.STORM_GIANTS);
                    // White - Target city.
                    drawCityTag(g, sourceCity.getTargetCity().getPosX(), sourceCity.getTargetCity().getPosY(),
                            EmpireType.SIRIANS);
                } else {
                    // White - Source city.
                    drawCityTag(g, sourceCity.getPosX(), sourceCity.getPosY(), EmpireType.SIRIANS);
                }
                break;
            case PRODUCTION_TARGET:
                for (final City city : controller.getGame().getCurrentPlayer().getCities()) {
                    // White - Potential city.
                    EmpireType tagColor = EmpireType.SIRIANS;
                    if (city == sourceCity) {
                        // Yellow - Source city.
                        tagColor = EmpireType.STORM_GIANTS;
                    } else if (city.getSourceCityCount() == City.MAX_SOURCE_CITY_COUNT) {
                        // Red - Overflow.
                        tagColor = EmpireType.ORCS_OF_KOR;
                    }
                    drawCityTag(g, city.getPosX(), city.getPosY(), tagColor);
                }
                break;
            case DELIVERY_REPORT:
                // Yellow - Source city.
                drawCityTag(g, sourceCity.getPosX(), sourceCity.getPosY(), EmpireType.STORM_GIANTS);
                // White - Target city.
                drawCityTag(g, targetCity.getPosX(), targetCity.getPosY(), EmpireType.SIRIANS);
                break;
        }
        drawViewingWindow(g, controller.getPlayingMap().getPosX(), controller.getPlayingMap().getPosY());
    }

    @Override
    public Cursor getCursor(MouseEvent e) {
        return Cursor.MAGNIFIER;
    }
}
