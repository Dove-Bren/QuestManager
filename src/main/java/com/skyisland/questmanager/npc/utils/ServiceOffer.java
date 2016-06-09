package com.skyisland.questmanager.npc.utils;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;

/**
 * An offer made by a service npc to purchase something.
 * @author Skyler
 *
 */
public class ServiceOffer extends Service {
	

	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link aliases aliases}
	 */
	public static void registerWithAliases() {
		for (aliases alias : aliases.values()) {
			ConfigurationSerialization.registerClass(ServiceOffer.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(ServiceOffer.class);
	}
	

	private enum aliases {
		FULL("com.SkyIsland.QuestManager.NPC.ServiceOffer"),
		DEFAULT(ServiceOffer.class.getName()),
		SHORT("ServiceOffer"),
		INFORMAL("OFFER");
		
		private String alias;
		
		aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
	}
	
	private String name;
	
	private int price;
	
	private ItemStack item;
	
	public ServiceOffer(String name, int price, ItemStack offerItem) {
		this.price = price;
		this.item = offerItem;
	}
	
	
	
	public String getName() {
		return name;
	}

	public int getPrice() {
		return price;
	}

	public ItemStack getItem() {
		return item;
	}

	public static ServiceOffer valueOf(Map<String, Object> map) {
		if (map == null) {
			return null;
		}
		
		/*
		 * name: name of trade for tooltip
		 * price: money offered
		 * item: item asked for
		 */
		
		String name = (String) map.get("name");
		int cost = (int) map.get("price");
		
		ItemStack item = (ItemStack) map.get("item");
		
		return new ServiceOffer(name, cost, item);
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		
		map.put("name", name);
		map.put("price", price);
		
		map.put("item", item);
		
		return map;
	}
}
