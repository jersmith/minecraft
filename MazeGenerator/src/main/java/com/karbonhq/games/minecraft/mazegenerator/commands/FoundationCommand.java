package com.karbonhq.games.minecraft.mazegenerator.commands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.karbonhq.games.minecraft.mazegenerator.MazeGeneratorPlugin;

import net.md_5.bungee.api.ChatColor;

public class FoundationCommand implements CommandExecutor {
	private final MazeGeneratorPlugin plugin;

	public FoundationCommand(MazeGeneratorPlugin plugin)
	{
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		int[] depthWidth = parseArgs(args);

		if (depthWidth.length < 2) {
			sendUsage(sender);
			return true;
		}

		int depth = depthWidth[0];
		int width = depthWidth[1];

		Player player = (Player) sender;
		World world = player.getWorld();
		if (player.isFlying()) {
			sender.sendMessage(ChatColor.RED + "You must be standing on the block you want to start from (not flying).");
			sendUsage(sender);
			return true;
		}

		BlockFace face = player.getFacing();
		BlockFace faceRight;
		switch(face) {
			case NORTH:
				faceRight = BlockFace.EAST;
				break;

			case EAST:
				faceRight = BlockFace.SOUTH;
				break;

			case SOUTH:
				faceRight = BlockFace.WEST;
				break;

			case WEST:
				faceRight = BlockFace.NORTH;
				break;

			default:
				sender.sendMessage(String.format(ChatColor.RED + "Couldn't figure out your facing, got %s", face.toString()));
				return true;
		}

		Location rootLocation = getPlayerStandOnBlockLocation(player.getLocation().add(0,-1,0));

		int forwardX = face.getModX();
		int forwardZ = face.getModZ();
		int rightX = faceRight.getModX();
		int rightZ = faceRight.getModZ();

		Material foundationMaterial = Material.BEDROCK;
		ItemStack heldItemStack = player.getInventory().getItemInMainHand();
		if (heldItemStack != null) {
			Material heldItem = heldItemStack.getType();
			if (heldItem.isSolid())
				foundationMaterial = heldItem;
		}

		for (int depthIndex = 0; depthIndex < depth; depthIndex++) {
			for (int widthIndex = 0; widthIndex < width; widthIndex++) {
				Location target = rootLocation.clone();
				target.add(
					forwardX * depthIndex + rightX * widthIndex,
					0,
					forwardZ * depthIndex + rightZ * widthIndex);
				world.getBlockAt(target).setType(foundationMaterial);
			}
		}

		return true;
	}

	private void sendUsage(CommandSender sender) {
		sender.sendMessage("Usage: /mazegen foundation [width] [height]");
		sender.sendMessage("Places a floor of [width]x[height], starting from the block you are standing on and extending ahead and to the right.");
		sender.sendMessage("If you are holding a block, the foundation will be made of that block; if you are not holding a block, it will be made of bedrock.");
	}

    private static Location getPlayerStandOnBlockLocation(Location locationUnderPlayer)
    {
        Location b11 = locationUnderPlayer.clone().add(0.3,0,-0.3);
        if (b11.getBlock().getType() != Material.AIR)
        {
            return b11;
        }
        Location b12 = locationUnderPlayer.clone().add(-0.3,0,-0.3);
        if (b12.getBlock().getType() != Material.AIR)
        {
            return b12;
        }
        Location b21 = locationUnderPlayer.clone().add(0.3,0,0.3);
        if (b21.getBlock().getType() != Material.AIR)
        {
            return b21;
        }
        Location b22 = locationUnderPlayer.clone().add(-0.3,0,+0.3);
        if (b22.getBlock().getType() != Material.AIR)
        {
            return b22;
        }
        return locationUnderPlayer;
    }

    private static int[] parseArgs(String[] args) {
		if (args.length < 3) {
			return new int[0];
		}

		int depth = -1;
		int width = -1;

		try {
			depth = Integer.parseInt(args[1]);
			width = Integer.parseInt(args[2]);
		} catch (NumberFormatException ex) {
			// Do nothing, we just assume it didn't parse
			return new int[0];
		}

		if (depth < 3 || width < 3) {
			return new int[0];
		}

		int[] depthWidth = new int[2];
		depthWidth[0] = depth;
		depthWidth[1] = width;
		return depthWidth;
	}
}
