package com.skyisland.questmanager.loot.dynamic;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface DynamicGenerator {
	
	public ItemStack generate(Material type);
}
