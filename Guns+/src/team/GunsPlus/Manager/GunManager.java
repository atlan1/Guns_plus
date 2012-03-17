package team.GunsPlus.Manager;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.inventory.ItemStack;


import team.GunsPlus.Enum.EffectType;
import team.GunsPlus.GunsPlus;
import team.GunsPlus.Item.Gun;

public class GunManager {
	private GunsPlus plugin;
	private Logger log;
	private String PRE;
	
	public GunManager(GunsPlus instance) {
		plugin = instance;
		log = GunsPlus.log;
		PRE = GunsPlus.PRE;
	}
	
	public Gun buildNewGun(String name, String texture) {
		try {
			Gun gun = new Gun(plugin, name, texture);
			GunsPlus.allGuns.add(gun);
			return gun;
		} catch (Exception e) {
			if (GunsPlus.warnings)
				log.log(Level.WARNING, PRE + "Config Error:" + e.getMessage());
			if (GunsPlus.debug)
				e.printStackTrace();
			return null;
		}
	}
	
	public void editGunValue(Gun gun, String name, float value) {
		gun.setValue(name, value);
	}
	
	public void editGunResource(Gun gun, String name, String res) {
		gun.setResource(name, res);
	}
	
	public void addGunEffect(Gun gun, EffectType et){
		gun.addEffect(et);
	}
	
	public void removeGunEffect(Gun gun, EffectType et){
		gun.removeEffect(et);
	}
	
	public void editAmmo(Gun gun, ArrayList<ItemStack> ammo){
		gun.setAmmo(ammo);
	}
	
	public void editObject(Gun gun, String name, Object o){
		gun.setObject(name, o);
	}
}
