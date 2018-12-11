package com.mcissoko.game.listener;

import com.mcissoko.game.view.RootLayoutController;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

public class FocusEventListener implements ChangeListener {

	private RootLayoutController controller;
	private String current;
	public FocusEventListener(RootLayoutController controller) {
		super();
		this.controller = controller;
	}

	@Override
	public void changed(ObservableValue observable, Object oldValue, Object newValue) {
		ReadOnlyBooleanProperty p = (ReadOnlyBooleanProperty) observable;
		TextField field =  (TextField) p.getBean();
		if(Boolean.FALSE.equals(newValue)) {
			return;
		}
		
		if(field.getId().equals(current)){
			return;
		}
		current = field.getId();
	    controller.executeEventEntered(current);
		
	}

	

}
