package team.GunsPlus.Manager;

import java.lang.reflect.InvocationTargetException;

import team.ApiPlus.Manager.ItemManager;
import team.GunsPlus.GunsPlus;
import team.GunsPlus.Item.Addition;

public class AdditionManager {
	
	public static Addition buildAddition(GunsPlus plugin, String name, String tex) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		Addition add = (Addition) ItemManager.getInstance().buildItem(plugin, name, tex, "Addition");
		GunsPlus.allAdditions.add(add);
		return add;
	}
}
