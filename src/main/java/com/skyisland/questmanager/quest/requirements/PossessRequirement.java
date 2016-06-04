package com.skyisland.questmanager.quest.requirements;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.player.Participant;
import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.quest.Goal;
import com.skyisland.questmanager.quest.requirements.factory.RequirementFactory;

/**
 * Requirement specification that requires the user to have some quantity of a specific item
 * This requirement can also check whether or not the name of the item matches one given to it
 * @author Skyler
 *
 */
public class PossessRequirement extends Requirement implements Listener {
	
	public static class PossessFactory extends RequirementFactory<PossessRequirement> {
		
		public PossessRequirement fromConfig(Goal goal, ConfigurationSection config) {
			PossessRequirement req = new PossessRequirement(goal);
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
	
	private PossessRequirement(Goal goal) {
		super(goal);
	}
	
	public PossessRequirement(Participant participants, Goal goal, String description, Material itemType) {
		this(participants, goal, description, itemType, 1);
	}
	
	public PossessRequirement(Participant participants, Goal goal, String description, Material itemType, int itemCount) {
		this(participants, goal, description, itemType, 1, "");
	}
	
	public PossessRequirement(Participant participants, Goal goal, String description, Material itemType, int itemCount,
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
		if (!e.isCancelled() && e.getItem().getItemStack().getType() == itemType) {
			
			for (QuestPlayer qp : participants.getParticipants()) {
				if (qp.getPlayer().getUniqueId().equals(e.getPlayer().getUniqueId())) {
					//adjust for that stupid 'hasn't happened yet' error
					int count = e.getItem().getItemStack().getAmount();
					e.getPlayer().getInventory().addItem(e.getItem().getItemStack());
					update();
					
					int pos = e.getPlayer().getInventory().first(itemType);
					ItemStack item = e.getPlayer().getInventory().getItem(pos);
					item.setAmount(item.getAmount() - count);
					if (e.getItem().getItemStack().hasItemMeta()) {
						item.setItemMeta(e.getItem().getItemStack().getItemMeta());
					}
					e.getPlayer().getInventory().setItem(pos, item);
					
					return;
				}
			}
			
		}
	}
	
	@EventHandler
	public void onInventoryChange(PlayerDropItemEvent e) {
		if (this.participants == null) {
			return;
		}
		if (!e.isCancelled() && e.getItemDrop().getItemStack().getType() == itemType) {
			
			for (QuestPlayer qp : participants.getParticipants()) {
				if (qp.getPlayer().getUniqueId().equals(e.getPlayer().getUniqueId())) {
					//adjust for that stupid 'hasn't happened yet' error
					int count = e.getItemDrop().getItemStack().getAmount();
					e.getPlayer().getInventory().addItem(e.getItemDrop().getItemStack());
					update();
					
					int pos = e.getPlayer().getInventory().first(itemType);
					ItemStack item = e.getPlayer().getInventory().getItem(pos);
					item.setAmount(item.getAmount() - count);
					if (e.getItemDrop().getItemStack().hasItemMeta()) {
						item.setItemMeta(e.getItemDrop().getItemStack().getItemMeta());
					}
					e.getPlayer().getInventory().setItem(pos, item);
					
					return;
				}
			}
			
		}
	}
	
	/**
	 * Checks all involved {@link Participant Participant(s)}
	 * to check if the required item & quantity requirements are satisfied.<br />
	 * <b>Note:</b> This does not check if the above quantity-requirement is met <i>across</i>
	 * all members, but instead of any single member has the required number of items.<br />
	 * TODO fix the above noted problem
	 */
	@Override
	protected void update() {
		sync();
		for (QuestPlayer player : participants.getParticipants()) {
				if (player.getPlayer().isOnline()) {
					
					//if (player.getPlayer().getPlayer().getInventory().containsAtLeast(new ItemStack(itemType), itemCount))
					int count = 0;
					Inventory inv = player.getPlayer().getPlayer().getInventory();
					
					for (ItemStack item : inv.all(itemType).values()) {
						if (itemName == null || 
								(item.hasItemMeta() && itemName.equals(item.getItemMeta().getDisplayName()))) {
							count += item.getAmount();
						}
					}
						
					if (!state && count >= itemCount) {
						//if we just achieved it, update the quest!
						this.state = true;
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
		//  type: "pr"
		//  itemYype: (Material. ENUM CONSTANT NAME)
		//  count: [int]
		
		if (!config.contains("type") || !config.getString("type").equals("pr")) {
			throw new InvalidConfigurationException("\n  ---Invalid type! Expected 'pr' but got " + config.getString("type", "null"));
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
	public String getDescription() {
		return this.desc;
	}
	
	
}
