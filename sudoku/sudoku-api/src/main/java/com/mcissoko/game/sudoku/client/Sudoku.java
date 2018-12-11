package com.mcissoko.game.sudoku.client;

import com.mcissoko.game.sudoku.core.Box;
import com.mcissoko.game.sudoku.core.Grid;
import com.mcissoko.game.sudoku.core.IMonitor;
import com.mcissoko.game.sudoku.core.IPromise;
import com.mcissoko.game.sudoku.core.enumeration.SudokuLevelEnum;

public interface Sudoku {

	
	/**
	 * Asynchronously solve the given Sudoku grid, by showing evolving result through the given monitor.
	 * 
	 * @param grid required
	 * @param mon can be null
	 * @return A promise 
	 */
	IPromise resolveAsyn (Grid grid, IMonitor mon);
	
	
	/**
	 * Asynchronously build a solvable sudoku grid depending on the given level
	 * 
	 * @param level 
	 * @return A promise
	 */
	IPromise newGridAsync(SudokuLevelEnum level);
	
	/**
	 * Solve the given Sudoku grid, by showing evolving result through the given monitor.
	 * 
	 * @param grid required
	 * @param mon can be null
	 * @return solve grid
	 */
	Grid resolve (Grid grid, IMonitor mon);
	
	/**
	 * Build a solvable sudoku grid depending on the given level
	 * 
	 * @param level 
	 * @return grid
	 */
	Grid newGrid(SudokuLevelEnum level);
	
	
	/**
	 * Get current grid
	 * @return
	 */
	Grid getGrid();
	
	
	/**
	 * Reset the content of a given box; set content to EMPTY ("").
	 * @param box
	 * @return
	 */
	Box resetContent(Box box);

}
