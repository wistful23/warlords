package com.def.warlords.control.form;

import com.def.warlords.game.Player;

import java.util.function.ToIntFunction;

/**
 * @author wistful23
 * @version 1.23
 */
public enum StatReportType {

    // @formatter:off
    ARMIES      ("Armies",  Player::getArmiesReport),
    CITIES      ("Cities",  Player::getCitiesReport),
    GOLD        ("Gold",    Player::getGoldReport),
    PRODUCTION  ("Produce", Player::getProductionReport),
    WINNING     ("Winning", Player::getWinningReport);
    // @formatter:on

    private final String name;
    private final ToIntFunction<Player> reportFunction;

    StatReportType(String name, ToIntFunction<Player> reportFunction) {
        this.name = name;
        this.reportFunction = reportFunction;
    }

    public String getName() {
        return name;
    }

    public int getReport(Player player) {
        return reportFunction.applyAsInt(player);
    }
}
