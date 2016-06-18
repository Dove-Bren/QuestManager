package com.skyisland.questmanager.quest;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import com.skyisland.questmanager.QuestManagerPlugin;
import com.skyisland.questmanager.configuration.state.GoalState;
import com.skyisland.questmanager.configuration.state.RequirementState;
import com.skyisland.questmanager.configuration.state.StatekeepingRequirement;
import com.skyisland.questmanager.fanciful.FancyMessage;
import com.skyisland.questmanager.player.utils.CompassTrackable;
import com.skyisland.questmanager.quest.requirements.Requirement;

/**
 * Tracks objectives in a quest.
 * Goals have specific requirements that must be met before they are considered clear.
 * @author Skyler
 *
 */
public class Junction extends Goal {
	
	public static class Path {
		
		private List<Requirement> requirements;
		
		private Integer resultIndex;
		
		public Path(List<Requirement> requirements, Integer resultIndex) {
			this.requirements = requirements;
			this.resultIndex = resultIndex;
		}
		
		public List<Requirement> getRequirements() {
			return requirements;
		}
		
		public Integer getResultIndex() {
			return resultIndex;
		}
		
		public boolean isComplete() {
			for (Requirement req : requirements) {
				if (req.isCompleted() == false) {
					return false;
				}
			}
			
			return true;
		}
	}
	
	private List<Path> paths;
	
	/**
	 * Creates a goal from the provided goal configuration
	 * @throws InvalidConfigurationException 
	 */
	public static Junction fromConfig(Quest quest, int index, ConfigurationSection config) throws InvalidConfigurationException {
		/* goal construction configuration involves:
		 * Goal name, description
		 * A list of requirements mapped to a result index.
		 * 
		 * The req's are in a list, with each element being a con section with the
		 * key being the type of req and the value being the config section for setting
		 * up the req
		 */
		
		if (!config.contains("type") || !config.getString("type").equals("juncnf")) {
			throw new InvalidConfigurationException();
		}
		
		String name, description;
		
		name = config.getString("name");
		description = config.getString("description");
		
		Junction goal = new Junction(quest, index, name, description);
		
		//List<Path> paths = new LinkedList<>();
		ConfigurationSection psex = config.getConfigurationSection("paths");
		
		
		for (String key : psex.getKeys(false)) {
			//each key is junk. But the section after is a list of paths
			ConfigurationSection section = psex.getConfigurationSection(key);
				
			List<Requirement> requirements = new LinkedList<>();
			List<ConfigurationSection> reqs = new LinkedList<>();
			for (String requirementKey : section.getConfigurationSection("requirements").getKeys(false)) {
				reqs.add( section.getConfigurationSection("requirements." + requirementKey));
			}
			
			for (ConfigurationSection req : reqs) {
				String type = req.getKeys(false).iterator().next();
				
				ConfigurationSection conf = req.getConfigurationSection(type);
				
				
				Requirement r = QuestManagerPlugin.questManagerPlugin.getRequirementManager()
						.instanceRequirement(type, goal, conf);
				
				if (r == null) {
					QuestManagerPlugin.questManagerPlugin.getLogger()
						.warning("    Invalid requirement type for goal: " + goal.getName());
				}
				
				requirements.add(r);
			}
			Integer next = (section.contains("resultIndex") ? section.getInt("resultIndex") : null);
			
			Path p = new Path(requirements, next);
			//paths.add(p);
			goal.addPath(p);
		}

		return goal;
		
	}
	
	public Junction(Quest quest, int index, String name, String description) {
		super(quest, index, null, name, description);
		
		this.paths = new LinkedList<>();
	}

	@Override
	public void loadState(GoalState state) throws InvalidConfigurationException {
		
		if (!state.getName().equals(getName())) {
			QuestManagerPlugin.questManagerPlugin.getLogger().warning("Loading state information"
					+ "from a file that has a mismatched goal name!");
		}
		
		//WARNING:
		//this is assuming that the lists are maintianed in the right order.
		//it should work this way, but this is a point of error!
		ListIterator<RequirementState> states = state.getRequirementStates().listIterator();
		for (Path path : paths)
		for (Requirement req : path.requirements) {
			req.sync();
			try {
				if (req instanceof StatekeepingRequirement) {
					((StatekeepingRequirement) req).loadState(states.next());
				}
			} catch (NoSuchElementException e) {
				QuestManagerPlugin.questManagerPlugin.getLogger().warning("Error when loading state for quest" 
						+ this.getQuest().getName() + "; Not enough requirement states!");
			}
		}
	}

	@Override
	public GoalState getState() {
		
		GoalState state = new GoalState();
		state.setName(getName());
		
		for (Path path : paths)
		for (Requirement req : path.requirements) {
			if (req instanceof StatekeepingRequirement) {
				state.addRequirementState(((StatekeepingRequirement) req).getState());
			}
		}
		
		return state;
	}
	
	/**
	 * Adds a new requirement to this goal
	 */
	@Override
	public void addRequirement(Requirement requirement) {
		; //doesn't make sense for junctions
	}
	
	public void addPath(Path p) {
		paths.add(p);
	}

	@Override
	public List<Requirement> getRequirements() {
		List<Requirement> reqs = new LinkedList<>();
		for (Path p : paths)
			reqs.addAll(p.requirements);
		return reqs;
	}
	
	@Override
	public String getRequirementBreakdown() {
		String builder = "";
		Iterator<Path> it = paths.iterator();
		Path path;
		while (it.hasNext()) {
			path = it.next();
			for (Requirement req : path.requirements) {
				builder += req.isCompleted() ? ChatColor.GREEN + "  " : ChatColor.DARK_RED + "  ";
				builder += req instanceof CompassTrackable ? "@" : "-";
				builder += req.getDescription() + "\n";
			}
			
			if (it.hasNext())
				builder += "   --OR--\n";
			
		}
		return builder;
	}
	
	@Override
	public FancyMessage getFancyRequirementBreakdown() {
		FancyMessage builder = new FancyMessage("");
		Iterator<Path> it = paths.iterator();
		Path path;
		while (it.hasNext()) {
			path = it.next();
			for (Requirement req : path.requirements) {
				builder.then((req.isCompleted() ? "  " : "  ")+ (req instanceof CompassTrackable ? "@" : "-") 
						+ req.getDescription() + "\n")
					.color(req.isCompleted() ? ChatColor.GREEN : ChatColor.DARK_RED);
			}
			
			if (it.hasNext())
				builder.then("   --OR--\n")
					.color(ChatColor.BLACK);
		}
		return builder;
		
	}
	
	/**
	 * Assesses and reports whether the goal has been completed.
	 * Please note that goals that have no requirements defaultly return true.
	 */
	@Override
	public boolean isComplete() {
		if (paths.isEmpty()) {
			return true;
		}
		
		for (Path p : paths) {
			if (p.isComplete())
				return true;
		}
		
		return false;
	}
	
	/**
	 * Perform cleanup before exiting/reloading
	 */
	@Override
	public void stop() {
		for (Path path : paths)
		for (Requirement req : path.requirements) {
			if (req instanceof StatekeepingRequirement) {
				((StatekeepingRequirement) req).stop();
			}
			if (req instanceof Listener) {
				HandlerList.unregisterAll((Listener) req);
			}
		}
	}
	
	/**
	 * Fetches the goal state pointed to by this junction, much like the regular Goal method but with a key
	 * difference: the returned goal is the result specified in the junction's ocnfig. This is where branching
	 * occurs. No checks are made to predict cycles, etc.
	 * <p>
	 * If none of the kept paths are completed when thsi call is made, will return the next state pointed to
	 * by the first path this Junction has.
	 * </p>
	 * @return
	 */
	public Goal fetchNextGoal() {
		/*
		 * This is where it gets _spicy_
		 * go through paths and find first finished one. Return that next index's fetched goal.
		 * if can't find any, default to the first in the list.
		 */
		
		Integer nextGoalIndex = null;
		boolean tripped = false;
		
		for (Path path : paths)
		if (path.isComplete()) {
			nextGoalIndex = path.resultIndex;
			tripped = true;
			break;
		}
		
		if (!tripped) {
			nextGoalIndex = paths.get(0).resultIndex;
		}
		
		if (nextGoalIndex == null)
			return null;
		
		try {
			return getQuest().getTemplate().fetchGoal(getQuest(), nextGoalIndex);
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
			QuestManagerPlugin.questManagerPlugin.getLogger().warning("Failed to get goal data from configuration!");
			return null;
		}
	}
	
}
