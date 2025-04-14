package java.awt.image;

import android.graphics.Bitmap;

import java.awt.Image;

public class BufferedImage extends Image {

    private final Bitmap bitmap;

    public BufferedImage(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public BufferedImage(int width, int height, int imageType) {
        this.bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getRGB(int x, int y) {
        return bitmap.getPixel(x, y);
    }

    public void setRGB(int x, int y, int rgb) {
        bitmap.setPixel(x, y, rgb);
    }

    public int getWidth() {
        return bitmap.getWidth();
    }

    public int getHeight() {
        return bitmap.getHeight();
    }
}
