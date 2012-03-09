package team.GunsPlus;

import java.util.ArrayList;

public enum EffectType {

	EXPLOSION(), LIGHTNING(), SMOKE(), POTION(), PUSH(), DRAW(), PLACE(), BREAK(), SPAWN(), FIRE(); 
	
	
	private ArrayList<Object> arguments = new ArrayList<Object>();
	private EffectSection section = EffectSection.UNDEFINED;
	
	public void setSection(EffectSection es){
		this.section = es;
	}
	
	public EffectSection getSection(){
		return section;
	}
	
	public ArrayList<Object> getArguments() {
		return arguments;
	}

	public void setArguments(ArrayList<Object> arguments) {
		this.arguments = arguments;
	}
	
	
}
