package team.GunsPlus.Item;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

import team.ApiPlus.API.Property.CollectionProperty;
import team.ApiPlus.API.Property.Property;
import team.ApiPlus.API.Property.PropertyHolder;
import team.ApiPlus.API.Effect.Effect;
import team.ApiPlus.API.Type.ToolType;
import team.GunsPlus.API.Classes.Shooter;
import team.GunsPlus.Util.GunUtils;

@SuppressWarnings("rawtypes")
public class GunTool extends ToolType implements Gun {

	public GunTool(Plugin plugin, String name, String texture) {
		super(plugin, name, texture);
	}

	private Map<String, Property> properties = new HashMap<String, Property>();

	@Override
	public void performEffects(Object... args) {
		GunUtils.performEffects((Shooter) args[0], (HashSet<LivingEntity>) args[1], (PropertyHolder) args[2], this);
	}

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

	@SuppressWarnings("unchecked")
	@Override
	public List<Effect> getEffects() {
		return (List<Effect>) ((CollectionProperty<Effect>) this.getProperty("EFFECTS")).getValue();
	}

	@Override
	public void setEffects(List<Effect> arg0) {
		this.setProperty("EFFECTS", new CollectionProperty<Effect>(arg0));
	}

}
