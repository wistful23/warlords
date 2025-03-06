package com.def.warlords.graphics;

/**
 * @author wistful23
 * @version 1.23
 */
public class SpriteArrayBuilder {

    private final int width, height;
    private final BitmapInfo bitmapInfo;

    private final Sprite[] sprites;
    private int index;

    public SpriteArrayBuilder(int count, BitmapInfo bitmapInfo) {
        this(count, 0, 0, bitmapInfo);
    }

    public SpriteArrayBuilder(int count, int width, int height, BitmapInfo bitmapInfo) {
        this.width = width;
        this.height = height;
        this.bitmapInfo = bitmapInfo;
        this.sprites = new Sprite[count];
    }

    public SpriteArrayBuilder add() {
        if (width == 0 || height == 0) {
            throw new IllegalStateException("Sprite size is not configured");
        }
        return add(new Sprite(width, height));
    }

    public SpriteArrayBuilder add(int tx, int ty) {
        if (width == 0 || height == 0) {
            throw new IllegalStateException("Sprite size is not configured");
        }
        return add(new SimpleSprite(width, height, bitmapInfo, tx, ty));
    }

    public SpriteArrayBuilder add(int width, int height, int tx, int ty) {
        return add(new SimpleSprite(width, height, bitmapInfo, tx, ty));
    }

    public SpriteArrayBuilder add(int width, int height, int tx, int ty, int dx, int dy) {
        return add(new OffsetSprite(width, height, bitmapInfo, tx, ty, dx, dy));
    }

    public SpriteArrayBuilder add(Sprite sprite) {
        if (index == sprites.length) {
            throw new IllegalStateException("Array is full");
        }
        sprites[index++] = sprite;
        return this;
    }

    public Sprite[] getSprites() {
        return sprites;
    }
}
