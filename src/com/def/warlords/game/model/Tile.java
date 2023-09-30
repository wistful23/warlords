package com.def.warlords.game.model;

import com.def.warlords.record.Record;
import com.def.warlords.record.RecordInputStream;
import com.def.warlords.record.RecordOutputStream;
import com.def.warlords.util.Logger;
import com.def.warlords.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wistful23
 * @version 1.23
 */
public class Tile implements Record, Locatable, Comparable<Tile> {

    private int px, py;
    private int value;

    private TerrainType terrain;

    // Parents.
    private City city;

    // Children.
    private Building building;
    private ArmyGroup group;
    private final List<Artifact> artifacts = new ArrayList<>();

    public Tile() {
    }

    public Tile(int px, int py, int value) {
        this.px = px;
        this.py = py;
        this.value = value;
        this.terrain = TerrainType.valueOf(value);
    }

    public int getValue() {
        return value;
    }

    public TerrainType getTerrain() {
        return terrain;
    }

    public boolean isWater() {
        return terrain == TerrainType.WATER || terrain == TerrainType.SHORE;
    }

    public boolean isMountain() {
        return terrain == TerrainType.MOUNTAIN;
    }

    public boolean isCity() {
        return terrain == TerrainType.CITY;
    }

    public boolean isTower() {
        return terrain == TerrainType.TOWER;
    }

    // Dependencies.
    public City getCity() {
        return city;
    }

    public ArmyGroup getGroup() {
        return group;
    }

    public Building getBuilding() {
        return building;
    }

    public int getArtifactCount() {
        return artifacts.size();
    }

    public List<Artifact> getArtifacts() {
        return new ArrayList<>(artifacts);
    }

    public int getCombatModifier() {
        int modifier = 0;
        // (f). Tower Present.
        if (terrain == TerrainType.TOWER) {
            modifier += 2;
        }
        // (g). Special Terrain Present.
        if (building != null) {
            modifier += 1;
        }
        // (h). City Present.
        if (city != null) {
            modifier += city.getCombatModifier();
        }
        return modifier;
    }

    // Occupation.
    public int getArmyCount() {
        return group != null ? group.getArmyCount() : 0;
    }

    public boolean isFull() {
        return group != null && group.isFull();
    }

    public boolean isNavy(Empire empire) {
        return group != null && group.isNavy() && group.getEmpire() == empire;
    }

    public boolean isOccupiedBy(Empire empire) {
        return (group != null && group.getEmpire() == empire) || (city != null && city.getEmpire() == empire);
    }

    public boolean isOccupiedByEnemy(Empire empire) {
        return (group != null && group.getEmpire() != empire) || (city != null && city.getEmpire() != empire);
    }

    public boolean canLocate(ArmyList armies) {
        if (isOccupiedByEnemy(armies.getEmpire())) {
            return false;
        }
        return group == null || group.getArmyCount() + armies.getCount() <= ArmyGroup.MAX_ARMY_COUNT;
    }

    // Actions.
    public void buildTower(Empire empire) {
        if (terrain != TerrainType.PLAIN && terrain != TerrainType.TOWER) {
            throw new IllegalStateException("Cannot build tower on " + terrain);
        }
        value = -127 + empire.getType().ordinal();
        terrain = TerrainType.TOWER;
    }

    public void raze() {
        if (terrain != TerrainType.CITY && terrain != TerrainType.TOWER) {
            throw new IllegalStateException("Cannot raze " + terrain);
        }
        if (city != null) {
            city.unregisterTile(this);
        }
        value = -116;
        terrain = TerrainType.RUINS;
    }

    // Locates the army group to this tile.
    // Requires the army group is registered in an empire.
    // Returns false if the army group is not located for some reason.
    public boolean locate(ArmyGroup group) {
        final Empire groupEmpire = group.getEmpire();
        if (groupEmpire == null) {
            throw new IllegalArgumentException("Army group is not registered in an empire");
        }
        if (isOccupiedByEnemy(groupEmpire)) {
            Logger.warn("Tile is occupied by another empire");
            return false;
        }
        return registerGroup(group);
    }

    // Locates the army to this tile.
    // Requires the army is registered in an empire.
    // Returns false if the army is not located for some reason.
    public boolean locate(Army army) {
        final Empire armyEmpire = army.getEmpire();
        if (armyEmpire == null) {
            throw new IllegalArgumentException("Army is not registered in an empire");
        }
        if (isOccupiedByEnemy(armyEmpire)) {
            Logger.info("Tile is occupied by another empire");
            return false;
        }
        if (group != null) {
            return group.registerArmy(army);
        }
        final ArmyGroup newGroup = new ArmyGroup();
        Util.assertTrue(armyEmpire.registerGroup(newGroup));
        Util.assertTrue(registerGroup(newGroup));
        Util.assertTrue(newGroup.registerArmy(army));
        return true;
    }

    // Locates the artifact to this tile.
    // Returns false if the artifact is not located for some reason.
    public boolean locate(Artifact artifact) {
        return registerArtifact(artifact);
    }

    @Override
    public void write(RecordOutputStream out) throws IOException {
        out.writeInt(px);
        out.writeInt(py);
        out.writeInt(value);
        out.writeEnum(terrain);
        // Parents.
        out.writeRecord(city);
        // Children.
        out.writeRecord(building);
        out.writeRecord(group);
        out.writeRecordList(artifacts);
    }

    @Override
    public void read(RecordInputStream in) throws IOException {
        px = in.readInt();
        py = in.readInt();
        value = in.readInt();
        terrain = in.readEnum(TerrainType.values());
        // Parents.
        city = in.readRecord(City::new);
        // Children.
        building = in.readRecord(Building::new, Crypt::new);
        group = in.readRecord(ArmyGroup::new);
        in.readRecordList(artifacts, Artifact::new);
    }

    @Override
    public int getPosX() {
        return px;
    }

    @Override
    public int getPosY() {
        return py;
    }

    @Override
    public int compareTo(Tile other) {
        return py != other.py ? py - other.py : px - other.px;
    }

    // Registration.
    void setCity(City city) {
        this.city = city;
    }

    boolean registerGroup(ArmyGroup group) {
        // The group has already registered in this tile.
        if (this.group == group) {
            return true;
        }
        // This tile has the registered group.
        if (this.group != null) {
            return false;
        }
        // The city of this tile is not registered in an empire but the group is registered in the empire.
        if (city != null) {
            if (city.getEmpire() == null && group.getEmpire() != null) {
                return false;
            }
        }
        // Unregister the group in the tile.
        final Tile groupTile = group.getTile();
        if (groupTile != null) {
            groupTile.unregisterGroup(group);
        }
        // Register the group in the empire of the city of this tile.
        if (city != null) {
            final Empire cityEmpire = city.getEmpire();
            if (cityEmpire != null) {
                Util.assertTrue(cityEmpire.registerGroup(group));
            }
        }
        // Set this tile in the group.
        group.setTile(this);
        // Add the group in this tile.
        this.group = group;
        return true;
    }

    void unregisterGroup(ArmyGroup group) {
        // The group is not registered in this tile.
        if (this.group != group) {
            return;
        }
        // Unset this tile in the group.
        group.setTile(null);
        // Remove the group from this tile.
        this.group = null;
    }

    boolean registerBuilding(Building building) {
        // The building has already registered in this tile.
        if (this.building == building) {
            return true;
        }
        // This tile has the registered building.
        if (this.building != null) {
            return false;
        }
        // The terrain is not a ruins type.
        if (terrain != TerrainType.RUINS) {
            return false;
        }
        // Unregister the building in the tile.
        final Tile buildingTile = building.getTile();
        if (buildingTile != null) {
            buildingTile.unregisterBuilding(building);
        }
        // Set this tile in the building.
        building.setTile(this);
        // Add the building in this tile.
        this.building = building;
        return true;
    }

    void unregisterBuilding(Building building) {
        // The building is not registered in this tile.
        if (this.building != building) {
            return;
        }
        // Unset this tile in the building.
        building.setTile(null);
        // Remove the building from this tile.
        this.building = null;
    }

    boolean registerArtifact(Artifact artifact) {
        final Tile artifactTile = artifact.getTile();
        // The artifact has already registered in this tile.
        if (artifactTile == this) {
            return true;
        }
        // Unregister the artifact in the tile.
        if (artifactTile != null) {
            artifactTile.unregisterArtifact(artifact);
        }
        // Unregister the artifact in the crypt.
        final Crypt artifactCrypt = artifact.getCrypt();
        if (artifactCrypt != null) {
            artifactCrypt.unregisterArtifact(artifact);
        }
        // Unregister the artifact in the hero.
        final Hero artifactHero = artifact.getHero();
        if (artifactHero != null) {
            artifactHero.unregisterArtifact(artifact);
        }
        // Set this tile in the artifact.
        artifact.setTile(this);
        // Add the artifact in this tile.
        artifacts.add(artifact);
        return true;
    }

    void unregisterArtifact(Artifact artifact) {
        // The artifact is not registered in this tile.
        if (artifact.getTile() != this) {
            return;
        }
        // Unset this tile in the artifact.
        artifact.setTile(null);
        // Remove the artifact from this tile.
        artifacts.remove(artifact);
    }
}
