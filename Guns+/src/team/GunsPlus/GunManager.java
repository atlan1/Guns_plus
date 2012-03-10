package team.GunsPlus;

import java.util.logging.Level;
import java.util.logging.Logger;

import team.GunsPlus.Classes.Gun;

public class GunManager {
	private GunsPlus plugin;
	private Logger log;
	private String PRE;
	
	public GunManager(GunsPlus instance) {
		plugin = instance;
		log = GunsPlus.log;
		PRE = plugin.PRE;
	}
	
	public Gun buildNewGun(String name, String texture) {
		try {
			Gun gun = new Gun(plugin, name, texture);
			GunsPlus.allGuns.add(gun);
			return gun;
		} catch (Exception e) {
			if (plugin.warnings)
				log.log(Level.WARNING, PRE + "Config Error:" + e.getMessage());
			if (plugin.debug)
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
	
	public void editGunEffect(Gun gun, EffectType et){
		gun.addEffect(et);
	}
}
