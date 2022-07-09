package com.karbonhq.games.minecraft.mazegenerator.commands;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.karbonhq.games.minecraft.mazegenerator.MazeGeneratorPlugin;

public class MazegenCommand implements CommandExecutor {
	private final MazeGeneratorPlugin plugin;

	public MazegenCommand(MazeGeneratorPlugin plugin)
	{
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player))
		{
			sender.sendMessage("Only players can generate mazes.");
			return false;
		}
		Player player = (Player)sender;
		if (player.getGameMode() != GameMode.CREATIVE) {
			sender.sendMessage("You must be in creative mode to create a maze.");
			return false;
		}

		if (args.length < 1) {
			return false;
		}

		switch(args[0].toLowerCase()) {
			case "foundation":
				return new FoundationCommand(this.plugin).onCommand(sender, command, label, args);

			case "create":
				return new CreateCommand(this.plugin).onCommand(sender, command, label, args);

			default:
				// Unrecognized command
				return false;
		}
	}
}
