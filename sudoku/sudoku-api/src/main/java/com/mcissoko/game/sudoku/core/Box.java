package com.mcissoko.game.sudoku.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;

import com.mcissoko.game.sudoku.core.enumeration.StateBoxEnum;

public class Box extends AbstractBox implements Serializable {

	private static final long serialVersionUID = -3308228191662064132L;
	
	private Position position;
	private Integer content;
	private Group group;
		
	private final Random randomizer;
	
	protected Box(Position position, Group group) {
		super();
		this.position = position;
		this.state = StateBoxEnum.EMPTY;
		candidates = new HashSet<>();
		triedCandidates = new HashSet<>();
		
		IntStream.rangeClosed(1, 9).forEach(i -> candidates.add(i));
		
		this.content = 0;
		randomizer = new Random();
		this.group = group;
	}
	
	protected int sizeOfCandidate(){
		return candidates.size();
	}
	
	protected void setCandidates(Set<Integer> candidates) {
		if(this.state == StateBoxEnum.FIXED){
			return;
		}
		this.candidates = new HashSet<>(candidates);
	}

	private Integer oneCandidate(){
		List<Integer> list = new ArrayList<>(candidates);
		list.removeAll(triedCandidates);
		if(list.isEmpty()){
			return 0;
		}
		Integer candidate = list.get(randomizer.nextInt(list.size()));
		
		return candidate;
	}
	
	protected void resetTried(){
		this.triedCandidates.clear();
	}
	
	protected void fix(Integer value){
		this.content = value;
		this.state = StateBoxEnum.FIXED;
		this.candidates.clear();
		this.group.removeCandidate(this);
		this.group.removeCandidateInGroupColumn(this);
		this.group.removeCandidateInGroupLine(this);
	}
	protected PlaySequence fill(){
		Integer candidate = oneCandidate();
		if(candidate == 0){
			return null;
		}
		fillContent(candidate);
		if(!this.group.isContentUnique(this, null)){
			this.resetContent();
			return this.fill();
		}
		
		PlaySequence playSequence = new PlaySequence(getGroup().getIndex(), getPosition().getIndex(), getContent(), getCandidates());
		getCandidates().clear();	
		return playSequence;
	}

	protected void removeCandidate(Integer candidate, PlaySequence playSequence){
		candidates.remove(candidate);
		playSequence.getSequences().add(new Sequence(this.group.getIndex(), getPosition().getIndex()));
	}
	
	protected void removeCandidate(Integer candidate){
		candidates.remove(candidate);
	}
	
	protected Set<Integer> getCandidates() {
		return candidates;
	}

	protected StateBoxEnum getEtat() {
		return state;
	}
	

	public Group getGroup() {
		return group;
	}
	
	protected Set<Integer> getTriedCandidates() {
		return triedCandidates;
	}

	protected void setState(StateBoxEnum state) {
		this.state = state;
	}
	
	public StateBoxEnum getState() {
		return state;
	}

	public Integer getContent() {
		return content;
	}

	protected void fillContent(Integer content) {
		this.content = content;
		this.state = StateBoxEnum.FILLED;
		
		if(!this.triedCandidates.contains(content)){
			this.triedCandidates.add(content);
		}
	}
	
	
	public void tryContent(Integer content) {
		this.content = content;
		this.state = StateBoxEnum.FILLED;
	}
	
	protected void resetContent() {
		if(this.state == StateBoxEnum.FIXED){
			return;
		}
		this.content =  0;
		this.state = StateBoxEnum.EMPTY;
	}

	public Position getPosition() {
		return position;
	}
	
	public String print(){
		if(this.state == StateBoxEnum.EMPTY){
			return "( ) ";
		}
		return "(" +this.content+ ") ";
	}
	
	@Override
	public String toString() {
		return getPosition().getCoordinate().toString() + print() + "  ";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((position == null) ? 0 : position.hashCode());
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
		Box other = (Box) obj;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		return true;
	}

	protected void restaureCandidate(Integer sellectedCandidate) {
		if(this.state == StateBoxEnum.FIXED || candidates.contains(sellectedCandidate)){
			return;
		}
		candidates.add(sellectedCandidate);
	}

	protected boolean hasCandidate(Integer content) {
		return this.candidates.contains(content);
	}

	
}
