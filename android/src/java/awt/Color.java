package java.awt;

public class Color {

    private final int rgb;

    public Color(int rgb) {
        this.rgb = rgb | 0xff000000;
    }

    public int getRGB() {
        return rgb;
    }
}
