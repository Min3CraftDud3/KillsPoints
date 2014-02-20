package me.rf2minecraft.killspoints;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class SettingsManager {
	private SettingsManager() { }
	private static SettingsManager instance = new SettingsManager();
	public static SettingsManager getInstance() {
		return instance;
	}
	private Plugin p;
	private FileConfiguration config;
	private File configfile;
	
	public void setup(Plugin p) {
		this.p = p;
		if(!p.getDataFolder().exists()) p.getDataFolder().mkdir();
		configfile = new File(p.getDataFolder(), "points.yml");
		if(!configfile.exists()) {
			try { configfile.createNewFile(); }
			catch (Exception e) { e.printStackTrace(); }
			}
		
		config = YamlConfiguration.loadConfiguration(configfile);
		}
	
	
	public int getPoints(Player p) {
		return config.getInt("points." + p.getName());
	}
	
	public void addPoints(Player p, int amount) {
		setPoints(p, getPoints(p) + amount);
	}
	
	public void removePoints(Player p, int amount) { 
		setPoints(p, getPoints(p) - amount);
	}
	
	public void setPoints(Player p, int amount) {
		config.set("points." + p.getName(), amount);
		save();
	}
	
	private void save() {
		try { config.save(configfile); }
		catch (Exception e) { e.printStackTrace(); }
	}
	
	public Plugin getPlugin() {
		return p;
	}
}
