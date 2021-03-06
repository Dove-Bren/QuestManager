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

package com.skyisland.questmanager.magic;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import com.skyisland.questmanager.configuration.utils.YamlWriter;
import com.skyisland.questmanager.effects.AuraEffect;
import com.skyisland.questmanager.scheduling.Alarm;
import com.skyisland.questmanager.scheduling.Alarmable;

/**
 * A designated rune for a rune caster spell.
 *
 */
public class SpellPylon implements Alarmable<Integer> {
	
	private static final String HEAD_YAML =
"skull:\n" 
+"  ==: org.bukkit.inventory.ItemStack\n" 
+"  type: SKULL_ITEM\n"
+"  damage: 3\n"
+"  meta:\n"
+"    ==: ItemMeta\n"
+"    meta-type: SKULL\n"
+"    display-name: Antique clock\n"
+"    internal: H4sIAAAAAAAAAE2Oy06DQBiFf01MkPgYbkmG2xAWLhqDdkgHhI7cdkAHy3SmNhRM4al8RFm6Oyff+ZKjA+jwtD9NUn4M310vuQb35ADPTlf7DjJrA5t2azitx426dVwDd42PTe5z7Lk66Kt04cPY8+sjaCO/jdPArzoA3GnwkNVy4vDL5xBVxREdilC2M8FrZ3skYyIuHjlnc/NKMFEr327wbvb/bd2xzl1Z2uGxOidTozK0s1PJt6nZqs+fOE+W0iqXaKFLzFIVCWpRlszRe4kiUUkqghtVb6JkXyjOg4UyskR5qkoR2JGVniir+jWbVGWSso1N+9DvCvSyvoc/RhRuhRgBAAA=";
	
	private static double movementsPerCycle = 50;
	
	private static double cycleDuration = 3.0;
	
	private String type;
	
	private Location location;
	
	private ArmorStand entity;
	
	private AuraEffect effect;
	
	/**
	 * Creates a spell pylon with the given type at the given location. Also sets the icon to
	 * that which is provided. If nothing is provided for icon (icon == null), a generic clock head
	 * is used instead (whcih still looks kinda cool)
	 * @param l Center location for the pylon
	 */
	public SpellPylon(String type, ItemStack icon, Location l) {
		this.type = type;
		this.location = l;
		
		entity = (ArmorStand) l.getWorld().spawnEntity(l, EntityType.ARMOR_STAND);
		
		entity.setVisible(false);
		entity.setAI(false);
		entity.setCustomName(ChatColor.DARK_BLUE + YamlWriter.toStandardFormat(type) + " Rune");
		entity.setCustomNameVisible(true);
		entity.setGravity(false);
		entity.setRemoveWhenFarAway(false);
		entity.setSmall(true);
		
		if (icon == null) {
			YamlConfiguration yc = new YamlConfiguration();
			try {
				yc.loadFromString(HEAD_YAML);
				icon = yc.getItemStack("skull");
			} catch (InvalidConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				icon = new ItemStack(Material.SKULL_ITEM);
			}
		}
		
		
		
		entity.setHelmet(icon);
		entity.setHeadPose(new EulerAngle((1.5 * Math.PI), 0, 0));
		
		effect = new AuraEffect(Effect.FLYING_GLYPH, 2);
		effect.play(entity);
		
		Alarm.getScheduler().schedule(this, 0, cycleDuration / movementsPerCycle);
	}
	
	/**
	 * Cleans up entities in the world used to identify this pylon
	 */
	public void remove() {
		entity.remove();
		effect.stop();
		Alarm.getScheduler().unregister(this);
	}

	@Override
	public void alarm(Integer reference) {
		//follow sin curve for y position.
		double periodStep = (Math.PI * 2) / movementsPerCycle;
		double offset = .4 * Math.sin(reference * periodStep);
		entity.teleport(location.clone().add(0, offset, 0));
		
		Alarm.getScheduler().schedule(this, reference + 1, cycleDuration / movementsPerCycle); 
	}
	
	public String getType() {
		return this.type;
	}
	
	public Location getLocation() {
		return location;
	}
}
