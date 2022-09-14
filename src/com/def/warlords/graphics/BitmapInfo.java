package com.def.warlords.graphics;

/**
 * @author wistful23
 * @version 1.23
 */
public enum BitmapInfo {

    // @formatter:off
    ARMIES  ("ARMIES.bmp",  0xff000070),
    BEG     ("BEG.bmp"),
    COMBAT1 ("COMBAT1.bmp"),
    COMBAT2 ("COMBAT2.bmp", 0xff0000a0),
    CURS    ("CURS.bmp",    0xffff55ff, 0xff0000aa),
    FANT    ("FANT.bmp"),
    FONTS   ("FONTS.bmp",   0xff707070, 0xfff0f0f0),
    HEAD    ("HEAD.bmp"),
    MAIN    ("MAIN.bmp"),
    MOUSE   ("MOUSE.bmp",   0xff0050c0),
    RAZE    ("RAZE.bmp"),
    SCENERY ("SCENERY.bmp"),
    SETUP   ("SETUP.bmp"),
    STRAT   ("STRAT.bmp"),
    WLC     ("WLC.bmp");
    // @formatter:on

    public static final int COUNT = values().length;

    private final String fileName;
    private final int transparentRGB, sourceRGB;

    BitmapInfo(String fileName) {
        this(fileName, 0);
    }

    BitmapInfo(String fileName, int transparentRGB) {
        this(fileName, transparentRGB, 0);
    }

    BitmapInfo(String fileName, int transparentRGB, int sourceRGB) {
        this.fileName = fileName;
        this.transparentRGB = transparentRGB;
        this.sourceRGB = sourceRGB;
    }

    public String getFileName() {
        return fileName;
    }

    public int getTransparentRGB() {
        return transparentRGB;
    }

    public int getSourceRGB() {
        return sourceRGB;
    }
}
