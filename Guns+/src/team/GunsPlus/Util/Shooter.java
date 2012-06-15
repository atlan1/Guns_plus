package team.GunsPlus.Util;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;

import team.ApiPlus.API.Operator;
import team.GunsPlus.Item.Gun;

public abstract class Shooter implements Operator{
	
	private boolean fireing = false;
	private Boolean reloading = null;
	private Boolean delaying = null;
	private Map<Gun, Integer> fireCounter = new HashMap<Gun, Integer>();
	
	public void resetReload(){
		this.reloading = null;
	}
	
	public void resetDelay(){
		this.delaying = null;
	}
	
	public boolean isReloading() {
		return reloading!=null&&reloading==true?true:false;
	}
	
	public boolean isOnReloadingQueue() {
		return reloading!=null&&reloading==false?true:false;
	}

	public boolean isOnDelayingQueue() {
		return delaying!=null&&delaying==false?true:false;
	}

	public boolean isDelayResetted(){
		return delaying==null?true:false;
	}
	
	public boolean isReloadResetted(){
		return reloading==null?true:false;
	}

	public void setReloading() {
		this.reloading = true;
	}
	
	public void setOnReloadingQueue() {
		this.reloading = false;
	}

	public boolean isDelaying() {
		return delaying!=null&&delaying==true?true:false;
	}

	public void setDelaying() {
		this.delaying = true;
	}
	
	public void setOnDelayingQueue() {
		this.delaying = false;
	}
	
	public boolean isFireing() {
		return fireing;
	}
	
	public void setFireing(boolean f) {
		fireing = f;
	}

	public int  getFireCounter(Gun g) {
		return fireCounter.containsKey(g)?fireCounter.get(g):-1;
	}

	public void setFireCounter(Gun g, int i) {
		fireCounter.put(g, i);
	}
	
	public void resetFireCounter(Gun g){
			fireCounter.put(g, 0);
	}

	public abstract void reload(Gun g);
	public abstract void delay(Gun g);
	public abstract void fire(Gun g);
	
	public abstract Location getLocation();
}
