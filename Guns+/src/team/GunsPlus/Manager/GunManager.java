package team.GunsPlus.Manager;

import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;


import team.GunsPlus.Addition;
import team.GunsPlus.Effect;
import team.GunsPlus.GunsPlus;
import team.GunsPlus.Item.Gun;

public class GunManager {
	
	public static Gun buildNewGun(GunsPlus plugin ,String name, String texture) {
			Gun gun = new Gun(plugin, name, texture);
			GunsPlus.allGuns.add(gun);
			return gun;
	}
	
	public static void copyGunProperties(Gun input, Gun result){
		result.setAmmo(input.getAmmo());
		result.setEffects(input.getEffects());
		result.setObjects(input.getObjects());
		result.setResources(input.getResources());
		result.setValues(input.getValues());
	}
	
	public static Gun buildNewAdditionGun(GunsPlus plugin ,String name, String texture, Addition a, Gun parent ){
		Gun gun = new Gun(plugin, name, texture);
		GunsPlus.allGuns.add(gun);
		copyGunProperties(parent, gun);
		for(String s : a.getNumberValues().keySet()){
			editGunValue( gun ,s, a.getNumberValues().get(s)+gun.getValue(s));
		}
		for(String s : a.getStringValues().keySet()){
			editGunResource(gun, s, a.getStringValues().get(s));
		}
		return gun;
	}
	
	public static void editGunValue(Gun gun, String name, float value) {
		gun.setValue(name, value);
	}
	
	public static void editGunResource(Gun gun, String name, String res) {
		gun.setResource(name, res);
	}
	
	public static void addGunEffect(Gun gun, Effect es){
		gun.addEffect(es);
	}
	
	public void removeGunEffect(Gun gun, Effect es){
		gun.removeEffect(es);
	}
	
	public static void editAmmo(Gun gun, ArrayList<ItemStack> ammo){
		gun.setAmmo(ammo);
	}
	
	public static void editObject(Gun gun, String name, Object o){
		gun.setObject(name, o);
	}
	
	public static void editEffects(Gun g, ArrayList<Effect> effs){
		g.setEffects(effs);
	}

}
