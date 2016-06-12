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

package com.skyisland.questmanager.player.skill.event;

import com.skyisland.questmanager.magic.spell.effect.SpellEffect;
import com.skyisland.questmanager.player.QuestPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Thrown when a {@link QuestPlayer QuestPlayer}'s spell
 * is applying a {@link SpellEffect SpellEffect}
 *
 */
public class MagicApplyEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
		
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	private QuestPlayer player;
	
	/**
	 * The base amount of damage the player is going to do
	 */
	private double damage;
	
	/**
	 * Damage modification from skills
	 */
	private double modifiedDamage;
	
	/**
	 * Multiplicitive damage modification from skills
	 */
	private double efficiency;
	
	public MagicApplyEvent(QuestPlayer player, double damage) {
		this.player = player;
		this.damage = damage;
		this.modifiedDamage = 0.0;
		this.efficiency = 1.0;}
	
	public QuestPlayer getPlayer() {
		return player;
	}

	public void setPlayer(QuestPlayer player) {
		this.player = player;
	}

	/**
	 * Gets the base amount of damage being done. If this were healing, it would be negative.
	 */
	public double getDamage() {
		return damage;
	}

	/**
	 * Sets the base amount of damage being done.
	 * This is not intended to be changed for simple 'bonus damage' adjustments. For that, see
	 * {@link #setModifiedDamage(double)}
	 */
	public void setDamage(double damage) {
		this.damage = damage;
	}

	/**
	 * Returns the current amount of bonus damage being dealt to the target. This includes negative amounts
	 * for damage penalties.
	 */
	public double getModifiedDamage() {
		return modifiedDamage;
	}

	/**
	 * Sets the damage modifier for the event. This includes bonuses and penalties to damage
	 */
	public void setModifiedDamage(double modifiedDamage) {
		this.modifiedDamage = modifiedDamage;
	}

	/**
	 * Gets the current efficiency of the event.
	 * This is a multiplicitive bonus/penalty done after modifications
	 */
	public double getEfficiency() {
		return efficiency;
	}

	/**
	 * Sets the efficiency the event will be executed at.
	 * This is the final modification done, and is multiplicitive (e.g. efficiency 1 does 100% of damage)
	 */
	public void setEfficiency(double efficiency) {
		this.efficiency = efficiency;
	}
	
	/**
	 * Performs all calculations with given parameters to determine the final damage dealt.
	 * This calculation also does not consider whether or not the attack missed.
	 * @return the damage that would be dealt.
	 */
	public double getFinalDamage() {
		double calc = damage + modifiedDamage;
		calc *= efficiency;
		
		return calc;
	}
}
