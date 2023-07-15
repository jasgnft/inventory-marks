package com.jasgnft.plugins.inventorymarks;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

import static com.jasgnft.plugins.inventorymarks.util.OverlayUtil.squareDraw;
import static com.jasgnft.plugins.inventorymarks.util.OverlayUtil.circleDraw;

public class InventoryMarksOverlay extends WidgetItemOverlay {

    private final ItemManager itemManager;
    private final InventoryMarksPlugin plugin;
    private final InventoryMarksConfig config;
    private final Cache<Long, Image> fillCache;
    private final Cache<Integer, Mark> markCache;

    @Inject
    private InventoryMarksOverlay(ItemManager itemManager, InventoryMarksPlugin plugin, InventoryMarksConfig config){
        this.itemManager = itemManager;
        this.plugin = plugin;
        this.config = config;
        showOnEquipment();
        showOnInventory();
        showOnInterfaces(
                WidgetID.CHAMBERS_OF_XERIC_STORAGE_UNIT_INVENTORY_GROUP_ID,
                WidgetID.CHAMBERS_OF_XERIC_STORAGE_UNIT_PRIVATE_GROUP_ID,
                WidgetID.CHAMBERS_OF_XERIC_STORAGE_UNIT_SHARED_GROUP_ID,
                WidgetID.GRAVESTONE_GROUP_ID
        );
        fillCache = CacheBuilder.newBuilder()
                .concurrencyLevel(1)
                .maximumSize(32)
                .build();
        markCache = CacheBuilder.newBuilder()
                .concurrencyLevel(1)
                .maximumSize(32)
                .build();
    }
    @Override
    public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem widgetItem) {
        final Mark mark = getMark(itemId);
        if (mark == null || mark.color == null) {
            return;
        }

        final Color color = mark.color;

        Rectangle bounds = widgetItem.getCanvasBounds();
        if (config.showMark()){
            if (config.markShape() == InventoryMarksConfig.MShape.SCUARE){
                squareDraw(graphics, color, bounds.getCenterX(), bounds.getCenterY(), config.markSize());
            }

            if (config.markShape() == InventoryMarksConfig.MShape.CIRCLE){
                circleDraw(graphics, color, bounds.getCenterX(), bounds.getCenterY(), config.markSize());
            }
        }

        if (config.showTagOutline()) {
            final BufferedImage outline = itemManager.getItemOutline(itemId, widgetItem.getQuantity(), color);
            graphics.drawImage(outline, (int) bounds.getX(), (int) bounds.getY(), null);
        }

        if (config.showTagFill()) {
            final Image image = getFillImage(color, widgetItem.getId(), widgetItem.getQuantity());
            graphics.drawImage(image, (int) bounds.getX(), (int) bounds.getY(), null);
        }
    }

    private Mark getMark(int itemId)
    {
        Mark mark = markCache.getIfPresent(itemId);
        if (mark == null)
        {
            mark = plugin.getMark(itemId);
            if (mark == null)
            {
                return null;
            }

            markCache.put(itemId, mark);
        }
        return mark;
    }

    private Image getFillImage(Color color, int itemId, int qty)
    {
        long key = (((long) itemId) << 32) | qty;
        Image image = fillCache.getIfPresent(key);
        if (image == null)
        {
            final Color fillColor = ColorUtil.colorWithAlpha(color, config.fillOpacity());
            image = ImageUtil.fillImage(itemManager.getImage(itemId, qty, false), fillColor);
            fillCache.put(key, image);
        }
        return image;
    }

    void invalidateCache()
    {
        fillCache.invalidateAll();
        markCache.invalidateAll();
    }
}
