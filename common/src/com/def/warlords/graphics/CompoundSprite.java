package com.def.warlords.graphics;

import java.awt.Graphics;
import java.util.stream.Stream;

/**
 * @author wistful23
 * @version 1.23
 */
public class CompoundSprite extends Sprite {

    private final Sprite[] sprites;

    public CompoundSprite(Sprite[] sprites) {
        super(Stream.of(sprites).mapToInt(Sprite::getWidth).max().orElse(0),
                Stream.of(sprites).mapToInt(Sprite::getHeight).max().orElse(0));
        this.sprites = sprites;
    }

    @Override
    public void draw(Graphics g, int x, int y) {
        for (final Sprite sprite : sprites) {
            sprite.draw(g, x, y);
        }
    }
}
