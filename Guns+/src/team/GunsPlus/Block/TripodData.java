package team.GunsPlus.Block;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.player.SpoutPlayer;

import team.GunsPlus.Enum.Target;
import team.GunsPlus.Item.Gun;

public class TripodData {

	
	private Gun gun;
	private ArrayList<ItemStack> ammo = new ArrayList<ItemStack>();
	private boolean automatic = false;
	private ArrayList<Target> targets = new ArrayList<Target>();
	private SpoutPlayer owner;
	private Location loc;
	private Location gunLoc;
	private Item droppedGun;
	
	
	public TripodData(SpoutPlayer own, Location l){
		setOwner(own);
		setLoc(l);
		Location loc = l;
		System.out.println("X:"+loc.getX()+"; Z:"+loc.getZ());
		loc = loc.getBlock().getLocation();
		Vector vec = loc.toVector();
		vec.add(new Vector(0.5, 0.6, 0.5));
		loc = vec.toLocation(loc.getWorld());
		setGunLoc(loc);
	}
	
	public void resetDroppedGun() {
		if(droppedGun!=null)
		{
			droppedGun.remove();
			droppedGun=null;
		}
	}
	public Item getDroppedGun() {
		return droppedGun;
	}

	public void setDroppedGun(Item droppedGun) {
		this.droppedGun = droppedGun;
	}
	public Gun getGun() {
		return gun;
	}
	public void setGun(Gun g) {
		this.gun = g;
	}
	public void resetGun(){
		this.gun = null;
	}
	public ArrayList<ItemStack> getAmmo() {
		return ammo;
	}
	public void setAmmo(ArrayList<ItemStack> ammo) {
		this.ammo = ammo;
	}
	public boolean isAutomatic() {
		return automatic;
	}
	public void setAutomatic(boolean automatic) {
		this.automatic = automatic;
	}
	public SpoutPlayer getOwner() {
		return owner;
	}
	public void setOwner(SpoutPlayer owner) {
		this.owner = owner;
	}

	public Location getLoc() {
		return loc;
	}

	public void setLoc(Location loc) {
		this.loc = loc;
	}
	
	public Location getGunLoc() {
		return gunLoc;
	}

	public void setGunLoc(Location gunLoc) {
		this.gunLoc = gunLoc;
	}

	public ArrayList<Target> getTargets() {
		return targets;
	}

	public void setTargets(ArrayList<Target> targets) {
		this.targets = targets;
	}
	
	public void addTarget(Target t){
		this.targets.add(t);
	}

	public void spawnGun(){
		if(gun!=null){
			droppedGun = getLoc().getWorld().dropItemNaturally(getLoc(), new SpoutItemStack(gun));
		}
	}

	public void update() {
		if(droppedGun==null&&gun!=null){
			spawnGun();
		}
		if(droppedGun!=null){
			if(droppedGun.getLocation()!=getGunLoc()){
				droppedGun.teleport(getGunLoc());
			}
		}
	}
	
}
