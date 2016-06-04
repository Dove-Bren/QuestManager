package com.SkyIsland.QuestManager.Configuration.State;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import com.SkyIsland.QuestManager.QuestManagerPlugin;
import com.SkyIsland.QuestManager.Player.Participant;
import com.SkyIsland.QuestManager.Quest.History.History;

/**
 * Wrapper for state info config
 * @author Skyler
 *
 */
public class QuestState {
	
	private String name;
	
	private int goalIndex;
	
	private GoalState goalState;
	
	private Participant participant;
	
	private History history;
	
	public QuestState() {
		this.name = "";
		this.goalState = null;
	}


	public void load(YamlConfiguration config) throws InvalidConfigurationException {
		
		if (!config.contains("saveTime") || !config.contains("participants") || !config.contains("name") 
				|| !config.contains("goalstate") || !config.contains("goalstate")) {
			throw new InvalidConfigurationException("Some keys were missing in a quest state! "
					+ (config.contains("name") ? config.getString("name") : ""));
		}
		
		this.name = config.getString("name");
		
		this.goalIndex = config.getInt("goalindex");
		
		this.goalState =  new GoalState();
		this.goalState.load(config.getConfigurationSection("goalstate"));

		history = null;
		if (config.contains("history")) {
			history = History.fromConfig(config.getConfigurationSection("history"));
		}
		if (history == null) {
			history = new History();
		}
		
		this.participant = (Participant) QuestManagerPlugin.questManagerPlugin.getPlayerManager()
				.getParticipant(config.getString("participants"));
		
	}
	
	public void save(File file) throws IOException {
		YamlConfiguration config = new YamlConfiguration();
		
		config.set("saveTime", (new Date()).getTime());
		
		config.set("name", name);
		
		config.set("goalindex", goalIndex);
		
		config.set("goalstate", goalState.asConfig());
		
		//config.set("goals", goalList);
		config.set("participants", participant.getIDString());
		
		if (history != null) {
			config.set("history", history.toConfig());
		}
		
		config.save(file);
	}
	
	public Participant getParticipant() {
		return this.participant;
	}
	
	public void setParticipant(Participant participant) {
		this.participant = participant;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the goalState
	 */
	public GoalState getGoalState() {
		return goalState;
	}
	
	public void setGoalState(GoalState goalState) {
		this.goalState = goalState;
	}
	
	public int getGoalIndex() {
		return goalIndex;
	}
	
	public void setGoalIndex(int index) {
		this.goalIndex = index;
	}
	
	public History getHistory() {
		return history;
	}
	
	public void setHistory(History history) {
		this.history = history;
	}
	
}
