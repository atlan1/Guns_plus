package team.GunsPlus;

import java.util.HashMap;

import team.GunsPlus.Enum.EffectSection;
import team.GunsPlus.Enum.EffectType;

public class Effect {

	private EffectType effecttype;
	private EffectSection effectsection;
	private HashMap<String, Object> arguments = new HashMap<String, Object>();
	
	public Effect(EffectType et, EffectSection es){
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

	public EffectSection getEffectsection() {
		return effectsection;
	}

	public void setEffectsection(EffectSection effectsection) {
		this.effectsection = effectsection;
	}

	public EffectType getEffecttype() {
		return effecttype;
	}

	public void setEffecttype(EffectType effecttype) {
		this.effecttype = effecttype;
	}
}
