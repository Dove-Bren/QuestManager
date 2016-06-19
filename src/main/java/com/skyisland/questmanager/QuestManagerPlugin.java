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

package com.skyisland.questmanager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import com.skyisland.questmanager.configuration.PluginConfiguration;
import com.skyisland.questmanager.configuration.utils.Chest;
import com.skyisland.questmanager.configuration.utils.LocationState;
import com.skyisland.questmanager.enemy.DefaultEnemy;
import com.skyisland.questmanager.enemy.NormalEnemy;
import com.skyisland.questmanager.enemy.StandardEnemy;
import com.skyisland.questmanager.fanciful.FancyMessage;
import com.skyisland.questmanager.fanciful.MessagePart;
import com.skyisland.questmanager.fanciful.TextualComponent;
import com.skyisland.questmanager.loot.Loot;
import com.skyisland.questmanager.magic.ImbuementHandler;
import com.skyisland.questmanager.magic.ImbuementSet;
import com.skyisland.questmanager.magic.SpellPylon;
import com.skyisland.questmanager.magic.SummonManager;
import com.skyisland.questmanager.magic.spell.ChargeSpell;
import com.skyisland.questmanager.magic.spell.SimpleSelfSpell;
import com.skyisland.questmanager.magic.spell.SimpleTargetSpell;
import com.skyisland.questmanager.magic.spell.Spell;
import com.skyisland.questmanager.magic.spell.SpellManager;
import com.skyisland.questmanager.magic.spell.SpellWeavingManager;
import com.skyisland.questmanager.magic.spell.SpellWeavingSpell;
import com.skyisland.questmanager.magic.spell.effect.AreaEffect;
import com.skyisland.questmanager.magic.spell.effect.BlockEffect;
import com.skyisland.questmanager.magic.spell.effect.CastPylonEffect;
import com.skyisland.questmanager.magic.spell.effect.DamageEffect;
import com.skyisland.questmanager.magic.spell.effect.DamageMPEffect;
import com.skyisland.questmanager.magic.spell.effect.DamageUndeadEffect;
import com.skyisland.questmanager.magic.spell.effect.FireEffect;
import com.skyisland.questmanager.magic.spell.effect.HealEffect;
import com.skyisland.questmanager.magic.spell.effect.InvokeSpellWeavingEffect;
import com.skyisland.questmanager.magic.spell.effect.StatusEffect;
import com.skyisland.questmanager.magic.spell.effect.SummonTamedEffect;
import com.skyisland.questmanager.magic.spell.effect.SwapEffect;
import com.skyisland.questmanager.npc.BankNPC;
import com.skyisland.questmanager.npc.DummyNPC;
import com.skyisland.questmanager.npc.ForgeNPC;
import com.skyisland.questmanager.npc.InnNPC;
import com.skyisland.questmanager.npc.LevelupNPC;
import com.skyisland.questmanager.npc.MuteNPC;
import com.skyisland.questmanager.npc.ServiceNPC;
import com.skyisland.questmanager.npc.ShopNPC;
import com.skyisland.questmanager.npc.SimpleBioptionNPC;
import com.skyisland.questmanager.npc.SimpleChatNPC;
import com.skyisland.questmanager.npc.SimpleQuestStartNPC;
import com.skyisland.questmanager.npc.TeleportNPC;
import com.skyisland.questmanager.npc.utils.BankStorageManager;
import com.skyisland.questmanager.npc.utils.ServiceCraft;
import com.skyisland.questmanager.npc.utils.ServiceOffer;
import com.skyisland.questmanager.player.Party;
import com.skyisland.questmanager.player.PlayerOptions;
import com.skyisland.questmanager.player.QuestPlayer;
import com.skyisland.questmanager.player.skill.CraftingSkill;
import com.skyisland.questmanager.player.skill.Skill;
import com.skyisland.questmanager.player.skill.SkillManager;
import com.skyisland.questmanager.player.skill.defaults.ArcherySkill;
import com.skyisland.questmanager.player.skill.defaults.AxeSkill;
import com.skyisland.questmanager.player.skill.defaults.BowSkill;
import com.skyisland.questmanager.player.skill.defaults.ConcentrationSkill;
import com.skyisland.questmanager.player.skill.defaults.CookingSkill;
import com.skyisland.questmanager.player.skill.defaults.DexteritySkill;
import com.skyisland.questmanager.player.skill.defaults.FashioningSkill;
import com.skyisland.questmanager.player.skill.defaults.FishingSkill;
import com.skyisland.questmanager.player.skill.defaults.ImbuementSkill;
import com.skyisland.questmanager.player.skill.defaults.LumberjackSkill;
import com.skyisland.questmanager.player.skill.defaults.MagerySkill;
import com.skyisland.questmanager.player.skill.defaults.MagicWeaverSkill;
import com.skyisland.questmanager.player.skill.defaults.MiningSkill;
import com.skyisland.questmanager.player.skill.defaults.PatienceSkill;
import com.skyisland.questmanager.player.skill.defaults.SmithingSkill;
import com.skyisland.questmanager.player.skill.defaults.SorcerySkill;
import com.skyisland.questmanager.player.skill.defaults.SpellWeavingSkill;
import com.skyisland.questmanager.player.skill.defaults.SwordAndShieldSkill;
import com.skyisland.questmanager.player.skill.defaults.SwordsmanshipSkill;
import com.skyisland.questmanager.player.skill.defaults.TacticsSkill;
import com.skyisland.questmanager.player.skill.defaults.TwoHandedSkill;
import com.skyisland.questmanager.player.utils.SpellWeavingInvoker;
import com.skyisland.questmanager.quest.Quest;
import com.skyisland.questmanager.quest.requirements.ArriveRequirement;
import com.skyisland.questmanager.quest.requirements.ChestRequirement;
import com.skyisland.questmanager.quest.requirements.CountdownRequirement;
import com.skyisland.questmanager.quest.requirements.DeliverRequirement;
import com.skyisland.questmanager.quest.requirements.InteractRequirement;
import com.skyisland.questmanager.quest.requirements.PositionRequirement;
import com.skyisland.questmanager.quest.requirements.PossessRequirement;
import com.skyisland.questmanager.quest.requirements.SlayRequirement;
import com.skyisland.questmanager.quest.requirements.TalkRequirement;
import com.skyisland.questmanager.quest.requirements.TimeRequirement;
import com.skyisland.questmanager.quest.requirements.VanquishRequirement;
import com.skyisland.questmanager.region.CuboidRegion;
import com.skyisland.questmanager.region.RegionManager;
import com.skyisland.questmanager.region.SphericalRegion;
import com.skyisland.questmanager.ui.ChatGuiHandler;
import com.skyisland.questmanager.ui.InventoryGuiHandler;
import com.skyisland.questmanager.ui.menu.action.PartyInviteAction;
import com.skyisland.questmanager.ui.menu.action.ShowSkillMenuAction;
import com.skyisland.questmanager.ui.menu.action.ShowSkillRecipesAction;
import com.skyisland.questmanager.ui.menu.inventory.ServiceInventory;
import com.skyisland.questmanager.ui.menu.inventory.ShopInventory;
import com.skyisland.questmanager.ui.menu.message.BioptionMessage;
import com.skyisland.questmanager.ui.menu.message.SimpleMessage;
import com.skyisland.questmanager.ui.menu.message.TreeMessage;

/**
 * Provided API and Command Line interaction between enlisted quest managers and
 * the user.
 * 
 */
public class QuestManagerPlugin extends JavaPlugin {
	
	public static QuestManagerPlugin questManagerPlugin;
	
	private RequirementManager reqManager;
	
	private PlayerManager playerManager;
	
	private RegionManager regionManager;
	
	private SpellManager spellManager;
	
	private SummonManager summonManager;
	
	private SkillManager skillManager;
	
	private BankStorageManager bankManager;
	
	private SpellWeavingManager spellWeavingManager;
	
	private ImbuementHandler imbuementHandler;
	
	private QuestManager manager;
	
	private ChatGuiHandler chatGuiHandler;
	
	private InventoryGuiHandler inventoryGuiHandler;
	
	private PluginConfiguration config;
	
	private File saveDirectory;
	
	private File questDirectory;
	
	private File regionDirectory;
	
	private File spellDirectory;
	
	private File skillDirectory;
	
	private final static String configFileName = "QuestManagerConfig.yml";
	
	private final static String playerConfigFileName = "players.yml";
	
	private final static String playerConfigBackupName = "players.backup";
	
	private final static String bankDataFileName = "banks.yml";
	
	private final static String spellWeavingFileName = "spellWeaving.yml";
	
	private final static String imbuementFileName = "imbuement.yml";
	
	public static final double VERSION = 1.00;
	
	public static Logger logger;
	
	@Override
	public void onLoad() {
		QuestManagerPlugin.questManagerPlugin = this;
		QuestManagerPlugin.logger = getLogger();
		reqManager = new RequirementManager();
		
		//load up config
		File configFile = new File(getDataFolder(), configFileName);
		
		config = new PluginConfiguration(configFile);		
				
		//perform directory checks
		saveDirectory = new File(getDataFolder(), config.getSavePath());
		if (!saveDirectory.exists()) {
			saveDirectory.mkdirs();
		}
		
		questDirectory = new File(getDataFolder(), config.getQuestPath());
		if (!questDirectory.exists()) {
			questDirectory.mkdirs();
		}
		
		regionDirectory = new File(getDataFolder(), config.getRegionPath());
		if (!regionDirectory.exists()) {
			regionDirectory.mkdirs();
		}
		
		spellDirectory = new File(getDataFolder(), config.getSpellPath());
		if (!spellDirectory.exists()) {
			spellDirectory.mkdirs();
		}
		
		skillDirectory = new File(getDataFolder(), config.getSkillPath());
		if (!skillDirectory.exists()) {
			skillDirectory.mkdirs();
		}
	
		//register our own requirements
		reqManager.registerFactory("ARRIVE", 
				new ArriveRequirement.ArriveFactory());
		reqManager.registerFactory("POSITION", 
				new PositionRequirement.PositionFactory());
		reqManager.registerFactory("POSSESS", 
				new PossessRequirement.PossessFactory());
		reqManager.registerFactory("VANQUISH", 
				new VanquishRequirement.VanquishFactory());
		reqManager.registerFactory("SLAY", 
				new SlayRequirement.SlayFactory());
		reqManager.registerFactory("DELIVER", 
				new DeliverRequirement.DeliverFactory());
		reqManager.registerFactory("TIME", 
				new TimeRequirement.TimeFactory());
		reqManager.registerFactory("COUNTDOWN", 
				new CountdownRequirement.CountdownFactory());
		reqManager.registerFactory("INTERACT", 
				new InteractRequirement.InteractFactory());
		reqManager.registerFactory("CHEST", 
				new ChestRequirement.ChestRequirementFactory());
		reqManager.registerFactory("TALK", 
				new TalkRequirement.TalkRequirementFactory());
		
	}
	
	@Override
	public void onEnable() {
		//register our Location util!
		registerConfigurationClasses();

		chatGuiHandler = new ChatGuiHandler(this, config.getMenuVerbose());
		inventoryGuiHandler = new InventoryGuiHandler();
		

		
		skillManager = new SkillManager();

		imbuementHandler = new ImbuementHandler(new File(getDataFolder(), imbuementFileName));
		
		regionManager = new RegionManager(regionDirectory, config.getMusicDurations(), 3);
		
		registerDefaultSkills();
		
		//preload Player data
			File playerFile = new File(getDataFolder(), playerConfigFileName);
			if (!playerFile.exists()) {
				try {
					YamlConfiguration tmp = new YamlConfiguration();
					tmp.createSection("players");
					tmp.createSection("parties");
					tmp.save(playerFile);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			//get Player data & manager
			try {
				Files.copy(playerFile.toPath(), (new File(getDataFolder(), playerConfigBackupName)).toPath(),
						StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				logger.warning("Unable to make backup file!");
			}
			
			YamlConfiguration playerConfig = new YamlConfiguration();
			try {
				playerConfig.load(playerFile);
			} catch (IOException | InvalidConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		playerManager = new PlayerManager(playerConfig);
		
		
		//parse config & instantiate manager
		manager = new QuestManager(
				questDirectory, 
				saveDirectory);
		
		manager.init();

		spellManager = new SpellManager(spellDirectory);
		
		summonManager = new SummonManager();
		
		bankManager = new BankStorageManager(new File(getDataFolder(), bankDataFileName));
		
		spellWeavingManager = new SpellWeavingManager(new File(getDataFolder(), spellWeavingFileName));
		new SpellWeavingInvoker();
		
		
//		SpellWeavingSpell spell = new SpellWeavingSpell("Combusion", 0, 15, "Catches stuff on fire");
//		spell.addSpellEffect(new FireEffect(10));
//		spell.setSpellRecipe(new SpellWeavingRecipe(Lists.asList("Soul", new String[]{"Spirit"}), false));
//		
//		spellWeavingManager.registerSpell(
//				spell
//				);
		
//		///////////////////////////////////////////////////////////////////////////////
//					
//		Vector v1 = new Vector(-600, 5, -800),
//		v2 = new Vector(-605, 3, -805);
//		Region r = new SphericalRegion(Bukkit.getWorld("QuestWorld"), v1, 4);
//		Enemy e = new DefaultEnemy(EntityType.ZOMBIE);
//		
//		regionManager.registerRegion(r);
//		regionManager.addEnemy(r, e);
//		
//		e = new DefaultEnemy(EntityType.SKELETON);
//		regionManager.addEnemy(r, e);
//		
//		///////////////////////////////////////////////////////////////////////////////		
	}
	
	@Override
	public void onDisable() {
		
		//unregister our scheduler
		Bukkit.getScheduler().cancelTasks(this);

		playerManager.getParties().forEach(Party::disband);
		
		//save user database
		playerManager.save(new File(getDataFolder(), playerConfigFileName));
		bankManager.save(new File(getDataFolder(), bankDataFileName));
		spellWeavingManager.save(new File(getDataFolder(), spellWeavingFileName));
		stopAllQuests();
		summonManager.removeSummons();
		for (QuestPlayer p : playerManager.getPlayers()) {
			p.getSpellPylons().forEach(SpellPylon::remove);
			
			p.clearSpellPylons();
		}
		
		
	}
	
	public void onReload() {
		onDisable();
		
		HandlerList.unregisterAll(this);
		
		onLoad();
		onEnable();
	}
	
	private void registerDefaultSkills() {
		skillManager.registerSkill(new TwoHandedSkill());
		skillManager.registerSkill(new SwordAndShieldSkill());
		skillManager.registerSkill(new MagerySkill());
		skillManager.registerSkill(new MagicWeaverSkill());
		skillManager.registerSkill(new AxeSkill());
		skillManager.registerSkill(new SwordsmanshipSkill());
		skillManager.registerSkill(new SpellWeavingSkill());
		skillManager.registerSkill(new BowSkill());
		skillManager.registerSkill(new TacticsSkill());
		skillManager.registerSkill(new ArcherySkill());
		skillManager.registerSkill(new SorcerySkill());
		skillManager.registerSkill(new ConcentrationSkill());
		skillManager.registerSkill(new ImbuementSkill());
		skillManager.registerSkill(new FishingSkill());
		skillManager.registerSkill(new MiningSkill());
		skillManager.registerSkill(new LumberjackSkill());
		skillManager.registerSkill(new CookingSkill());
		skillManager.registerSkill(new FashioningSkill());
		skillManager.registerSkill(new SmithingSkill());
		skillManager.registerSkill(new DexteritySkill());
		skillManager.registerSkill(new PatienceSkill());
	}
	
	public static void registerConfigurationClasses() {
		LocationState.registerWithAliases();
		QuestPlayer.registerWithAliases();
		Party.registerWithAliases();
		MuteNPC.registerWithAliases();
		SimpleChatNPC.registerWithAliases();
		SimpleBioptionNPC.registerWithAliases();
		SimpleQuestStartNPC.registerWithAliases();
		InnNPC.registerWithAliases();
		ForgeNPC.registerWithAliases();
		ShopNPC.registerWithAliases();
		TeleportNPC.registerWithAliases();
		SimpleMessage.registerWithAliases();
		BioptionMessage.registerWithAliases();
		TreeMessage.registerWithAliases();
		ShopInventory.registerWithAliases();
		ServiceInventory.registerWithAliases();
		ServiceCraft.registerWithAliases();
		ServiceOffer.registerWithAliases();
		ServiceNPC.registerWithAliases();
		LevelupNPC.registerWithAliases();
		DummyNPC.registerWithAliases();
		ConfigurationSerialization.registerClass(MessagePart.class);
		ConfigurationSerialization.registerClass(TextualComponent.ArbitraryTextTypeComponent.class);
		ConfigurationSerialization.registerClass(TextualComponent.ComplexTextTypeComponent.class);
		ConfigurationSerialization.registerClass(FancyMessage.class);
		Chest.registerWithAliases();
		CuboidRegion.registerWithAliases();
		SphericalRegion.registerWithAliases();
		DefaultEnemy.registerWithAliases();
		NormalEnemy.registerWithAliases();
		StandardEnemy.registerWithAliases();
		SimpleSelfSpell.registerWithAliases();
		SimpleTargetSpell.registerWithAliases();
		ChargeSpell.registerWithAliases();
		HealEffect.registerWithAliases();
		DamageEffect.registerWithAliases();
		StatusEffect.registerWithAliases();
		BlockEffect.registerWithAliases();
		AreaEffect.registerWithAliases();
		DamageMPEffect.registerWithAliases();
		SwapEffect.registerWithAliases();
		SummonTamedEffect.registerWithAliases();
		FireEffect.registerWithAliases();
		InvokeSpellWeavingEffect.registerWithAliases();
		DamageUndeadEffect.registerWithAliases();
		CastPylonEffect.registerWithAliases();
		SpellWeavingSpell.registerWithAliases();
		Loot.registerWithAliases();
		ConfigurationSerialization.registerClass(PlayerOptions.class);
		BankStorageManager.registerSerialization();
		BankNPC.registerWithAliases();
		ImbuementSet.registerWithAliases();
	}
	
	
	/**
	 * Attempts to softly stop all running quest managers and quests.
	 * Quest managers (and underlying quests) may not be able to stop softly,
	 * and this method is not guaranteed to stop all quests (<i>especially</i>
	 * immediately).
	 */
	public void stopAllQuests() {
		if (manager == null) {
			return;
		}
	
		manager.stopQuests();
	}
	
	/**
	 * Performs a hard stop to all quests.
	 * Quests that are halted are not expected to perform any sort of save-state
	 * procedure, not halt in a particularly pretty manner.
	 * Halting a quest <i>guarantees</i> that it will stop immediately upon
	 * receipt of the halt notice.
	 */
	public void haltAllQuests() {
		
		if (manager == null) {
			return;
		}
		
		manager.haltQuests();
		
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("QuestManager")) {
			if (args.length == 0) {
				return false;
			}
			
			if (args[0].equals("reload")) {
				if (args.length == 1) {
					logger.info("Reloading QuestManager...");
					sender.sendMessage(ChatColor.DARK_BLUE + "Reloading QuestManager..." + ChatColor.RESET);
					onReload();
					logger.info("Done");
					sender.sendMessage(ChatColor.DARK_BLUE + "Done" + ChatColor.RESET);
					return true;
				}
				if (args[1].equals("villager") || args[1].equals("villagers")) {
					
					sender.sendMessage(ChatColor.DARK_GRAY + "Resetting villagers..." + ChatColor.RESET);
					getManager().resetNPCs();
					sender.sendMessage(ChatColor.DARK_GRAY + "Done!" + ChatColor.RESET);
					return true;
				}
				
			}
			
			if (args[0].equalsIgnoreCase("grantSpell")) {
				if (args.length < 3) {
					sender.sendMessage(ChatColor.RED + "usage: /questmanager grantspell [user] [spell]");
					return true;
				}
				
				String playerName = args[1];
				String spellName = args[2];
				
				if (args.length > 3) {
					for (int i = 3; i < args.length; i++) {
						spellName += " " + args[i];
					}
				}
				
				Player player = Bukkit.getPlayer(playerName);
				
				if (player == null) {
					sender.sendMessage("Unable to find player " + playerName);
					return true;
				}
				
				Spell spell = spellManager.getSpell(spellName);
				if (spell == null) {
					sender.sendMessage(ChatColor.RED + "Unable to find defined spell " + ChatColor.DARK_PURPLE
							+ spellName + ChatColor.RESET);
					sender.sendMessage("Please pick a spell from the following:");
					String msg = "";
					
					if (spellManager.getSpells().isEmpty()) {
						msg = ChatColor.RED + "No defined spells!";
					} else {
						boolean flip = false;
						for (String name : spellManager.getSpells()) {
							msg += (flip ? ChatColor.BLUE : ChatColor.GREEN);
							msg += "   " + name;
						}
					}
					
					sender.sendMessage(msg);
				} else {
					QuestPlayer qp = playerManager.getPlayer(player);
					qp.addSpell(spellName);
					sender.sendMessage(ChatColor.GREEN + playerName + " has been given " + ChatColor.DARK_PURPLE
							+ spellName);
				}
				
				return true;
			}
			
			return false;
			
		}
		
		if (cmd.getName().equals("questlog")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("Only players can use this command!");
				return true;
			}
			
			QuestPlayer qp = playerManager.getPlayer((OfflinePlayer) sender);
			
			qp.addQuestBook();
			qp.addJournal();
			
			return true;
		}
		
		if (cmd.getName().equals("qhistory")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("Only players can use this command!");
				return true;
			}
			if (args.length != 1) {
				return false;
			}
			
			QuestPlayer qp = playerManager.getPlayer((OfflinePlayer) sender);
			int id;
			try {
				id = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				return false;
			}
			
			for (Quest q : qp.getCurrentQuests()) {
				if (q.getID() == id) {
					qp.setFocusQuest(q.getName());
					return true;
				}
			}
			
			return false;
		}
		
		if (cmd.getName().equals("qcomp")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("Only players can use this command!");
				return true;
			}
			
			
			if (args.length == 0) {
				//no args, just reset compass?
				QuestPlayer qp = playerManager.getPlayer((OfflinePlayer) sender);
				qp.updateCompass(false);
				return true;
			}
			return false;
		}
		
		if (cmd.getName().equals("party")) {
			
			if (args.length == 0) {
				return false;
			}
			if (!(sender instanceof Player)) {
				sender.sendMessage("This command must be executed by a player!");
				return false;
			}
			
			QuestPlayer qp = playerManager.getPlayer((OfflinePlayer) sender);
			if (qp.getParty() == null) {
				sender.sendMessage("You're not in a party!");
				return true;
			}
			
			String msg = ChatColor.DARK_GREEN + "[Party]" + ChatColor.RESET +  "<" + sender.getName() + "> ";
			for (String part : args) {
				msg += part + " ";
			}
			qp.getParty().tellMembers(msg);
			return true;
		}
		
		if (cmd.getName().equals("leave")) {
			
			if (!(sender instanceof Player)) {
				sender.sendMessage("This command must be executed by a player!");
				return false;
			}
			
			QuestPlayer qp = playerManager.getPlayer((OfflinePlayer) sender);
			
			if (qp.getParty() == null) {
				sender.sendMessage("You are not in a party!");
				return true;
			}
			
			qp.getParty().removePlayer(qp, ChatColor.YELLOW + "You left the party"+ ChatColor.RESET);
			return true;
		}
		
		if (cmd.getName().equals("boot")) {
			if (args.length == 0) {
				return false;
			}
			if (!(sender instanceof Player)) {
				sender.sendMessage("This command must be executed by a player!");
				return false;
			}
			
			QuestPlayer qp = playerManager.getPlayer((OfflinePlayer) sender);
			
			if (qp.getParty() == null) {
				sender.sendMessage("You are not in a party!");
				return true;
			}
			
			Party party = qp.getParty();
			
			if (party.getLeader().getIDString().equals(qp.getIDString())) {
				//can boot people
				QuestPlayer other = null;
				for (QuestPlayer op : party.getMembers()) {
					if (op.getPlayer().getName().equals(args[0])) {
						other = op;
						break;
					}
				}
				
				if (other == null) {
					sender.sendMessage(ChatColor.DARK_RED + "Unable to find the player " + ChatColor.BLUE + args[0]
							+ ChatColor.DARK_RED + " in your party!" + ChatColor.RESET);
					return true;
				}
				
				party.removePlayer(other, ChatColor.DARK_RED + "You've been kicked from the party" + ChatColor.RESET);
				return true;
			} else {
				//not leader, can't boot
				sender.sendMessage(ChatColor.DARK_RED + "You are not the leader of the party, and cannot boot people!" + ChatColor.RESET);
				return true;
			}
			
		}
		
		if (cmd.getName().equals("invite")) {
			if (args.length == 0) {
				return false;
			}
			if (!(sender instanceof Player)) {
				sender.sendMessage("This command must be executed by a player!");
				return false;
			}
			
			QuestPlayer qp = playerManager.getPlayer((OfflinePlayer) sender);
			
			if (qp.getParty() != null) {
				
				//are they the leader?
				Party party = qp.getParty();
				if (!party.getLeader().getIDString().equals(qp.getIDString())) {
					//not the leader, can't invite people
					sender.sendMessage(ChatColor.DARK_RED + "Only the party leader can invite new members!" + ChatColor.RESET);
					return true;
				}
			}
			
			//to get here, either is leader or not in a party
			QuestPlayer other = null;
			for (QuestPlayer p : playerManager.getPlayers()) {
				if (p.getPlayer() == null || p.getPlayer().getName() == null) {
					continue;
				}
				if (p.getPlayer().getName().equals(args[0])) {
					other = p;
					break;
				}
			}
			
			if (other == null) {
				sender.sendMessage(ChatColor.DARK_RED + "Unable to find the player "
						+ ChatColor.BLUE + args[0] + ChatColor.RESET);
				return true;
			}
			
			(new PartyInviteAction(qp, other)).onAction();
			
			return true;
		}
		
		if (cmd.getName().equals("player")) {
			if (args.length == 0) {
				return false;
			}
			if (!(sender instanceof Player)) {
				sender.sendMessage("This command must be executed by a player!");
				return false;
			}
			
			QuestPlayer qp = playerManager.getPlayer((OfflinePlayer) sender);
			
			if (args[0].equals("title")) {
				qp.showTitleMenu();
				return true;
			}
			
			if (args[0].equals("options")) {
				qp.showPlayerOptionMenu();
				return true;
			}
			
			if (args[0].equals("recipe")) {
				String skillName = args[1];
				
				if (args.length > 2) {
					for (int i = 2; i < args.length; i++) {
						skillName += " " + args[i];
					}
				}
				CraftingSkill skill = null;
				for (Skill s : skillManager.getAllSkills()) {
					if (s.getName().equals(skillName)) {
						if (s instanceof CraftingSkill) {
							skill = (CraftingSkill) s;
							break;
						} else {
							sender.sendMessage(ChatColor.RED + "That skill has no associated recipes!");
							return true;
						}
					}
				}
				
				if (skill == null) {
					sender.sendMessage(ChatColor.YELLOW + "Unable to find the skill " + skillName);
					return true;
				}
				
				(new ShowSkillRecipesAction(qp, skill)).onAction();
				
				return true;
			}
			
			if (args[0].toLowerCase().equals("skills")) {
				new ShowSkillMenuAction(qp).onAction();
				return true;
			}
		}
		
		return false;
	}
	
	public RequirementManager getRequirementManager() {
		return this.reqManager;
	}
	
	public PluginConfiguration getPluginConfiguration() {
		return this.config;
	}
	
	public PlayerManager getPlayerManager() {
		return playerManager;
	}
	
	public ChatGuiHandler getChatGuiHandler() {
		return chatGuiHandler;
	}
	
	public InventoryGuiHandler getInventoryGuiHandler() {
		return inventoryGuiHandler;
	}
	
	public QuestManager getManager() {
		return manager;
	}
	
	public RegionManager getEnemyManager() {
		return regionManager;
	}
	
	public SpellManager getSpellManager() {
		return spellManager;
	}
	
	public SummonManager getSummonManager() {
		return summonManager;
	}
	
	public SkillManager getSkillManager() {
		return skillManager;
	}
	
	public BankStorageManager getBankManager() {
		return bankManager;
	}
	
	public SpellWeavingManager getSpellWeavingManager() {
		return spellWeavingManager;
	}
	
	public ImbuementHandler getImbuementHandler() {
		return imbuementHandler;
	}
}
