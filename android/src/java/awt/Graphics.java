package java.awt;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public class Graphics {

    private final Bitmap bitmap;
    private final Canvas canvas;

    private final Paint paint = new Paint();

    public Graphics(int width, int height) {
        this.bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        this.canvas = new Canvas(bitmap);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public Color getColor() {
        return new Color(paint.getColor());
    }

    public void setColor(Color color) {
        paint.setColor(color.getRGB());
    }

    public void fillRect(int x, int y, int width, int height) {
        if (width < 0 || height < 0) {
            return;
        }
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawRect(new Rect(x, y, x + width, y + height), paint);
    }

    public void drawRect(int x, int y, int width, int height) {
        if (width < 0 || height < 0) {
            return;
        }
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        canvas.drawRect(new Rect(x, y, x + width, y + height), paint);
    }

    public boolean drawImage(Image image,
                             int dx1, int dy1, int dx2, int dy2,
                             int sx1, int sy1, int sx2, int sy2,
                             ImageObserver observer) {
        if (image == null) {
            return true;
        }
        canvas.drawBitmap(((BufferedImage) image).getBitmap(),
                new Rect(sx1, sy1, sx2, sy2),
                new Rect(dx1, dy1, dx2, dy2), null);
        return true;
    }
}
