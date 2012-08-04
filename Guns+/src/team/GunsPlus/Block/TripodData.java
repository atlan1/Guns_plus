package team.GunsPlus.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.item.GenericCustomItem;

import team.ApiPlus.API.PropertyContainer;
import team.ApiPlus.Util.Task;
import team.ApiPlus.Util.Utils;
import team.GunsPlus.GunsPlus;
import team.GunsPlus.GunsPlusPlayer;
import team.GunsPlus.Enum.Projectile;
import team.GunsPlus.Enum.Target;
import team.GunsPlus.Item.Ammo;
import team.GunsPlus.Item.Gun;
import team.GunsPlus.Util.GunUtils;
import team.GunsPlus.Util.PlayerUtils;
import team.GunsPlus.Util.Shooter;
import team.GunsPlus.Util.Util;

public class TripodData extends Shooter implements InventoryHolder {

	private Inventory inventory = Bukkit.getServer().createInventory(this, Tripod.tripodinvsize, "Tripod Inventory");
	private Inventory owner_inv = Bukkit.getServer().createInventory(null, InventoryType.PLAYER);
	private Gun gun;
	private boolean automatic = false;
	private boolean working = false;
	private boolean entered = false;
	private Set<Target> targets = new HashSet<Target>();
	private String ownername;
	private GunsPlusPlayer owner;
	private Location gunLoc;
	private Location location;
	private Item droppedGun;
//	private Item chair;
	private TripodAI ai = new TripodAI(this);
	
	public TripodData(String name, Location l, Gun g, Set<Target> tars){
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
	
	public TripodData(GunsPlusPlayer own, Location l, Gun g, Set<Target> tars){
		setOwnername(own.getPlayer().getName());
		setOwner(own);
		setLocation(l);
		setGunLoc(Util.getMiddle(l, 0.6f));
		setGun(g);
		setTargets(tars);
	}
	
	public void destroy(){
		ai.stopAI();
		if(isEntered())
			setEntered(false);
		removeDroppedGun();
	}
	
	public void dropContents(){
		if(getGun()!=null)
			location.getWorld().dropItemNaturally(location, new SpoutItemStack((GenericCustomItem)getGun(), 1));
		for(ItemStack a : getInventory().getContents()) if(a!=null)location.getWorld().dropItemNaturally(location, a);
	}

	public void update() {
		if(droppedGun==null&&gun!=null){
			if(!isEntered())
				dropGun();
		}
		if(droppedGun!=null&&!isEntered()){
			if(!droppedGun.getLocation().equals(getGunLoc())){
				droppedGun.teleport(getGunLoc());
			}
			if(droppedGun.isDead()){
				dropGun();
			}
		}else if(droppedGun!=null&&isEntered()){
			droppedGun.remove();
		}
		
		if(owner!=null){
			Location ownerlocation = Util.getMiddle(getLocation(), 0.0f);
			if(isEntered()&&!owner.getPlayer().getLocation().toVector().equals(ownerlocation.toVector())){
				Location l = ownerlocation.clone();
				l.setYaw(getOwner().getPlayer().getLocation().getYaw());
				l.setPitch(getOwner().getPlayer().getLocation().getPitch());
				getOwner().getPlayer().teleport(l);
			}
		}else if(owner==null&&Bukkit.getPlayerExact(getOwnername())!=null){
			setOwner(PlayerUtils.getPlayerByName(getOwnername()));
		}
		
		if(isWorking()&&isEntered()){
			setWorking(false);
		}
		
		SpoutBlock sb = (SpoutBlock) getLocation().getBlock();
		if(sb.getCustomBlock()!=null&&!sb.getCustomBlock().equals(GunsPlus.tripod)){
			sb.setCustomBlock(GunsPlus.tripod);
		}
	}
	
	@Override
	public void reload(Gun g){
		if(getFireCounter(g) == 0)return;
		if(isReloadResetted()){
			setOnReloadingQueue();
		}
		if(isOnReloadingQueue()){
			Task reloadTask = new Task(GunsPlus.plugin, this, g){
				public void run() {
					Shooter s = (Shooter) this.getArg(0);
					Gun g = (Gun) this.getArg(1);
					s.resetReload();
					s.resetFireCounter(g);
				}
			};
			reloadTask.startTaskDelayed((Integer) g.getProperty("RELOADTIME"));
			setReloading();
			if(!(g.getProperty("RELOADSOUND")==null)){
				Util.playCustomSound(GunsPlus.plugin, getLocation(), (String) g.getProperty("RELOADSOUND"), (Integer) g.getProperty("RELOADSOUNDVOLUME"));
			}
			return;
		}else if(isReloading()){
			return;
		}
	}
	
	@Override
	public void delay(Gun g){
		if(isDelayResetted()){
			setOnDelayingQueue();
		}
		if(isOnDelayingQueue()){
			Task t = new Task(GunsPlus.plugin, this){
				public void run() {
					Shooter sp = (Shooter) this.getArg(0);
					sp.resetDelay();
				}
			};
			t.startTaskDelayed((Integer) g.getProperty("SHOTDELAY"));
			setDelaying();
		}else if(isDelaying()){
			return;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void fire(Gun g){
		Inventory inv = getInventory();
		if(!GunUtils.isMountable(g)){
			return;
		}
		if (!GunUtils.checkInvForAmmo(inv, (ArrayList<ItemStack>)g.getProperty("AMMO"))){
			setFireing(false);
			return;
		}
		if (isReloading()){
			setFireing(false);
			return;
		}
		else if (isDelaying()){
			setFireing(false);
			return;
		}
		else if(isOutOfAmmo(g)){
			setFireing(false);
			return;
		}else{
			PropertyContainer pc = new PropertyContainer(g.getProperties());
			Ammo usedAmmo = GunUtils.getFirstCustomAmmo(inv,(ArrayList<ItemStack>)g.getProperty("AMMO"));
			if(usedAmmo != null){
				Util.setProperties(usedAmmo, pc);
			}
			HashMap<LivingEntity, Integer> targets_damage = new HashMap<LivingEntity, Integer>(GunUtils.getTargets(this.getLocation(), pc, false));
			if(targets_damage.isEmpty()){
				Location from = Util.getBlockInSight(this.getLocation(), 2, 5).getLocation();
				GunUtils.shootProjectile(from, this.getLocation().getDirection().toLocation(getLocation().getWorld()),
						(Projectile) pc.getProperty("PROJECTILE"));
			}
			for (LivingEntity tar : new HashSet<LivingEntity>(targets_damage.keySet())) {
				int damage = targets_damage.get(tar);
				Location from = Util.getBlockInSight(this.getLocation(), 2, 5).getLocation();
				GunUtils.shootProjectile(from, tar.getEyeLocation(),(Projectile) pc.getProperty("PROJECTILE"));
				if (damage < 0){
					targets_damage.put(tar, Math.abs(damage));
					damage = targets_damage.get(tar);
				}
				if (!((Integer) pc.getProperty("CRITICAL")<=0)&&Utils.getRandomInteger(1, 100) <= (Integer) pc.getProperty("CRITICAL")) {
					damage = tar.getHealth() + 1;
				}
				if(this.owner!=null)
					tar.damage(damage, owner.getPlayer());
				else
					tar.damage(damage);
			}
			
			g.performEffects(this, new HashSet<LivingEntity>(new ArrayList<LivingEntity>(targets_damage.keySet())), pc);

			if (!(pc.getProperty("SHOTSOUND") == null)) {
				if ((Integer)pc.getProperty("SHOTDELAY") < 5
						&& Utils.getRandomInteger(0, 100) < 35) {
					Util.playCustomSound(GunsPlus.plugin, getLocation(),
							(String) pc.getProperty("SHOTSOUND"),
							(Integer) pc.getProperty("SHOTSOUNDVOLUME"));
				} else {
					Util.playCustomSound(GunsPlus.plugin, getLocation(),
							(String) pc.getProperty("SHOTSOUND"),
							(Integer) pc.getProperty("SHOTSOUNDVOLUME"));
				}

			}
			
			GunUtils.removeAmmo(inv, (ArrayList<ItemStack>) pc.getProperty("AMMO"));
			
			setFireCounter(g, getFireCounter(g) + 1);
			
			if (GunsPlus.autoreload && getFireCounter(g) >= ((Number) pc.getProperty("SHOTSBETWEENRELOAD")).intValue())
				reload(g);
			if ((Integer) pc.getProperty("SHOTDELAY") > 0)
				delay(g);
			
			setFireing(false);
		}
	}
	
	@Override
	public Inventory getInventory() {
		return inventory;
	}

	public void setEntered(boolean entered) {
		if(entered==true){
			owner_inv.setContents(owner.getPlayer().getInventory().getContents());
			owner.getPlayer().getInventory().setContents(new ItemStack[owner.getPlayer().getInventory().getSize()]);
			owner.getPlayer().setItemInHand(new SpoutItemStack((GenericCustomItem)getGun(), 1));
			if(Tripod.forcezoom)
				owner.zoom(gun);
//			 if (owner.getPlayer().getVehicle() != null){
//		        owner.getPlayer().getVehicle().eject();
//		     }
//			 if (!(getLocation().getBlock().getRelative(BlockFace.DOWN).getTypeId() == 0)&&!getLocation().getBlock().isLiquid()){
//		    	  dropChair();
//				  enterChair(owner.getPlayer());
//		     }
		}else{
			owner.getPlayer().getInventory().setContents(owner_inv.getContents());
//			chair.eject();
//			removeChair();
		}
		this.entered = entered;
	}
	
//	private void dropChair(){
//		Location location = getLocation().add(0.5, -0.5, 0.5);
//		chair = location.getWorld().dropItemNaturally(location, new ItemStack(Material.CLAY_BALL));
//		chair.setPickupDelay(Integer.MAX_VALUE);
//		chair.teleport(location);
//		chair.setVelocity(new Vector(0, 0, 0));
//	}
//	
//	private void removeChair(){
//		if(chair!=null)
//			chair.remove();
//	}
//	
//	private void enterChair(SpoutPlayer sp){
//		if(chair!=null){
//			chair.setPassenger(sp);
//		}
//	}

	public Inventory getOwnerInventory() {
		return owner_inv;
	}

	public void setOwnerInventory(Inventory owner_inv) {
		this.owner_inv = owner_inv;
	}

	public void dropGun(){
		if(gun!=null){
			droppedGun = getLocation().getWorld().dropItemNaturally(getLocation(), new SpoutItemStack((GenericCustomItem)gun));
			droppedGun.setPickupDelay(Integer.MAX_VALUE);
		}
	}
	
	public void removeDroppedGun() {
		if(droppedGun!=null){
			droppedGun.remove();
		}
	}
	
	public Item getDroppedGun() {
		return droppedGun;
	}

	public Gun getGun() {
		return gun;
	}
	
	public void setGun(Gun g) {
		this.gun = g;
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
	
	private void setOwner(GunsPlusPlayer owner) {
		this.owner = owner;
	}
	
	public Location getGunLoc() {
		return gunLoc;
	}

	private void setGunLoc(Location gunLoc) {
		this.gunLoc = gunLoc;
	}

	public Set<Target> getTargets() {
		return targets;
	}

	public void setTargets(Set<Target> tars) {
		this.targets = tars;
	}
	
	public String getOwnername() {
		return ownername;
	}

	public void setOwnername(String ownername) {
		this.ownername = ownername;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public boolean isWorking() {
		return working;
	}

	public void setWorking(boolean working) {
		if(working)
			ai.startAI();
		else
			ai.stopAI();
		this.working = working;
	}

	public boolean isEntered() {
		return entered;
	}

	public TripodAI getAI() {
		return ai;
	}
	
	public void setAI(TripodAI a){
		ai = a;
	}
}
