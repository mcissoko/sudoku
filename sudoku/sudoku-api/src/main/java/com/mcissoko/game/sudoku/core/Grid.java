package com.mcissoko.game.sudoku.core;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mcissoko.game.sudoku.core.enumeration.GroupIndexEnum;
import com.mcissoko.game.sudoku.core.enumeration.StateBoxEnum;

public class Grid implements Serializable{

	Logger log = LoggerFactory.getLogger(Grid.class);  

	private static final long serialVersionUID = 4581943640090887910L;
	private Map<GroupIndexEnum, Group> groups;
	private Deque<PlaySequence> playSequence;
	
	private boolean filled;
	
	Grid() {
		this.filled = false;
		initGroups();
	}
	
	private void initGroups(){
		groups = new HashMap<>();
		playSequence = new ArrayDeque<>();
		for(GroupIndexEnum i: GroupIndexEnum.values()){
			groups.put(i, new Group(i, this));
		}
	}
	
	protected Box random(){
		
		Random randomizer = new Random();
		
		int i = randomizer.nextInt(groups.size());
		GroupIndexEnum index = GroupIndexEnum.fromIndex(i);
		Group group = groups.get(index);
		Box box = group.random();
		return box;
	}
	
	protected void addPlaySequence(PlaySequence playSequence){
		this.playSequence.addFirst(playSequence);
	}
	
	private PlaySequence getLastPlaySequence(){
		if(playSequence.isEmpty()){
			return null;
		}
		return this.playSequence.pop();
	}
	
	
	protected Deque<PlaySequence> getPlaySequence() {
		return playSequence;
	}

	protected boolean isNumberInGrilleLine(Box box, List<Box> occurrences) {
		
		Group group = groups.get(box.getPosition().getGroupIndex());
		
		GroupIndexEnum[] indexVoisinsLigne = group.getGroupLine();
		
		boolean resultatLigne2 = getGroup(indexVoisinsLigne[0]).isNumberInGroupLine(box, occurrences);
		boolean resultatLigne3 = getGroup(indexVoisinsLigne[1]).isNumberInGroupLine(box, occurrences);
		
		boolean ok = !resultatLigne2 && !resultatLigne3; 
		if(!ok){
			ok = false; 
		}
		return ok;
	}
	
	
	protected boolean isNumberInGrilleColumn(Box box, List<Box> occurrences) {

		Group group = groups.get(box.getPosition().getGroupIndex());

		GroupIndexEnum[] indexVoisinsColonne = group.getGroupColumn();

		boolean resultatColonne1 = getGroup(indexVoisinsColonne[0]).isNumberInGroupColumn(box, occurrences);
		boolean resultatColonne2 = getGroup(indexVoisinsColonne[1]).isNumberInGroupColumn(box, occurrences);

		boolean ok = !resultatColonne1 && !resultatColonne2;
		if (!ok) {
			ok = false;
		}
		return ok;
	}
	
	protected void resetContent(){
		for(Group group: groups.values()){
			group.resetContent();
		}
	}
	
	public boolean isValid(){
		for(Group group: groups.values()){
			if(group.isValid()){
				continue;
			}else{
				return false;
			}
		}
		return true;
	}
	
	public boolean isSudoku(){
		for(Group group: groups.values()){
			if(group.isSudoku()){
				continue;
			}else{
				return false;
			}
		}
		return true;
	}
	
	
	public Group getGroup(GroupIndexEnum groupIndexEnum) {
		return groups.get(groupIndexEnum);
	}
	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("-----------------------------------------\n");
		
		int startPosition = 1;
		int endPosition = 3;
		
		int startGroup = 1;
		int endGroup = 3;
		
		while (endGroup <= 9) {
				while (endPosition <= 9) {
					for (int i = startGroup; i <= endGroup; i++) {
						Group g = this.getGroup(GroupIndexEnum.fromIndex(i));
						for (int k = startPosition; k <= endPosition; k++) {
							sb.append(g.getBoxes().get(PositionIndexEnum.fromIndex(k)).print());
						}
						sb.append("+ ");
					}
					sb.append("\n");
					startPosition = startPosition + 3;
					endPosition = endPosition + 3;
				}
				sb.append("-----------------------------------------\n");
				startGroup = startGroup + 3;
				endGroup = endGroup + 3;
				
				startPosition = 1;
				endPosition = 3;
		}
		
		return sb.toString();
	}

	protected Box restaure() {
		PlaySequence last = getLastPlaySequence();
		if(last == null){
			return null;
		}
		Group group = getGroup(last.getGroupIndex());
		Box box = group.getBox(last.getPositionIndex());
		
		box.resetContent();
		box.setCandidates(last.getCandidates());
		for(Sequence seq: last.getSequences()){
			Group g = getGroup(seq.getGroupIndex());
			g.getBox(seq.getPositionIndex()).restaureCandidate(last.getSellectedCandidate());
		}
		
		if(box.getCandidates().size() == 1){
			log.info("Un seul candidat disponible: {}, on remonte d'un cran", box);

			box.resetTried();
			return this.restaure();
		}
		
		return box;
	}

	protected List<Box> checkNumberIsResolved(Integer content) {
		List<Box> lst = new ArrayList<>();
		for(Group group: this.groups.values()){
			Box box = group.isNumberExists(content);
			if(box == null){
				return new ArrayList<>();
			}
			lst.add(box);
		}
		
		return lst;
	}
	
	public Grid copy() {

		Grid copy = new Grid();
		for (GroupIndexEnum groupIndexEnum : GroupIndexEnum.values()) {
			Group group = copy.getGroup(groupIndexEnum);

			for (PositionIndexEnum positionIndexEnum : PositionIndexEnum.values()) {
				Box box = this.getGroup(groupIndexEnum).getBox(positionIndexEnum);
				if (box.getState() != StateBoxEnum.EMPTY) {
					group.fixBox(positionIndexEnum, box.getContent());
				}

			}
		}
		return copy;

	}

	public boolean isFilled() {
		return filled;
	}

	protected void setFilled(boolean filled) {
		this.filled = filled;
	}
	
	
	
}
