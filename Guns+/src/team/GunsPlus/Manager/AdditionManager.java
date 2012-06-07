package team.GunsPlus.Manager;

import team.GunsPlus.GunsPlus;
import team.GunsPlus.Item.Addition;

public class AdditionManager {
	
	public static Addition buildAddition(GunsPlus plugin, String name, String tex){
		Addition add = new Addition(plugin, name, tex);
		GunsPlus.allAdditions.add(add);
		return add;
	}
}
