package com.def.warlords.tools;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author wistful23
 * @version 1.23
 */
public class GridMaker {

    private static final String IMAGE_NAME = "ARMIES";

    private static final int GRID_WIDTH = 32;
    private static final int GRID_HEIGHT = 14;

    public static void main(String[] args) throws IOException {
        new GridMaker().start();
    }

    public void start() throws IOException {
        final String path = "/img/" + IMAGE_NAME + ".bmp";
        final InputStream in = getClass().getResourceAsStream(path);
        if (in == null) {
            throw new FileNotFoundException("Image was not found: " + path);
        }
        final BufferedImage image = ImageIO.read(in);
        final Graphics g = image.getGraphics();
        g.setColor(Color.RED);
        for (int x = GRID_WIDTH - 1; x < image.getWidth(); x += GRID_WIDTH) {
            g.drawLine(x, 0, x, image.getHeight() - 1);
        }
        for (int y = GRID_HEIGHT - 1; y < image.getHeight(); y += GRID_HEIGHT) {
            g.drawLine(0, y, image.getWidth() - 1, y);
        }
        final File file = new File(IMAGE_NAME + "_grid.bmp");
        ImageIO.write(image, "bmp", file);
        System.out.println("Done");
    }
}
