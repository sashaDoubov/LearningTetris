package tetris_2;
//Board.java      	 
import java.awt.*;
import java.util.*;


/**
 Represents a Tetris board -- essentially a 2-d grid
 of booleans. Supports tetris pieces and row clearning.
 Has an "undo" feature that allows clients to add and remove pieces efficiently.
 Does not do any drawing or have any idea of pixels. Intead,
 just represents the abtsract 2-d board.
  See Tetris-Architecture.html for an overview.
  
 This is the starter file version -- a few simple things are filled in already
  
 @author	Nick Parlante
 @version	1.0, Mar 1, 2001
*/
public final class Board  {
	private int width;
	private int height;
	
	private int widths[];
	private int heights[];
	
	private int xWidths[];
	private int xHeights[];
	
	private boolean[][] grid;
	private boolean[][] xGrid;
	
	private boolean DEBUG = false;
	
	private boolean committed;
	
	public int score;
	private int xScore;
	/**
	 Creates an empty board of the given width and height
	 measured in blocks.
	*/
	public int getScore(){
		return score;
	}
	public void resetScore(){
		score = 0;
		xScore = 0;
	}
	
	public Board(int aWidth, int aHeight) {
		score = 0;
		xScore = 0;
		
		width = aWidth;
		height = aHeight;

		grid = new boolean[width][height];
		xGrid = new boolean[width][height];
		
		widths = new int[height];
		heights = new int[width];
		
		xWidths = new int[height];
		xHeights = new int[width];
		
		committed = true;
	}
	
	
	/**
	 Returns the width of the board in blocks.
	*/
	public int getWidth() {
		return width;
	}
	
	
	/**
	 Returns the height of the board in blocks.
	*/
	public int getHeight() {
		return height;
	}
	
	
	/**
	 Returns the max column height present in the board.
	 For an empty board this is 0.
	*/
	public int getMaxHeight() {	
		
		int max = 0;
		for (int i = 0; i < width; i++)
		{
			max = Math.max(heights[i], max);
		}
		return max;
	}
	
	
	/**
	 Checks the board for internal consistency -- used
	 for debugging.
	*/
	public void sanityCheck() {
		if (DEBUG) {
			for (int i = 0; i < height; i ++)
			{
				int innerWidth = 0;
				
				for (int j = 0; j < width; j++)
				{
					if (grid[j][i])
						innerWidth++;
				}
				if (innerWidth != widths[i])
					throw new RuntimeException(String.format("grid width %d != widths %d",innerWidth, widths[i]));
			}
			
			int max = 0;
			for (int i = 0; i < width; i++){
				int innerHeight = 0;
				for (int j = 0; j < height; j++)
				{
					if (grid[i][j])
						innerHeight = j + 1;
				}
				if (innerHeight > max)
					max = innerHeight;
				if (innerHeight != heights[i])
					throw new RuntimeException(String.format("grid height %d != heights %d",innerHeight, heights[i]));			
			}
			
			if (getMaxHeight() != max)
				throw new RuntimeException(String.format("grid max %d != max %d",getMaxHeight(), max));
		}
	}
	public void checkClear()
	{
		if (DEBUG) {
			int oldFullRows = 0;
			int totalRows = 0;
			for (int i = 0; i < height; i++)
			{
				if (xWidths[i] == width)
					oldFullRows++;
			}
			System.out.println(String.format("old full rows %d",oldFullRows));
			int totalElemsOld = 0;
			for (int i = 0; i < width; i++)
			{
				for (int j = 0; j < height; j++)
				{
					if (xGrid[i][j])
						totalElemsOld++;
				}
				
			}
			int totalElemsNew  = 0;
			for (int i = 0; i < width; i++)
			{
				for (int j = 0; j < height; j++)
				{
					if (grid[i][j])
						totalElemsNew++;
				}
			}
			
			if (totalElemsNew + width*oldFullRows != totalElemsOld)
				throw new RuntimeException(String.format("New elems cleared  %d != old %d",totalElemsNew + width*oldFullRows, totalElemsOld));
			
		}
	}
	
	/**
	 Given a piece and an x, returns the y
	 value where the piece would come to rest
	 if it were dropped straight down at that x.
	 
	 <p>
	 Implementation: use the skirt and the col heights
	 to compute this fast -- O(skirt length).
	*/
	public int dropHeight(Piece piece, int x) {
		
		int[] skirt = piece.getSkirt();
		int maxRestingHeight = 0;
		
		for(int i = 0; i < piece.getWidth(); i++)
		{
			if (maxRestingHeight < heights[i + x] - skirt[i])
			{
				maxRestingHeight = heights[i + x] - skirt[i];
			}
		}
		//System.out.println("DROPPEDHEIGHT");
		//System.out.println(maxRestingHeight);
		return maxRestingHeight;
		//return (maxRestingHeight + skirtMin);
	}
	
	
	
	/**
	 Returns the height of the given column --
	 i.e. the y value of the highest block + 1.
	 The height is 0 if the column contains no blocks.
	*/
	public int getColumnHeight(int x) {
		return heights[x];
	}
	
	
	/**
	 Returns the number of filled blocks in
	 the given row.
	*/
	public int getRowWidth(int y) {
		return widths[y];
	}
	
	
	/**
	 Returns true if the given block is filled in the board.
	 Blocks outside of the valid width/height area
	 always return true.
	*/
	public final boolean getGrid(int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height)
			return true;
		
		return grid[x][y];
	}
	public void performBackup()
	{
		xScore = score;
		
		System.arraycopy(heights, 0, xHeights, 0, width);
		System.arraycopy(widths, 0, xWidths, 0, height);
		
		for (int i = 0; i < width; i++)
		{
			System.arraycopy(grid[i], 0, xGrid[i], 0, height);
		}
	}
	
	public static final int PLACE_OK = 0;
	public static final int PLACE_ROW_FILLED = 1;
	public static final int PLACE_OUT_BOUNDS = 2;
	public static final int PLACE_BAD = 3;
	
	/**
	 Attempts to add the body of a piece to the board.
	 Copies the piece blocks into the board grid.
	 Returns PLACE_OK for a regular placement, or PLACE_ROW_FILLED
	 for a regular placement that causes at least one row to be filled.
	 
	 <p>Error cases:
	 If part of the piece would fall out of bounds, the placement
	 does not change the board at all, and PLACE_OUT_BOUNDS is returned.
	 If the placement is "bad" --interfering with existing blocks in the grid --
	 then the placement is halted partially complete and PLACE_BAD is returned.
	 An undo() will remove the bad placement.
	*/
	

	public int place(Piece piece, int x, int y) {
		
		if (!committed)
			return PLACE_BAD;
		
		Point[] pcBody = piece.getBody();
		boolean filled = false;
		
		performBackup();
		
		// important, set the commit here (for bad placement, or fill)
		committed = false;
		
		// validate board
		for (int i = 0; i < pcBody.length;i++)
		{
			int xCoord = (int)pcBody[i].getX() + x;
			int yCoord = (int)pcBody[i].getY() + y;
						
			if (xCoord >= width || yCoord >= height || xCoord < 0 || yCoord < 0)
				return PLACE_OUT_BOUNDS;
		}

		for (int i = 0; i < pcBody.length;i++)
		{
			int xCoord = (int)pcBody[i].getX() + x;
			int yCoord = (int)pcBody[i].getY() + y;
			
			

			if (grid[xCoord][yCoord])
				return PLACE_BAD;
			
			
			grid[xCoord][yCoord] = true;
						
			widths[yCoord]++;
			
			heights[xCoord] = Math.max(heights[xCoord], yCoord + 1);
			
			if (widths[yCoord] == width)
				filled = true;
			
		}
		
		sanityCheck();
		
		if (filled)
			return PLACE_ROW_FILLED;
		return PLACE_OK;
		
	}

	/**
	 Deletes rows that are filled all the way across, moving
	 things above down. Returns true if any row clearing happened.
	 
	 <p>Implementation: This is complicated.
	 Ideally, you want to copy each row down
	 to its correct location in one pass.
	 Note that more than one row may be filled.
	*/
	public boolean clearRows() {
		if (committed){
			performBackup();
			committed = false;
		}
		
		int firstFilledRow = -1;
		int emptyRow = getMaxHeight();
		
		for (int i = 0; i < height; i++)
		{
			if (widths[i] == width){
				firstFilledRow = i;
				// increase score
				score++;
				break;
			}
			
		}
		if (firstFilledRow == -1)
			return false;

		// performing the array copies
		int fromInd = firstFilledRow + 1;
		
		for (int to = firstFilledRow; to < emptyRow; to++)
		{ 
			// if it's filled, skip that row
			while(widths[fromInd] == width){
					// increase score
					score++;
					fromInd++;
			}
			
			for (int j = 0; j < width; j++)
			{
				grid[j][to] = grid[j][fromInd];
			}
			widths[to] = widths[fromInd];
			
			fromInd++;
			
		}
		
		// reseting the height arrays
		for (int i = 0; i < width; i++)
		{
			boolean changedHeight = false;
			// upperbound by the previous element
			for (int j = heights[i]; j >= 0; j--)
			{
				// if it's true
				if (grid[i][j])
				{
					heights[i] = j + 1;
					changedHeight = true;
					break;
				}
			}
			if (!changedHeight)
				heights[i] = 0;
		}
		//checkClear();
		sanityCheck();
		committed = false;
		return true;
	}

	
	

	/**
	 If a place() happens, optionally followed by a clearRows(),
	 a subsequent undo() reverts the board to its state before
	 the place(). If the conditions for undo() are not met, such as
	 calling undo() twice in a row, then the second undo() does nothing.
	 See the overview docs.
	*/
	public void undo() {
		if (committed)
			return;
		Object temp;
		
		temp = widths;
		widths  = xWidths;
		xWidths = (int []) temp;
		
		temp = heights;
		heights = xHeights;
		xHeights = (int []) temp;
		
		temp = grid;
		grid = xGrid;
		xGrid = (boolean [][])temp;
		
		score = xScore;
		
		committed = true;
		
		sanityCheck();
	}
	
	
	/**
	 Puts the board in the committed state.
	 See the overview docs.
	*/
	public void commit() {
		committed = true;
		return;
	}
}


