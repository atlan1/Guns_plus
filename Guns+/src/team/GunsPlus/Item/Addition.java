package team.GunsPlus.Item;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import team.ApiPlus.API.Type.ItemTypeProperty;
import team.GunsPlus.GunsPlus;

public class Addition extends ItemTypeProperty{
	
	private Map<String, Object> properties = new HashMap<String, Object>();
	
	public Addition(GunsPlus gp, String n, String tex){
		super(gp, n, tex);
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

	public Map<String, Object> getNumberProperties() {
		Map<String, Object> values = new HashMap<String, Object>();
		for(Object o : new HashSet<Object>(properties.values())){
			if(o instanceof Float || o instanceof Integer || o instanceof Double || o instanceof Short){
				for (Entry<String, Object> entry : properties.entrySet()) {
			         if (o.equals(entry.getValue())) {
			             values.put(entry.getKey(), o);
			         }
			     }
			}
		}
		return values;
	}
	
	public Map<String, Object> getOverridableProperties() {
		Map<String, Object> values = new HashMap<String, Object>(properties);
		for(String s : new HashSet<String>(values.keySet())){
			for(String s2 : getNumberProperties().keySet()){
				if(s.equals(s2)){
					values.remove(s);
				}
			}
		}
		return values;
	}
}
