package team.GunsPlus.Item;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

import team.ApiPlus.API.PropertyHolder;
import team.ApiPlus.API.Effect.Effect;
import team.ApiPlus.API.Type.ToolType;
import team.GunsPlus.Util.GunUtils;
import team.GunsPlus.Util.Shooter;

public class GunTool extends ToolType implements Gun{

	public GunTool(Plugin plugin, String name, String texture) {
		super(plugin, name, texture);
	}

	private Map<String, Object> properties = new HashMap<String, Object>();
	
	@SuppressWarnings("unchecked")
	@Override
	public  void performEffects(Object... args) {
		GunUtils.performEffects((Shooter)args[0], (HashSet<LivingEntity>)args[1], (PropertyHolder) args[2], this);
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

	@SuppressWarnings("unchecked")
	@Override
	public List<Effect> getEffects() {
		return (List<Effect>) getProperty("EFFECTS");
	}

	@Override
	public void setEffects(List<Effect> effects) {
		editProperty("EFFECTS", effects);
		addProperty("EFFECTS", effects);
	}

	@Override
	public void setProperty(String id, Object property) {
		addProperty(id, property);
		editProperty(id, property);
	}
	
}
