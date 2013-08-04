package team.GunsPlus.Effect;

import java.util.HashMap;
import java.util.Map;
import team.ApiPlus.API.Property.Property;
import team.ApiPlus.API.Property.PropertyHolder;
import team.ApiPlus.API.Effect.EffectTarget;

@SuppressWarnings("rawtypes")
public class EffectTargetImpl implements EffectTarget, PropertyHolder {

	private EffectTargetType ett;

	public EffectTargetImpl(EffectTargetType ett) {
		this.ett = ett;
	}

	private Map<String, Property> properties = new HashMap<String, Property>();

	@Override
	public Property getProperty(String id) {
		return properties.get(id);
	}

	@Override
	public void addProperty(String id, Property property) {
		if(!properties.containsKey(id))
			properties.put(id, property);
	}

	@Override
	public Map<String, Property> getProperties() {
		return properties;
	}

	@Override
	public void setProperties(Map<String, Property> properties) {
		this.properties = new HashMap<String, Property>(properties);
	}

	@Override
	public void removeProperty(String id) {
		if(properties.containsKey(id))
			properties.remove(id);
	}

	@Override
	public void editProperty(String id, Property property) {
		if(properties.containsKey(id))
			properties.put(id, property);
	}

	@Override
	public void setProperty(String id, Property property) {
		addProperty(id, property);
		editProperty(id, property);
	}

	public EffectTargetType getType() {
		return ett;
	}

}
