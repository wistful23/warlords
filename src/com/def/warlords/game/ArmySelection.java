package com.def.warlords.game;

import com.def.warlords.game.model.Army;
import com.def.warlords.game.model.ArmyGroup;
import com.def.warlords.game.model.ArmyList;
import com.def.warlords.game.model.Tile;
import com.def.warlords.util.Util;

/**
 * @author wistful23
 * @version 1.23
 */
public class ArmySelection {

    private ArmyGroup selectedGroup;

    public ArmyGroup getSelectedGroup() {
        return selectedGroup;
    }

    public boolean isEmpty() {
        return selectedGroup == null || selectedGroup.getSelectedArmyCount() == 0;
    }

    public ArmyList getSelectedArmies() {
        if (isEmpty()) {
            throw new IllegalStateException("Selection is empty");
        }
        return selectedGroup.getSelectedArmies();
    }

    public void reset() {
        if (selectedGroup != null) {
            selectedGroup.setSelected(false);
            selectedGroup = null;
        }
    }

    public void select(ArmyGroup group) {
        if (group == null) {
            throw new IllegalArgumentException("Select group is null");
        }
        if (group == selectedGroup && selectedGroup.getSelectedArmyCount() == 1) {
            selectNext();
        } else {
            reset(group, false);
            selectFirst();
        }
    }

    public void selectAll(ArmyGroup group, boolean activeOnly) {
        if (group == null) {
            throw new IllegalArgumentException("Select group is null");
        }
        if (activeOnly) {
            reset(group, false);
            selectActive();
        } else {
            reset(group, true);
        }
    }

    // Moves the selected armies to `tile` and updates this selection.
    void moveTo(Tile tile) {
        if (isEmpty()) {
            throw new IllegalStateException("Selection is empty");
        }
        if (selectedGroup.isSelected() && tile.locate(selectedGroup)) {
            // Successfully moved the whole group.
            return;
        }
        selectedGroup.getSelectedArmies().forEach(tile::locate);
        if (selectedGroup.getSelectedArmyCount() > 0) {
            throw new IllegalStateException("Cannot move selection");
        }
        selectedGroup = tile.getGroup();
    }

    // `group` must be not null.
    private void reset(ArmyGroup group, boolean selected) {
        Util.assertNotNull(group);
        if (group != selectedGroup) {
            if (selectedGroup != null) {
                selectedGroup.setSelected(false);
            }
            selectedGroup = group;
        }
        selectedGroup.setSelected(selected);
    }

    // Called iff all armies are unselected.
    private void selectActive() {
        selectedGroup.getArmies().stream().filter(Army::isActive).forEach(army -> army.setSelected(true));
    }

    // Called iff all armies are unselected.
    private void selectFirst() {
        selectedGroup.getArmies().getFirst().setSelected(true);
    }

    // Called iff one army is selected.
    private void selectNext() {
        final int armyCount = selectedGroup.getArmyCount();
        for (int index = 0; index < armyCount; ++index) {
            final Army army = selectedGroup.getArmy(index);
            if (army.isSelected()) {
                army.setSelected(false);
                selectedGroup.getArmy((index + 1) % armyCount).setSelected(true);
                return;
            }
        }
        // This point has to be unreachable.
        Util.fail();
    }
}
