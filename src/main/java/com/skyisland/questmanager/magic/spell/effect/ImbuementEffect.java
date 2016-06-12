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

/**
 * This effect can be used for imbuement.
 * Compatable effects are able to be scaled up or down on a whim -- as is expected for combining
 * effects with some generic scale. These effects are expected to scale up or down based on their given
 * potency -- A value where 1 signifies a normal, 100% effective effect. 
 * @author Skyler
 *
 */
public abstract class ImbuementEffect extends SpellEffect {

	private String displayName;
	
	/**
	 * Returns a 'copy' of this effect with parameters tweaked to be at <i>potency</i> potency.
	 * Values of potency are not bounded, but are defined to be 100% at 1.0. In other words, at 1.0, the
	 * effect returned should be exactly the same as the original effect.
	 */
	public abstract ImbuementEffect getCopyAtPotency(double potency);
	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public String getDisplayName() {
		return displayName;
	}
}
