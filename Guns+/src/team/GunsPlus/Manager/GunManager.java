package team.GunsPlus.Manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.inventory.ItemStack;


import team.GunsPlus.Addition;
import team.GunsPlus.Effect;
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
	
	public static void copyGunProperties(Gun input, Gun result){
		result.setAmmo(input.getAmmo());
		result.setEffects(input.getEffects());
		result.setObjects(input.getObjects());
		result.setResources(input.getResources());
		result.setValues(input.getValues());
		for(Gun g:GunsPlus.allGuns) System.out.println(g.getName().startsWith("Pistol")?"COPY:"+g.getName()+"|"+g.getValue("ZOOMFACTOR"):"");
		System.out.println(result.getName()+"|"+result.getValue("ZOOMFACTOR"));
	}
	
	public static void buildAdditionGun(GunsPlus plugin ,Gun gun, Addition a ){
		try {
			HashMap<String, Float> values = a.getNumberValues();
			for(String name : values.keySet()){
				System.out.println("ADDING: "+gun.getName()+"|"+name.toUpperCase()+"|"+gun.getValue(name)+"->"+values.get(name));
				editGunValue( gun ,name,(values.get(name)+gun.getValue(name)));
			}
			for(String name : a.getStringValues().keySet()){
				editGunResource(gun, name, a.getStringValues().get(name));
			}		
			for(Gun g:GunsPlus.allGuns) System.out.println(g.getName().startsWith("Pistol")?"BUILDADD:"+g.getName()+"|"+g.getValue("ZOOMFACTOR"):"");
			System.out.println(gun.getName()+"|"+gun.getValue("ZOOMFACTOR"));
		} catch (Exception e) {
			if (GunsPlus.warnings)
				GunsPlus.log.log(Level.WARNING, GunsPlus.PRE + "Config Error:" + e.getMessage());
			if (GunsPlus.debug)
				e.printStackTrace();
		}
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
