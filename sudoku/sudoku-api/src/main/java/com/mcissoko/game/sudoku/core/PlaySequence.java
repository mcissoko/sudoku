package com.mcissoko.game.sudoku.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mcissoko.game.sudoku.core.enumeration.GroupIndexEnum;

class PlaySequence {

	private GroupIndexEnum groupIndex;
	private  PositionIndexEnum positionIndex;
	private Integer sellectedCandidate;
	private Set<Integer> candidates;
	
	private List<Sequence> sequences;

	PlaySequence(GroupIndexEnum groupIndex, PositionIndexEnum positionIndex, Integer sellectedCandidate, Set<Integer> candidates) {
		super();
		this.groupIndex = groupIndex;
		this.positionIndex = positionIndex;
		this.sellectedCandidate = sellectedCandidate;
		
		this.candidates = new HashSet<>(candidates);
		
		this.sequences = new ArrayList<>();
		
	}

	PlaySequence(GroupIndexEnum groupIndex, PositionIndexEnum positionIndex) {
		super();
		this.groupIndex = groupIndex;
		this.positionIndex = positionIndex;
	}
	
	protected List<Sequence> getSequences() {
		return sequences;
	}

	protected void setSequences(List<Sequence> sequences) {
		this.sequences = sequences;
	}

	protected GroupIndexEnum getGroupIndex() {
		return groupIndex;
	}

	protected PositionIndexEnum getPositionIndex() {
		return positionIndex;
	}

	protected Integer getSellectedCandidate() {
		return sellectedCandidate;
	}

	protected Set<Integer> getCandidates() {
		return candidates;
	}
	
	
}
