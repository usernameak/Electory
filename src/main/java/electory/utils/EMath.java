package electory.utils;

public class EMath {
    public static int addIntColors(int a, int c) {
        int r = ((a & 0xFF0000) >> 16) + ((c & 0xFF0000) >> 16);
        int g = ((a & 0x00FF00) >> 8) + ((c & 0x00FF00) >> 8);
        int b = ((a & 0x0000FF)) + ((c & 0x0000FF));
        if (r >= 256) r = 255;
        if (g >= 256) g = 255;
        if (b >= 256) b = 255;
        return (a & 0xFF000000) | (r << 16) | (g << 8) | b;
    }
}
