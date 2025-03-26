package com.def.warlords.graphics;

import com.def.warlords.control.Platform;
import com.def.warlords.util.Logger;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wistful23
 * @version 1.23
 */
public final class BitmapFactory {

    private static BitmapFactory instance;

    public static void createInstance(Platform platform) {
        if (instance != null) {
            throw new IllegalStateException("Bitmap factory is already created");
        }
        instance = new BitmapFactory(platform);
    }

    public static BitmapFactory getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Bitmap factory is not created");
        }
        return instance;
    }

    private final Platform platform;

    private final Bitmap[] bitmaps = new Bitmap[BitmapInfo.COUNT];
    private final Map<Long, Bitmap> transformedBitmaps = new HashMap<>();

    private BitmapFactory(Platform platform) {
        this.platform = platform;
        ImageIO.setUseCache(false);
    }

    public Bitmap fetchBitmap(BitmapInfo bitmapInfo) {
        final int bitmapIndex = bitmapInfo.ordinal();
        Bitmap bitmap = bitmaps[bitmapIndex];
        if (bitmap == null) {
            final BufferedImage image = loadImage(bitmapInfo.getFileName(), bitmapInfo.getTransparentRGB());
            bitmap = new Bitmap(image);
            bitmaps[bitmapIndex] = bitmap;
        }
        return bitmap;
    }

    public Bitmap transformBitmap(BitmapInfo bitmapInfo, Color destinationColor) {
        if (bitmapInfo.getSourceRGB() == 0) {
            return fetchBitmap(bitmapInfo);
        }
        final long transformedBitmapKey =
                ((long) bitmapInfo.ordinal() << 32) | (destinationColor.getRGB() & 0xffffffffL);
        Bitmap transformedBitmap = transformedBitmaps.get(transformedBitmapKey);
        if (transformedBitmap == null) {
            final BufferedImage image =
                    transformImage(fetchBitmap(bitmapInfo).getImage(), bitmapInfo.getSourceRGB(), destinationColor);
            transformedBitmap = new Bitmap(image);
            transformedBitmaps.put(transformedBitmapKey, transformedBitmap);
        }
        return transformedBitmap;
    }

    // NOTE: This method doesn't cache the transformed main bitmap.
    public Bitmap transformMainBitmap() {
        BufferedImage image = fetchBitmap(BitmapInfo.MAIN).getImage();
        image = transformImage(image, 0xff908060, Palette.GRAY_LIGHT);
        image = transformImage(image, 0xff706040, Palette.GRAY);
        image = transformImage(image, 0xff504030, Palette.GRAY_DARK);
        return new Bitmap(image);
    }

    private BufferedImage loadImage(String fileName, int transparentRGB) {
        BufferedImage image = null;
        try (final InputStream in = platform.getResourceAsStream("img/" + fileName)) {
            if (in == null) {
                throw new FileNotFoundException("Image was not found: " + fileName);
            }
            image = ImageIO.read(in);
        } catch (IOException e) {
            Logger.error("Could not load image: " + fileName);
            e.printStackTrace();
        }
        if (image != null && transparentRGB != 0) {
            image = transformImage(image, transparentRGB, 0);
        }
        return image;
    }

    private BufferedImage transformImage(BufferedImage sourceImage, int sourceRGB, Color destinationColor) {
        return transformImage(sourceImage, sourceRGB, destinationColor.getRGB());
    }

    private BufferedImage transformImage(BufferedImage sourceImage, int sourceRGB, int destinationRGB) {
        if (sourceImage == null) {
            return null;
        }
        final int height = sourceImage.getHeight();
        final int width = sourceImage.getWidth();
        final BufferedImage destinationImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                final int rgb = sourceImage.getRGB(x, y);
                destinationImage.setRGB(x, y, rgb == sourceRGB ? destinationRGB : rgb);
            }
        }
        return destinationImage;
    }
}
