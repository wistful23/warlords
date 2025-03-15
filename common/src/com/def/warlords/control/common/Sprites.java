package com.def.warlords.control.common;

import com.def.warlords.game.PlayerLevel;
import com.def.warlords.game.model.Army;
import com.def.warlords.game.model.ArmyFactory;
import com.def.warlords.game.model.ArmyType;
import com.def.warlords.game.model.EmpireType;
import com.def.warlords.graphics.*;

import static com.def.warlords.control.common.Dimensions.*;

/**
 * @author wistful23
 * @version 1.23
 */
// NOTE: Formatting is disabled for this class.
public final class Sprites {

    // Delivery report buttons.
    public static final SpritePair BUTTON_OK = new SpritePair(48, 26, BitmapInfo.ARMIES, 464, 332, 464, 306);
    public static final SpritePair BUTTON_END_REPORT = new SpritePair(96, 26, BitmapInfo.ARMIES, 416, 280, 416, 254);

    // Command bar buttons.
    public static final SpritePair CMD_BUTTON_FLAG = new SpritePair(36, 76, BitmapInfo.ARMIES, 512, 144, 552, 144);
    public static final SpritePair CMD_BUTTON_SWORD = new SpritePair(36, 78, BitmapInfo.ARMIES, 512, 220, 552, 220);
    public static final SpritePair CMD_BUTTON_INFO = new SpritePair(36, 34, BitmapInfo.ARMIES, 512, 298, 552, 298);
    public static final SpritePair CMD_BUTTON_CENTER = new SpritePair(36, 30, BitmapInfo.ARMIES,  512, 332, 552, 332);
    public static final SpritePair CMD_BUTTON_NEXT = new SpritePair(36, 30, BitmapInfo.ARMIES, 592, 144, 592, 174);
    public static final SpritePair CMD_BUTTON_QUIT = new SpritePair(36, 30, BitmapInfo.ARMIES, 592, 204, 592, 234);
    public static final SpritePair CMD_BUTTON_DEFEND = new SpritePair(36, 30, BitmapInfo.ARMIES, 592, 264, 592, 294);

    // Setup.
    public static final Sprite SETUP_PANEL  = new SimpleSprite(360, 320, BitmapInfo.SETUP, 0, 0);
    public static final Sprite SETUP_BANNER = new SimpleSprite(218, 314, BitmapInfo.SETUP, 360, 0);
    public static final Sprite SETUP_WAR_BEGIN =
            new OffsetSprite(new CompoundSprite(new SpriteArrayBuilder(4, BitmapInfo.SETUP)
                    .add(56, 60, 2, 328, 3, 0)
                    .add(53, 58, 72, 328, 9, 68)
                    .add(74, 58, 143, 328, 4, 136)
                    .add(118, 70, 231, 328, 0, 212)
                    .getSprites()), 59, 14);
    public static final SpritePair SETUP_BUTTON_LOAD = new SpritePair(109, 36, BitmapInfo.SETUP, 352, 356, 528, 356);
    public static final SpritePair SETUP_BUTTON_START = new SpritePair(109, 36, BitmapInfo.SETUP, 352, 318, 208, 264);

    // Player levels.
    private static final int PLAYER_LEVEL_WIDTH = 30;
    private static final int PLAYER_LEVEL_HEIGHT = 30;
    private static final Sprite[] playerLevelSprites =
            new SpriteArrayBuilder(PlayerLevel.COUNT, PLAYER_LEVEL_WIDTH, PLAYER_LEVEL_HEIGHT, BitmapInfo.SETUP)
                    .add(464, 322)
                    .add(464, 354)
                    .add(496, 322)
                    .add(496, 354)
                    .add(528, 322)
                    .getSprites();

    public static Sprite getPlayerLevelSprite(PlayerLevel playerLevel) {
        return playerLevelSprites[playerLevel.ordinal()];
    }

    public static final Sprite PLAYER_DESTROYED =
            new SimpleSprite(PLAYER_LEVEL_WIDTH, PLAYER_LEVEL_HEIGHT, BitmapInfo.SCENERY,
                    14 * TILE_WIDTH, 6 * TILE_HEIGHT);

    // Empire swords.
    private static final int EMPIRE_SWORD_WIDTH = 30;
    private static final int EMPIRE_SWORD_HEIGHT = 32;
    private static final Sprite[] empireSwordSprite = new Sprite[EmpireType.COUNT];
    static {
        for (final EmpireType empireType : EmpireType.values()) {
            empireSwordSprite[empireType.ordinal()] =
                    new SimpleSprite(EMPIRE_SWORD_WIDTH, EMPIRE_SWORD_HEIGHT, BitmapInfo.SCENERY,
                            15 * TILE_WIDTH, TILE_HEIGHT + empireType.getOffsetOrdinal() * EMPIRE_SWORD_HEIGHT);
        }
    }

    public static Sprite getEmpireSwordSprite(EmpireType empireType) {
        return empireSwordSprite[empireType.ordinal()];
    }

    // Army backdrops.
    public static final Sprite ARMY_BACKDROP_GREEN =
            new SimpleSprite(ARMY_WIDTH, ARMY_HEIGHT, BitmapInfo.SCENERY, 14 * TILE_WIDTH, 3 * TILE_HEIGHT);
    public static final Sprite ARMY_BACKDROP_GRAY =
            new SimpleSprite(ARMY_WIDTH, ARMY_HEIGHT, BitmapInfo.SCENERY, 14 * TILE_WIDTH, 7 * TILE_HEIGHT);

    // Loc errors.
    private static final int LOC_ERROR_WIDTH = 34;
    private static final int LOC_ERROR_HEIGHT = 32;
    public static final Sprite LOC_ERROR_NONE = new Sprite(LOC_ERROR_WIDTH, LOC_ERROR_HEIGHT);
    public static final Sprite LOC_ERROR_GOLD =
            new SimpleSprite(LOC_ERROR_WIDTH, LOC_ERROR_HEIGHT, BitmapInfo.SCENERY, 14 * TILE_WIDTH, 8 * TILE_HEIGHT);
    public static final Sprite LOC_ERROR_CAPACITY =
            new SimpleSprite(LOC_ERROR_WIDTH, LOC_ERROR_HEIGHT, BitmapInfo.SCENERY, 15 * TILE_WIDTH, 330);
    public static final Sprite LOC_ERROR_ITSELF =
            new SimpleSprite(LOC_ERROR_WIDTH, LOC_ERROR_HEIGHT, BitmapInfo.SCENERY,
                    15 * TILE_WIDTH, 330 + LOC_ERROR_HEIGHT);

    // Army blood.
    public static final Sprite ARMY_BLOOD =
            new SimpleSprite(ARMY_WIDTH, ARMY_HEIGHT, BitmapInfo.ARMIES, 2 * ARMY_WIDTH, 9 * ARMY_HEIGHT);

    // Landscape.
    public static final Sprite COMBAT_GRASS = new SimpleSprite(224, 130, BitmapInfo.COMBAT1, 0, 0);
    public static final Sprite COMBAT_FOREST = new SimpleSprite(224, 58, BitmapInfo.COMBAT2, 0, 0);
    public static final Sprite COMBAT_FOREGROUND_HILLS =
            new CompoundSprite(new SpriteArrayBuilder(2, BitmapInfo.COMBAT2)
                    .add(79, 42, 227, 0)
                    .add(94, 42, 227, 0, 16, 0)
                    .getSprites());
    public static final Sprite COMBAT_BACKGROUND_HILLS = new SimpleSprite(90, 126, BitmapInfo.COMBAT1, 227, 2);
    public static final Sprite COMBAT_CITY = new SimpleSprite(89, 90, BitmapInfo.COMBAT1, 321, 38);
    public static final Sprite COMBAT_SHIP = new SimpleSprite(60, 82, BitmapInfo.COMBAT1, 416, 0);

    // Empire flags.
    public static final Sprite DEFENDER_FLAG_STAND = new SimpleSprite(91, 14, BitmapInfo.COMBAT2, 227, 42);
    public static final Sprite ATTACKER_FLAG_STAND = new SimpleSprite(182, 20, BitmapInfo.COMBAT2, 331, 40);

    private static final int DEFENDER_FLAG_WIDTH = 87;
    private static final int DEFENDER_FLAG_HEIGHT = 14;
    private static final Sprite[] defenderFlagSprites =
            new SpriteArrayBuilder(EmpireType.COUNT, DEFENDER_FLAG_WIDTH, DEFENDER_FLAG_HEIGHT, BitmapInfo.COMBAT2)
                    .add()
                    .add(390,  64)
                    .add(390,  78)
                    .add(390,  92)
                    .add(390, 106)
                    .add(486,  64)
                    .add(486,  92)
                    .add(486,  78)
                    .add(486, 106)
                    .getSprites();

    public static Sprite getDefenderFlagSprite(EmpireType empireType) {
        return defenderFlagSprites[empireType.ordinal()];
    }

    private static final int ATTACKER_FLAG_WIDTH = 172;
    private static final int ATTACKER_FLAG_HEIGHT = 20;
    private static final Sprite[] attackerFlagSprites =
            new SpriteArrayBuilder(EmpireType.COUNT, ATTACKER_FLAG_WIDTH, ATTACKER_FLAG_HEIGHT, BitmapInfo.COMBAT2)
                    .add()
                    .add(  9,  60)
                    .add(201,  60)
                    .add(  9,  80)
                    .add(  9, 100)
                    .add(201,  80)
                    .add(337,   0)
                    .add(201, 100)
                    .add(337,  20)
                    .getSprites();

    public static Sprite getAttackerFlagSprite(EmpireType empireType) {
        return attackerFlagSprites[empireType.ordinal()];
    }

    // Empire banners.
    private static final int BANNER_WIDTH = 31;
    private static final int BANNER_HEIGHT = 38;
    private static final int BANNER_TOP_HEIGHT = 26;
    private static final Sprite[] empireBannerSprites =
            new SpriteArrayBuilder(EmpireType.COUNT, BANNER_WIDTH, BANNER_HEIGHT, BitmapInfo.COMBAT2)
                    .add()
                    .add(544,  0)
                    .add(576,  0)
                    .add(608,  0)
                    .add(576, 40)
                    .add(608, 40)
                    .add(608, 82)
                    .add(576, 82)
                    .add(new CompoundSprite(new SpriteArrayBuilder(2, BitmapInfo.COMBAT2)
                            .add(BANNER_WIDTH, BANNER_TOP_HEIGHT, 544, 38)
                            .add(BANNER_WIDTH, BANNER_HEIGHT - BANNER_TOP_HEIGHT, 576, 66, 0, BANNER_TOP_HEIGHT)
                            .getSprites()))
                    .getSprites();

    public static Sprite getEmpireBannerSprite(EmpireType empireType) {
        return empireBannerSprites[empireType.ordinal()];
    }

    // Army factories.
    private static final Sprite[] armyFactorySprites =
            new SpriteArrayBuilder(ArmyType.COUNT, BitmapInfo.WLC)
                    .add( 62, 122, 112,   0, 32,  4)
                    .add( 63, 128,  48,   0, 24,  0)
                    .add( 46, 136,   0,   0, 32,  0)
                    .add( 56, 112, 264,   0, 32,  8)
                    .add( 93, 130, 312, 112, 16,  4)
                    .add( 88,  74,   0, 136, 16, 28)
                    .add( 83, 128, 176,   0, 24,  4)
                    .add( 74, 102, 324,   0, 20, 12)
                    .add( 95, 100, 216, 128, 16,  8)
                    .add(128,  84,  88, 128,  0, 24)
                    .getSprites();

    public static Sprite getArmyFactorySprite(ArmyFactory armyFactory) {
        return armyFactorySprites[armyFactory.getType().ordinal()];
    }

    // Armies.
    private static final Sprite[][] armySprites = new Sprite[EmpireType.COUNT][ArmyType.COUNT];
    static {
        for (final EmpireType empireType : EmpireType.values()) {
            for (final ArmyType armyType : ArmyType.values()) {
                final int column = armyType.ordinal();
                int row = empireType.ordinal() - 1;
                if (empireType == EmpireType.NEUTRAL && armyType == ArmyType.LT_INF) {
                    // There are no army sprites for the NEUTRAL empire except the LT_INF army.
                    row = EmpireType.COUNT - 1;
                }
                if (row >= 0) {
                    armySprites[empireType.ordinal()][armyType.ordinal()] =
                            new SimpleSprite(ARMY_WIDTH, ARMY_HEIGHT, BitmapInfo.ARMIES,
                                    column * ARMY_WIDTH, row * ARMY_HEIGHT);
                }
            }
        }
    }

    public static Sprite getArmySprite(EmpireType empireType, ArmyType armyType) {
        return armySprites[empireType.ordinal()][armyType.ordinal()];
    }

    public static Sprite getArmySprite(ArmyFactory armyFactory) {
        return getArmySprite(armyFactory.getCity().getEmpire().getType(), armyFactory.getType());
    }

    public static Sprite getArmySprite(Army army) {
        return getArmySprite(army.getEmpire().getType(), army.getType());
    }

    private Sprites() {
    }
}
