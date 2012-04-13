package team.GunsPlus.Manager;

import team.GunsPlus.GunsPlus;
import team.GunsPlus.Item.Addition;

public class AdditionManager {
	
	public static Addition buildAddition(GunsPlus plugin, String name, String tex){
		Addition add = new Addition(plugin, name, tex);
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
