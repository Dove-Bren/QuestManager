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

package com.skyisland.questmanager.magic.spell;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.effects.AuraEffect;
import com.skyisland.questmanager.magic.MagicUser;
import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.player.skill.event.MagicCastEvent;
import com.skyisland.questmanager.player.skill.event.MagicCastEvent.MagicType;
import com.skyisland.questmanager.scheduling.Alarm;
import com.skyisland.questmanager.scheduling.Alarmable;

/**
 * A spell that must charge for a while, and then releases a spell
 *
 */
public class ChargeSpell extends SimpleSelfSpell implements Listener {
	
	public static final String DISTURBED_MESSAGE = ChatColor.RED + "Your charging spell was disturbed" + ChatColor.RESET;
	
	public static final String CANCEL_MESSAGE = ChatColor.YELLOW + "You cancelled your charging spell" + ChatColor.RESET;
	
	private static final Effect DEFAULT_EFFECT = Effect.WITCH_MAGIC;
	
	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link aliases aliases}
	 */
	public static void registerWithAliases() {
		for (aliases alias : aliases.values()) {
			ConfigurationSerialization.registerClass(ChargeSpell.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(ChargeSpell.class);
	}
	

	private enum aliases {
		DEFAULT(ChargeSpell.class.getName()),
		LONG("ChargeSpell"),
		SHORT("SCharge");
		
		private String alias;
		
		aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
	}
	
	private static class Reminder implements Alarmable<Integer> {
		
		private MagicUser user;
		
		private ChargeSpell spell;
		
		public Reminder(ChargeSpell spell, MagicUser user, double duration) {
			Alarm.getScheduler().schedule(this, 0, duration);
			this.spell = spell;
			this.user = user;
		}

		@Override
		public void alarm(Integer reference) {
			//tell charger
			spell.finishCharge(user);
		}
		
		public void stop() {
			Alarm.getScheduler().unregister(this);
		}
		
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Reminder)) {
				return false;
			}
			
			return user.getEntity().getUniqueId().equals(
					((Reminder) o).user.getEntity().getUniqueId());
					
		}
	}
	
	private boolean canMove;
	
	private boolean canGetHit;
	
	private Spell heldSpell;
	
	private double castingTime;
	
	private Map<MagicUser, AuraEffect> effects;
	
	private Map<MagicUser, Reminder> reminders;
	
	public ChargeSpell(int cost, int difficulty, String name, String description,
			Spell futureSpell, boolean canMove, boolean canGetHit, double castingTime) {
		super(cost, difficulty, name, description);
		this.heldSpell = futureSpell;
		this.canMove = canMove;
		this.canGetHit = canGetHit;
		this.castingTime = castingTime;
		this.effects = new HashMap<>();
		this.reminders = new HashMap<>();
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		
		map.put("cost", getCost());
		map.put("name", getName());
		map.put("description", getDescription());
		
		map.put("effects", getSpellEffects());
		
		if (castEffect != null) {
			map.put("casteffect", castEffect.name());
		}
		if (castSound != null) {
			map.put("castsound", castSound.name());
		}
		
		map.put("canMove", canMove);
		map.put("canGetHit", canGetHit);
		map.put("spell", heldSpell);
		map.put("castingTime", castingTime);

		return map;
	}
	
	public static ChargeSpell valueOf(Map<String, Object> map) {

		if (!map.containsKey("cost") || !map.containsKey("name") || !map.containsKey("description")
				|| !map.containsKey("difficulty") || !map.containsKey("castingTime")
				|| !map.containsKey("canMove") || !map.containsKey("canGetHit") || !map.containsKey("spell")) {
			QuestManagerPlugin.logger.warning(
					"Unable to load spell " 
						+ (map.containsKey("name") ? (String) map.get("name") : "")
						+ ": Missing some keys!"
					);
			return null;
		}
		
		ChargeSpell spell = new ChargeSpell(
				(int) map.get("cost"),
				(int) map.get("difficulty"),
				(String) map.get("name"),
				(String) map.get("description"),
				(Spell) map.get("spell"),
				(boolean) map.get("canMove"),
				(boolean) map.get("canGetHit"),
				(double) map.get("castingTime")
				);
		
		if (map.containsKey("casteffect")) {
			spell.setCastEffect(Effect.valueOf((String) map.get("casteffect")));
		}
		if (map.containsKey("castsound")) {
			spell.setCastSound(Sound.valueOf((String) map.get("castsound")));
		}
		
		return spell;
	}
	
	@Override
	public void cast(MagicUser caster) {
		//start them a' charging
		
		if (caster instanceof QuestPlayer) {
			//do nothing with info, just let everything know it's happening (e.g. cancel other charges)
			MagicCastEvent e = new MagicCastEvent((QuestPlayer) caster, MagicType.UTILITY, this);
			Bukkit.getPluginManager().callEvent(e);
		}
		
		if (effects.isEmpty()) {
			startListening();
		}

		effects.put(caster, new AuraEffect(castEffect == null ? DEFAULT_EFFECT : castEffect));
		effects.get(caster).play(caster.getEntity());
		reminders.put(caster, new Reminder(this, caster, castingTime));
		
		caster.getEntity().getWorld().playSound(caster.getEntity().getLocation(), castSound, 1, 1);
		
	}
	
	private void startListening() {
		Bukkit.getPluginManager().registerEvents(this, QuestManagerPlugin.questManagerPlugin);
	}
	
	private void stopListening() {
		HandlerList.unregisterAll(this);
	}
	
	private void doneCasting(MagicUser caster) {
		if (effects.containsKey(caster)) {
			effects.get(caster).stop();
			effects.remove(caster);
			reminders.get(caster).stop();
			reminders.remove(caster);
		}
		
		if (effects.isEmpty()) {
			//now we're empty, so stop listening
			stopListening();
		}
	}

	@EventHandler
	public void onEntityMove(PlayerMoveEvent e) {
		if (canMove) {
			return;
		}
		
		if (!QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getWorlds()
				.contains(e.getPlayer().getWorld().getName())) {
			return;
		}
		QuestPlayer qp = QuestManagerPlugin.questManagerPlugin.getPlayerManager().getPlayer(e.getPlayer());
		
		if (!effects.containsKey(qp)) {
			return;
		}
		
		if (e.getTo().toVector().equals(e.getPlayer().getLocation().toVector())) {
			return; //just turned head?
		}
		
		
		//a current charge has moved, and is not allowed to
		e.getPlayer().sendMessage(DISTURBED_MESSAGE);
		doneCasting(qp);
	}
	
	@EventHandler
	public void onEntityHurt(EntityDamageEvent e) {
		if (canGetHit) {
			return;
		}
		
		if (!(e.getEntity() instanceof Player)) {
			return;
		}
		
		if (!QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getWorlds()
				.contains(e.getEntity().getWorld().getName())) {
			return;
		}
		QuestPlayer qp = QuestManagerPlugin.questManagerPlugin.getPlayerManager().getPlayer((Player) e.getEntity());
		
		if (!effects.containsKey(qp)) {
			return;
		}
		
		//a current charge has moved, and is not allowed to
		((Player) e.getEntity()).sendMessage(DISTURBED_MESSAGE);
		doneCasting(qp);
	}
	
	public void onMagicCast(MagicCastEvent e) {
		if (effects.containsKey(e.getPlayer())) {
			//casting another spell! how dare they!
			e.getPlayer().getPlayer().getPlayer().sendMessage(CANCEL_MESSAGE);
			doneCasting(e.getPlayer());
		}
	}
	
	protected void finishCharge(MagicUser caster) {
		doneCasting(caster);
		heldSpell.cast(caster);
	}
	
	public Spell getHeldSpell() {
		return this.heldSpell;
	}
}
