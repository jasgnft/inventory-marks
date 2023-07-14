package com.jasgnft.plugins.inventorymarks;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class InventoryMarksPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(InventoryMarksPlugin.class);
		RuneLite.main(args);
	}
}