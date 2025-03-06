package com.def.warlords.tools;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * @author wistful23
 * @version 1.23
 */
public class ColorPicker {

    private static final String IMAGE_NAME = "ARMIES";

    public static void main(String[] args) throws IOException {
        new ColorPicker().start();
    }

    public void start() throws IOException {
        final String path = "/img/" + IMAGE_NAME + ".bmp";
        final InputStream in = getClass().getResourceAsStream(path);
        if (in == null) {
            throw new FileNotFoundException("Image was not found: " + path);
        }
        final BufferedImage image = ImageIO.read(in);
        final Set<Integer> colors = new HashSet<>();
        for (int x = 0; x < image.getWidth(); ++x) {
            for (int y = 0; y < image.getHeight(); ++y) {
                colors.add(image.getRGB(x, y));
            }
        }
        for (final int color : colors) {
            System.out.println(Integer.toHexString(color));
        }
    }
}
