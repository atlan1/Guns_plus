package team.GunsPlus.Manager;

import java.util.ArrayList;
import java.util.logging.Level;

import org.bukkit.inventory.ItemStack;


import team.GunsPlus.Enum.EffectSection;
import team.GunsPlus.Addition;
import team.GunsPlus.GunsPlus;
import team.GunsPlus.Item.Gun;

public class GunManager {
	
	public static Gun buildNewGun(GunsPlus plugin ,String name, String texture) {
		try {
			Gun gun = new Gun(plugin, name, texture);
			GunsPlus.allGuns.add(gun);
			return gun;
		} catch (Exception e) {
			if (GunsPlus.warnings)
				GunsPlus.log.log(Level.WARNING, GunsPlus.PRE + "Config Error:" + e.getMessage());
			if (GunsPlus.debug)
				e.printStackTrace();
			return null;
		}
	}
	
	public static void editGunValue(Gun gun, String name, float value) {
		gun.setValue(name, value);
	}
	
	public static void editGunResource(Gun gun, String name, String res) {
		gun.setResource(name, res);
	}
	
	public static void addGunEffect(Gun gun, EffectSection es){
		gun.addEffect(es);
	}
	
	public void removeGunEffect(Gun gun, EffectSection es){
		gun.removeEffect(es);
	}
	
	public static void editAmmo(Gun gun, ArrayList<ItemStack> ammo){
		gun.setAmmo(ammo);
	}
	
	public static void editObject(Gun gun, String name, Object o){
		gun.setObject(name, o);
	}
	
	public static  void addGunAdditions(Gun g, ArrayList<Addition> adds){
		g.setAdditions(adds);
	}
	
	public static void addGunAddition(Gun g ,Addition a){
		g.addAddition(a);
	}
	
	public static void removeGunAddition(Gun g , Addition a){
		g.removeAddition(a);
	}
}
