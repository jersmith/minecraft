package com.karbonhq.games.minecraft.mazegenerator;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class MazeGeneratorPlugin extends JavaPlugin {
	@Override
	public void onEnable() {
	}
	
	@Override
	public void onDisable() {
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("mazegen"))
		{
			sender.sendMessage("Congratulations, you invoked mazegen! It does nothing yet, though.");
			return true;
		}
		return false;
	}
}
