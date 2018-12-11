package com.mcissoko.game.data;

import com.mcissoko.game.sudoku.core.IndexBox;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class BoxUi {

	
	private TextField groupBox;
	private Label groupCandidatesLabel;
	private IndexBox indexBox;
	private boolean inError;
	private boolean fixed;
	private boolean changeFromUndo;
	private boolean changeFromRedo;
	
	public BoxUi(TextField groupBox, Label groupCandidatesLabel, IndexBox indexBox) {
		super();
		this.groupBox = groupBox;
		this.groupCandidatesLabel = groupCandidatesLabel;
		this.indexBox = indexBox;
		this.inError = false;
		this.fixed = false;
		this.changeFromUndo = false;
		this.changeFromUndo = false;
	}
	public TextField getGroupBox() {
		return groupBox;
	}
	public void setGroupBox(TextField groupBox) {
		this.groupBox = groupBox;
	}
	public Label getGroupCandidatesLabel() {
		return groupCandidatesLabel;
	}
	public void setGroupCandidatesLabel(Label groupCandidatesLabel) {
		this.groupCandidatesLabel = groupCandidatesLabel;
	}
	public IndexBox getIndexBox() {
		return indexBox;
	}
	public void setIndexBox(IndexBox indexBox) {
		this.indexBox = indexBox;
	}
	public boolean isInError() {
		return inError;
	}
	public void setInError(boolean inError) {
		this.inError = inError;
	}
	public boolean isFixed() {
		return fixed;
	}
	public void setFixed(boolean fixed) {
		this.fixed = fixed;
	}
	public boolean isChangeFromUndo() {
		return changeFromUndo;
	}
	public void setChangeFromUndo(boolean changeFromUndo) {
		this.changeFromUndo = changeFromUndo;
	}
	public boolean isChangeFromRedo() {
		return changeFromRedo;
	}
	public void setChangeFromRedo(boolean changeFromRedo) {
		this.changeFromRedo = changeFromRedo;
	}
	
	
	
}
