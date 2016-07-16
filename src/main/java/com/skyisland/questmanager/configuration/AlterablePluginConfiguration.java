package com.skyisland.questmanager.configuration;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.Sound;

import com.skyisland.questmanager.QuestManagerPlugin;

/**
 * A PluginConfiguration that allows modification.
 * @author Skyler
 *
 */
public class AlterablePluginConfiguration extends PluginConfiguration {
	
	public static AlterablePluginConfiguration copyOf(PluginConfiguration config) {
		AlterablePluginConfiguration copy = new AlterablePluginConfiguration();
		copy.config = config.config;
		copy.conservative = config.conservative;
		return copy;
	}
	
	private AlterablePluginConfiguration() {
		
	}
	
	public AlterablePluginConfiguration(File configFile) {
		super(configFile);
	}

	public void setConservative(boolean conservative) {
		this.conservative = conservative;
	}
	

	public void setVersion(double version) {
		config.set(PluginConfigurationKey.VERSION.getKey(), version);
	}
	
	public void setWorlds(List<String> worlds) {
		config.set(PluginConfigurationKey.WORLDS.getKey(), worlds);
	}
	
	public void setQuestPath(String path) {
		config.set(PluginConfigurationKey.QUESTDIR.getKey(), path);
	}
	
	public void setKeepOnError(boolean keep) {
		config.set(PluginConfigurationKey.CONSERVATIVE.getKey(), keep);
	}
	
	public void setVillagerCleanup(boolean clean) {
		config.set(PluginConfigurationKey.CLEANUPVILLAGERS.getKey(), clean);
	}
	
	public void setXPMoney(boolean flag) {
		config.set(PluginConfigurationKey.XPMONEY.getKey(), flag);
	}
	
	public void setMaxPartySize(int size) {
		config.set(PluginConfigurationKey.PARTYSIZE.getKey(), size);
	}
	
	public void setSummonLimit(int limit) {
		config.set(PluginConfigurationKey.SUMMONLIMIT.getKey(), limit);
	}
	
	public void setAllowTaming(boolean flag) {
		config.set(PluginConfigurationKey.ALLOWTAMING.getKey(), flag);
	}
	
	public void setUsePortals(boolean flag) {
		config.set(PluginConfigurationKey.PORTALS.getKey(), flag);
	}
	
	public void setAdjustXP(boolean flag) {
		config.set(PluginConfigurationKey.ADJUSTXP.getKey(), flag);
	}
	
	public void setMagicEnabled(boolean flag) {
		config.set(PluginConfigurationKey.ALLOWMAGIC.getKey(), flag);
	}
	
	public void setStartingMana(int amount) {
		config.set(PluginConfigurationKey.MANADEFAULT.getKey(), amount);
	}
	
	public void setMagicRegenDay(double rate) {
		config.set(PluginConfigurationKey.DAYREGEN.getKey(), rate);
	}
	
	public void setMagicRegenNight(double rate) {
		config.set(PluginConfigurationKey.NIGHTREGEN.getKey(), rate);
	}
	
	public void setMagicRegenOutside(boolean flag) {
		config.set(PluginConfigurationKey.OUTSIDEREGEN.getKey(), flag);
	}
	
	public void setMagicRegenKill(double rate) {
		config.set(PluginConfigurationKey.KILLREGEN.getKey(), rate);
	}
	
	public void setMagicRegenXP(double rate) {
		config.set(PluginConfigurationKey.XPREGEN.getKey(), rate);
	}
	
	public void setMagicRegenFood(double rate) {
		config.set(PluginConfigurationKey.FOODREGEN.getKey(), rate);
	}
	
	public void setMenuVerbose(boolean flag) {
		config.set(PluginConfigurationKey.VERBOSEMENUS.getKey(), flag);
	}
	
	public void setAllowCrafting(boolean flag) {
		config.set(PluginConfigurationKey.ALLOWCRAFTING.getKey(), flag);
	}
	
	public void setAllowNaming(boolean flag) {
		config.set(PluginConfigurationKey.ALLOWNAMING.getKey(), flag);
	}
	
	public void setChatTitle(boolean flag) {
		config.set(PluginConfigurationKey.TITLECHAT.getKey(), flag);
	}
	
	public void setCompassEnabled(boolean flag) {
		config.set(PluginConfigurationKey.COMPASS.getKey(), flag);
	}
	
	public void setCompassType(Material type) {
		config.set(PluginConfigurationKey.COMPASSTYPE.getKey(), type.name());
		
	}
	
	public void setCompassName(String name) {
		config.set(PluginConfigurationKey.COMPASSNAME.getKey(), name);
	}
	
	public void setSavePath(String path) {
		config.set(PluginConfigurationKey.SAVEDIR.getKey(), path);
	}
	
	public void setRegionPath(String path) {
		config.set(PluginConfigurationKey.REGIONDIR.getKey(), path);
	}
	
	public void setSpellPath(String path) {
		config.set(PluginConfigurationKey.SPELLDIR.getKey(), path);
	}
	
	public void setSkillPath(String path) {
		config.set(PluginConfigurationKey.SKILLDIR.getKey(), path);
	}
	
	public void setSkillCap(int cap) {
		config.set(PluginConfigurationKey.SKILLCAP.getKey(), cap);
	}
	
	public void setSkillGrowthOnSuccess(double rate) {
		config.set(PluginConfigurationKey.SKILLSUCCESSGROWTH.getKey(), rate);
	}
	
	public void setSkillGrowthOnFail(double rate) {
		config.set(PluginConfigurationKey.SKILLFAILGROWTH.getKey(), rate);
	}
	
	public void setSkillCutoff(int cutoff) {
		config.set(PluginConfigurationKey.SKILLGROWTHCUTOFF.getKey(), cutoff);
	}
	
	public void setSkillUpperCutoff(int cutoff) {
		config.set(PluginConfigurationKey.SKILLGROWTHUPPERCUTOFF.getKey(), cutoff);
	}
	
	public void setSpellHolderName(String name) {
		config.set(PluginConfigurationKey.HOLDERNAME.getKey(), name);
	}
	
	public void setAlterType(Material type) {
		config.set(PluginConfigurationKey.ALTERTYPE.getKey(), type.name());
	}
	
	public void setAllowSpellWeaving(boolean flag) {
		config.set(PluginConfigurationKey.ALLOWWEAVING.getKey(), flag);
	}
	
	public void setUseWeavingInvoker(boolean flag) {
		config.set(PluginConfigurationKey.USEINVOKER.getKey(), flag);
	}
	
	public void setSpellInvokerName(String name) {
		config.set(PluginConfigurationKey.INVOKERNAME.getKey(), name);
	}
	
	public void setInvokerType(Material type) {
		config.set(PluginConfigurationKey.INVOKERTYPE.getKey(), type.name());
	}
	
	public void setRecallerType(Material type) {
		config.set(PluginConfigurationKey.RECALLERTYPE.getKey(), type);
	}
	
	public void setMarkType(Material type) {
		config.set(PluginConfigurationKey.MARKLOCTYPE.getKey(), type);
	}
	
	public void setSingleRecall(boolean singleRecall) {
		config.set(PluginConfigurationKey.MARKONCE.getKey(), singleRecall);
	}
	
	public void setRecallCost(double cost) {
		config.set(PluginConfigurationKey.RECALLCOST.getKey(), cost);
	}
	
	public void setRecallerName(String name) {
		config.set(PluginConfigurationKey.RECALLERNAME.getKey(), name);
	}
	
	public void setMusicDurations(Map<Sound, Double> map) {
		for (Sound sound : map.keySet()) {
			config.set(PluginConfigurationKey.MUSICDURATIONS.getKey() + "." + sound.name(), map.get(sound));
		}
	}
	
	/**
	 * Perform an un-typed set. This method is <strong>dangerous</strong> and should only be used when:
	 * <ul>
	 * <li>You know the type stored in the field</li>
	 * <li>You cannot call the normal method (you're being dynamic and cool or something)</li>
	 * </ul>
	 * @param key
	 * @param value
	 */
	public void setBaseValue(PluginConfigurationKey key, Object value) {
		if (!key.def.getClass().isAssignableFrom(value.getClass())) {
			QuestManagerPlugin.logger.warning("Could not set raw value in Config; type mismatch"
					+ key.getDef().getClass().getName() + " <> " + value.getClass().getName());
			return;
		}
		config.set(key.getKey(), value);
	}
}
