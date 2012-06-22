package team.GunsPlus.Manager;

import java.lang.reflect.InvocationTargetException;

import team.ApiPlus.Manager.ItemManager;
import team.ApiPlus.Manager.PropertyManager;
import team.GunsPlus.GunsPlus;
import team.GunsPlus.Item.Addition;
import team.GunsPlus.Item.Gun;
import team.GunsPlus.Item.GunItem;
import team.GunsPlus.Item.GunTool;

public class GunManager{
	
	public static  Gun buildNewGun(GunsPlus plugin ,String name, String texture) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
			Gun gun = null;
			if(GunsPlus.toolholding){
				gun =   new GunTool(plugin, name, texture);
			}else {
				gun =  (GunItem) ItemManager.getInstance().buildItem(GunsPlus.plugin, name, texture, "Gun");
			}
			GunsPlus.allGuns.add(gun);
			return gun;
	}
	
	public static  Gun buildNewAdditionGun(GunsPlus plugin ,String name, String texture, Addition a, Gun parent ) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		Gun gun = null;
		if(GunsPlus.toolholding){
			gun =   new GunTool(plugin, name, texture);
		}else {
			gun =  (GunItem) ItemManager.getInstance().buildItem(GunsPlus.plugin, name, texture, "Gun");
		}
		GunsPlus.allGuns.add(gun);
		PropertyManager.copyProperties(parent, gun);
		for(String s : a.getNumberProperties().keySet()){
			gun.editProperty(s, ((Number)a.getProperty(s)).doubleValue()+((Number)gun.getProperty(s)).doubleValue());
		}
		for(String s : a.getOverridableProperties().keySet()){
			gun.editProperty(s, a.getOverridableProperties().get(s));
		}
		return gun;
	}

}
