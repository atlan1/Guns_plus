package team.GunsPlus.Classes;

import java.util.Random;
import java.util.ArrayList;


import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.gui.GenericTexture;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.material.item.GenericCustomItem;
import org.getspout.spoutapi.player.SpoutPlayer;

import team.GunsPlus.GunsPlus;

public class Gun extends GenericCustomItem{
	public GunsPlus plugin;
	private int damage;
	private int headshotdamage;
	private int range;
	private int reload;
	private int delay;
	private int counter;
	private int magazine;
	private float recoil;
	private float knockback;
	private ArrayList<ItemStack> ammo = new ArrayList<ItemStack>();
	private int zoomfactor;
	private String shotSound;
	private String reloadSound;
	private String projectile;
	private GenericTexture zTex;
	private ArrayList<Effect> effects;
	private int spread;
	private int accIn;
	private int accOut;
	private int critical;
	private double projectileSpeed;
	public Gun(Plugin pl, int dmg, int rng, int rtime, int dtime, int nbrs, double rec,double knock, ArrayList<ItemStack>  amm, int zoom, String sSound, String rSound, int hdmg, int spready, int ai, int ao,int cri, String zt,String pro,double prospeed, ArrayList<Effect> e, GunsPlus instance, String url, String name){
		super(pl, name, url);
		damage= dmg;
		range = rng;
		ammo = amm;
		reload = rtime;
		delay = dtime;
		magazine = nbrs;
		counter = magazine;
		recoil = (float) rec;
		plugin = instance;
		zoomfactor = zoom;
		shotSound = sSound;
		reloadSound = rSound;
		headshotdamage = hdmg;
		knockback = (float) knock;
		effects = e;
		spread = spready;
		accIn = ai;
		accOut = ao;
		critical =cri;
		projectile = pro;
		projectileSpeed = prospeed;
		if(zt!=null){
			zTex = new GenericTexture(zt);
			zTex.setAnchor(WidgetAnchor.SCALE).setX(0).setY(0).setPriority(RenderPriority.Normal);
		}
	}

	
	public void zoom(Player p){
		if(!plugin.zoom.containsKey(p)){
			plugin.util.zoomIn(p, zTex, zoomfactor);
			plugin.zoom.put(p, true);
			if(plugin.generalConfig.getBoolean("send-notifications"))
			((SpoutPlayer)p).sendNotification(this.getName(), "Zoomed in!", Material.ENDER_PEARL);
		}else{
			plugin.util.zoomOut(p);
			plugin.zoom.remove(p);
			if(plugin.generalConfig.getBoolean("send-notifications"))
			((SpoutPlayer)p).sendNotification(this.getName(), "Zoomed out!", Material.GOLDEN_APPLE);
		}
		
	}
	
	public void shot(Player p){
		if(plugin.util.checkInvForAmmo(p, ammo)){
			if(plugin.reload.containsKey(p)&&plugin.reload.get(p)==true){
				return;
			}else if(plugin.delay.containsKey(p)&&plugin.delay.get(p)==true){
				return;
			}else{
					theActualShot(p);
				if(knockback!=0){
					plugin.util.performKnockBack(p, knockback);
				}
				if(recoil!=0){
					plugin.util.performRecoil(p, recoil);
				}
				
				if(counter<=0){
					reload(p);
				}else{
					if(delay>0){
						delay(p);
					}
				}
				
			}
			return;
		}
	}
	
	public void theActualShot(Player p){
		if(projectile!=null||projectile==""){
			plugin.util.shootProjectile(projectile,projectileSpeed, p);
		}
		
		ArrayList<LivingEntity> le1 =  plugin.util.getTargetsWithSpread(p, this.range, false, this.spread, plugin.zoom.containsKey(p)?this.accIn:this.accOut);
		ArrayList<LivingEntity> le2 =  plugin.util.getTargetsWithSpread(p, this.range, true, this.spread, plugin.zoom.containsKey(p)?this.accIn:this.accOut);
		for(int i=0;i<le1.size();i++){
			boolean rem1 = false;
			LivingEntity e1 = le1.get(i);
			for(int j=0;j<le2.size();j++){
				boolean rem2 = false;
				LivingEntity e2 = le2.get(j);
				if(e1==e2){
					rem1 = true;
				}
				if(e2==p){
					rem2 = true;
				}
				if(rem2==true){
					le2.remove(e2);
				}
			}
			if(e1==p){
				rem1 = true;
			}
			if(rem1==true){
				le1.remove(e1);
			}
		}
		Random rand = new Random();
		int random = rand.nextInt(101);
		boolean isCritical = false;
		if(random<=this.critical){
			if(plugin.generalConfig.getBoolean("send-notifications")&&!(le1.isEmpty()&&le2.isEmpty()))
			((SpoutPlayer)p).sendNotification(this.getName(), "Critical hit!", Material.DIAMOND_SWORD);
			isCritical = true;
		}
		if(le1.isEmpty()==false||le2.isEmpty()==false){
			for(LivingEntity body: le1){
				if(body!=p){
					if(isCritical)body.damage(body.getHealth(), p);
					else body.damage(damage, p);
				}
				
				plugin.util.performEffects(effects, body, p, range);
			}
			for(LivingEntity head: le2){
				if(head!=p){
					if(isCritical)head.damage(head.getHealth(), p);
					else head.damage(headshotdamage, p);
					if(plugin.generalConfig.getBoolean("send-notifications"))
					((SpoutPlayer)p).sendNotification(this.getName(), "Headshot!", Material.ARROW);
				}
				
				plugin.util.performEffects(effects, head, p, range);
			}
		}else{
			LivingEntity nullent =null;
			plugin.util.performEffects(effects, nullent, p, range);
		}
		
		plugin.util.removeAmmo(this.ammo, p);
		if(!(shotSound==null)){
			plugin.util.playCustomSound(p, shotSound);
		}
		counter--;
	}

	public void reload(Player p){
		
		if(counter==magazine)return;
		if(plugin.generalConfig.getBoolean("send-notifications"))
			((SpoutPlayer)p).sendNotification(this.getName(), "Reloading...", Material.WATCH);
		if(!plugin.reload.containsKey(p)){
			plugin.reload.put(p, false);
		}
		if(plugin.reload.containsKey(p)&&plugin.reload.get(p)==false){
			Task t = new Task(plugin, p){
				public void run() {
					Player p = (Player) this.getArg(0);
					plugin.reload.remove(p);
					counter = magazine;
				}
			};
			t.startDelayed(reload);
			plugin.reload.put(p, true);
			if(!(reloadSound==null)){
				plugin.util.playCustomSound(p, reloadSound);
			}
			return;
		}else if(plugin.reload.containsKey(p)&&plugin.reload.get(p)==true){
			return;
		}
	}
	
	public void delay(Player p){
		if(!plugin.delay.containsKey(p)){
			plugin.delay.put(p, false);
		}
		if(plugin.delay.containsKey(p)&&plugin.delay.get(p)==false){
			Task t = new Task(plugin, p){
				public void run() {
					Player p = (Player) this.getArg(0);
					plugin.delay.remove(p);
				}
			};
			t.startDelayed(delay);
			plugin.delay.put(p, true);
		}else if(plugin.delay.containsKey(p)&&plugin.delay.get(p)==true){
			return;
		}
	}
	
	public int getDamage() {
		return damage;
	}


	public void setDamage(int damage) {
		this.damage = damage;
	}


	public int getRange() {
		return range;
	}


	public void setRange(int range) {
		this.range = range;
	}


	public int getReload() {
		return reload;
	}


	public void setReload(int reload) {
		this.reload = reload;
	}


	public int getDelay() {
		return delay;
	}


	public void setDelay(int delay) {
		this.delay = delay;
	}


	public int getMagazine() {
		return magazine;
	}


	public void setMagazine(int magazine) {
		this.magazine = magazine;
	}


	public float getRecoil() {
		return recoil;
	}


	public void setRecoil(float recoil) {
		this.recoil = recoil;
	}


	public ArrayList<ItemStack> getAmmo() {
		return ammo;
	}


	public void setAmmo(ArrayList<ItemStack> ammo) {
		this.ammo = ammo;
	}


	public int getZoomfactor() {
		return zoomfactor;
	}


	public void setZoomfactor(int zoomfactor) {
		this.zoomfactor = zoomfactor;
	}


	public String getReloadSound() {
		return reloadSound;
	}


	public void setReloadSound(String reloadSound) {
		this.reloadSound = reloadSound;
	}


	public String getShotSound() {
		return shotSound;
	}


	public void setShotSound(String shotSound) {
		this.shotSound = shotSound;
	}


	public int getHeadshotdamage() {
		return headshotdamage;
	}


	public void setHeadshotdamage(int headshotdamage) {
		this.headshotdamage = headshotdamage;
	}


	public ArrayList<Effect> getEffects() {
		return effects;
	}


	public void setEffects(ArrayList<Effect> effects) {
		this.effects = effects;
	}


	public float getKnockback() {
		return knockback;
	}


	public void setKnockback(float knockback) {
		this.knockback = knockback;
	}


	public int getCritical() {
		return critical;
	}


	public void setCritical(int critical) {
		this.critical = critical;
	}
	
	public int getAccIn() {
		return accIn;
	}


	public void setAccOut(int out) {
		this.accOut = out;
	}
	
	public int getAccOut() {
		return accOut;
	}


	public void setAccIn(int in) {
		this.accIn = in;
	}


	public String getProjectile() {
		return projectile;
	}


	public void setProjectile(String projectile) {
		this.projectile = projectile;
	}


	public GenericTexture getzTex() {
		return zTex;
	}


	public void setzTex(GenericTexture zTex) {
		this.zTex = zTex;
	}


	public double getProjectileSpeed() {
		return projectileSpeed;
	}


	public void setProjectileSpeed(int projectileSpeed) {
		this.projectileSpeed = projectileSpeed;
	}


	public int getCounter() {
		return counter;
	}
}
