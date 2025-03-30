package com.def.warlords.control;

import com.def.warlords.sound.Player;

import java.io.IOException;
import java.io.InputStream;

/**
 * Platform-specific features.
 *
 * @author wistful23
 * @version 1.23
 */
public interface Platform {

    void repaint();

    InputStream getResourceAsStream(String fileName) throws IOException;
    Player getAudioPlayer(String fileName, Runnable listener) throws IOException;

    void startSecondaryLoop();
    void stopSecondaryLoop();
}
