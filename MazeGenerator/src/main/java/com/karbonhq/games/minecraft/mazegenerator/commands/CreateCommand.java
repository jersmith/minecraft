package com.karbonhq.games.minecraft.mazegenerator.commands;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Door;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

import com.karbonhq.games.minecraft.mazegenerator.MazeGeneratorPlugin;

import net.md_5.bungee.api.ChatColor;

public class CreateCommand implements CommandExecutor {
	private final MazeGeneratorPlugin plugin;

	public CreateCommand(MazeGeneratorPlugin plugin)
	{
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player) sender;
		World world = player.getWorld();
		RayTraceResult lookingAt = player.rayTraceBlocks(10);
		Block hitBlock = (lookingAt != null ? lookingAt.getHitBlock() : null);

		if (hitBlock == null) {
			sender.sendMessage(ChatColor.RED + "You must be looking at a maze foundation within 10 blocks.");
			sendUsage(sender);
			return true;
		}

		int[] foundationDimensions = findBestRectangle(world, hitBlock);
		if (foundationDimensions.length == 0) {
			sender.sendMessage(ChatColor.RED + "You must be looking at a square of at least 3x3.");
			sendUsage(sender);
			return true;
		}

		placeOuterWalls(world, hitBlock, foundationDimensions);
		placeRoof(world, hitBlock, foundationDimensions);
		placeInsides(world, hitBlock, foundationDimensions);

		return true;
	}

	private void sendUsage(CommandSender sender) {
		sender.sendMessage("Usage: /mazegen create");
		sender.sendMessage("Creates a maze on the foundation the player is looking at. The walls and roof will be made of the same material as the foundation.");
		sender.sendMessage("Doors placed on the edges of the maze area will be left intact and torches placed next to them.");
		sender.sendMessage("All existing blocks inside the maze will be destroyed.");
	}

	private int[] findBestRectangle(World world, Block hitBlock) {
		int baseX = hitBlock.getX();
		int baseY = hitBlock.getY();
		int baseZ = hitBlock.getZ();
		Material baseType = hitBlock.getType();
		Block targetBlock;

		// Start by finding the biggest square centered on the target
		int squareRadius = 0;
		boolean foundMinXEdge = false;
		boolean foundMaxXEdge = false;
		boolean foundMinZEdge = false;
		boolean foundMaxZEdge = false;

		// TODO: Arbitrary maximum size of 30
		while (squareRadius < 30 && !(
			foundMinXEdge || foundMaxXEdge || foundMinZEdge || foundMaxZEdge
		)) {
			squareRadius++;
			// Check first row
			for (int x = baseX - squareRadius; x <= baseX + squareRadius; x++) {
				targetBlock = world.getBlockAt(x, baseY, baseZ - squareRadius);
				if (targetBlock.getType() != baseType) {
					foundMinZEdge = true;
					break;
				}
			}

			// Check last row
			for (int x = baseX - squareRadius; x <= baseX + squareRadius; x++) {
				targetBlock = world.getBlockAt(x, baseY, baseZ + squareRadius);
				if (targetBlock.getType() != baseType) {
					foundMaxZEdge = true;
					break;
				}
			}

			// Check first column
			for (int z = baseZ - squareRadius; z <= baseZ + squareRadius; z++) {
				targetBlock = world.getBlockAt(baseX - squareRadius, baseY, z);
				if (targetBlock.getType() != baseType) {
					foundMinXEdge = true;
					break;
				}
			}

			// Check last column
			for (int z = baseZ - squareRadius; z <= baseZ + squareRadius; z++) {
				targetBlock = world.getBlockAt(baseX + squareRadius, baseY, z);
				if (targetBlock.getType() != baseType) {
					foundMaxXEdge = true;
					break;
				}
			}
		}
		squareRadius--;
		if (squareRadius < 1)
			return new int[0];

		int minX = baseX - squareRadius;
		int maxX = baseX + squareRadius;
		int minZ = baseZ - squareRadius;
		int maxZ = baseZ + squareRadius;

		// Arbitrarily try to grow in -X, +X, -Z, +Z
		foundMinXEdge = false;
		while (!foundMinXEdge) {
			minX--;
			for (int z = minZ; z <= maxZ; z++) {
				targetBlock = world.getBlockAt(minX, baseY, z);
				if (targetBlock.getType() != baseType) {
					foundMinXEdge = true;
					break;
				}
			}
		}
		minX++;

		foundMaxXEdge = false;
		while (!foundMaxXEdge) {
			maxX++;
			for (int z = minZ; z <= maxZ; z++) {
				targetBlock = world.getBlockAt(maxX, baseY, z);
				if (targetBlock.getType() != baseType) {
					foundMaxXEdge = true;
					break;
				}
			}
		}
		maxX--;

		foundMinZEdge = false;
		while (!foundMinZEdge) {
			minZ--;
			for (int x = minX; x <= maxX; x++) {
				targetBlock = world.getBlockAt(x, baseY, minZ);
				if (targetBlock.getType() != baseType) {
					foundMinZEdge = true;
					break;
				}
			}
		}
		minZ++;

		foundMaxZEdge = false;
		while (!foundMaxZEdge) {
			maxZ++;
			for (int x = minX; x <= maxX; x++) {
				targetBlock = world.getBlockAt(x, baseY, maxZ);
				if (targetBlock.getType() != baseType) {
					foundMaxZEdge = true;
					break;
				}
			}
		}
		maxZ--;

		return new int[] { minX, maxX, minZ, maxZ };
	}

	private void placeOuterWalls(World world, Block hitBlock, int[] foundationDimensions) {
		int minX = foundationDimensions[0];
		int maxX = foundationDimensions[1];
		int baseY = hitBlock.getY();
		int minZ = foundationDimensions[2];
		int maxZ = foundationDimensions[3];
		Block targetBlock;

		for (int y = 1; y <= 2; y++)
		{
			// Place walls (leaving doors alone)
			for (int x = minX; x <= maxX; x++) {
				targetBlock = world.getBlockAt(x, baseY + y, minZ);
				if (isDoor(targetBlock)) {
					if (y == 2) {
						if (x > minX && !isDoor(world, x - 1, baseY + y, minZ)) {
							placeTorch(world, x - 1, baseY + y, minZ - 1, BlockFace.NORTH);
						}
						if (x < maxX && !isDoor(world, x + 1, baseY + y, minZ)) {
							placeTorch(world, x + 1, baseY + y, minZ - 1, BlockFace.NORTH);
						}
					}
				} else
					targetBlock.setType(hitBlock.getType());

				targetBlock = world.getBlockAt(x, baseY + y, maxZ);
				if ((targetBlock.getBlockData() instanceof Door)) {
					if (y == 2) {
						if (x > minX && !isDoor(world, x - 1, baseY + y, maxZ)) {
							placeTorch(world, x - 1, baseY + y, maxZ + 1, BlockFace.SOUTH);
						}
						if (x < maxX && !isDoor(world, x + 1, baseY + y, maxZ)) {
							placeTorch(world, x + 1, baseY + y, maxZ + 1, BlockFace.SOUTH);
						}
					}
				} else
					targetBlock.setType(hitBlock.getType());
			}

			for (int z = minZ; z <= maxZ; z++) {
				targetBlock = world.getBlockAt(minX, baseY + y, z);
				if ((targetBlock.getBlockData() instanceof Door)) {
					if (y == 2) {
						if (z > minZ && !isDoor(world, minX, baseY + y, z - 1)) {
							placeTorch(world, minX - 1, baseY + y, z - 1, BlockFace.WEST);
						}
						if (z < maxZ && !isDoor(world, minX, baseY + y, z + 1)) {
							placeTorch(world, minX - 1, baseY + y, z + 1, BlockFace.WEST);
						}
					}
				} else
					targetBlock.setType(hitBlock.getType());

				targetBlock = world.getBlockAt(maxX, baseY + y, z);
				if ((targetBlock.getBlockData() instanceof Door)) {
					if (y == 2) {
						if (z > minZ && !isDoor(world, maxX, baseY + y, z - 1)) {
							placeTorch(world, maxX + 1, baseY + y, z - 1, BlockFace.EAST);
						}
						if (z < maxZ && !isDoor(world, maxX, baseY + y, z + 1)) {
							placeTorch(world, maxX + 1, baseY + y, z + 1, BlockFace.EAST);
						}
					}
				} else
					targetBlock.setType(hitBlock.getType());
			}
		}
	}

	private boolean isDoor(Block targetBlock) {
		return (targetBlock.getBlockData() instanceof Door);
	}

	private boolean isDoor(World world, int x, int y, int z) {
		return isDoor(world.getBlockAt(x, y, z));
	}

	private void placeTorch(World world, int x, int y, int z, BlockFace blockFace) {
		Block torchBlock = world.getBlockAt(x, y, z);
		if (!(torchBlock.getType() == Material.AIR))
			return;

		torchBlock.setType(Material.WALL_TORCH);
		Directional torchDirection = (Directional) torchBlock.getBlockData();
		torchDirection.setFacing(blockFace);
		torchBlock.setBlockData(torchDirection);
	}

	private void placeRoof(World world, Block hitBlock, int[] foundationDimensions) {
		int minX = foundationDimensions[0];
		int maxX = foundationDimensions[1];
		int baseY = hitBlock.getY();
		int minZ = foundationDimensions[2];
		int maxZ = foundationDimensions[3];
		Block targetBlock;

		for (int x = minX; x <= maxX; x++)
			for (int z = minZ; z <= maxZ; z++) {
				targetBlock = world.getBlockAt(x, baseY + 3, z);
				targetBlock.setType(hitBlock.getType());
			}
	}

	private void placeInsides(World world, Block hitBlock, int[] foundationDimensions) {
		int minX = foundationDimensions[0];
		int maxX = foundationDimensions[1];
		int baseY = hitBlock.getY();
		int minZ = foundationDimensions[2];
		int maxZ = foundationDimensions[3];
		Block targetBlock;

		for (int y = 1; y <= 2; y++)
			for (int x = minX + 1; x < maxX; x++)
				for (int z = minZ + 1; z < maxZ; z++) {
					targetBlock = world.getBlockAt(x, baseY + y, z);
					targetBlock.setType(Material.AIR);
				}

		// TODO: Make an actual maze
	}
}
