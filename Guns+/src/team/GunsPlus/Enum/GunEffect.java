package team.GunsPlus.Enum;

import java.util.HashMap;
import java.util.Map;

import team.ApiPlus.API.Effect.Effect;
import team.ApiPlus.API.Effect.EffectSection;
import team.ApiPlus.API.Effect.EffectType;
import team.GunsPlus.Enum.GunEffectSection;
import team.GunsPlus.Enum.GunEffectType;

public class GunEffect implements Effect{

	private Map<String, Object> properties = new HashMap<String, Object>();
	private GunEffectType effecttype;
	private GunEffectSection effectsection;
	
	public GunEffect(GunEffectType et, GunEffectSection es){
		setEffectType(et);
		setEffectSection(es);
	}

	public EffectSection getEffectSection() {
		return effectsection;
	}

	public void setEffectSection(EffectSection effectsection) {
		this.effectsection =  (GunEffectSection) effectsection;
	}

	public EffectType getEffectType() {
		return effecttype;
	}

	public void setEffectType(EffectType effecttype) {
		this.effecttype = (GunEffectType) effecttype;
	}
	
	@Override
	public Object getProperty(String id) {
		return properties.get(id);
	}

	@Override
	public void addProperty(String id, Object property) {
		if(!properties.containsKey(id))
			properties.put(id, property);
	}

	@Override
	public Map<String, Object> getProperties() {
		return properties;
	}

	@Override
	public void setProperties(Map<String, Object> properties) {
		this.properties = new HashMap<String, Object>(properties);
	}

	@Override
	public void removeProperty(String id) {
		if(properties.containsKey(id))
			properties.remove(id);
	}

	@Override
	public void editProperty(String id, Object property) {
		if(properties.containsKey(id))
			properties.put(id, property);
	}
}
