package io.github.jersmith.MazeGenerator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class MazeGenerator extends JavaPlugin {
    @Override
    public void onEnable() {
    }
    
    @Override
    public void onDisable() {
        // TODO Insert logic to be performed when the plugin is disabled
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if (cmd.getName().equalsIgnoreCase("mazegen")) { 
    		if (!(sender instanceof Player)) {
    			sender.sendMessage("This command can only be run by a player.");
    		} else {
    			Player player = (Player) sender;
    			String name = player.getDisplayName();
    			Location location = player.getLocation();
    			sender.sendMessage(name + " is at " + location);
    			location.add(10.0, 0.0, 0.0);
    			this.generateMaze(location, 8);
    		}
    		
    		return true;
    	}
        
    	return false; 
    }
    
    public void generateCube(Location loc, int length) {
        // Set one corner of the cube to the given location.
        // Uses getBlockN() instead of getN() to avoid casting to an int later.
        int x1 = loc.getBlockX(); 
        int y1 = loc.getBlockY();
        int z1 = loc.getBlockZ();

        // Figure out the opposite corner of the cube by taking the corner and adding length to all coordinates.
        int x2 = x1 + length;
        int y2 = y1 + length;
        int z2 = z1 + length;

        World world = loc.getWorld();

        // Loop over the cube in the x dimension.
        for (int xPoint = x1; xPoint <= x2; xPoint++) { 
            // Loop over the cube in the y dimension.
            for (int yPoint = y1; yPoint <= y2; yPoint++) {
                // Loop over the cube in the z dimension.
                for (int zPoint = z1; zPoint <= z2; zPoint++) {
                    // Get the block that we are currently looping over.
                    Block currentBlock = world.getBlockAt(xPoint, yPoint, zPoint);
                    currentBlock.setType(Material.DIRT);
                }
            }
        }
    }
    
    public void generateMaze(Location loc, int length) {
    	PrimsMaze maze = new PrimsMaze(length);
    	boolean[][] grid = maze.grid();
    	
    	int height = loc.getBlockY();
    	int xStart = loc.getBlockX();
    	int zStart = loc.getBlockZ();
    	
    	World world = loc.getWorld();
    	
    	for (int i = 0; i < length; i++) {
    		for (int j = 0; j < length; j++) {
    			if (grid[i][j]) {
    				Block currentBlock = world.getBlockAt(xStart + j, height, zStart + i);
    				currentBlock.setType(Material.DIRT);
    			}
    		}
    	}
    }
}
