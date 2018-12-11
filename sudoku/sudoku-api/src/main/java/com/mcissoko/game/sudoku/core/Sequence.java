package com.mcissoko.game.sudoku.core;

import com.mcissoko.game.sudoku.core.PositionIndexEnum;
import com.mcissoko.game.sudoku.core.enumeration.GroupIndexEnum;

public class Sequence {

	private final GroupIndexEnum groupIndex;
	private final  PositionIndexEnum positionIndex;
	private final Integer newContent;
	private final Integer oldContent;
	
	public Sequence(GroupIndexEnum groupIndex, PositionIndexEnum positionIndex) {
		super();
		this.groupIndex = groupIndex;
		this.positionIndex = positionIndex;
		this.newContent = null;
		this.oldContent = null;
	}
	
	public Sequence(GroupIndexEnum groupIndex, PositionIndexEnum positionIndex, Integer newContent, Integer oldContent) {
		super();
		this.groupIndex = groupIndex;
		this.positionIndex = positionIndex;
		this.newContent = newContent;
		this.oldContent = oldContent;
	}
	public GroupIndexEnum getGroupIndex() {
		return groupIndex;
	}
	public PositionIndexEnum getPositionIndex() {
		return positionIndex;
	}
	public Integer getOldContent() {
		return oldContent;
	}
	public Integer getNewContent() {
		return newContent;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((groupIndex == null) ? 0 : groupIndex.hashCode());
		result = prime * result + ((newContent == null) ? 0 : newContent.hashCode());
		result = prime * result + ((oldContent == null) ? 0 : oldContent.hashCode());
		result = prime * result + ((positionIndex == null) ? 0 : positionIndex.hashCode());
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
		Sequence other = (Sequence) obj;
		if (groupIndex != other.groupIndex)
			return false;
		if (newContent == null) {
			if (other.newContent != null)
				return false;
		} else if (!newContent.equals(other.newContent))
			return false;
		if (oldContent == null) {
			if (other.oldContent != null)
				return false;
		} else if (!oldContent.equals(other.oldContent))
			return false;
		if (positionIndex != other.positionIndex)
			return false;
		return true;
	}
	
	
	@Override
	public String toString() {
		
		return "["+groupIndex+"|"+positionIndex+"|"+newContent+"|"+oldContent+"]";
	}

}
