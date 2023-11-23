package com.def.warlords.control;

import com.def.warlords.graphics.BitmapInfo;

/**
 * @author wistful23
 * @version 1.23
 */
public enum SurrenderMode {

    // @formatter:off
    PEACE_OFFER         (BitmapInfo.BEG),
    PEACE_OFFER_REFUSED (BitmapInfo.HEAD);
    // @formatter:on

    private final BitmapInfo bitmapInfo;

    SurrenderMode(BitmapInfo bitmapInfo) {
        this.bitmapInfo = bitmapInfo;
    }

    public BitmapInfo getBitmapInfo() {
        return bitmapInfo;
    }
}
