/*
 *  QuestManager: An RPG plugin for the Bukkit API.
 *  Copyright (C) 2015-2016 Github Contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.skyisland.questmanager.player.skill;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.skyisland.questmanager.QuestManagerPlugin;

/**
 * Item with some sort of given quality
 *
 */
public class QualityItem {

	public static final double normalQuality = 1.0;
	
	private ItemStack item;
	
	private double quality;
	
	public QualityItem(ItemStack item) {
		if (!item.hasItemMeta() || !item.getItemMeta().hasLore()
				|| !item.getItemMeta().getLore().get(0).toLowerCase().contains("quality: ")) {
			this.item = item;
			this.quality = normalQuality;
			return;
		}
		
		String line = ChatColor.stripColor(item.getItemMeta().getLore().get(0));
		line = line.toLowerCase().substring(line.indexOf("quality: ") + 9).trim();
		double quality = normalQuality;
		try {
			quality = Double.parseDouble(line);
		} catch (Exception e) {
			e.printStackTrace();
			QuestManagerPlugin.questManagerPlugin.getLogger().info("Just pretending it said " + normalQuality);
		}
		this.item = item;
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		lore.remove(0);
		meta.setLore(lore);
		this.item.setItemMeta(meta);
		this.quality = quality;
	}
	
	public QualityItem(ItemStack item, double quality) {
		this.item = item;
		this.quality = quality;
	}

	public ItemStack getUnderlyingItem() {
		return item;
	}
	
	/**
	 * Returns a formatted item
	 */
	public ItemStack getItem() {
		if (item == null) {
			return item;
		}

		ItemStack ret = item.clone();
		String line = ChatColor.DARK_GRAY + "Quality: " + ChatColor.GOLD + String.format("%.2f", quality);
		
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
		
		return ret;
	}

	public void setItem(ItemStack item) {
		this.item = item;
	}

	public double getQuality() {
		return quality;
	}

	public void setQuality(double quality) {
		this.quality = quality;
	}
	
	@Override
	public QualityItem clone() {
		return new QualityItem(item.clone(), quality);
	}
	
	public boolean isSimilar(ItemStack item) {
		if (item == null && this.item == null)
			return true;
		
		if (item.getType() == this.item.getType())
		if (item.getData() == this.item.getData()) {
			ItemMeta d1, d2;
			d1 = item.getItemMeta();
			d2 = this.item.getItemMeta();
			if (d1 == null && d2 == null) {
				return true;
			}
			if (d1 == null || d2 == null) {
				return false; //after the and, this is XOR
			}
			
			if (d1.getDisplayName().equals(d2.getDisplayName()))
				return true;
		}
		
		return false;
			
	}
	
	public boolean isSimilar(QualityItem item) {
		if (this.quality == item.quality)
			return isSimilar(item.item);
		
		return false;
	}
}
