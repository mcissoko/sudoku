package com.mcissoko.game.sudoku.core;

public class ResultMapper {

	private boolean gridFilled;
	private Box candidate;
	public ResultMapper(boolean gridFilled, Box candidate) {
		super();
		this.gridFilled = gridFilled;
		this.candidate = candidate;
	}
	public boolean isGridFilled() {
		return gridFilled;
	}
	public void setGridFilled(boolean gridFilled) {
		this.gridFilled = gridFilled;
	}
	public Box getCandidate() {
		return candidate;
	}
	public void setCandidate(Box candidate) {
		this.candidate = candidate;
	}
	
	
}
