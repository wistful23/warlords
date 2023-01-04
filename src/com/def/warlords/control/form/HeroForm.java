package com.def.warlords.control.form;

import com.def.warlords.control.common.GameHelper;
import com.def.warlords.control.common.Sprites;
import com.def.warlords.control.common.StrategicMapComponent;
import com.def.warlords.game.model.*;
import com.def.warlords.graphics.Font;
import com.def.warlords.graphics.FontFactory;
import com.def.warlords.graphics.Palette;
import com.def.warlords.gui.*;
import com.def.warlords.util.Util;

import java.awt.Graphics;
import java.util.List;

/**
 * @author wistful23
 * @version 1.23
 */
public class HeroForm extends Form {

    private final Kingdom kingdom;
    private Hero currentHero;

    private final List<Hero> heroes;

    public HeroForm(FormController controller, Kingdom kingdom, Hero currentHero) {
        super(controller);
        this.kingdom = kingdom;
        this.currentHero = currentHero;
        heroes = currentHero.getEmpire().getHeroes();
        heroes.sort(null);
    }

    @Override
    void init() {
        final Font font = FontFactory.getInstance().getMonospacedFont();
        // Map.
        add(new Map());
        // Panel.
        add(new GrayPanel(41, 26, 293, 288));
        // Hero.
        add(new Image(67, 46, Sprites.ARMY_BACKDROP_GRAY));
        add(new Image(67, 46, Sprites.getArmySprite(currentHero)));
        // Name.
        add(new GreenLabel(140, 48, 157, currentHero.getName()));
        // Location.
        final City city = GameHelper.getNearest(kingdom.getCities(), currentHero);
        add(new Label(96, 78, 40, font, Label.Alignment.RIGHT,
                city.getEmpire() != null && city.getArmies().contains(currentHero) ? "In" : "Near"));
        add(new GreenLabel(140, 74, 157, city.getName()));
        // Armies.
        final ArmyList armies = currentHero.getGroup().getArmies();
        armies.remove(currentHero);
        armies.arrange(false);
        for (int index = 0; index < ArmyGroup.MAX_ARMY_COUNT - 1; ++index) {
            final int x = 67 + 35 * index;
            add(new Image(x, 100, Sprites.ARMY_BACKDROP_GRAY));
            if (index < armies.size()) {
                add(new Image(x, 100, Sprites.getArmySprite(armies.get(index))));
            }
        }
        // Battle bonus.
        add(new Label(67, 136, 65, font, Label.Alignment.RIGHT, "Battle"));
        add(new GreenLabel(136, 132, 37, Label.Alignment.RIGHT, currentHero.getTotalBattle() + ""));
        add(new Label(142, 136, font, Palette.YELLOW, "+"));
        // Command bonus.
        add(new Label(67, 162, 65, font, Label.Alignment.RIGHT, "Command"));
        add(new GreenLabel(136, 158, 37, Label.Alignment.RIGHT, currentHero.getTotalCommand() + ""));
        add(new Label(142, 162, font, Palette.YELLOW, "+"));
        // Strength.
        add(new Label(202, 136, 65, font, Label.Alignment.RIGHT, "Strength"));
        add(new GreenLabel(271, 132, 37, Label.Alignment.RIGHT, currentHero.getStrength() + ""));
        // Movement points.
        add(new Label(202, 162, 65, font, Label.Alignment.RIGHT, "Move"));
        add(new GreenLabel(271, 158, 37, Label.Alignment.RIGHT, currentHero.getMovementPoints() + ""));
        // Artifacts.
        add(new Frame(63, 216, 59, 22, Frame.Type.PRESSED));
        final Label bonusLabel = add(new Label(69, 220, 52, font, Label.Alignment.LEFT, ""));
        // NOTE: W sorts the artifacts.
        add(new ItemList<>(125, 190, 189, 78, 3, currentHero.getArtifacts(), Artifact::getName,
                add(new TextButton(71, 186, " Up ")), add(new TextButton(66, 242, "Down")),
                artifact -> bonusLabel.setText(artifact.getBattle() > 0 ? "Bat +\t" + artifact.getBattle()
                                                                        : "Com +\t" + artifact.getCommand())));
        // Hero number.
        add(new Frame(63, 274, 46, 22, Frame.Type.GRAY));
        add(new Label(67, 278, font, heroes.indexOf(currentHero) + 1 + "\tof\t" + heroes.size()));
        // NOTE: W doesn't have a trailing space in the 'Next' button.
        add(new TextButton(125, 272, " Next ", source -> {
            currentHero = Util.nextElement(heroes, currentHero);
            reset();
        }));
        add(new TextButton(195, 272, " Prev ", source -> {
            currentHero = Util.prevElement(heroes, currentHero);
            reset();
        }));
        add(new TextButton(267, 272, " OK ", source -> deactivate()));
    }

    private class Map extends StrategicMapComponent {

        @Override
        public void positionSelected(int posX, int posY) {
            final Hero hero = GameHelper.getNearest(heroes, posX, posY, true);
            if (hero != null) {
                currentHero = hero;
                reset();
            }
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            for (final Hero hero : heroes) {
                drawCityTag(g, hero.getPosX(), hero.getPosY(),
                        hero == currentHero ? EmpireType.SIRIANS : EmpireType.ORCS_OF_KOR);
            }
        }
    }
}
