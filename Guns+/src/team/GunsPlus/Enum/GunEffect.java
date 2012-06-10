package team.GunsPlus.Enum;

import java.util.HashMap;


public class GunEffect {

	private GunEffectType effecttype;
	private GunEffectSection effectsection;
	private HashMap<String, Object> arguments = new HashMap<String, Object>();
	
	public GunEffect(GunEffectType et, GunEffectSection es){
		setEffecttype(et);
		setEffectsection(es);
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

	public GunEffectSection getEffectsection() {
		return effectsection;
	}

	public void setEffectsection(GunEffectSection effectsection) {
		this.effectsection = effectsection;
	}

	public GunEffectType getEffecttype() {
		return effecttype;
	}

	public void setEffecttype(GunEffectType effecttype) {
		this.effecttype = effecttype;
	}
}
