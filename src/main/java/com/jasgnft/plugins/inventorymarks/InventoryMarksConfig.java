package com.jasgnft.plugins.inventorymarks;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup(InventoryMarksConfig.GROUP)
public interface InventoryMarksConfig extends Config
{
	String GROUP = "inventorymarks";

	@ConfigSection(
			name = "Preset Colors",
			description = "Preset colors for the marks",
			position = 0
	)
	String markColorSection = "markColorSection";

	@ConfigItem(
			position = 0,
			keyName = "groupColor1",
			name = "Group 1 Color",
			description = "Color of the Mark",
			section = markColorSection
	)
	default Color getGroup1Color()
	{
		return new Color(255, 0, 0);
	}

	@ConfigItem(
			position = 1,
			keyName = "groupColor2",
			name = "Group 2 Color",
			description = "Color of the Mark",
			section = markColorSection
	)
	default Color getGroup2Color()
	{
		return new Color(0, 255, 0);
	}

	@ConfigItem(
			position = 2,
			keyName = "groupColor3",
			name = "Group 3 Color",
			description = "Color of the Mark",
			section = markColorSection
	)
	default Color getGroup3Color()
	{
		return new Color(0, 0, 255);
	}

	@ConfigItem(
			position = 3,
			keyName = "groupColor4",
			name = "Group 4 Color",
			description = "Color of the Mark",
			section = markColorSection
	)
	default Color getGroup4Color()
	{
		return new Color(255, 0, 255);
	}

	@ConfigItem(
			position = 4,
			keyName = "groupColor5",
			name = "Group 5 Color",
			description = "Color of the Mark",
			section = markColorSection
	)
	default Color getGroup5Color()
	{
		return new Color(255, 255, 0);
	}

	@ConfigItem(
			position = 5,
			keyName = "groupColor6",
			name = "Group 6 Color",
			description = "Color of the Mark",
			section = markColorSection
	)
	default Color getGroup6Color()
	{
		return new Color(0, 255, 255);
	}

	@ConfigSection(
			name = "Mark options",
			description = "Diffrents mark options",
			position = 1
	)
	String markOptionSection = "markOptionSection";

	@ConfigItem(
			position = 0,
			keyName = "showmark",
			name = "Show mark",
			description = "Show a square mark on inventory items",
			section = markOptionSection
	)
	default boolean showMark()
	{
		return false;
	}

	enum MShape {
		SCUARE,
		CIRCLE,
	}

	@ConfigItem(
			position = 1,
			keyName = "markshape",
			name = "Mark shape",
			description = "Shape of the mark",
			section = markOptionSection
	)
	default MShape markShape(){
		return MShape.SCUARE;
	}

	@Range(
			min = 2,
			max = 20
	)
	@ConfigItem(
			position = 2,
			keyName = "marksize",
			name = "Mark size",
			description = "Change the size of the mark",
			section = markOptionSection
	)
	default int markSize()
	{
		return 10;
	}

	@ConfigItem(
			position = 3,
			keyName = "showTagOutline",
			name = "Outline",
			description = "Configures whether or not item tags show be outlined",
			section = markOptionSection
	)
	default boolean showTagOutline()
	{
		return true;
	}

	@ConfigItem(
			position = 4,
			keyName = "tagFill",
			name = "Fill",
			description = "Configures whether or not item tags should be filled",
			section = markOptionSection
	)
	default boolean showTagFill()
	{
		return false;
	}

	@Range(
			max = 255
	)
	@ConfigItem(
			position = 5,
			keyName = "fillOpacity",
			name = "Fill opacity",
			description = "Configures the opacity of the tag \"Fill\"",
			section = markOptionSection
	)
	default int fillOpacity()
	{
		return 50;
	}

}
