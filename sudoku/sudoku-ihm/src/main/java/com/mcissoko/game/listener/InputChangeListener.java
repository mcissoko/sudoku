package com.mcissoko.game.listener;

import com.mcissoko.game.sudoku.core.enumeration.InputActionEnum;
import com.mcissoko.game.view.RootLayoutController;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

public class InputChangeListener implements ChangeListener{

	private RootLayoutController controller;

	
	public InputChangeListener(RootLayoutController controller) {
		super();
		this.controller = controller;
	}


	@Override
	public void changed(ObservableValue observable, Object oldValue, Object newValue) {
		
		if(this.controller.isInitiating()){
			//System.out.println(((StringProperty) observable).getName() + " - " + newValue + "/" + observable);
			return;
		}
		String ov = (String) oldValue;
		String nv = (String) newValue;
		String value;
		InputActionEnum actionEnum;
		if(nv.isEmpty() && !ov.isEmpty()){
			actionEnum = InputActionEnum.REMOVE;
			value = ov;
		} else if(!nv.isEmpty() && !ov.isEmpty()){
			actionEnum = InputActionEnum.CHANGE;
			value = nv;
		}else{
			actionEnum = InputActionEnum.NEW;
			value = nv;
		}
		
	     StringProperty prop = (StringProperty) observable;
	     controller.excuteValueChanged( ((TextField) prop.getBean()).getId(), actionEnum, value, ov );
		
	}

}
