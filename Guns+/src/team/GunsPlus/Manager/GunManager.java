package team.GunsPlus.Manager;

import team.ApiPlus.Manager.PropertyManager;
import team.GunsPlus.GunsPlus;
import team.GunsPlus.Item.Addition;
import team.GunsPlus.Item.Gun;

public class GunManager{
	
	public static Gun buildNewGun(GunsPlus plugin ,String name, String texture) {
			Gun gun = new Gun(plugin, name, texture);
			GunsPlus.allGuns.add(gun);
			return gun;
	}
	
	public static Gun buildNewAdditionGun(GunsPlus plugin ,String name, String texture, Addition a, Gun parent ){
		Gun gun = new Gun(plugin, name, texture);
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
