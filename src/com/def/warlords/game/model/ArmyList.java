package com.def.warlords.game.model;

import com.def.warlords.util.Util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author wistful23
 * @version 1.23
 */
public class ArmyList extends ArrayList<Army> {

    private final Empire empire;

    public ArmyList(int initialCapacity, Empire empire) {
        super(initialCapacity);
        this.empire = empire;
    }

    public ArmyList(List<Army> armies, Empire empire) {
        super(armies);
        this.empire = empire;
    }

    public int getCount() {
        return size();
    }

    public Empire getEmpire() {
        return empire;
    }

    // Arranges the armies in the specified order.
    public void arrange(boolean ascending) {
        // NOTE: W uses a weird order for navy groups.
        final Comparator<Army> comparator = Comparator.comparing(Army::ordinal);
        sort(ascending ? comparator : comparator.reversed());
    }

    // Returns the first army if it exists, otherwise returns null.
    public Army getFirst() {
        return stream().max(Comparator.comparing(Army::ordinal)).orElse(null);
    }

    // Returns the hero if it exists, otherwise returns null.
    public Hero getHero() {
        return (Hero) stream().filter(Army::isHero).findFirst().orElse(null);
    }

    // Returns true if this army list is not empty and contains only heroes.
    public boolean isHeroList() {
        return !isEmpty() && stream().allMatch(Army::isHero);
    }

    // Returns true if this army list contains at least one navy.
    public boolean isNavy() {
        return stream().anyMatch(Army::isNavy);
    }

    // Returns the available movement points of the armies.
    public int getMovementPoints() {
        final Stream<Army> stream = isNavy() ? stream().filter(Army::isNavy) : stream();
        return stream.mapToInt(Army::getMovementPoints).min().orElse(0);
    }

    // Returns the movement cost required to move the armies to `terrain` or FORBIDDEN_MOVEMENT_COST if not possible.
    public int getMovementCost(TerrainType terrain) {
        final Stream<Army> stream;
        if (isHeroList()) {
            stream = stream();
        } else if (isNavy()) {
            stream = stream().filter(Army::isNavy);
        } else {
            stream = stream().filter(Util.not(Army::isHero));
        }
        return stream.mapToInt(army -> army.getMovementCost(terrain)).max().orElse(ArmyType.FORBIDDEN_MOVEMENT_COST);
    }

    // Updates the state for each army from this army list.
    public void updateState(Army.State state) {
        forEach(army -> army.updateState(state));
    }

    // Decreases movement points of each army from this army list.
    public void move(int movementConst) {
        forEach(army -> army.move(movementConst));
    }

    // Blesses each army from this army list.
    public int bless(Building temple) {
        return stream().mapToInt(army -> army.bless(temple) ? 1 : 0).sum();
    }

    // Disbands all non-hero armies.
    public void disband() {
        stream().filter(Util.not(Army::isHero)).forEach(Army::unregisterSelf);
        removeIf(Util.not(Army::isHero));
    }

    // Calculates Attacking Force Combat Modifier.
    public int afcm(Tile tile) {
        if (isEmpty()) {
            return 0;
        }
        int modifier = empire.getCombatModifier(tile) + stream().mapToInt(Army::getCombatModifier).sum();
        // (b). Flying Army Present.
        if (stream().anyMatch(Army::isFlying)) {
            ++modifier;
        }
        // (c). Special Army Present.
        if (stream().anyMatch(Army::isSpecial)) {
            ++modifier;
        }
        return modifier;
    }

    // Calculates Defending Force Combat Modifier.
    public int dfcm(Tile tile) {
        return !isEmpty() ? afcm(tile) + tile.getCombatModifier() : 0;
    }
}
