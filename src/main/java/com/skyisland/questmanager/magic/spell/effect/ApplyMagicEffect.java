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
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Entity;

import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.fanciful.FancyMessage;
import com.skyisland.questmanager.magic.MagicUser;
import com.skyisland.questmanager.magic.spell.status.MagicStatusEffect;
import com.skyisland.questmanager.player.PlayerOptions;
import com.skyisland.questmanager.player.QuestPlayer;

/**
 * Apply a Magic Status Effect
 */
public class ApplyMagicEffect extends ImbuementEffect {
	
	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link aliases aliases}
	 */
	public static void registerWithAliases() {
		for (aliases alias : aliases.values()) {
			ConfigurationSerialization.registerClass(ApplyMagicEffect.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(ApplyMagicEffect.class);
	}
	

	private enum aliases {
		DEFAULT(ApplyMagicEffect.class.getName()),
		OLD("com.SkyIsland.QuestManager.Magic.Spell.Effect." + ApplyMagicEffect.class.getSimpleName()),
		LONGI("SpellMagicStatus"),
		LONG("MagicStatusSpell"),
		SHORT("SMagicStatus");
		
		private String alias;
		
		aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
	}
	
	public static ApplyMagicEffect valueOf(Map<String, Object> map) {
		return new ApplyMagicEffect((MagicStatusEffect) map.get("effect"));
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		
		map.put("effect", effect);
		
		return map;
	}
	
	private MagicStatusEffect effect;
	
	public ApplyMagicEffect(MagicStatusEffect effect) {
		this.effect = effect;
	}
	
	@Override
	public void apply(Entity e, MagicUser cause) {
		QuestPlayer target = null;
		for (QuestPlayer qp : QuestManagerPlugin.questManagerPlugin.getPlayerManager().getPlayers()) {
			if (qp.getPlayer().getUniqueId().equals(e.getUniqueId())) {
				target = qp;
				break;
			}
		}
		
		if (target == null)
			return;
		
		effect.apply(target);
		
		if (target.getOptions().getOption(PlayerOptions.Key.CHAT_COMBAT_DAMAGE)) {
			
			String msg;
			if (cause instanceof QuestPlayer && ((QuestPlayer) cause).getPlayer().getUniqueId()
					.equals(target.getPlayer().getUniqueId())) {
				//healed self
				msg = ChatColor.DARK_GRAY + "You gained the effect ";
			} else {
				String name = cause.getEntity().getCustomName();
				if (name == null) {
					name = cause.getEntity().getType().toString();
				}
				msg = ChatColor.GRAY + cause.getEntity().getCustomName() + ChatColor.DARK_GRAY 
						+ " gave you the effect ";
			}
			
			String name = effect.getName();
			
			FancyMessage message = new FancyMessage(msg);
			message.then(name)
				.color(ChatColor.DARK_PURPLE)
				.tooltip(effect.getDescription());

			message.send(target.getPlayer().getPlayer());
			
		}
	}
	
	@Override
	public void apply(Location loc, MagicUser cause) {
		//can't damage a location
		//do nothing 
		;
	}
	
	public MagicStatusEffect getEffect() {
		return effect;
	}

	@Override
	public ImbuementEffect getCopyAtPotency(double potency) {
		return new ApplyMagicEffect(effect.copyAtPotential(potency));	
	}
}
