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

package com.skyisland.questmanager.configuration;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.configuration.utils.LocationState;
import com.skyisland.questmanager.npc.NPC;
import com.skyisland.questmanager.npc.SimpleQuestStartNPC;
import com.skyisland.questmanager.player.Participant;
import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.quest.Goal;
import com.skyisland.questmanager.quest.Quest;
import com.skyisland.questmanager.quest.requirements.Requirement;
import com.skyisland.questmanager.ui.menu.message.Message;

/**
 * Wrapper for quest configuration
 *
 */
public class QuestConfiguration {
	
	public enum EndType {
		SAME,
		OTHERNPC,
		NOTURNIN;
	}
	
	public static final double CONFIG_VERSION = 1.00;
	
	private YamlConfiguration config;
	
	private Map<Integer, ConfigurationSection> goalCache;
	
	private int firstKey;
		
	public QuestConfiguration(YamlConfiguration config) throws InvalidConfigurationException {
		
		this.config = config;
		
		if (!config.contains("configversion")) {
			QuestManagerPlugin.logger.warning("Invalid quest "
					+ "configuration!");
			
			//just null out the config
			config = null;
			
			throw new InvalidConfigurationException();
		}
		
		//check config has all the fields we need, for safety
		checkConfig();
		
	}
	
	
	/**
	 * Checks the held configuration file for missing/corrupted/outdated fields, and corrects
	 * them when possible.
	 * This is an internal method with straight implementation details.
	 * @see QuestConfigurationField
	 */
	private void checkConfig() {
		
		if (config.getDouble("configversion", 0.0) - CONFIG_VERSION > .001) {
			String name = config.getString(QuestConfigurationField.NAME.getKey(), "NO NAME");
			QuestManagerPlugin.logger.warning("The quest [" + name + "] has an invalid version!\n"
					+ "QuestManager Configuration Version: " + CONFIG_VERSION + " doesn't match quest's: " 
					+ config.getDouble("configversion", 0.0));
			
		}
		
		//Check each field and put in defaults if they aren't there (niave approach)
		for (QuestConfigurationField field : QuestConfigurationField.values()) {
			if (!config.contains(field.getKey())) {
				QuestManagerPlugin.logger.warning("[" + getName() + "] "
						+ "Failed to find field information: " + field.name());
				QuestManagerPlugin.logger.info("Adding default value...");
				config.set(field.getKey(), field.getDefault());
			}
		}
	}
	
	/**
	 * Returns the stored quest name
	 * @return The name of the quest, or it's registered {@link QuestConfigurationField default}
	 */
	public String getName() {
		return config.getString(QuestConfigurationField.NAME.getKey(), (String) QuestConfigurationField.NAME.getDefault());
	}
	
	/**
	 * Gets the quest description
	 */
	public String getDescription() {
		return config.getString(QuestConfigurationField.DESCRIPTION.getKey(), (String) QuestConfigurationField.DESCRIPTION.getDefault());
	}
	
	/**
	 * Returns the end hint for this quest.
	 * This usually denotes which NPC to turn it into, in a slightly more custimized fashion than just subbing in the name
	 */
	public String getEndHint() {
		return config.getString(QuestConfigurationField.ENDHINT.getKey(), (String) QuestConfigurationField.ENDHINT.getDefault());
	}
	
	/**
	 * Gets whether or not the embedded quest has {@link Quest#keepState save-state} enabled
	 */
	public boolean getSaveState() {
		return config.getBoolean(QuestConfigurationField.SAVESTATE.getKey(), 
				(boolean) QuestConfigurationField.SAVESTATE.getDefault());
	}
	
	public boolean isRepeatable() {
		return config.getBoolean(QuestConfigurationField.REPEATABLE.getKey(), 
				(boolean) QuestConfigurationField.REPEATABLE.getDefault());
	}
	
	/**
	 * Is this quest a session quest? Session quests can only have one instantiation at a time,
	 * or one session at a time.
	 */
	public boolean isSession() {
		return config.getBoolean(QuestConfigurationField.SESSION.getKey(), 
				(boolean) QuestConfigurationField.SESSION.getDefault());
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getRequiredQuests() {
		if (!config.contains(QuestConfigurationField.PREREQS.getKey())) {
			return (List<String>) QuestConfigurationField.PREREQS.getDefault();
		}
		
		return config.getStringList(QuestConfigurationField.PREREQS.getKey());
				
	}
	
	public boolean getUseParty() {
		return config.getBoolean(
				QuestConfigurationField.USEPARTY.getKey(),
				(Boolean) QuestConfigurationField.USEPARTY.getDefault());
	}
	
	public boolean getRequireParty() {
		return config.getBoolean(
				QuestConfigurationField.REQUIREPARTY.getKey(),
				(Boolean) QuestConfigurationField.REQUIREPARTY.getDefault());
	}
	
	public Location getStartingLocation() {
		Object o = config.get(QuestConfigurationField.STARTLOC.getKey());
		if (o instanceof LocationState) {
			LocationState ls = (LocationState) o;
			return ls.getLocation();
		} else {
			return null;
		}
	}
	
	public Location getExitLocation() {
		Object o = config.get(QuestConfigurationField.EXIT.getKey());
		if (o instanceof LocationState) {
			LocationState ls = (LocationState) o;
			return ls.getLocation();
		} else {
			return null;
		}
	}
	
	public boolean getFailOnDeath() {
		return config.getBoolean(
				QuestConfigurationField.FAILONDEATH.getKey(),
				(Boolean) QuestConfigurationField.FAILONDEATH.getDefault());
	}
	
	public Collection<NPC> getAuxNPCs() {
		
		List<NPC> npcs = new LinkedList<>();
		
		//get list of NPCs and get them created
		if (config.contains(QuestConfigurationField.NPCS.getKey())) {
			ConfigurationSection npcSection = config.getConfigurationSection(
					QuestConfigurationField.NPCS.getKey());
			
			NPC npc;
			if (!(npcSection == null) && !npcSection.getKeys(false).isEmpty()) {
				for (String key : npcSection.getKeys(false)) {
					npc = (NPC) npcSection.get(key);
					npc.setQuestName(this.getName());
					npcs.add(npc);
					npc.listen();
				}
			}
		}
		
		return npcs;
	}
	
	/**
	 * Reads and instantiates a new starting npc for this quest.
	 * It's common practice to only call this method a single time, as you only need one copy
	 * of 'earl' who people talk to to give the quest.
	 * @return The new NPC instance
	 */
	public NPC GetStartingNPCInstance() {
		//load up starting NPC information
		SimpleQuestStartNPC startingNPC = null;
		if (!config.contains(QuestConfigurationField.START.getKey())) {
			QuestManagerPlugin.logger.info(
					  "Quest has no starting npc specified: " + getName());
		} else {
			startingNPC = (SimpleQuestStartNPC) config.get(QuestConfigurationField.START.getKey());
			startingNPC.listen();
			startingNPC.setQuestTemplate(this);
			startingNPC.setQuestName(getName());
			
			if (config.contains(QuestConfigurationField.END.getKey())) {
				
				if (config.getString(QuestConfigurationField.END.getKey() + ".type",
						(String) QuestConfigurationField.END.getDefault()).equals("same")) {
					
					Message msg = (Message) config.get(QuestConfigurationField.END.getKey() + ".value");
					
					if (msg == null) {
						QuestManagerPlugin.logger.info(
								  "Quest has no end action value specified: " + getName());
					} else {
						startingNPC.markAsEnd(msg);
					}
				} else {
					//it's an NPC they're specifying?
					
				}
			} else {
				QuestManagerPlugin.logger.info(
						  "Quest has no end action specified: " + getName());
			}
		}
		
		return startingNPC;
	}
	
	public EndType getEndType() {
		try {
			return EndType.valueOf((String) config.getString(QuestConfigurationField.END.getKey()
				+ ".type").toUpperCase());
		} catch (Exception e) {
			return EndType.SAME;
		}
	}
	
	/**
	 * Returns the complete {@link Quest Quest} this configuration represents.
	 * Subsequent calls to this method return new instances of the represented quest. It is
	 * up to the caller to keep track of returned quests and optimize performance when simply
	 * needing a reference to previously-instantiated Quests
	 * @return A new quest instance
	 * @throws InvalidConfigurationException
	 * @throws SessionConflictException
	 */
	public Quest instanceQuest(Participant participant) throws InvalidConfigurationException,
		SessionConflictException {
				
		if (!config.contains(QuestConfigurationField.GOALS.getKey())) {
			return null;
		}
		
		if (isSession())
		for (Quest q : QuestManagerPlugin.questManagerPlugin.getManager().getRunningQuests())
		if (q.getName().equals(getName())){
			//can't instantiate it, cause one's already going
			throw new SessionConflictException();
		}
		
		if (goalCache == null) {
			fetchGoalCache();
		}
			
		Quest quest = new Quest(this, participant);
		
		quest.setGoal(fetchFirstGoal(quest));
		
		//activate first goal
		quest.getCurrentGoal().getRequirements().forEach(Requirement::activate);
		
		//get fame and reward info
		quest.setFame(config.getInt(QuestConfigurationField.FAME.getKey()));
		quest.setTitleReward(config.getString(QuestConfigurationField.TITLEREWARD.getKey()));
		quest.setSpellReward(config.getString(QuestConfigurationField.SPELLREWARD.getKey()));
		quest.setMoneyReward(config.getInt(QuestConfigurationField.MONEYREWARD.getKey()));
		
		@SuppressWarnings("unchecked")
		List<ItemStack> rewards = (List<ItemStack>) config.getList(QuestConfigurationField.REWARDS.getKey());

		
		if (rewards != null && !rewards.isEmpty())
			rewards.forEach(quest::addItemReward);
		
		if (participant != null)
			for (QuestPlayer qp : participant.getParticipants()) {
				qp.addQuest(quest);
			}
		
		
		
		return quest;
	}
	
	/**
	 * Fetches (and instantiates) a goal by it's index. This key represents the unique identifier for the
	 * goal in the Quest. Unless the underlying quest is changed, this key is static per goal, and can be used
	 * between server restarts.
	 * <p>
	 * To get the first goal (regardless of it's index) in this quest, use {@link #fetchFirstGoal()} instead.
	 * </p>
	 * @param key
	 * @return
	 * @throws InvalidConfigurationException 
	 */
	public Goal fetchGoal(Quest hostQuest, int key) throws InvalidConfigurationException {
		if (goalCache == null)
			fetchGoalCache();
		if (!goalCache.containsKey(key))
			return null;
	
		return Goal.fromConfig(hostQuest, key, goalCache.get(key));
	}
	
	public Goal fetchFirstGoal(Quest hostQuest) throws InvalidConfigurationException {
		return fetchGoal(hostQuest, firstKey);
	}
	
	private void fetchGoalCache() {
		ConfigurationSection questSection = config.getConfigurationSection(
				QuestConfigurationField.GOALS.getKey());
		goalCache = new HashMap<>();
		boolean first = true;
		
		if (questSection == null)
			return;
		
		Integer mapKey;
		for (String key : questSection.getKeys(false)) {
			try {
				mapKey = Integer.parseInt(key);
			} catch (NumberFormatException e) {
				QuestManagerPlugin.logger.warning("Failed to parse integer key in quest: " + key);
				continue;
			}
			
			goalCache.put(mapKey, questSection.getConfigurationSection(key));
			if (first || firstKey > mapKey) {
				firstKey = mapKey;
				first = false;
			}
		}
	}
}
