package com.mcissoko.game.sudoku.core;

import com.mcissoko.game.sudoku.client.Sudoku;

public class SudokuFactory {


	public static Sudoku create() {
		return new SudokuImpl();
	}

}
