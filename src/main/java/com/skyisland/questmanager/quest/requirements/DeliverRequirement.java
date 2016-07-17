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

package com.skyisland.questmanager.quest.requirements;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.configuration.state.RequirementState;
import com.skyisland.questmanager.configuration.state.StatekeepingRequirement;
import com.skyisland.questmanager.player.Participant;
import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.quest.Goal;
import com.skyisland.questmanager.quest.requirements.factory.RequirementFactory;

/**
 * Requirement specification that checks for an itemstack and removes it when it's there
 *
 */
public class DeliverRequirement extends Requirement implements Listener, StatekeepingRequirement {
	
	public static class DeliverFactory extends RequirementFactory<DeliverRequirement> {
		
		public DeliverRequirement fromConfig(Goal goal, ConfigurationSection config) {
			DeliverRequirement req = new DeliverRequirement(goal);
			req.participants = goal.getQuest().getParticipants();
			try {
				req.fromConfig(config);
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
			}
			return req;
		}
	}
	
	private Material itemType;
	
	private int itemCount;
	
	private String itemName;
	
	private DeliverRequirement(Goal goal) {
		super(goal);
	}
	
	public DeliverRequirement(Participant participants, Goal goal, Material itemType) {
		this(participants, goal, "", itemType, 1);
	}
	
	public DeliverRequirement(Participant participants, Goal goal, String description, Material itemType) {
		this(participants, goal, description, itemType, 1);
	}
	
	public DeliverRequirement(Participant participants, Goal goal, String description, Material itemType, int itemCount) {
		this(participants, goal, description, itemType, 1, "");
	}
	
	public DeliverRequirement(Participant participants, Goal goal, String description, Material itemType, int itemCount,
			String itemName) {
		super(goal, description);
		state = false;
		this.itemType = itemType;
		this.itemCount = itemCount;
		this.itemName = itemName;
		this.participants = participants;
		
		if (itemName.trim().isEmpty()) {
			this.itemName = null;
		}
	}
	
	@Override
	public void activate() {
		Bukkit.getPluginManager().registerEvents(this, QuestManagerPlugin.questManagerPlugin);
	}

	/**
	 * @return the itemType
	 */
	public Material getItemType() {
		return itemType;
	}

	/**
	 * @return the itemCount
	 */
	public int getItemCount() {
		return itemCount;
	}
	
	@EventHandler
	public void onInventoryChange(PlayerPickupItemEvent e) {
		if (this.participants == null) {
			return;
		}
		if (state) {
			return;
		}
		if (!e.isCancelled() && e.getItem().getItemStack().getType() == itemType) {
			
			for (QuestPlayer qp : participants.getParticipants()) {
				if (qp.getPlayer().getUniqueId().equals(e.getPlayer().getUniqueId())) {
					final Requirement req = this;
					Bukkit.getScheduler().runTaskLater(QuestManagerPlugin.questManagerPlugin, req::update, 1);
				}
			}
			
		}
	}
	
	@EventHandler
	public void onInventoryChange(PlayerDropItemEvent e) {
		if (this.participants == null) {
			return;
		}
		if (state) {
			return;
		}
		if (!e.isCancelled() && e.getItemDrop().getItemStack().getType() == itemType) {
			
			for (QuestPlayer qp : participants.getParticipants()) {
				if (qp.getPlayer().getUniqueId().equals(e.getPlayer().getUniqueId())) {
					final Requirement req = this;
					Bukkit.getScheduler().runTaskLater(QuestManagerPlugin.questManagerPlugin, req::update, 1);
				}
			
			}
		}
	}
	
	/**
	 * Checks all involved {@link Participant}s
	 * to check if the required item and quantity requirements are satisfied.
	 * <b>Note:</b> This does not check if the above quantity-requirement is met <i>across</i>
	 * all members, but instead of any single member has the required number of items.
	 * TODO fix the above noted problem
	 */
	@Override
	protected void update() {
		sync();
		if (state) {
			//no need to check anything, cause we've already been met
			return;
		}
		
		for (QuestPlayer player : participants.getParticipants()) {
			if (player.getPlayer().isOnline()) {
			
				int count = 0;
				Inventory inv = player.getPlayer().getPlayer().getInventory();
				String name;
				
				for (ItemStack item : inv.all(itemType).values()) {
					name = null;
					if (item.hasItemMeta() && item.getItemMeta().hasDisplayName())
						name = item.getItemMeta().getDisplayName();
					if ((itemName == null && name == null) || 
						(itemName != null && name != null && name.equals(itemName))) {
						count += item.getAmount();
					}
				}
					
				if (count >= itemCount) {
					//if we just achieved it, update the quest!
					this.state = true;
					
					
						//gotta go through and find ones that match the name
						int left = itemCount;
						ItemStack item;
						for (int i = 0; i <= 35; i++) {
							item = inv.getItem(i);
							if (item != null && item.getType() == itemType)
							if (  (itemName == null && (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()))
								|| (item.hasItemMeta() && item.getItemMeta().getDisplayName() != null && item.getItemMeta().getDisplayName().equals(itemName))	
									) {
								//deduct from this item stack as much as we can, up to 'left'
								//but if there's more than 'left' left, just remove it
								int amt = item.getAmount();
								if (amt <= left) {
									//gonna remove entire stack
									item.setType(Material.AIR);
									item.setAmount(0);
									item.setItemMeta(null);
								} else {
									item.setAmount(amt - left);
								}
								
								inv.setItem(i, item);
								left-=amt;
								
								if (left <= 0) {
									break;
								}
							}
						}
					
					HandlerList.unregisterAll(this);
					updateQuest();
				}
				return;
			}
		}
		
		state = false;
	}

	@Override
	public void fromConfig(ConfigurationSection config) throws InvalidConfigurationException {
		//we need to load information about what we need to possess and how much
		//our config is 
		//  type: "delr"
		//  itemYype: (Material. ENUM CONSTANT NAME)
		//  count: [int]
		//  name: [string]
		
		if (!config.contains("type") || !config.getString("type").equals("delr")) {
			throw new InvalidConfigurationException("\n  ---Invalid type! Expected 'delr' but got " + config.getString("type", "null"));
		}
		
		this.itemType = Material.valueOf(
				config.getString("itemType", "AIR"));
		
		this.itemCount = config.getInt("count", 1);
		
		this.itemName = config.getString("name", "");
		if (itemName.trim().isEmpty()) {
			itemName = null;
		}
		
		this.desc = config.getString("description", "Collect " + itemCount + " " +
				itemName == null ? itemType.toString() : itemName);
	}

	@Override
	public RequirementState getState() {
		/*
		 * state info is just whether this has been filled or not
		 * state: true/false
		 */
		YamlConfiguration config = new YamlConfiguration();
		config.set("state", state);
		
		RequirementState image = new RequirementState(config);
		
		return image;
	}

	@Override
	public void loadState(RequirementState state)
			throws InvalidConfigurationException {
		ConfigurationSection config = state.getConfig();
		if (!config.contains("state")) {
			throw new InvalidConfigurationException();
		}
		
		this.state = config.getBoolean("state");
		
	}

	@Override
	public void stop() {
		;
	}
	
	@Override
	public String getDescription() {
		return desc;
	}
}
