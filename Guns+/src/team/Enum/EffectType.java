package team.Enum;

import java.util.HashMap;


public enum EffectType {

	EXPLOSION(), LIGHTNING(), SMOKE(), POTION(), PUSH(), DRAW(), PLACE(), BREAK(), SPAWN(), FIRE(); 
	
	
	private HashMap<String, Object> arguments = new HashMap<String, Object>();
	private EffectSection section = EffectSection.UNDEFINED;
	
	public void setSection(EffectSection es){
		this.section = es;
	}
	
	public EffectSection getSection(){
		return section;
	}
	
	public HashMap<String, Object> getArguments() {
		return arguments;
	}
	
	public Object getArgument(String id){
		return this.arguments.containsKey(id)?this.arguments.get(id):null;
	}

	public void addArgument(String id, Object argument) {
		this.arguments.put(id, argument);
	}
	
	
}
