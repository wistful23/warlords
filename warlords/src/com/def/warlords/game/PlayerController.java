package com.def.warlords.game;

import com.def.warlords.game.model.*;

import java.util.List;

/**
 * @author wistful23
 * @version 1.23
 */
public interface PlayerController extends TurnController {

    // Build.
    enum BuildStatus {
        PROHIBITED,
        NOT_ENOUGH_GOLD,
        COMPLETED
    }

    boolean isImproveCityDefenceApproved(City city);
    void onImproveCityDefenceStatus(BuildStatus status);
    boolean isBuildTowerApproved();
    void onBuildTowerStatus(BuildStatus status);

    // Raze.
    boolean isRazeApproved(Tile tile);
    void onCityRazed(City city);
    void onTowerRazed();

    // Search.
    void onTerrainSearch(List<Artifact> artifacts);
    void onTempleFound(int blessedCount);
    void onLibraryFound();
    // Returns false if the hero ignored the sage.
    boolean onSageFound(int gold);

    // Crypt.
    void onAlliesJoined(Hero hero, GuardType guard, int count);
    void onGuardFight(Hero hero, GuardType guard, boolean slain);
    void onArtifactFound(Hero hero, Artifact artifact);
    // Returns true if the hero prayed at the altar.
    boolean onAltarFound();
    void onAltarResult(Hero hero, boolean ignored);
    // Returns true if the hero sat in the throne.
    boolean onThroneFound();
    void onThroneResult(Hero hero, boolean downgraded);
    void onGoldFound(Hero hero, int value);

    // Combat.
    void onCombat(ArmyList attackingArmies, ArmyList defendingArmies, Tile tile, List<Boolean> protocol);

    // Production.
    void selectProduction(City city);
}
