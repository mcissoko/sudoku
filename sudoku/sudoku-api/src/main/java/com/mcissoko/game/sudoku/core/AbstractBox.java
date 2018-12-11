package com.mcissoko.game.sudoku.core;

import java.util.Set;

import com.mcissoko.game.sudoku.core.enumeration.StateBoxEnum;

public abstract class AbstractBox {
	protected Set<Integer> candidates;
	protected StateBoxEnum state;

	protected Set<Integer> triedCandidates;
}
