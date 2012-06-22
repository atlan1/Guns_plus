package team.GunsPlus.Util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.BlockIterator;

import team.ApiPlus.API.Effect.Effect;
import team.ApiPlus.API.Effect.LocationEffect;
import team.GunsPlus.GunsPlus;

public class FlightPathEffect implements Runnable {

	private Effect effect;
	private Location l;
	private int maxrange;
	
	public FlightPathEffect(final Location l, int r, LocationEffect e) {
		this.effect = e;
		this.l = l;
		this.maxrange = r;
	}
	
	@Override
	public void run() {
		BlockIterator bitr = new BlockIterator(l, 0d, maxrange);
		while(bitr.hasNext()){
			Block b = bitr.next();
			if(Util.isTransparent(b)&&!Util.isTripod(b)){
				effect.performEffect(b.getLocation());
			}
		}
	}
	
	public void start(){
		Bukkit.getScheduler().scheduleSyncDelayedTask(GunsPlus.plugin, this);
	}
	
	public void setEffect(Effect e){
		effect = e;
	}
	
	public void setMaxRange(int e){
		maxrange = e;
	}
	
	public void setStartingPoint(final Location e){
		l = e.clone();
	}

}
