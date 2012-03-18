package team.GunsPlus.Manager;

import team.GunsPlus.Addition;
import team.GunsPlus.GunsPlus;

public class AdditionManager {
	
	public static Addition buildAddition(String name){
		Addition add = new Addition(name);
		GunsPlus.allAdditions.add(add);
		return add;
	}
	
	public static void removeNumberValue(Addition a,String id){
		a.removeValue(id);
	}
	
	public static void removeStringValue(Addition a, String id){
		a.removeString(id);
	}
	
	public static void editNumberValue(Addition a, String id, Float number){
		a.setValue(id, number);
	}
	
	public static void editStringValue(Addition a, String id, String string){
		a.setString(id, string);
	}
}
