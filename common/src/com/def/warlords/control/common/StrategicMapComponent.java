package com.def.warlords.control.common;

import com.def.warlords.game.model.EmpireType;
import com.def.warlords.graphics.Bitmap;
import com.def.warlords.graphics.BitmapInfo;
import com.def.warlords.graphics.Palette;
import com.def.warlords.gui.Component;
import com.def.warlords.util.Util;

import java.awt.Graphics;
import java.awt.event.MouseEvent;

import static com.def.warlords.control.common.Dimensions.*;

/**
 * @author wistful23
 * @version 1.23
 */
public class StrategicMapComponent extends Component {

    public enum CityFrameColor {
        WHITE,
        YELLOW,
        RED
    }

    public enum DeliveryDirection {
        SOURCE,
        TARGET
    }

    private static final int MAP_X = 411;
    private static final int MAP_Y = 14;
    private static final int MAP_WIDTH = 218;
    private static final int MAP_HEIGHT = 312;
    private static final int MAP_MARGIN_X = 3;
    private static final int MAP_MARGIN_Y = 4;

    private static final int CITY_FRAME_WIDTH = 8;
    private static final int CITY_FRAME_HEIGHT = 10;
    private static final int CITY_TAG_WIDTH = 6;
    private static final int CITY_TAG_HEIGHT = 6;
    private static final int ARMY_TAG_WIDTH = 4;
    private static final int ARMY_TAG_HEIGHT = 4;

    private static final int LAST_DELIVERY_CITY_COLOR = 14;

    protected static void drawViewingWindow(Graphics g, int posX, int posY) {
        final int x = MAP_X + 2 * posX;
        // The viewing window sprite contains the top margin.
        final int y = MAP_Y - MAP_MARGIN_Y + 2 * posY;
        Bitmap.drawSprite(g, x, y, ARMY_WIDTH, ARMY_HEIGHT, BitmapInfo.ARMIES, 3 * ARMY_WIDTH, 9 * ARMY_HEIGHT);
    }

    protected static void drawCityFrame(Graphics g, int posX, int posY, CityFrameColor color) {
        final int x = MAP_X + 2 * posX - 2;
        final int y = MAP_Y + 2 * posY - 2;
        Bitmap.drawSprite(g, x, y, CITY_FRAME_WIDTH, CITY_FRAME_HEIGHT,
                BitmapInfo.ARMIES, 400, 224 + color.ordinal() * CITY_FRAME_HEIGHT);
    }

    // NOTE: The NEUTRAL empire indicates a razed city.
    protected static void drawCityTag(Graphics g, int posX, int posY, EmpireType empireType) {
        final int x = MAP_X + 2 * posX - 1;
        final int y = MAP_Y + 2 * posY;
        Bitmap.drawSprite(g, x, y, CITY_TAG_WIDTH, CITY_TAG_HEIGHT,
                BitmapInfo.ARMIES, 392, 224 + empireType.getOffsetOrdinal() * CITY_TAG_HEIGHT);
    }

    protected static void drawDeliveryCityTag(Graphics g, int posX, int posY, int color, DeliveryDirection direction) {
        final int x = MAP_X + 2 * posX - 1;
        final int y = MAP_Y + 2 * posY;
        Bitmap.drawSprite(g, x, y, CITY_TAG_WIDTH, CITY_TAG_HEIGHT, BitmapInfo.ARMIES,
                400 + 8 * direction.ordinal(),
                254 + Util.truncate(color, LAST_DELIVERY_CITY_COLOR) * (CITY_TAG_HEIGHT + 2));
    }

    protected static void drawArmyTag(Graphics g, int posX, int posY, EmpireType empireType) {
        final int x = MAP_X + 2 * posX;
        final int y = MAP_Y + 2 * posY;
        g.setColor(empireType == EmpireType.LORD_BANE ? Palette.RED : Palette.BLACK);
        g.fillRect(x, y, ARMY_TAG_WIDTH, ARMY_TAG_HEIGHT);
        g.setColor(empireType.getColor());
        g.fillRect(x, y, ARMY_TAG_WIDTH - 1, ARMY_TAG_HEIGHT - 2);
    }

    protected StrategicMapComponent() {
        super(MAP_X, MAP_Y, MAP_WIDTH, MAP_HEIGHT);
    }

    protected void positionSelected(int posX, int posY) {
    }

    protected void positionDragged(int posX, int posY) {
    }

    @Override
    public void paint(Graphics g) {
        Bitmap.drawSprite(g, MAP_X - MAP_MARGIN_X, MAP_Y - MAP_MARGIN_Y,
                MAP_WIDTH + 2 * MAP_MARGIN_X, MAP_HEIGHT + 2 * MAP_MARGIN_Y, BitmapInfo.STRAT, 0, 0);
    }

    @Override
    public boolean mousePressed(MouseEvent e) {
        positionSelected((e.getX() - MAP_X) / 2, (e.getY() - MAP_Y) / 2);
        return true;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (contains(e.getPoint())) {
            positionDragged((e.getX() - MAP_X) / 2, (e.getY() - MAP_Y) / 2);
        }
    }
}
