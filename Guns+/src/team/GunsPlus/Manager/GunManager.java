package team.GunsPlus.Manager;

import java.util.Map.Entry;

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
		gun.copyData(parent);
		gun.setResources(a.getResources());
		for(Entry<String, Float> data : a.getValues().entrySet()) {
			gun.setValue(data.getKey(), gun.getValue(data.getKey()) + data.getValue());
		}
		return gun;
	}

}
