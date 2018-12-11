package com.mcissoko.game.sudoku.core;

import java.io.Serializable;

/**
 * Immutable object 
 * 
 * @author Cissoko
 *
 *	Premiere position = (1,1)
 */
public class Coordinate implements Serializable{


	private static final long serialVersionUID = -7764733183225721586L;
	/**
	 * Debut de ligne = 1 (non 0)
	 * Fin de ligne = 3
	 */
	private final int line;
	/**
	 * Debut de colonne = 1 (non 0)
	 * Fin de colonne = 3
	 */
	private final int column;
	
	protected Coordinate(int line, int colunn) {
		super();
		this.line = line;
		this.column = colunn;
	}

	public int getLine() {
		return line;
	}

	public int getColumn() {
		return column;
	}

	@Override
	public String toString() {
		return line + "|" + column;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + column;
		result = prime * result + line;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coordinate other = (Coordinate) obj;
		if (column != other.column)
			return false;
		if (line != other.line)
			return false;
		return true;
	}
	
	
}
