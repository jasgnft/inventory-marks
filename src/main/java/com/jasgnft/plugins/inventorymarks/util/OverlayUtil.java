package com.jasgnft.plugins.inventorymarks.util;
import java.awt.*;
public class OverlayUtil {
    public static void squareDraw(Graphics2D graphics, Color color, double x, double y, int size)
    {
        graphics.setColor(color);
        graphics.fillRect((int) x - size / 2, (int) y - size / 2, size, size);
    }
}
