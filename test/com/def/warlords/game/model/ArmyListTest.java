package com.def.warlords.game.model;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.testng.Assert.*;

@Test
public class ArmyListTest {

    private Empire empire;
    private Army giants, cavalry, griffins, demons, dragons, navy;
    private Hero hero1, hero2;
    private Tile plain, ruins, road, shore, water, forest, hill, mountain, city, bridge;
    private Building temple1, temple2;
    private Artifact artifact1, artifact2;

    @BeforeMethod
    public void setUp() {
        empire = new Empire(EmpireType.GREY_DWARVES);
        giants = new Army(ArmyType.GIANTS, "Giants", 1, 5, 13);
        cavalry = new Army(ArmyType.CAVALRY, "Cavalry", 2, 6, 23);
        griffins = new Army(ArmyType.GRIFFINS, "Griffins", 3, 6, 21);
        demons = new Army(ArmyType.DEMONS, "Demons", 4, 7, 18);
        dragons = new Army(ArmyType.DRAGONS, "Dragons", 5, 8, 25);
        navy = new Army(ArmyType.NAVY, "Navy", 6, 8, 55);
        hero1 = new Hero("Hero1", false);
        hero2 = new Hero("Hero2", true);
        plain = new Tile(0, 0, 0);
        ruins = new Tile(0, 0, -116);
        road = new Tile(0, 0, -100);
        shore = new Tile(0, 0, -99);
        water = new Tile(0, 0, -86);
        forest = new Tile(0, 0, -72);
        hill = new Tile(0, 0, -71);
        mountain = new Tile(0, 0, -43);
        city = new Tile(0, 0, -7);
        bridge = new Tile(0, 0, -3);
        temple1 = new Building(BuildingType.TEMPLE, "Temple1");
        temple2 = new Building(BuildingType.TEMPLE, "Temple2");
        artifact1 = new Artifact("Artifact1", 1, 3);
        artifact2 = new Artifact("Artifact1", 2, 1);
        assertTrue(new City("City", city, null, 5, 123).registerTile(city));
    }

    @Test
    public void arrange() {
        final ArmyList armies = new ArmyList(6, empire);
        Collections.addAll(armies, giants, cavalry, hero1, demons, navy, dragons);
        armies.arrange(true);
        assertEquals(armies, Arrays.asList(giants, cavalry, dragons, demons, hero1, navy));
        armies.arrange(false);
        assertEquals(armies, Arrays.asList(navy, hero1, demons, dragons, cavalry, giants));
    }

    @Test
    public void getFirst() {
        final ArmyList armies = new ArmyList(5, empire);
        assertNull(armies.getFirst());
        armies.add(cavalry);
        assertEquals(armies.getFirst(), cavalry);
        armies.add(giants);
        assertEquals(armies.getFirst(), cavalry);
        armies.add(demons);
        assertEquals(armies.getFirst(), demons);
        armies.add(hero1);
        assertEquals(armies.getFirst(), hero1);
        armies.add(navy);
        assertEquals(armies.getFirst(), navy);
        armies.add(hero2);
        assertEquals(armies.getFirst(), navy);
    }

    @Test
    public void getHero() {
        final ArmyList armies = new ArmyList(5, empire);
        assertNull(armies.getHero());
        armies.add(cavalry);
        assertNull(armies.getHero());
        armies.add(hero1);
        assertEquals(armies.getHero(), hero1);
        armies.add(demons);
        assertEquals(armies.getHero(), hero1);
        armies.add(hero2);
        assertEquals(armies.getHero(), hero1);
    }

    @Test
    public void isHeroList() {
        final ArmyList armies = new ArmyList(5, empire);
        assertFalse(armies.isHeroList());
        armies.add(hero1);
        assertTrue(armies.isHeroList());
        armies.add(demons);
        assertFalse(armies.isHeroList());
        armies.add(hero2);
        assertFalse(armies.isHeroList());
        armies.remove(demons);
        assertTrue(armies.isHeroList());
    }

    @Test
    public void isNavy() {
        final ArmyList armies = new ArmyList(5, empire);
        assertFalse(armies.isNavy());
        armies.add(giants);
        assertFalse(armies.isNavy());
        armies.add(navy);
        assertTrue(armies.isNavy());
        armies.add(hero1);
        assertTrue(armies.isNavy());
    }

    @Test
    public void getMovementPoints() {
        final ArmyList armies = new ArmyList(5, empire);
        assertEquals(armies.getMovementPoints(), 0);
        armies.add(demons);
        assertEquals(armies.getMovementPoints(), 18);
        armies.add(cavalry);
        assertEquals(armies.getMovementPoints(), 18);
        armies.add(giants);
        assertEquals(armies.getMovementPoints(), 13);
        armies.add(navy);
        assertEquals(armies.getMovementPoints(), 55);
        armies.add(hero1);
        assertEquals(armies.getMovementPoints(), 55);
    }

    @Test
    public void getMovementCost() {
        final ArmyList armies = new ArmyList(5, empire);
        // Empty.
        assertEquals(armies.getMovementCost(plain), ArmyType.FORBIDDEN_MOVEMENT_COST);
        assertEquals(armies.getMovementCost(water), ArmyType.FORBIDDEN_MOVEMENT_COST);
        armies.add(hero1);
        assertEquals(armies.getMovementCost(hill), 6);
        armies.add(giants);
        assertEquals(armies.getMovementCost(road), 1);
        assertEquals(armies.getMovementCost(forest), 5);
        assertEquals(armies.getMovementCost(hill), 4);
        assertEquals(armies.getMovementCost(mountain), ArmyType.FORBIDDEN_MOVEMENT_COST);
        assertEquals(armies.getMovementCost(water), ArmyType.FORBIDDEN_MOVEMENT_COST);
        armies.add(demons);
        assertEquals(armies.getMovementCost(forest), 5);
        assertEquals(armies.getMovementCost(hill), 5);
        assertEquals(armies.getMovementCost(shore), ArmyType.FORBIDDEN_MOVEMENT_COST);
        armies.add(navy);
        // Navy.
        assertEquals(armies.getMovementCost(plain), ArmyType.FORBIDDEN_MOVEMENT_COST);
        assertEquals(armies.getMovementCost(forest), ArmyType.FORBIDDEN_MOVEMENT_COST);
        assertEquals(armies.getMovementCost(water), 1);
        assertEquals(armies.getMovementCost(shore), 2);
        assertEquals(armies.getMovementCost(bridge), 2);
        armies.add(dragons);
        assertEquals(armies.getMovementCost(hill), ArmyType.FORBIDDEN_MOVEMENT_COST);
        assertEquals(armies.getMovementCost(mountain), ArmyType.FORBIDDEN_MOVEMENT_COST);
        assertTrue(new Empire(EmpireType.LORD_BANE).registerArmy(griffins));
        assertTrue(hill.locate(griffins));
        assertEquals(armies.getMovementCost(hill), ArmyType.FORBIDDEN_MOVEMENT_COST);
        armies.remove(navy);
        // Attack.
        assertEquals(armies.getMovementCost(hill), 2);
        assertEquals(armies.getMovementCost(road), 1);
        assertEquals(armies.getMovementCost(mountain), ArmyType.FORBIDDEN_MOVEMENT_COST);
        assertEquals(armies.getMovementCost(shore), ArmyType.FORBIDDEN_MOVEMENT_COST);
        assertTrue(empire.registerArmy(navy));
        assertTrue(shore.locate(navy));
        // Boarding.
        assertEquals(armies.getMovementCost(shore), 2);
        armies.remove(demons);
        armies.remove(giants);
        armies.add(hero2);
        assertEquals(armies.getMovementCost(bridge), 1);
        assertEquals(armies.getMovementCost(forest), 2);
        assertEquals(armies.getMovementCost(hill), 2);
        assertEquals(armies.getMovementCost(water), 2);
        assertEquals(armies.getMovementCost(mountain), 3);
    }

    @Test
    public void move() {
        final ArmyList armies = new ArmyList(5, empire);
        Collections.addAll(armies, giants, cavalry, demons, navy, hero1, hero2);
        armies.move(15);
        assertEquals(giants.getMovementPoints(), 0);
        assertEquals(cavalry.getMovementPoints(), 8);
        assertEquals(demons.getMovementPoints(), 3);
        assertEquals(navy.getMovementPoints(), 40);
        assertEquals(hero1.getMovementPoints(), 0);
        assertEquals(hero2.getMovementPoints(), 1);
    }

    @Test
    public void bless() {
        final ArmyList armies = new ArmyList(5, empire);
        assertEquals(armies.bless(temple1), 0);
        Collections.addAll(armies, cavalry, hero1);
        assertEquals(armies.bless(temple1), 2);
        assertEquals(armies.bless(temple1), 0);
        armies.clear();
        Collections.addAll(armies, giants, demons, navy, hero2);
        assertEquals(armies.bless(temple2), 4);
        Collections.addAll(armies, cavalry, hero1);
        assertEquals(armies.bless(temple1), 4);
        assertEquals(armies.bless(temple2), 2);
    }

    @Test
    public void disband() {
        final ArmyList armies = new ArmyList(5, empire);
        assertTrue(empire.registerArmy(giants));
        assertTrue(empire.registerArmy(navy));
        Collections.addAll(armies, giants, navy);
        armies.disband();
        assertNull(giants.getEmpire());
        assertNull(navy.getEmpire());
        assertTrue(armies.isEmpty());
        assertTrue(empire.registerArmy(giants));
        assertTrue(empire.registerArmy(hero1));
        assertTrue(empire.registerArmy(cavalry));
        assertTrue(empire.registerArmy(demons));
        assertTrue(empire.registerArmy(hero2));
        Collections.addAll(armies, giants, hero1, cavalry, demons, hero2);
        armies.disband();
        assertEquals(armies.size(), 2);
        assertNull(giants.getEmpire());
        assertEquals(hero1.getEmpire(), empire);
        assertNull(cavalry.getEmpire());
        assertNull(demons.getEmpire());
        assertEquals(hero2.getEmpire(), empire);
    }

    @Test
    public void afcm() {
        final ArmyList armies = new ArmyList(5, empire);
        assertEquals(armies.afcm(plain), 0);
        assertEquals(armies.afcm(forest), 0);
        armies.add(giants);
        plain.buildTower(empire);
        assertEquals(armies.afcm(plain), 0);
        assertEquals(armies.afcm(forest), -1);
        assertEquals(armies.afcm(hill), 2);
        armies.add(hero1);
        assertEquals(armies.afcm(forest), 0);
        assertEquals(armies.afcm(hill), 3);
        hero2.takeArtifact(artifact1);
        armies.add(hero2);
        assertEquals(armies.afcm(hill), 7);
        assertEquals(armies.afcm(city), 5);
        hero2.takeArtifact(artifact2);
        Collections.addAll(armies, griffins, dragons);
        assertEquals(armies.afcm(forest), 7);
        assertEquals(armies.afcm(hill), 10);
    }

    @Test
    public void dfcm() {
        final ArmyList armies = new ArmyList(5, empire);
        assertEquals(armies.dfcm(plain), 0);
        assertEquals(armies.dfcm(hill), 0);
        assertEquals(armies.dfcm(city), 0);
        armies.add(cavalry);
        assertTrue(ruins.registerBuilding(temple1));
        assertEquals(armies.dfcm(ruins), 1);
        assertEquals(armies.dfcm(forest), -1);
        armies.add(dragons);
        plain.buildTower(empire);
        assertEquals(armies.dfcm(plain), 4);
        assertEquals(armies.dfcm(hill), 4);
        hero1.takeArtifact(artifact1);
        Collections.addAll(armies, griffins, demons, navy, hero1);
        assertEquals(armies.dfcm(city), 7);
    }

    @Test
    public void attackFromManual() {
        final ArmyList armies = new ArmyList(4, new Empire(EmpireType.GREY_DWARVES));
        armies.add(new Army(ArmyType.DWARVES, "Dwarves1", 2, 4, 9));
        armies.add(new Army(ArmyType.DWARVES, "Dwarves2", 2, 4, 9));
        armies.add(new Army(ArmyType.GRIFFINS, "Griffins", 8, 6, 18));
        armies.add(new Army(ArmyType.DRAGONS, "Dragons", 4, 8, 18));
        final int afcm = armies.afcm(city);
        assertEquals(afcm, 2);
        final int[] as = armies.stream().mapToInt(army -> army.getTotalStrength(city, afcm)).toArray();
        assertEquals(as, new int[] {6, 6, 8, 9});
    }

    @Test
    public void defenceFromManual() {
        final ArmyList armies = new ArmyList(3, new Empire(EmpireType.ORCS_OF_KOR));
        armies.add(new Army(ArmyType.LT_INF, "Light Infantry", 2, 3, 10));
        armies.add(new Army(ArmyType.WOLVES, "Wolf Riders", 4, 6, 15));
        final Hero hero = new Hero("Roger Orcfinger", true);
        hero.increaseStrength();
        hero.takeArtifact(new Artifact("Spear of Ank", 1, 0));
        armies.add(hero);
        final int dfcm = armies.dfcm(city);
        assertEquals(dfcm, 2);
        final int[] ds = armies.stream().mapToInt(army -> army.getTotalStrength(city, dfcm)).toArray();
        assertEquals(ds, new int[] {5, 8, 9});
    }
}
