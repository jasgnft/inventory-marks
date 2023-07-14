package com.jasgnft.plugins.inventorymarks;

import com.google.gson.Gson;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;
import net.runelite.client.ui.components.colorpicker.RuneliteColorPicker;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ColorUtil;

import javax.inject.Inject;
import javax.swing.*;
import java.applet.Applet;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


@PluginDescriptor(
		name = "Inventory Marks",
		description = "Add the ability to mark items in your inventory",
		tags = {"highlight", "items", "overlay", "tagging", "mark", "jasgnft"},
		enabledByDefault = false
)

@Slf4j
public class InventoryMarksPlugin extends Plugin
{
	private static final String ITEM_KEY_PREFIX = "item_";
	private static final String MARK_KEY_PREFIX = "mark_";

	@Inject
	private Client client;

	@Inject
	private ConfigManager configManager;

	@Inject
	private InventoryMarksOverlay overlay;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private Gson gson;

	@Inject
	private ColorPickerManager colorPickerManager;

	@Provides
	InventoryMarksConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(InventoryMarksConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(overlay);
		convertConfig();
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
	}

	Mark getMark(int itemId){
		String mark = configManager.getConfiguration(InventoryMarksConfig.GROUP, MARK_KEY_PREFIX + itemId);
		if (mark == null || mark.isEmpty()){
			return null;
		}
		return gson.fromJson(mark, Mark.class);
	}

	void  setMark(int itemId, Mark mark){
		String json = gson.toJson(mark);
		configManager.setConfiguration(InventoryMarksConfig.GROUP, MARK_KEY_PREFIX + itemId, json);
	}

	void unSetMark(int itemId){
		configManager.unsetConfiguration(InventoryMarksConfig.GROUP, MARK_KEY_PREFIX + itemId);
	}

	private void convertConfig(){
		String migrated = configManager.getConfiguration(InventoryMarksConfig.GROUP, "migrated");
		if (!"1".equals("migrated")){
			return;
		}

		int remove = 0;
		List<String> keys = configManager.getConfigurationKeys(InventoryMarksConfig.GROUP + "." + ITEM_KEY_PREFIX);
		for (String key : keys){
			String[] str = key.split("\\.", 2);
			if (str.length == 2){
				configManager.unsetConfiguration(str[0], str[1]);
				++remove;
			}
		}
		log.debug("Removed {} old marks", remove);
		configManager.setConfiguration(InventoryMarksConfig.GROUP, "migrated", "2");
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged){
		if (configChanged.getGroup().equals(InventoryMarksConfig.GROUP)){
			overlay.invalidateCache();
		}
	}

	@Subscribe
	public void onMenuOpened(final MenuOpened event){
		if (!client.isKeyPressed(KeyCode.KC_SHIFT)){
			return;
		}
		final MenuEntry[] entries =event.getMenuEntries();
		for (int idx = entries.length - 1; idx >= 0; --idx){
			final MenuEntry entry = entries[idx];
			final Widget w = entry.getWidget();

			if (w != null && WidgetInfo.TO_GROUP(w.getId()) == WidgetID.INVENTORY_GROUP_ID
					&& "Examine".equals(entry.getOption()) && entry.getIdentifier() == 10){
				final int itemId = w.getItemId();
				final Mark mark = getMark(itemId);

				final MenuEntry parent = client.createMenuEntry(idx)
						.setOption("Inventory mark")
						.setTarget(entry.getTarget())
						.setType(MenuAction.RUNELITE_SUBMENU);

				for (Color color : invColors()){
					if (mark == null || !mark.color.equals(color)) {
						client.createMenuEntry(idx)
								.setOption(ColorUtil.prependColorTag("Color", color))
								.setType(MenuAction.RUNELITE)
								.setParent(parent)
								.onClick(e -> {
									Mark m = new Mark();
									m.color = color;
									setMark(itemId, m);
								});
					}
				}

				client.createMenuEntry(idx)
						.setOption("Pick")
						.setType(MenuAction.RUNELITE)
						.setParent(parent)
						.onClick(e -> {
							Color color = mark == null ? Color.WHITE : mark.color;
							SwingUtilities.invokeLater(() ->{
								RuneliteColorPicker colorPicker = colorPickerManager.create(SwingUtilities.windowForComponent((Applet) client),
										color, "Inventory Mark", true);
								colorPicker.setOnClose(c -> {
									Mark m = new Mark();
									m.color = c;
									setMark(itemId, m);
								});
								colorPicker.setVisible(true);
							});
						});
				if (mark != null) {
					client.createMenuEntry(idx)
							.setOption("Reset")
							.setType(MenuAction.RUNELITE)
							.setParent(parent)
							.onClick(e -> unSetMark(itemId));
				}
			}
		}
	}

	private List<Color> invColors() {
		List<Color> colors = new ArrayList<>();
		ItemContainer container = client.getItemContainer(InventoryID.INVENTORY);
		for (Item item : container.getItems()) {
			Mark mark = getMark(item.getId());
			if (mark != null && mark.color != null) {
				if (!colors.contains(mark.color)){
					colors.add(mark.color);
				}
			}
		}
		return colors;
	}
}
