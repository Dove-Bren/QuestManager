package com.skyisland.questmanager.player.skill;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.skyisland.questmanager.QuestManagerPlugin;

public class FoodItem extends QualityItem {
		
	public static int defaultFoodLevel = 4;
	
	private int foodLevel;
	
	public FoodItem(ItemStack item, int foodLevel) {
		super(item);
		this.foodLevel = foodLevel;
	}
	
	public static FoodItem wrapItem(ItemStack input) {
		QualityItem qItem = new QualityItem(input.clone());
		ItemStack item = qItem.getUnderlyingItem();
		
		if (!item.hasItemMeta() || !item.getItemMeta().hasLore()
				|| !item.getItemMeta().getLore().get(0).toLowerCase().contains("food level: ")) {
			return new FoodItem(item, defaultFoodLevel);
		}
		
		String line = ChatColor.stripColor(item.getItemMeta().getLore().get(0));
		line = line.toLowerCase().substring(line.indexOf("food level: ") + 13).trim();
		int foodLevel = defaultFoodLevel;
		try {
			foodLevel = Integer.parseInt(line);
		} catch (Exception e) {
			e.printStackTrace();
			QuestManagerPlugin.questManagerPlugin.getLogger().info("Just pretending it said " + defaultFoodLevel);
		}
		
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		lore.remove(0);
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		FoodItem food = new FoodItem(item, foodLevel);
		food.setQuality(qItem.getQuality());
		
		return food;
	}
	
	/**
	 * Gets a formateed itemstack that has food level in the lore
	 * @return
	 */
	public ItemStack getItem() {
		if (getUnderlyingItem() == null) {
			return null;
		}

		ItemStack ret = getUnderlyingItem().clone();
		String line = ChatColor.DARK_GRAY + "Food Level: " + ChatColor.DARK_PURPLE + foodLevel;
		
		ItemMeta meta = ret.getItemMeta();
		List<String> lore;
		if (meta.getLore() != null && !meta.getLore().isEmpty()) {
			lore = new ArrayList<>(meta.getLore().size());
			lore.add(line);
			lore.addAll(meta.getLore());
		} else {
			lore = new ArrayList<>(1);
			lore.add(line);
		}
		
		meta.setLore(lore);
		
		ret.setItemMeta(meta);
		
		ItemStack swap = getUnderlyingItem();
		this.setItem(ret);
		ret = super.getItem();
		this.setItem(swap);
		
		return ret;
	}

	public int getFoodLevel() {
		return foodLevel;
	}
	
}
