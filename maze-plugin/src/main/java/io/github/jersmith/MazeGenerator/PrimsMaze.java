package io.github.jersmith.MazeGenerator;

public class PrimsMaze {
	private int length;
	
	public PrimsMaze(int length) {
		this.length = length;
		
	}
	
	public boolean[][] grid() {
		boolean[][] g = new boolean[this.length][this.length];
		
		for(int i = 0; i < this.length; i++) {
			g[0][i] = true;
			g[i][0] = true;
			g[this.length - 1][i] = true;
			g[i][this.length - 1] = true;
			
			// Put one block offset in the square so we can see
			// orientation
			g[1][1] = true;
		}
		
		return g;
	}
}
