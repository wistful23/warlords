package com.def.warlords.platform;

public final class PlatformHolder {

    private static Platform platform;

    public static Platform getPlatform() {
        if (platform == null) {
            throw new IllegalStateException("Platform is not set");
        }
        return platform;
    }

    public static void setPlatform(Platform platform) {
        PlatformHolder.platform = platform;
    }

    private PlatformHolder() {
    }
}
