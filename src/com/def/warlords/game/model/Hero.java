package com.def.warlords.game.model;

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
public class Hero extends Army {

    private static final int STRENGTH = 5;
    private static final int MOVEMENT = 12;
    private static final int MOVEMENT_VETERAN = 16;

    // Children.
    private final List<Artifact> artifacts = new ArrayList<>();

    public Hero() {
    }

    public Hero(String name, boolean veteran) {
        super(ArmyType.HERO, name, 0, STRENGTH, veteran ? MOVEMENT_VETERAN : MOVEMENT);
    }

    // Dependencies.
    public List<Artifact> getArtifacts() {
        return new ArrayList<>(artifacts);
    }

    // Actions.
    public void joinAllies(Kingdom kingdom, ArmyFactory allyFactory, int allyCount) {
        final Empire empire = getEmpire();
        if (empire == null) {
            throw new IllegalStateException("Hero is not registered in an empire");
        }
        final ArmyGroup group = getGroup();
        if (group == null) {
            throw new IllegalStateException("Hero is not registered in a group");
        }
        final Tile tile = group.getTile();
        if (tile == null) {
            throw new IllegalStateException("Hero is not registered in a tile");
        }
        final City city = tile.getCity();
        for (int i = 0; i < allyCount; ++i) {
            final Army ally = allyFactory.produce();
            Util.assertTrue(empire.registerArmy(ally));
            if (tile.locate(ally)) {
                continue;
            }
            if (city != null && city.locate(ally)) {
                continue;
            }
            boolean located = false;
            for (final Tile neighborTile : kingdom.getNeighborTiles(tile, false)) {
                if (neighborTile.locate(ally)) {
                    // NOTE: It might locate the ally on the water.
                    located = true;
                    break;
                }
            }
            if (!located) {
                // NOTE: W can locate allies farther than neighbor tiles.
                Logger.warn("Can't join " + (allyCount - i) + " allies");
                empire.unregisterArmy(ally);
                return;
            }
        }
    }

    public void takeArtifact(Artifact artifact) {
        Util.assertTrue(registerArtifact(artifact));
    }

    public void dropArtifact(Artifact artifact) {
        if (artifact.getHero() != this) {
            throw new IllegalArgumentException("Artifact doesn't belong to this hero");
        }
        if (getGroup() == null) {
            throw new IllegalStateException("Hero is not registered in a group");
        }
        final Tile tile = getGroup().getTile();
        if (tile == null) {
            throw new IllegalStateException("Hero is not registered in a tile");
        }
        Util.assertTrue(tile.registerArtifact(artifact));
    }

    // Features.
    public int getTotalBattle() {
        int total = 0;
        for (final Artifact artifact : artifacts) {
            total += artifact.getBattle();
        }
        return total;
    }

    public int getTotalCommand() {
        int total = 0;
        for (final Artifact artifact : artifacts) {
            total += artifact.getCommand();
        }
        return total;
    }

    @Override
    public int getCombatModifier() {
        int modifier = super.getCombatModifier();
        // (a). Hero Present.
        final int strength = getStrength();
        if (strength >= 4) ++modifier;
        if (strength >= 7) ++modifier;
        if (strength >= 9) ++modifier;
        // (d). Command Item Present.
        modifier += getTotalCommand();
        return modifier;
    }

    @Override
    public int getTotalStrength() {
        return Util.truncate(getStrength() + getTotalBattle(), MAX_STRENGTH);
    }

    @Override
    public void kill(Tile tile) {
        getArtifacts().forEach(tile::registerArtifact);
        super.kill(tile);
    }

    @Override
    public void write(RecordOutputStream out) throws IOException {
        super.write(out);
        // Children.
        out.writeRecordList(artifacts);
    }

    @Override
    public void read(RecordInputStream in) throws IOException {
        super.read(in);
        // Children.
        in.readRecordList(artifacts, Artifact::new);
    }

    @Override
    public int getTypeId() {
        return 1;
    }

    // Registration.
    boolean registerArtifact(Artifact artifact) {
        final Hero artifactHero = artifact.getHero();
        // The artifact has already registered in this hero.
        if (artifactHero == this) {
            return true;
        }
        // Unregister the artifact in the hero.
        if (artifactHero != null) {
            artifactHero.unregisterArtifact(artifact);
        }
        // Unregister the artifact in the crypt.
        final Crypt artifactCrypt = artifact.getCrypt();
        if (artifactCrypt != null) {
            artifactCrypt.unregisterArtifact(artifact);
        }
        // Unregister the artifact in the tile.
        final Tile artifactTile = artifact.getTile();
        if (artifactTile != null) {
            artifactTile.unregisterArtifact(artifact);
        }
        // Set this hero in the artifact.
        artifact.setHero(this);
        // Add the artifact in this hero.
        artifacts.add(artifact);
        return true;
    }

    void unregisterArtifact(Artifact artifact) {
        // The artifact is not registered in this hero.
        if (artifact.getHero() != this) {
            return;
        }
        // Unset this hero in the artifact.
        artifact.setHero(null);
        // Remove the artifact from this hero.
        artifacts.remove(artifact);
    }
}
