package team.GunsPlus;

import java.util.HashMap;
import java.util.Map;

public class Addition {
	
	private String name;
	private Map<String,Float> numbers = new HashMap<String,Float>(); 
	private Map<String, String> strings = new HashMap<String, String>();
	
	
	public Addition(String n){
		name = n;
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String n) {
		this.name = n;
	}
	
	public String getString(String s) {
		return strings.containsKey(s)?strings.get(s):null;
	}

	public void setString(String name, String resource) {
		this.strings.put(name, resource);
	}
	
	public void setValue(String name, Float value) {
		numbers.put(name, value);
	}
	
	public void removeString(String name) {
		if(strings.containsKey(name))
			this.strings.remove(name);
	}
	
	public void removeValue(String name) {
		if(numbers.containsKey(name))
			this.numbers.remove(name);
	}
	
	public float getValue(String name) {
		return numbers.containsKey(name)?numbers.get(name):null;
	}
	
	public Map<String, Float> getNumberValues(){
		return numbers;
	}
	
	public Map<String, String> getStringValues(){
		return strings;
	}
	
	
	
}
