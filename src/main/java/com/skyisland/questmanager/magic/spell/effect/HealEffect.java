package com.skyisland.questmanager.magic.spell.effect;

import java.util.HashMap;
import java.util.Map;

import com.skyisland.questmanager.configuration.utils.YamlWriter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.magic.MagicUser;
import com.skyisland.questmanager.player.PlayerOptions;
import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.player.skill.event.MagicApplyEvent;

public class HealEffect extends ImbuementEffect {
	
	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link aliases aliases}
	 */
	public static void registerWithAliases() {
		for (aliases alias : aliases.values()) {
			ConfigurationSerialization.registerClass(HealEffect.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(HealEffect.class);
	}
	

	private enum aliases {
		DEFAULT(HealEffect.class.getName()),
		LONGI("SpellHeal"),
		LONG("HealSpell"),
		SHORT("SHeal");
		
		private String alias;
		
		private aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
	}
	
	public static HealEffect valueOf(Map<String, Object> map) {
		return new HealEffect((double) map.get("amount"));
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("amount", amount);
		
		return map;
	}
	
	private double amount;
	
	public HealEffect(double amount) {
		this.amount = amount;
	}
	
	@Override
	public void apply(Entity target, MagicUser cause) {
		if (target instanceof LivingEntity) {
			LivingEntity e = (LivingEntity) target;
			
			double curAmount = amount; //Potency update
			if (cause instanceof QuestPlayer) {
				QuestPlayer qp = (QuestPlayer) cause;
				MagicApplyEvent aEvent = new MagicApplyEvent(qp, -curAmount); //negative cause healing
				Bukkit.getPluginManager().callEvent(aEvent);
				
				curAmount = -aEvent.getFinalDamage(); //negative cause healing
			}
			
			EntityRegainHealthEvent event = new EntityRegainHealthEvent(e, curAmount, RegainReason.MAGIC);
			Bukkit.getPluginManager().callEvent(event);
			
			if (event.isCancelled()) {
				return;
			}
			
			if (target instanceof Player) {
				Player p = (Player) target;
				if (QuestManagerPlugin.questManagerPlugin.getPlayerManager().getPlayer(p)
						.getOptions().getOption(PlayerOptions.Key.CHAT_COMBAT_DAMAGE)) {
					
					String msg;
					if (cause instanceof QuestPlayer && ((QuestPlayer) cause).getPlayer().getUniqueId()
							.equals(target.getUniqueId())) {
						//healed self
						msg = ChatColor.DARK_GRAY + "You were healed for " + ChatColor.GREEN + "%.2f"
								+ ChatColor.DARK_GRAY + " damage" + ChatColor.RESET;
					} else {
						String name = cause.getEntity().getCustomName();
						if (name == null) {
							name = YamlWriter.toStandardFormat(cause.getEntity().getType().toString());
						}
						msg = ChatColor.GRAY + name + ChatColor.DARK_GRAY 
								+ " healed you for " + ChatColor.GREEN + "%.2f" + ChatColor.DARK_GRAY
								+ " damage" + ChatColor.RESET;
					}
					
					p.sendMessage(String.format(msg, curAmount));
				
				}
				
			}
			
			if (cause instanceof QuestPlayer) {
				QuestPlayer qp = (QuestPlayer) cause;
				if (qp.getOptions().getOption(PlayerOptions.Key.CHAT_COMBAT_DAMAGE)
					&& qp.getPlayer().isOnline() && !qp.getPlayer().getUniqueId()
					.equals(target.getUniqueId())) {
					Player p = qp.getPlayer().getPlayer();
					
					String msg;
					String name = e.getCustomName();
					if (name == null) {
						name = YamlWriter.toStandardFormat(cause.getEntity().getType().toString());
					}
					msg = ChatColor.DARK_GRAY + "You healed " + ChatColor.GRAY + name + ChatColor.DARK_GRAY 
							+ " for " + ChatColor.GREEN + "%.2f" + ChatColor.DARK_GRAY + " damage"
							+ ChatColor.RESET;
					
					p.sendMessage(String.format(msg, curAmount));
				}
			}
			
			e.setHealth(Math.min(e.getMaxHealth(), 
			e.getHealth() + curAmount));
		}
	}
	
	@Override
	public void apply(Location loc, MagicUser cause) {
		//can't damage a location
		//do nothing 
		;
	}
	
	@Override
	public HealEffect getCopyAtPotency(double potency) {
		HealEffect effect = new HealEffect(amount * potency);
		effect.setDisplayName(getDisplayName());
		return effect;
	}
	
}
