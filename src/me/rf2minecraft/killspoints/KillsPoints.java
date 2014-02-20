package me.rf2minecraft.killspoints;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class KillsPoints extends JavaPlugin {
	public void onEnable() {
		SettingsManager.getInstance().setup(this);
		Bukkit.getServer().getPluginManager().registerEvents(new DeathListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new SignListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
	}
	
	public class DeathListener implements Listener {
		@EventHandler
		public void onPlayerDeath(PlayerDeathEvent e) {
			Player killed = e.getEntity();
			Player killer = killed.getKiller();
			if(!(killer instanceof Player)) return;
			SettingsManager.getInstance().addPoints(killer, 50);
			killed.sendMessage(ChatColor.DARK_RED + "{KillsPoints}" + ChatColor.RED + " You lost 25 points after being killed by: " + killer.getName());
			killer.sendMessage(ChatColor.DARK_RED + "{KillsPoints}" + ChatColor.RED + " You gained 50 points for killing: " + killed.getName());
			SettingsManager.getInstance().removePoints(killed, 25);
		}
	}
	
	public class SignListener implements Listener {
		@EventHandler
		public void onSignChange(SignChangeEvent e) {
			if(e.getLine(0).equalsIgnoreCase("[Shop]")) {
				e.setLine(0, ChatColor.DARK_RED + ("[Shop]"));
			}
			if(e.getLine(1).isEmpty()) {
				e.getBlock().breakNaturally();
				e.getPlayer().sendMessage(ChatColor.DARK_RED + "{KillsPoints}" + ChatColor.RED + " Important information missing: ITEM, COST, AMOUNT");
			} else {
			if(e.getLine(2).isEmpty()) {
				e.getBlock().breakNaturally();
				e.getPlayer().sendMessage(ChatColor.DARK_RED + "{KillsPoints}" + ChatColor.RED + " Important information missing: ITEM, COST");
				}
			}
		}
	}
	
	public class PlayerListener implements Listener {
	@SuppressWarnings("deprecation")
	@EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(e.getClickedBlock().getType().equals(Material.SIGN) || e.getClickedBlock().getType().equals(Material.SIGN_POST) || e.getClickedBlock().getType().equals(Material.WALL_SIGN)){
			  Sign sign = (Sign) e.getClickedBlock().getState();
			  if(sign.getLine(0).contains("[Shop]")) {
				  int amount = Integer.parseInt(sign.getLine(1));
				  int item = Integer.parseInt(sign.getLine(2));
				  int cost = Integer.parseInt(sign.getLine(3));
				  if(cost > SettingsManager.getInstance().getPoints(e.getPlayer())) {
					  e.getPlayer().sendMessage(ChatColor.DARK_RED + "{KillsPoints}" + ChatColor.RED + " You do not have enough points!");
				  } else {
					  e.getPlayer().getInventory().addItem(new ItemStack(item, amount));
					  SettingsManager.getInstance().removePoints(e.getPlayer(), cost);
				  	e.getPlayer().sendMessage(ChatColor.DARK_RED + "{KillsPoints} " + ChatColor.RED + cost + " was taken from your balance.");
				  }
			  }
			}
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		if (cmd.getName().equalsIgnoreCase("points")) {
			if (args.length == 0) {
				if (sender instanceof Player) {
					sender.sendMessage(ChatColor.DARK_RED + "{KillPoints}" + ChatColor.RED + " You currently have: " + SettingsManager.getInstance().getPoints(p) + " point(s).");
				} else {
					sender.sendMessage(ChatColor.DARK_RED + "You are a computer, not a player. You do not have any points.");
				}
			} else {
				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("give")) {
						sender.sendMessage(ChatColor.DARK_RED + "{KillPoints}" + ChatColor.RED + " Not enough arguments. /points give <player> <amount>");
					}
					if (args[0].equalsIgnoreCase("take")) {
						sender.sendMessage(ChatColor.DARK_RED + "{KillPoints}" + ChatColor.RED + " Not enough arguments. /points take <player> <amount>");
					}
				} else {
					if (args.length > 3) {
						sender.sendMessage(ChatColor.DARK_RED + "{KillPoints}" + ChatColor.RED + " Too many arguments!");
					} else {
						if (args.length == 3) {
							if (args[0].equalsIgnoreCase("give")) {
								int amount = Integer.parseInt(args[2]);
								Player player = Bukkit.getServer().getPlayer(args[1]);
								SettingsManager.getInstance().addPoints(player, amount);
								sender.sendMessage(ChatColor.DARK_RED + "{KillPoints}" + ChatColor.RED + " You have given " + player.getName() + " " + amount + "" + " points!");
							}
							if (args[0].equalsIgnoreCase("take")) {
								int amount = Integer.parseInt(args[2]);
								Player player = Bukkit.getServer().getPlayer(args[1]);
								SettingsManager.getInstance().removePoints(player, amount);
								sender.sendMessage(ChatColor.DARK_RED + "{KillPoints}" + ChatColor.RED + " You have taken " + amount + " points from " + player.getName() + "!");
							}
						}
					}
				}
			}
		}
		return true;
		}
	}
}