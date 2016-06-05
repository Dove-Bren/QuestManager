package com.skyisland.questmanager.magic.spell.effect;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.skyisland.questmanager.configuration.utils.YamlWriter;
import com.skyisland.questmanager.magic.MagicUser;
import com.skyisland.questmanager.player.PlayerOptions;
import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.player.skill.event.MagicApplyEvent;

public class DamageUndeadEffect extends ImbuementEffect {
	
	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link aliases aliases}
	 */
	public static void registerWithAliases() {
		for (aliases alias : aliases.values()) {
			ConfigurationSerialization.registerClass(DamageUndeadEffect.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(DamageUndeadEffect.class);
	}
	

	private enum aliases {
		DEFAULT(DamageUndeadEffect.class.getName()),
		OLD("com.SkyIsland.QuestManager.Magic.Spell.Effect." + DamageUndeadEffect.class.getSimpleName()),
		LONGI("SpellDamageUndead"),
		LONG("DamageUndeadSpell"),
		SHORT("SDamageUndead");
		
		private String alias;
		
		aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
	}
	
	private static boolean isUndead(EntityType type) {
		switch (type) {
		case SKELETON:
		case ZOMBIE:
		case PIG_ZOMBIE:
			return true;
		default:
			return false;
		}
	}
	
	public static DamageUndeadEffect valueOf(Map<String, Object> map) {
		return new DamageUndeadEffect((double) map.get("damage"));
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		
		map.put("damage", damage);
		
		return map;
	}
	
	private double damage;
	
	public DamageUndeadEffect(double damage) {
		this.damage = damage;
	}
	
	@Override
	public void apply(Entity e, MagicUser cause) {
		if (e instanceof LivingEntity)
		if (isUndead(e.getType())) {
			
			double curDamage = damage;
			if (cause instanceof QuestPlayer) {
				QuestPlayer qp = (QuestPlayer) cause;
				MagicApplyEvent aEvent = new MagicApplyEvent(qp, curDamage);
				Bukkit.getPluginManager().callEvent(aEvent);
				
				curDamage = aEvent.getFinalDamage();
			}
					
			if (cause instanceof QuestPlayer) {
				QuestPlayer qp = (QuestPlayer) cause;
				if (qp.getOptions().getOption(PlayerOptions.Key.CHAT_COMBAT_DAMAGE)
					&& qp.getPlayer().isOnline()) {
					Player p = qp.getPlayer().getPlayer();
					
					String msg;
					String name = e.getCustomName();
					if (name == null) {
						name = YamlWriter.toStandardFormat(cause.getEntity().getType().toString());
					}
					msg = ChatColor.DARK_GRAY + "You damaged " + ChatColor.GRAY + name + ChatColor.DARK_GRAY 
							+ " for " + ChatColor.DARK_RED + "%.2f" + ChatColor.DARK_GRAY + " damage"
							+ ChatColor.RESET;
					
					p.sendMessage(String.format(msg, curDamage));
				}
			}
			
			LivingEntity targ = (LivingEntity) e;
			targ.damage(curDamage, cause.getEntity());
		}
	}
	
	@Override
	public void apply(Location loc, MagicUser cause) {
		//can't damage a location
		//do nothing 
		;
	}

	@Override
	public ImbuementEffect getCopyAtPotency(double potency) {
		DamageUndeadEffect effect = new DamageUndeadEffect(damage * potency);
		effect.setDisplayName(getDisplayName());
		return effect;		
	}
}
