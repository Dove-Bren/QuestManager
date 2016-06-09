package com.skyisland.questmanager.ui.menu.inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.ui.menu.action.FillableInventoryAction;
import com.skyisland.questmanager.ui.menu.action.MenuAction;

/**
 * Gui used to 'receive items' from a player's inventory. Allows specification of a filter
 * for runtime awesomeness
 * @author Skyler
 *
 */
public class ContributionInventory extends GuiInventory {
	
	private static final ItemStack submitIcon = new ItemStack(Material.STAINED_GLASS_PANE);
	
	{
		ItemMeta meta = submitIcon.getItemMeta();
		meta.setDisplayName("Submit");
		submitIcon.setItemMeta(meta);
		submitIcon.setDurability((short) 13);
	}
	
	private Map<Integer, ItemStack> items;
	
	private ItemFilter filter;
	
	private int maxItems;
	
	private int slots;
	
	private boolean wholeStacks;
	
	private String invName;
	
	private Player player;
	
	private Inventory inv;
	
	private MenuAction action;
	
	public ContributionInventory(Player player, MenuAction finishAction, int maxItems) {
		this(player, finishAction, maxItems, null, "Give Items");
	}
	
	public ContributionInventory(Player player, MenuAction finishAction, int maxItems, ItemFilter filter) {
		this(player, finishAction, maxItems, filter, "Give Items");
	}
	
	public ContributionInventory(Player player, MenuAction finishAction, int maxItems, ItemFilter filter, String name) {
		this(player, finishAction, maxItems, filter, name, false);
	}
	
	public ContributionInventory(Player player, MenuAction finishAction, int maxItems, ItemFilter filter, String name,
			boolean wholeStacks) {
		this.player = player;
		this.wholeStacks = wholeStacks;
		this.filter = filter;
		this.maxItems = maxItems;
		this.invName = name;
		this.action = finishAction;
		
		this.items = new HashMap<>();
		
		if (!player.getPlayer().isOnline()) {
			System.out.println("Cannot fetch inventory for offline player [GuiInventory@getFormattedInventory]");
			return;
		}
		
		Player p = player.getPlayer().getPlayer();
		
		slots = 9 * ((int) Math.ceil((double) (maxItems + 1) / 9.0));
		
		this.inv = Bukkit.createInventory(p, slots, invName);
		if (!items.isEmpty()) {
			for (Entry<Integer, ItemStack> e : items.entrySet()) {
				int val = e.getKey();
				inv.setItem(val, e.getValue());
			}
		}
		
		//pad with barrier blocks for clarity
		for (int i = maxItems; i < slots - 1; i++) {
			inv.setItem(i, new ItemStack(Material.BARRIER));
		}
		
		inv.setItem(slots - 1, submitIcon);
	}
	
	@Override
	public Inventory getFormattedInventory(QuestPlayer player) {
				
		return inv;
	}

	@Override
	public Map<String, Object> serialize() {
		return null; //Runtime only, not serializable
	}

	@Override
	public InventoryItem getItem(int pos) {
		//real logic is here
		/*
		 * Check slot. If in player's inventory, try to take. If in top inventory, try and put back.
		 */

		if (pos >= slots) {
			//in player's inventory
			doContribute(pos);
		} else if (pos == slots - 1) {
			doSubmit();
		} else {
			doReturn(pos);
		}
		
		
		return null; //tell it to do nothing
	}
	
	private void doContribute(int pos) {
		ItemStack clicked = player.getOpenInventory().getItem(pos);
		if (clicked == null) {
			return;
		}
		
		//make sure we have room
		int slot = -1;
		for (int i = 0; i < maxItems; i++) {
			if (items.get(i) == null || items.get(i).getType() == Material.AIR) {
				slot = i;
				break;
			}
		}
		
		if (slot == -1) {
			//can't find space. just cancel.
			return;
		}
		
		//if we have a filter, apply it
		if (filter != null && filter.filterItem(clicked) == false) {
			return;
		}
		
		//else we got an item and a spot. now just make sure we don't already have one of that type
		for (int i = 0; i < maxItems; i++) {
			if (items.get(i) != null && items.get(i).getType() == clicked.getType()) {
				//already have one of this type
				return;
			}
		}
		
		//take from inventory, as defined in 'wholestacks'
		int amount = (wholeStacks ? clicked.getAmount() : 1);
		if (clicked.getAmount() > amount) {
			clicked.setAmount(clicked.getAmount() - amount);
		} else {
			player.getOpenInventory().setItem(pos, null);
		}
		
		ItemStack newItem = clicked.clone();
		newItem.setAmount(amount);
		items.put(slot, newItem);
		updateInventory();
		
	}
	
	private void doReturn(int pos) {
		if (pos >= maxItems) {
			//clicking a barrier block
			return;
		}
		
		ItemStack item = items.get(pos);
		if (item == null) {
			return;
		}
		
		items.put(pos, null);
		this.player.getInventory().addItem(item);
		updateInventory();
	}
	
	private void doSubmit() {
		player.closeInventory();
		
		if (action != null) {
			if (action instanceof FillableInventoryAction) {
				ItemStack[] ret = new ItemStack[maxItems];
				for (int i = 0; i < maxItems; i++) {
					ret[i] = items.get(i);
				}
				
				((FillableInventoryAction) action).provideItems(ret);
			}
			action.onAction();
		}
	}
	
	private void updateInventory() {
		for (int i = 0; i < maxItems; i++) {
			inv.setItem(i, items.get(i));
		}
	}
}
