package com.skyisland.questmanager.ui.menu.inventory.minigames;

import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Lists;
import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.configuration.utils.YamlWriter;
import com.skyisland.questmanager.effects.ChargeEffect;
import com.skyisland.questmanager.fanciful.FancyMessage;
import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.player.skill.QualityItem;
import com.skyisland.questmanager.player.skill.defaults.CookingSkill;
import com.skyisland.questmanager.player.skill.defaults.CookingSkill.CombineRecipe;
import com.skyisland.questmanager.player.skill.event.CraftEvent;
import com.skyisland.questmanager.ui.menu.inventory.GuiInventory;
import com.skyisland.questmanager.ui.menu.inventory.InventoryItem;

public class CombiningGui extends GuiInventory {
	
	public static CookingSkill skillLink = null;
	
	public static final Sound mixSound = Sound.BLOCK_BREWING_STAND_BREW;
	/////
	public static final Sound failSound = Sound.ENTITY_CAT_DEATH;
	
	public static final Sound clickSound = Sound.UI_BUTTON_CLICK;
	
	public static final String failMessage = ChatColor.RED + "The recipe was good, but you lacked the skill for that craft";
	
	public static final String noRecipeMessage = ChatColor.RED + "The combination didn't result in anything useful";
	
	public static final String winMessage = "You created ";
	
	public static final ItemStack combineIcon = new ItemStack(Material.BOWL);
	
	private static final ChargeEffect succeedEffect = new ChargeEffect(Effect.HAPPY_VILLAGER);
	
	private static final ChargeEffect failEffect = new ChargeEffect(Effect.VILLAGER_THUNDERCLOUD);
	
	{
		ItemMeta meta = combineIcon.getItemMeta();
		meta.setDisplayName("Combine");
		combineIcon.setItemMeta(meta);
	}
	
	private Player player;
	
	private Inventory inv;
	
	public CombiningGui(Player player, String name) {
		this.player = player;
		this.inv = Bukkit.createInventory(player, InventoryType.BREWING, name);
		
		inv.setItem(4, combineIcon);
		
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
		System.out.println(pos);
		
		/*
		 * 0,1,2 are bottom slots
		 * 3 is ingredient slot
		 * 4 is fuel slot
		 */
		
		if (pos == 4) {
			submit();
			return null;
		}
		
		if (pos == 3)
			return null; //nothing to do... right?
		
		if (pos > 4) {
			submitItem(pos + 4);
			return null;
		}
		
		//ingredient slot. taking back?
		if (inv.getItem(pos) != null) {
			player.getInventory().addItem(inv.getItem(pos));
			inv.setItem(pos, null);
		}
		
		return null; //tell it to do nothing
	}

	public static void setCookingSkill(CookingSkill skill) {
		skillLink = skill;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof CombiningGui) {
			return ((CombiningGui) o).player.getUniqueId().equals(player.getUniqueId());
		}
		
		return false;
	}
	
	private void submit() {
		//get all our items and look for a recipe, and then try to perform it
		if (skillLink == null)
			return;
		
		if (inv.getItem(0) == null && inv.getItem(1) == null && inv.getItem(2) == null)
			return;
		

		List<ItemStack> args = Lists.newArrayList(inv.getItem(0), inv.getItem(1), inv.getItem(2));
		
		CombineRecipe recipe = skillLink.getMixingRecipe(inv.getItem(0), inv.getItem(1), inv.getItem(2));
		if (recipe == null) {
			//one last check; are they combining quality items?
			boolean same = true;
			Material type = null;
			short data = 0;
			for (ItemStack item : args) {
				if (item == null)
					continue;
				if (type == null) {
					type = item.getType();
					data = item.getDurability();
					continue;
				}
				
				if (item.getType() != type || item.getDurability() != data) {
					same = false;
					break;
				}
			}
			
			if (same) {
				combineQuality(args);
				inv.setItem(0, null);
				inv.setItem(1, null);
				inv.setItem(2, null);
				return;
			}

			inv.setItem(0, null);
			inv.setItem(1, null);
			inv.setItem(2, null);
			player.sendMessage(noRecipeMessage);
			return;
		}
		

		inv.setItem(0, null);
		inv.setItem(1, null);
		inv.setItem(2, null);
		
		QuestPlayer qp = QuestManagerPlugin.questManagerPlugin.getPlayerManager().getPlayer(player);
		QualityItem result = new QualityItem(recipe.result);
		
		double sum = 0;
		int count = 0;
		QualityItem qi;
		for (ItemStack item : args) {
			if (item == null)
				continue;
			qi = new QualityItem(item);
			count ++;
			sum += qi.getQuality();
		}
		
		result.setQuality(Math.max(0, sum / (double) count));
		
		CraftEvent event = new CraftEvent(qp, CraftEvent.CraftingType.COOKING, recipe.difficulty, result.clone());
		Bukkit.getPluginManager().callEvent(event);
		
		if (event.isFail()) {
			skillLink.perform(qp, recipe.difficulty, true);
			player.sendMessage(failMessage);
			failEffect.play(player, player.getLocation());
			player.getWorld().playSound(player.getLocation(), failSound, 1, 1);
			return;
		}
		
		skillLink.perform(qp, recipe.difficulty, false);
		succeedEffect.play(player, player.getLocation());
		
		result.setQuality(event.getQualityModifier() * result.getQuality());
		result.getUnderlyingItem().setAmount((int) Math.round(event.getQuantityModifier() * result.getUnderlyingItem().getAmount()));
		
		String name;
		if (result.getItem().getItemMeta() == null || result.getItem().getItemMeta().getDisplayName() == null) {
			name = YamlWriter.toStandardFormat(result.getItem().getType().toString());
		} else {
			name = result.getItem().getItemMeta().getDisplayName();
		}
		
		FancyMessage msg = new FancyMessage(winMessage)
				.color(ChatColor.GREEN)
			.then(result.getItem().getAmount() > 1 ? result.getItem().getAmount() + "x " : "a ")
			.then("[" + name + "]")
				.color(ChatColor.DARK_PURPLE)
				.itemTooltip(result.getItem());
		
		
		msg.send(player);
		player.getWorld().playSound(player.getEyeLocation(), mixSound, 1, 1);
		if (!(player.getInventory().addItem(result.getItem())).isEmpty()) {
			player.sendMessage(ChatColor.RED + "There is no space left in your inventory");
			player.getWorld().dropItem(player.getEyeLocation(), result.getItem());
		}
	}
	
	private void submitItem(int slot) {
		int pos = -1; //where we're going to insert it. 0-2
		for (int i = 0; i < 3; i++) {
			if (inv.getItem(i) == null) {
				pos = i;
				break;
			}
		}
		
		if (pos == -1) {
			//no space, do nothing
			return;
		}
		
		slot = slot % 36;
		
		ItemStack item = player.getInventory().getItem(slot);
		ItemStack replace = null;
		if (item.getAmount() > 1) {
			replace = item.clone();
			replace.setAmount(item.getAmount() - 1);
		}
		
		player.getInventory().setItem(slot, replace);
		
		item.setAmount(1);
		inv.setItem(pos, item);
	}
	
	private void combineQuality(List<ItemStack> inputs) {
		double sum = 0;
		int count = 0;
		ItemStack example = null;
		for (ItemStack item : inputs) {
			if (item == null)
				continue;
			
			example = item;
			sum += (new QualityItem(item)).getQuality();
			count++;
		}
		
		if (count == 0)
			return;
		
		QualityItem output = new QualityItem(example);
		output.setQuality(sum / count);
		output.getUnderlyingItem().setAmount(count);
		
		player.getWorld().playSound(player.getLocation(), mixSound, 1, 1);
		
		if (!(player.getInventory().addItem(output.getItem())).isEmpty()) {
			player.sendMessage(ChatColor.RED + "There is no space left in your inventory");
			player.getWorld().dropItem(player.getEyeLocation(), output.getItem());
		}
	}
	
}
