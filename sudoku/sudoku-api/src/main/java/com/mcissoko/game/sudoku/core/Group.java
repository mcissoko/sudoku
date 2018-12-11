package com.mcissoko.game.sudoku.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.mcissoko.game.sudoku.core.enumeration.GroupIndexEnum;
import com.mcissoko.game.sudoku.core.enumeration.StateBoxEnum;

public class Group implements Serializable{

	private static final long serialVersionUID = 3330283792812169026L;
	private final GroupIndexEnum index;
	private Map<PositionIndexEnum, Box> boxes;
	
	private GroupIndexEnum[] groupLine;
	private GroupIndexEnum[] groupColumn;
	
	private Grid grid;
		
	protected Group(GroupIndexEnum groupIndex, Grid grid) {
		this.index = groupIndex;
		this.init();
		this.grid = grid;
		
	}

	private void init(){
		boxes = new HashMap<>();
		
		for(PositionIndexEnum position: PositionIndexEnum.values()){
			boxes.put(position, new Box(new Position(index, position), this));
		}		
		initGroupLine();
		initGroupColumn();
	}
	
	private void initGroupLine(){
		groupLine = new GroupIndexEnum[2];
		switch (this.index) {
		case INDEX_1:
			groupLine[0] = GroupIndexEnum.INDEX_2;
			groupLine[1] = GroupIndexEnum.INDEX_3;
			break;
		case INDEX_2:
			groupLine[0] = GroupIndexEnum.INDEX_1;
			groupLine[1] = GroupIndexEnum.INDEX_3;
			break;
			
		case INDEX_3:
			groupLine[0] = GroupIndexEnum.INDEX_1;
			groupLine[1] = GroupIndexEnum.INDEX_2;
			break;
		case INDEX_4:
			groupLine[0] = GroupIndexEnum.INDEX_5;
			groupLine[1] = GroupIndexEnum.INDEX_6;
			break;
		case INDEX_5:
			groupLine[0] = GroupIndexEnum.INDEX_4;
			groupLine[1] = GroupIndexEnum.INDEX_6;
			break;	
		case INDEX_6:
			groupLine[0] = GroupIndexEnum.INDEX_4;
			groupLine[1] = GroupIndexEnum.INDEX_5;
			break;
		case INDEX_7:
			groupLine[0] = GroupIndexEnum.INDEX_8;
			groupLine[1] = GroupIndexEnum.INDEX_9;
			break;	
		case INDEX_8:
			groupLine[0] = GroupIndexEnum.INDEX_7;
			groupLine[1] = GroupIndexEnum.INDEX_9;
			break;
		case INDEX_9:
			groupLine[0] = GroupIndexEnum.INDEX_7;
			groupLine[1] = GroupIndexEnum.INDEX_8;
			break;	
		default:
			break;
		}

		
	}
	
	private void initGroupColumn(){
		groupColumn = new GroupIndexEnum[2];
		switch (this.index) {
		case INDEX_1:
			groupColumn[0] = GroupIndexEnum.INDEX_4;
			groupColumn[1] = GroupIndexEnum.INDEX_7;
			break;
		case INDEX_2:
			groupColumn[0] = GroupIndexEnum.INDEX_5;
			groupColumn[1] = GroupIndexEnum.INDEX_8;
			break;
			
		case INDEX_3:
			groupColumn[0] = GroupIndexEnum.INDEX_6;
			groupColumn[1] = GroupIndexEnum.INDEX_9;
			break;
		case INDEX_4:
			groupColumn[0] = GroupIndexEnum.INDEX_1;
			groupColumn[1] = GroupIndexEnum.INDEX_7;
			break;
		case INDEX_5:
			groupColumn[0] = GroupIndexEnum.INDEX_2;
			groupColumn[1] = GroupIndexEnum.INDEX_8;
			break;	
		case INDEX_6:
			groupColumn[0] = GroupIndexEnum.INDEX_3;
			groupColumn[1] = GroupIndexEnum.INDEX_9;
			break;
		case INDEX_7:
			groupColumn[0] = GroupIndexEnum.INDEX_1;
			groupColumn[1] = GroupIndexEnum.INDEX_4;
			break;	
		case INDEX_8:
			groupColumn[0] = GroupIndexEnum.INDEX_2;
			groupColumn[1] = GroupIndexEnum.INDEX_5;
			break;
		case INDEX_9:
			groupColumn[0] = GroupIndexEnum.INDEX_3;
			groupColumn[1] = GroupIndexEnum.INDEX_6;
			break;	
		default:
			break;
		}
		
		
		
	}
	
	
	/**
	 * Vérifie si le numero n'est pas présente plus d'une fois dans le groupement
	 * @param content
	 * @return
	 */
	protected boolean isContentUniqueInGroup(Box box, List<Box> occurrences) {
		int content = box.getContent();
		int occurrenceCount = 0;
		for(Box b : boxes.values()){
			if (b.sizeOfCandidate() == 1 && b.getState() == StateBoxEnum.EMPTY){
				if(b.getCandidates().iterator().next().equals(content)){
					occurrenceCount++;
				}
			}else{
				if (b.getContent().equals(content)) {
					if(occurrences != null){
						occurrences.add(b);
					}
					occurrenceCount++;
				}				
			}
		}
		
		return occurrenceCount == 1;
	}
	
	public boolean isContentUnique(Box box, List<Box> occurrences) {
		
		return isContentUniqueInGroup(box, occurrences) && this.grid.isNumberInGrilleLine(box, occurrences) && this.grid.isNumberInGrilleColumn(box, occurrences);
	}
	
	protected boolean notExist(int content) {
		
		for(Box box : boxes.values()){
			if (box.getContent() == content) {
				return false;
			}
		}
		
		return true;
	}


	protected boolean isNumberInGroupLine(Box box, List<Box> occurrences) {
		int valeur = box.getContent();
		int ligne = box.getPosition().getCoordinate().getLine();
		
		for(Box b : boxes.values()){
		
			if(b.getPosition().getCoordinate().getLine() == ligne){
				if (b.sizeOfCandidate() == 1 && b.getState() == StateBoxEnum.EMPTY){
					if(b.getCandidates().iterator().next().equals(valeur)){
						return true;
					}
				}else{
					if(b.getContent().equals(valeur)){
						if(occurrences != null){
							occurrences.add(b);
						}
						return true;					
					}
				}
			}
		}
		return false;
	}
	protected void removeCandidateInGroupLine(Box filled, PlaySequence playSequence) {
		int line = filled.getPosition().getCoordinate().getLine();
		for(Box box : boxes.values()){
			if(box.getPosition().getCoordinate().getLine() == line){
				if(box.hasCandidate(filled.getContent())){
					box.removeCandidate(filled.getContent(), playSequence);
				}
			}
		}
	}
	
	protected void removeCandidateInGroupLine(Box filled) {
		int line = filled.getPosition().getCoordinate().getLine();
		for (GroupIndexEnum gi : this.groupLine) {
			Group group = this.grid.getGroup(gi);
			for (Box box : group.getBoxes().values()) {
				if (box.getPosition().getCoordinate().getLine() == line) {
					if (box.hasCandidate(filled.getContent())) {
						box.removeCandidate(filled.getContent());
					}
				}
			}
		}
	}
	
	protected boolean isNumberInGroupColumn(Box box, List<Box> occurrences) {
		int valeur = box.getContent();
		int colonne = box.getPosition().getCoordinate().getColumn();
		
		for(Box b : boxes.values()){
			
			if(b.getPosition().getCoordinate().getColumn() == colonne){
				if (b.sizeOfCandidate() == 1 && b.getState() == StateBoxEnum.EMPTY){
					if(b.getCandidates().iterator().next().equals(valeur)){
						return true;
					}
				}else{ 
					if(b.getContent().equals(valeur)){
						if(occurrences != null){
							occurrences.add(b);
						}
						return true;					 
					}
				}
			}
		}
		return false;
	}
	
	protected void removeCandidate(Box filled) {
		boxes.values().forEach(c -> c.getCandidates().remove(filled.getContent()));
	}
	
	protected void removeCandidateInGroupColumn(Box filled, PlaySequence playSequence) {
		int column = filled.getPosition().getCoordinate().getColumn();
		for(Box box : boxes.values()){
			if(box.getPosition().getCoordinate().getColumn() == column){

				if(box.hasCandidate(filled.getContent())){
					box.removeCandidate(filled.getContent(), playSequence);
				}
			}
		}
		
	}
	
	protected void removeCandidateInGroupColumn(Box filled) {
		int column = filled.getPosition().getCoordinate().getColumn();
		for(GroupIndexEnum gi: this.groupColumn) {
			Group group = this.grid.getGroup(gi);
			for(Box box : group.getBoxes().values()){
				if(box.getPosition().getCoordinate().getColumn() == column){

					if(box.hasCandidate(filled.getContent())){
						box.removeCandidate(filled.getContent());
					}
				}
			}
		}
		
	}
	
	public boolean isValid() {
		
		List<Box> empty = boxes.values().parallelStream().filter(c -> c.getState() == StateBoxEnum.EMPTY).collect(Collectors.toList());
		if(!empty.isEmpty()) {			
			List<Integer> filledList = boxes.values().parallelStream().filter(c -> c.getState() != StateBoxEnum.EMPTY).map(Box::getContent).collect(Collectors.toList()); 
			List<Integer> toFill = IntStream.rangeClosed(1, 9).boxed().filter(i -> !filledList.contains(i)).collect(Collectors.toList()); 
			for(Integer i: toFill) {
				boolean found = false;
				for(Box box: empty) {
					if(box.getCandidates().contains(i)) {
						found = true;
					}
				}
				if(!found) {
					return false;
				}
			}
		}
		
		return true;
		
		/*
		//List<Box> empty = cases.values().parallelStream().filter(c -> c.getState() == StateBoxEnum.EMPTY).collect(Collectors.toList());
		List<Integer> filledList = cases.values().parallelStream()
				.filter(c -> c.getState() != StateBoxEnum.EMPTY)
				.map(Box::getContent).collect(Collectors.toList()); 
		
		Set<Integer> candidates = cases.values().parallelStream()
				.filter(c -> c.getState() == StateBoxEnum.EMPTY)
				.map(Box::getCandidates).collect(HashSet::new, Set::addAll, Set::addAll);
		boolean possible = IntStream.rangeClosed(1, 9).boxed().filter(i -> !filledList.contains(i)).filter(j -> !candidates.contains(j)).count() <= 0;
		if(!possible) {
			return false;
		}
		//*/
	}
		
	/**
	 * Vérifie si le groupement est conforme au sudoku:
	 * chacune des 9 cases a un contenu unique
	 * et le contenu de chaque case est unique sur la ligne/colonne
	 * globale de la grille
	 * 
	 * 
	 * @param grille
	 * @return
	 */
	protected boolean isSudoku(){
		
		for(Box box : boxes.values()){
			int content = box.getContent();
			if (content > 0 && isContentUnique(box, null)) {
				continue;
			}else{
				return false;
			}
		}
		
		return true;
	}
	
	protected void removeCandidate(Box filled, PlaySequence playSequence){
		PositionIndexEnum index = filled.getPosition().getIndex();
		for(PositionIndexEnum positionIndexEnum: PositionIndexEnum.values()){
			Box box = boxes.get(positionIndexEnum);
			
			if (index == box.getPosition().getIndex()) {
				continue;
			}
			
			if(box.hasCandidate(filled.getContent())){
				box.removeCandidate(filled.getContent(), playSequence);
			}
		}
		Stream.of(groupLine).forEach(line -> grid.getGroup(line).removeCandidateInGroupLine(filled, playSequence));
		Stream.of(groupColumn).forEach(column -> grid.getGroup(column).removeCandidateInGroupColumn(filled, playSequence));
	}
	
	protected void resetContent(){
		for(Box box : boxes.values()){
			box.resetContent();
		}
	}
	
	
	protected Group fillBox(PositionIndexEnum positionIndex, int content){
		boxes.get(positionIndex).fillContent(content);
		return this;
	}
	
	public Group fixBox(PositionIndexEnum positionIndex, int content){
		boxes.get(positionIndex).fix(content);
		return this;
	}
	
	public GroupIndexEnum[] getGroupLine() {
		return groupLine;
	}

	public GroupIndexEnum[] getGroupColumn() {
		return groupColumn;
	}	

	public GroupIndexEnum getIndex() {
		return index;
	}

	protected Map<PositionIndexEnum, Box> getBoxes() {
		return boxes;
	}
	public  Box getBox(PositionIndexEnum index) {
		return boxes.get(index);
	}
	protected ResultMapper checkCandidate(ResultMapper result){
		Box candidate = result.getCandidate();
		boolean gridFilled = true;
		for(PositionIndexEnum positionIndexEnum: PositionIndexEnum.values()){
			Box box = boxes.get(positionIndexEnum);
			
			if(box.getState() == StateBoxEnum.FILLED || box.getState() == StateBoxEnum.FIXED){
				gridFilled = gridFilled & true;
				continue;
			}
			if((candidate.getState() == StateBoxEnum.FILLED || candidate.getState() == StateBoxEnum.FIXED) || (box.sizeOfCandidate() < candidate.sizeOfCandidate())){
				candidate = box;
			}
			gridFilled = gridFilled & (box.sizeOfCandidate() == 0);
		}
		result.setCandidate(candidate);
		result.setGridFilled(gridFilled);
		return result;
	}

	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		int startPosition = 1;
		int endPosition = 3;
		while (endPosition <= 9) {
			
			for (int i = startPosition; i <= endPosition; i++) {
				sb.append(boxes.get(PositionIndexEnum.fromIndex(i)).toString());
			}
			sb.append("\n");
			startPosition = startPosition + 3;
			endPosition = endPosition + 3;
		}
	
		return sb.toString();
	}
	
	protected Box random() {
		Random randomizer = new Random();		
		
		int i = randomizer.nextInt(boxes.size());
		PositionIndexEnum index = PositionIndexEnum.fromIndex(i);
		Box box = boxes.get(index);
		
		return box;
	}

	protected Box isNumberExists(Integer content) {
		if(content == null){
			return null;
		}
		for(Box box : boxes.values()){
			if(content.equals(box.getContent())){
				return box;
			}
		}
		return null;
	}
}

