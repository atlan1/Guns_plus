package team.GunsPlus.Manager;

import java.lang.reflect.InvocationTargetException;

import team.ApiPlus.Manager.ItemManager;
import team.ApiPlus.Manager.PropertyManager;
import team.GunsPlus.GunsPlus;
import team.GunsPlus.Item.Addition;
import team.GunsPlus.Item.Ammo;
import team.GunsPlus.Item.Gun;
import team.GunsPlus.Item.GunItem;
import team.GunsPlus.Item.GunTool;
import team.GunsPlus.Util.Util;

public class ItemBuilder{
	
	public static  Gun buildGun(GunsPlus plugin ,String name, String texture) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
			Gun gun = null;
			if(GunsPlus.toolholding){
				gun =   new GunTool(plugin, name, texture);
			}else {
				gun =  (GunItem) ItemManager.getInstance().buildItem(GunsPlus.plugin, name, texture, "Gun");
			}
			GunsPlus.allGuns.add(gun);
			return gun;
	}
	
	public static  Gun buildAdditionGun(GunsPlus plugin ,String name, String texture, Addition a, Gun parent ) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		Gun gun = null;
		if(GunsPlus.toolholding){
			gun =   new GunTool(plugin, name, texture);
		}else {
			gun =  (GunItem) ItemManager.getInstance().buildItem(GunsPlus.plugin, name, texture, "Gun");
		}
		GunsPlus.allGuns.add(gun);
		PropertyManager.copyProperties(parent, gun, true);
		Util.setProperties(a, gun);
		return gun;
	}

	public static Addition buildAddition(GunsPlus plugin, String name, String tex) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		Addition add = (Addition) ItemManager.getInstance().buildItem(plugin, name, tex, "Addition");
		GunsPlus.allAdditions.add(add);
		return add;
	}
	
	public static Ammo buildAmmo(GunsPlus plugin, String name, String tex) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		Ammo a = (Ammo) ItemManager.getInstance().buildItem(plugin, name, tex, "Ammo");
		GunsPlus.allAmmo.add(a);
		return a;
	}

}
