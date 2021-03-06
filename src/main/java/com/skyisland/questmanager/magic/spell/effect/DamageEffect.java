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

package com.skyisland.questmanager.magic.spell.effect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.configuration.utils.YamlWriter;
import com.skyisland.questmanager.magic.MagicUser;
import com.skyisland.questmanager.npc.NPC;
import com.skyisland.questmanager.player.PlayerOptions;
import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.player.skill.event.MagicApplyEvent;

public class DamageEffect extends ImbuementEffect {
	
	public static final String DAMAGE_META_KEY = "QM_magic_damage";
	
	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link aliases aliases}
	 */
	public static void registerWithAliases() {
		for (aliases alias : aliases.values()) {
			ConfigurationSerialization.registerClass(DamageEffect.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(DamageEffect.class);
	}
	

	private enum aliases {
		DEFAULT(DamageEffect.class.getName()),
		OLD("com.SkyIsland.QuestManager.Magic.Spell.Effect." + DamageEffect.class.getSimpleName()),
		LONGI("SpellDamage"),
		LONG("DamageSpell"),
		SHORT("SDamage");
		
		private String alias;
		
		aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
	}
	
	public static DamageEffect valueOf(Map<String, Object> map) {
		return new DamageEffect((double) map.get("damage"));
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		
		map.put("damage", damage);
		
		return map;
	}
	
	private double damage;
	
	public DamageEffect(double damage) {
		this.damage = damage;
	}
	
	@Override
	public void apply(Entity e, MagicUser cause) {
		if (e instanceof LivingEntity) {
			double curDamage = damage;
			if (cause instanceof QuestPlayer) {
				QuestPlayer qp = (QuestPlayer) cause;
				MagicApplyEvent aEvent = new MagicApplyEvent(qp, curDamage);
				Bukkit.getPluginManager().callEvent(aEvent);
				
				curDamage = aEvent.getFinalDamage();
			}			
			
			LivingEntity targ = (LivingEntity) e;
			targ.setMetadata(DAMAGE_META_KEY, new FixedMetadataValue
					(QuestManagerPlugin.questManagerPlugin, true));
			
			boolean invi = false;
			double snapshot = targ.getHealth();
			if (!(targ instanceof Player && (((Player) targ).getGameMode() == GameMode.CREATIVE || ((Player) targ).getGameMode() == GameMode.SPECTATOR))) {
				List<MetadataValue> meta = targ.getMetadata(NPC.NPC_META_KEY);
				
				if (meta == null || meta.isEmpty() || !meta.get(0).asBoolean()) {
					targ.setInvulnerable(false);
					invi = true;
				}
			}
			
			targ.damage(curDamage, cause.getEntity());
//			targ.damage(0.0, cause.getEntity());
//			targ.setHealth(Math.max(0.0, Math.min(targ.getMaxHealth(), targ.getHealth() - curDamage)));
			
			//make sure they didn't invincible their way out damage
			if (invi && Math.abs(snapshot - targ.getHealth()) < .0001) {
				//targ.setHealth(targ.getHealth() - curDamage);
				targ.setHealth(Math.max(0.0, Math.min(targ.getMaxHealth(), targ.getHealth() - curDamage)));
			}
			targ.setMetadata(DAMAGE_META_KEY, new FixedMetadataValue
					(QuestManagerPlugin.questManagerPlugin, false));
			
			if (cause instanceof QuestPlayer) {
				QuestPlayer qp = (QuestPlayer) cause;
				if (qp.getOptions().getOption(PlayerOptions.Key.CHAT_COMBAT_DAMAGE)
					&& qp.getPlayer().isOnline()) {
					Player p = qp.getPlayer().getPlayer();
					
					String msg;
					String name = e.getCustomName();
					
					if (e instanceof Player)
						name = ((Player) e).getName();
					
					if (name == null) {
						name = YamlWriter.toStandardFormat(cause.getEntity().getType().toString());
					}
					msg = ChatColor.DARK_GRAY + "You damaged " + ChatColor.GRAY + name + ChatColor.DARK_GRAY 
							+ " for " + ChatColor.DARK_RED + "%.2f" + ChatColor.DARK_GRAY + " damage"
							+ ChatColor.RESET;
					
					p.sendMessage(String.format(msg, curDamage));
				}
			}
			
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
		DamageEffect effect = new DamageEffect(damage * potency);
		effect.setDisplayName(getDisplayName());
		return effect;		
	}
	
	public double getDamage() {
		return damage;
	}
	
	public void setDamage(double damage) {
		this.damage = damage;
	}
}
