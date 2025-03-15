package com.def.warlords.control;

import com.def.warlords.control.common.Sprites;
import com.def.warlords.game.model.Army;
import com.def.warlords.game.model.City;
import com.def.warlords.game.model.Tile;
import com.def.warlords.graphics.Bitmap;
import com.def.warlords.graphics.BitmapInfo;

import java.awt.Graphics;

import static com.def.warlords.control.common.Dimensions.*;

/**
 * @author wistful23
 * @version 1.23
 */
public final class PlayingMapPainter {

    public static void drawTile(Graphics g, int x, int y, Tile tile) {
        final int value = tile.getValue();
        final City city = tile.getCity();
        final int tx, ty;
        if (city != null) {
            final int ord = city.getEmpire().getType().ordinal();
            final int pos = (value + 42) % 4;
            tx = ord % 7 * 2 + pos % 2;
            ty = ord / 7 * 2 + pos / 2 + 6;
        } else {
            if (value >= -127 && value <= -119) {
                tx = value + 127;
                ty = 0;
            } else if (value >= -118 && value <= -115) {
                tx = value + 128;
                ty = 0;
            } else if (value >= -114 && value <= -100) {
                tx = value + 114;
                ty = 1;
            } else if (value >= -99 && value <= -44) {
                tx = (value + 99) % 14;
                ty = (value + 99) / 14 + 2;
            } else if (value == -43) {
                tx = 14;
                ty = 5;
            } else if (value >= -6 && value <= -5) {
                tx = 4;
                ty = value + 14;
            } else if (value >= -4 && value <= -3) {
                tx = value + 9;
                ty = 8;
            } else {
                tx = 9;
                ty = 0;
            }
        }
        Bitmap.drawSprite(g, x, y, TILE_WIDTH, TILE_HEIGHT, BitmapInfo.SCENERY, tx * TILE_WIDTH, ty * TILE_HEIGHT);
    }

    public static void drawArmyGroup(Graphics g, int x, int y, int count, Army frontArmy) {
        final int armyX = x + TILE_HEIGHT - ARMY_WIDTH;
        final int armyY = y + TILE_HEIGHT - ARMY_HEIGHT;
        Sprites.getArmySprite(frontArmy).draw(g, armyX, armyY);
        // Flagstaff.
        Bitmap.drawSprite(g, x, armyY, ARMY_WIDTH, ARMY_HEIGHT, BitmapInfo.ARMIES, ARMY_WIDTH, 9 * ARMY_HEIGHT);
        // Flags.
        final int tx = (count - 1) % 4;
        final int ty = frontArmy.getEmpire().getType().getOffsetOrdinal();
        final int flagWidth = 14 + tx * 8;
        final int flagHeight = 10;
        Bitmap.drawSprite(g, x, armyY - flagHeight, flagWidth, flagHeight,
                BitmapInfo.ARMIES, tx * ARMY_WIDTH, 10 * ARMY_HEIGHT + ty * flagHeight);
        if (count > 4) {
            Bitmap.drawSprite(g, x, y + 16, 12, flagHeight,
                    BitmapInfo.ARMIES, 5 * ARMY_WIDTH, 10 * ARMY_HEIGHT + ty * flagHeight);
        }
    }

    private PlayingMapPainter() {
    }
}
