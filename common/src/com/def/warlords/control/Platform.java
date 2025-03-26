package com.def.warlords.control;

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

    InputStream getResourceAsStream(String name) throws IOException;
}
