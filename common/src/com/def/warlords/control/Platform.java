package com.def.warlords.control;

import com.def.warlords.sound.Sound;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Platform-specific features.
 *
 * @author wistful23
 * @version 1.23
 */
public interface Platform {

    InputStream getResourceAsStream(String fileName) throws IOException;
    BufferedImage getBufferedImage(String fileName) throws IOException;
    Sound getSound(String fileName, Runnable listener) throws IOException;

    void startSecondaryLoop();
    void stopSecondaryLoop();

    void invokeLater(Runnable action, int delay);
}
