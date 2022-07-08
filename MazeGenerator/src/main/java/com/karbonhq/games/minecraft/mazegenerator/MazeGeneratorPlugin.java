package com.karbonhq.games.minecraft.mazegenerator;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.karbonhq.games.minecraft.mazegenerator.commands.MazegenCommand;

public final class MazeGeneratorPlugin extends JavaPlugin {
	@Override
	public void onEnable() {
		this.getCommand("mazegen").setExecutor(new MazegenCommand(this));
	}
	
	@Override
	public void onDisable() {
	}
}
