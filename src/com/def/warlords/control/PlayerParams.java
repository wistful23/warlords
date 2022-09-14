package com.def.warlords.control;

import com.def.warlords.game.PlayerLevel;
import com.def.warlords.game.model.EmpireType;

/**
 * @author wistful23
 * @version 1.23
 */
public class PlayerParams {

    private final EmpireType empireType;
    private PlayerLevel level;

    public PlayerParams(EmpireType empireType, PlayerLevel level) {
        this.empireType = empireType;
        this.level = level;
    }

    public EmpireType getEmpireType() {
        return empireType;
    }

    public PlayerLevel getLevel() {
        return level;
    }

    public void nextLevel() {
        level = level.next();
    }
}
