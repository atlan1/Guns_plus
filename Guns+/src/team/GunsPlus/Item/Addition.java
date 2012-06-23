package team.GunsPlus.Item;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.plugin.Plugin;

import team.ApiPlus.API.Type.ItemTypeProperty;

public class Addition extends ItemTypeProperty{
	
	public Addition(Plugin gp, String n, String tex){
		super(gp, n, tex);
	}

	public Map<String, Object> getNumberProperties() {
		Map<String, Object> values = new HashMap<String, Object>();
		for(Object o : new HashSet<Object>(this.getProperties().values())){
			if(o instanceof Float || o instanceof Integer || o instanceof Double || o instanceof Short){
				for (Entry<String, Object> entry : getProperties().entrySet()) {
			         if (o.equals(entry.getValue())) {
			             values.put(entry.getKey(), o);
			         }
			     }
			}
		}
		return values;
	}
	
	public Map<String, Object> getOverridableProperties() {
		Map<String, Object> values = new HashMap<String, Object>(getProperties());
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
