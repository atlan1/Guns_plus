package team.GunsPlus.Item;
import org.bukkit.plugin.Plugin;

import team.ApiPlus.API.Type.ItemType;

public class Ammo extends ItemType {

	private int damage = 0;
	
	public Ammo(Plugin plugin, String name, String texture, int dmg) {
		super(plugin, name, texture);
		damage = dmg;
	}
	
	public int getDamage(){
		return damage;
	}
	
	public void setDamage(int dmg){
		damage = dmg;
	}
}
