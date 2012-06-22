package team.GunsPlus.Util;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import team.ApiPlus.API.Effect.Effect;
import team.ApiPlus.API.Effect.LocationEffect;
import team.ApiPlus.Util.Utils;
import team.GunsPlus.GunsPlus;

public class SphereEffect implements Runnable {

	private Effect effect;
	private Location l;
	private int radius;
	
	public SphereEffect(final Location l, int r, LocationEffect e) {
		this.effect = e;
		this.l = l;
		this.radius = r;
	}
	
	@Override
	public void run() {
		List<Block> list = Utils.getSphere(l, radius);
		for(Block b:list){
			effect.performEffect(b.getLocation());
		}
	}
	
	public void start(){
		Bukkit.getScheduler().scheduleSyncDelayedTask(GunsPlus.plugin, this);
	}
	
	public void setEffect(Effect e){
		effect = e;
	}
	
	public void setRadius(int e){
		radius = e;
	}
	
	public void setCenter(final Location e){
		l = e.clone();
	}

}
