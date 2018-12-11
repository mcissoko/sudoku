package com.mcissoko.game.sudoku.core;

import java.io.Serializable;

import com.mcissoko.game.sudoku.core.enumeration.GroupIndexEnum;
/**
 * Immutable Object
 * 
 * @author Cissoko
 *
 */
public class Position implements Serializable {

	private static final long serialVersionUID = 7252669053265697657L;
	private final PositionIndexEnum index;
	private final GroupIndexEnum groupIndex;
	private Coordinate coordinate;
	
	protected Position(GroupIndexEnum groupIndexEnum, PositionIndexEnum positionIndexEnum) {
		super();
		this.groupIndex = groupIndexEnum;
		this.index = positionIndexEnum;
		initCordinate();
	}
	
	private void initCordinate(){
		
		if(groupIndex.getIndex() <= 3){
			int column ;
			int facteur = 3*(groupIndex.getIndex() - 1);
			
			if(index.getIndex() <= 3){
				column = (index.getIndex()) + facteur;
				this.coordinate = new Coordinate(1, column);
			}else if(index.getIndex() > 3 && index.getIndex() <= 6){
				column = (index.getIndex() - 3) + facteur;
				this.coordinate = new Coordinate(2, column);
			}else{
				column = (index.getIndex() - 6) + facteur;
				this.coordinate = new Coordinate(3, column);
			}
		}else if(groupIndex.getIndex() > 3 && groupIndex.getIndex() <= 6){
			int x;
			if(index.getIndex() <= 3){
				x = index.getIndex() + 3*(groupIndex.getIndex() - 4);
				this.coordinate = new Coordinate(4, x);
			}else if(index.getIndex() > 3 && index.getIndex() <= 6){
				if(groupIndex.getIndex() == 4){
					x = index.getIndex() - 3 ;
				}else if(groupIndex.getIndex() == 5){
					x = index.getIndex();
				}else{
					x = index.getIndex() + 3;
				}
				
				this.coordinate = new Coordinate(5, x);
			}else{
				x = index.getIndex() - 3*(6 - groupIndex.getIndex());
				this.coordinate = new Coordinate(6, x);
			}
		}else if(groupIndex.getIndex() > 6  && groupIndex.getIndex() <= 9){
			int x;
			if(index.getIndex() <= 3){
				x = index.getIndex() + 3*(groupIndex.getIndex() - 7);
				this.coordinate = new Coordinate(7, x);
			}else if(index.getIndex() > 3 && index.getIndex() <= 6){
				if(groupIndex.getIndex() == 7){
					x = index.getIndex() - 3 ;
				}else if(groupIndex.getIndex() == 8){
					x = index.getIndex();
				}else{
					x = index.getIndex() + 3;
				}
				
				this.coordinate = new Coordinate(8, x);
			}else{
				x = index.getIndex() - 3*(9 - groupIndex.getIndex());
				this.coordinate = new Coordinate(9, x);
			}
		}
		
	}

	public Coordinate getCoordinate() {
		return coordinate;
	}

	public GroupIndexEnum getGroupIndex() {
		return groupIndex;
	}

	public PositionIndexEnum getIndex() {
		return index;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((coordinate == null) ? 0 : coordinate.hashCode());
		result = prime * result + ((groupIndex == null) ? 0 : groupIndex.hashCode());
		result = prime * result + ((index == null) ? 0 : index.hashCode());
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
		Position other = (Position) obj;
		if (coordinate == null) {
			if (other.coordinate != null)
				return false;
		} else if (!coordinate.equals(other.coordinate))
			return false;
		if (groupIndex != other.groupIndex)
			return false;
		if (index != other.index)
			return false;
		return true;
	}

	
	
	
}
