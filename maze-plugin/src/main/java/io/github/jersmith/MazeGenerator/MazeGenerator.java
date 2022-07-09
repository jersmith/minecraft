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
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if (cmd.getName().equalsIgnoreCase("build")) { 
    		if (!(sender instanceof Player)) {
    			sender.sendMessage("This command can only be run by a player.");
    		} else {
    			Player player = (Player) sender;
    			String name = player.getDisplayName();
    			Location location = player.getLocation();
    			sender.sendMessage(name + " is at " + location);
    			location.add(10.0, 0.0, 0.0);
    			
    			int mazeSize = 16;
    			
    			if (args.length > 0) {
    				mazeSize = Integer.parseInt(args[0]);
    				if (mazeSize > 50) {
    					mazeSize = 50;
    				}
    			}
    			this.generateMaze(location, mazeSize);
    		}
    		
    		return true;
    	}
        
    	return false; 
    }
    
    public void generateMaze(Location loc, int length) {
    	PrimsMaze maze = new PrimsMaze(length);
    	boolean[][] grid = maze.grid();
    	
    	int gridLength = grid[0].length;
    	int entrance = getEntrance(grid);
    	int exit = getExit(grid);
    	
    	int height = loc.getBlockY();
    	int xStart = loc.getBlockX();
    	int zStart = loc.getBlockZ();
    	
    	World world = loc.getWorld();
    	
    	for (int i = 0; i < gridLength; i++) {
    		for (int j = 0; j < gridLength; j++) {
    			Block currentBlock = world.getBlockAt(xStart + j, height, zStart + i);
    			Block secondLayer = world.getBlockAt(xStart + j, height + 1, zStart + i);
    			if (grid[i][j] == true) {
    				currentBlock.setType(Material.AIR);
    				secondLayer.setType(Material.AIR);
    			} else {
    				currentBlock.setType(Material.STONE);
    				secondLayer.setType(Material.STONE);
    			}
    		}
    	}
    	
    	// Put a wall around the maze
    	for (int k = 0; k <= gridLength; k++) {
    		for (int l = 0; l <= gridLength; l++) {
    			Block south1 = world.getBlockAt(xStart - 1 + l, height, zStart - 1);
    			Block south2 = world.getBlockAt(xStart - 1 + l, height + 1, zStart - 1);
    			Block north1 = world.getBlockAt(xStart - 1 + l, height, zStart + gridLength);
    			Block north2 = world.getBlockAt(xStart - 1 + l, height + 1, zStart + gridLength);
    			Block west1 = world.getBlockAt(xStart - 1, height, zStart - 1 + k);
    			Block west2 = world.getBlockAt(xStart - 1, height + 1, zStart - 1 + k);
    			Block east1 = world.getBlockAt(xStart + gridLength, height, zStart - 1 + k);
    			Block east2 = world.getBlockAt(xStart + gridLength, height + 1, zStart - 1 + k);
    			
    			if (l != entrance) {
	    			south1.setType(Material.OBSIDIAN);
	    			south2.setType(Material.OBSIDIAN);
    			}
    			
    			if (l != exit) {
	    			north1.setType(Material.OBSIDIAN);
	    			north2.setType(Material.OBSIDIAN);
    			}
    			
    			west1.setType(Material.OBSIDIAN);
    			west2.setType(Material.OBSIDIAN);
    			east1.setType(Material.OBSIDIAN);
    			east2.setType(Material.OBSIDIAN);
    		}
    	}
    }
    
    public int getEntrance(boolean[][] grid) {
    	int gridLength = grid[0].length;
    	// Find the first opening on the South wall starting from center
    	for (int i = (int) gridLength/2; i < gridLength; i++) {
    		if (grid[0][i] == true) return i;
    	}
    	
    	return 0;
    }
    
    public int getExit(boolean[][]grid) {
       	int gridLength = grid[0].length;
    	// Find the first opening on the South wall starting from center
    	for (int i = (int) gridLength/2; i < gridLength; i++) {
    		if (grid[gridLength - 1][i] == true) return i;
    	}
    	
    	return 0;
    }
}
