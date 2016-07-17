package com.skyisland.questmanager.magic.spell.status;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.magic.MagicUser;
import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.scheduling.Tickable;

import io.puharesource.mc.titlemanager.api.ActionbarTitleObject;

/**
 * Status effect that takes a portion of damage and applies it to the damager instead of the target.
 * @author Skyler
 *
 */
public class ReflectEffect implements MagicStatusEffect, Listener, Tickable {
	
	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link aliases aliases}
	 */
	public static void registerWithAliases() {
		for (aliases alias : aliases.values()) {
			ConfigurationSerialization.registerClass(ReflectEffect.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(ReflectEffect.class);
	}
	

	private enum aliases {
		DEFAULT(ReflectEffect.class.getName()),
		LONGI("ReflectEffect");
		
		private String alias;
		
		aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
	}
	
	private static class UserRecord {
		
		private MagicUser user;
		
		private long ticksLeft;
		
		public UserRecord(MagicUser user, long duration) {
			this.user = user;
			this.ticksLeft = duration;
		}
		
		@Override
		public boolean equals(Object o) {
			if (o instanceof UserRecord)
				return ((UserRecord) o).user.equals(user);
			
			return false;
		}
		
		@Override
		public int hashCode() {
			return 61231
					+ user.hashCode();
		}
		
	}
	
	private List<UserRecord> victims;
	
	private long duration;
	
	private double damage;
	
	private boolean percentage;
	
	/**
	 * Creates a reflection effect that reflects the given amount of damage. The effect lasts for the specified duration.
	 * The damage reflected is not a percentage, but a flat amount.
	 * @param damage
	 * @param duration
	 * @see #ReflectEffect(double, boolean, long)
	 */
	public ReflectEffect(double damage, long duration) {
		this(damage, false, duration);
	}
	
	/**
	 * Creates a reflection effect that reflects the specified damage.
	 * @param damage The damage to reflect
	 * @param isPercentage Whether <i>damage</i> is a percentage rather than a flat amount (.20 being 20%)
	 * @param duration How long the effect lasts, in ticks
	 */
	public ReflectEffect(double damage, boolean isPercentage, long duration) {
		this.damage = damage;
		this.duration = duration;
		this.percentage = isPercentage;
		
		victims = new LinkedList<>();
		
		MagicStatusEffect.statusTicker.register(this);
		Bukkit.getPluginManager().registerEvents(this, QuestManagerPlugin.questManagerPlugin);
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		
		map.put("damage", damage);
		map.put("isPercentage", percentage);
		map.put("duration", duration);
		
		return map;
	}
	
	public static ReflectEffect valueOf(Map<String, Object> map) {
		return new ReflectEffect(
				(Double) map.get("damage"),
				(Boolean) map.get("isPercentage"),
				(Integer) map.get("duration")
				);
	}
	
	@Override
	public boolean tick() {
		if (victims.isEmpty())
			return false;
		Iterator<UserRecord> it = victims.iterator();
		UserRecord record;
		while (it.hasNext()) {
			record = it.next();
			record.ticksLeft -= statusTicker.getDelay();
			if (record.ticksLeft < 0) {
				it.remove();
				if (record.user instanceof QuestPlayer)
				if (((QuestPlayer) record.user).getPlayer().isOnline())
					alertPlayer(((QuestPlayer) record.user).getPlayer().getPlayer());
			}
		}
		
		return false;
	}

	@Override
	public String getName() {
		return "Reflect";
	}

	@Override
	public String getDescription() {
		return "Reflects " + String.format("%.2f", damage) + (percentage ? "%" : "") + " damage back onto attacker";
	}

	@Override
	public void apply(MagicUser target) {
		victims.add(new UserRecord(target, duration));
	}

	@Override
	public void cancel() {
		victims.clear();
	}
	
	private void alertPlayer(Player p) {
		new ActionbarTitleObject(ChatColor.DARK_GRAY + "Reflect faded" + ChatColor.RESET).send(p);
	}

	@Override
	public void cancel(MagicUser target) {
		if (victims.isEmpty())
			return;
		
		Iterator<UserRecord> it = victims.iterator();
		while (it.hasNext()) {
			if (it.next().user.equals(target)) {
				it.remove();
				return;
			}
		}
	}
	
	@EventHandler
	public void onUserDamaged(EntityDamageByEntityEvent e) {
		if (victims.isEmpty())
			return;
		
		if (e.getCause() == DamageCause.CUSTOM)
			return;
		
		if (e.getEntity() instanceof LivingEntity)
			for (UserRecord record : victims)
			if (record.user.getEntity() != null && record.user.getEntity().getUniqueId().equals(e.getEntity().getUniqueId())) {
				//activate
				double amount;
				if (percentage)
					amount = damage * e.getFinalDamage();
				else
					amount = damage;
				
				e.setDamage(e.getDamage() - amount);
				((LivingEntity) e.getDamager()).damage(amount, e.getEntity());
				
				return;
			}
	}

	@Override
	public ReflectEffect copyAtPotential(double potential) {
		return new ReflectEffect(damage, percentage, Math.round(duration * potential));
	}
}
