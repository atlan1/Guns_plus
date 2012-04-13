package team.GunsPlus.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.inventory.SpoutItemStack;

import team.GunsPlus.GunsPlus;
import team.GunsPlus.GunsPlusPlayer;
import team.GunsPlus.Enum.Target;
import team.GunsPlus.Item.Gun;
import team.GunsPlus.Util.GunUtils;
import team.GunsPlus.Util.PlayerUtils;
import team.GunsPlus.Util.Shooter;
import team.GunsPlus.Util.Util;

public class TripodData extends Shooter implements InventoryHolder {

	private Inventory inventory = Bukkit.getServer().createInventory(this, GunsPlus.tripodinvsize, "Tripod Inventory");
	private Inventory owner_inv = Bukkit.getServer().createInventory(null, InventoryType.PLAYER);
	private Gun gun;
	private boolean automatic = false;
	private boolean working = false;
	private boolean entered = false;
	private List<Target> targets = new ArrayList<Target>();
	private String ownername;
	private GunsPlusPlayer owner;
	private Location gunLoc;
	private Item droppedGun;
	
	public TripodData(String name, Location l, Gun g, ArrayList<Target> tars){
		setOwnername(name);
		setLocation(l);
		setGunLoc(Util.getMiddle(l, 0.6f));
		setGun(g);
		setTargets(tars);
	}
	
	public TripodData(GunsPlusPlayer own, Location l){
		setOwnername(own.getPlayer().getName());
		setOwner(own);
		setLocation(l);
		setGunLoc(Util.getMiddle(l, 0.6f));
	}
	
	public TripodData(GunsPlusPlayer own, Location l, Gun g, ArrayList<Target> tars){
		setOwnername(own.getPlayer().getName());
		setOwner(own);
		setLocation(l);
		setGunLoc(Util.getMiddle(l, 0.6f));
		setGun(g);
		setTargets(tars);
	}

	public void update() {
		if(droppedGun==null&&gun!=null){
			spawnGun();
		}
		if(droppedGun!=null){
			if(!droppedGun.getLocation().equals(getGunLoc())){
				droppedGun.teleport(getGunLoc());
			}
			if(droppedGun.isDead()){
				spawnGun();
			}
		}
		if(owner!=null){
			if(isEntered()&&!owner.getPlayer().getLocation().toVector().equals(getOwnerLocation().toVector())){
				Location l = getOwnerLocation().clone();
				l.setYaw(getOwner().getPlayer().getLocation().getYaw());
				l.setPitch(getOwner().getPlayer().getLocation().getPitch());
				getOwner().getPlayer().teleport(l);
			}
		}else if(owner==null&&Bukkit.getPlayerExact(getOwnername())!=null){
			setOwner(PlayerUtils.getPlayerByName(getOwnername()));
		}
		if(isWorking()){
			//TODO add support for auto target and fire of tripods
//			LivingEntity le = checkForTarget((int)gun.getValue("RANGE"), getTargets());
//			System.out.println(""+le);
//			if(le != null){
//				setGunLoc(Util.setLookingAt(getGunLoc(), le.getEyeLocation()));
//				this.fire(gun, getInventory());
//			}
		}
		if(isWorking()&&isEntered()){
			setWorking(false);
		}
	}
	
	public LivingEntity checkForTarget(int r, List<Target> t){
		List<Entity> near = getDroppedGun().getNearbyEntities(r, r, r);
		LivingEntity le = null;
		if(getDroppedGun()==null)return le;
		for(Target tar : t){
			tar.getRealEntity();
			for(Entity e : near){
				if(e.getType().equals(tar.getRealEntity())&&e instanceof LivingEntity){
					le = (LivingEntity) e;
					return le;
				}
			}
		}
		return le;
	}

	@Override
	public Map<LivingEntity, Integer> getTargets(Gun gun) {
		return GunUtils.getTargets(getLocation(), gun, false);
	}

	@Override
	public void damage(LivingEntity target, int damage) {
		target.damage(damage);
	}
	
	public Location getOwnerLocation(){
		return Util.getMiddle(getLocation(), 0.0f);
	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}
	
	@Override //dont do anything because you can't really perform recoil on a block (can you =D)
	public void recoil(Gun g) {}

	public void setEntered(boolean entered) {
		if(entered==true){
			owner_inv.setContents(owner.getPlayer().getInventory().getContents());
			owner.getPlayer().getInventory().setContents(new ItemStack[owner.getPlayer().getInventory().getSize()]);
			owner.getPlayer().setItemInHand(new SpoutItemStack(getGun(), 1));
			if(GunsPlus.forcezoom)
				owner.zoom(gun);
		}else{
			owner.getPlayer().getInventory().setContents(owner_inv.getContents());
		}
		this.entered = entered;
	}

	public void addTarget(Target t){
		this.targets.add(t);
	}

	public Inventory getOwnerInv() {
		return owner_inv;
	}

	public void setOwnerInv(Inventory owner_inv) {
		this.owner_inv = owner_inv;
	}

	public void spawnGun(){
		if(gun!=null){
			droppedGun = getLocation().getWorld().dropItemNaturally(getLocation(), new SpoutItemStack(gun));
			droppedGun.setPickupDelay(Integer.MAX_VALUE);
		}
	}
	
	public void resetDroppedGun() {
		if(droppedGun!=null){
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
	public boolean isAutomatic() {
		return automatic;
	}
	public void setAutomatic(boolean automatic) {
		this.automatic = automatic;
	}
	public GunsPlusPlayer getOwner() {
		return owner;
	}
	public void setOwner(GunsPlusPlayer owner) {
		this.owner = owner;
	}
	
	public Location getGunLoc() {
		return gunLoc;
	}

	public void setGunLoc(Location gunLoc) {
		this.gunLoc = gunLoc;
	}

	public List<Target> getTargets() {
		return targets;
	}

	public void setTargets(ArrayList<Target> targets) {
		this.targets = targets;
	}
	
	public String getOwnername() {
		return ownername;
	}

	public void setOwnername(String ownername) {
		this.ownername = ownername;
	}

	public boolean isWorking() {
		return working;
	}

	public void setWorking(boolean working) {
		this.working = working;
	}

	public boolean isEntered() {
		return entered;
	}
}
