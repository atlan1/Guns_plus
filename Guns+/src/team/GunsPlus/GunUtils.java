package team.GunsPlus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import net.minecraft.server.MobEffect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.EnderSignal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.MaterialData;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.getspout.spoutapi.sound.SoundManager;

import team.GunsPlus.GunsPlus;
import team.GunsPlus.Util;
import team.old.GunsPlus.Classes.Effect;
import team.GunsPlus.Classes.Gun;
import team.old.GunsPlus.Classes.Task;

public class GunUtils {
	
		public static boolean holdsGun(Player p) {
			ItemStack is = p.getItemInHand();
			for(Gun g : GunsPlus.allGuns){
				SpoutItemStack sis = new SpoutItemStack(g);
				if(is.getTypeId()==sis.getTypeId()&&is.getDurability()==sis.getDurability()){
					return true;
				}
			}
			return false;
		}
		
		public static void playCustomSound(JavaPlugin plugin,Player player, String url)
		{
			SoundManager SM = SpoutManager.getSoundManager();
			SpoutPlayer sp = SpoutManager.getPlayer(player);
			SM.playCustomSoundEffect(plugin, sp, url, false, sp.getLocation() ,25, 50);
			SM.playGlobalCustomSoundEffect(plugin, url, false, sp.getLocation(), 100, 100);
		}
		
		public static void shootProjectile(String name,double speed, Player p){
			BlockIterator bitr = new BlockIterator(p);
			Block b = null;
			while(bitr.hasNext()){
				b = bitr.next();
				if(b!=p.getEyeLocation().getBlock())break;
			}
			Location l = p.getEyeLocation();
			if(b!=null)
				l = b.getLocation();
			Entity e = null;
			if(name.equalsIgnoreCase("arrow")){
				e = l.getWorld().spawn(l, Arrow.class);
				Arrow a = (Arrow) e;
				a.setShooter(p);
				a.setVelocity(p.getLocation().getDirection().multiply(speed));
			}else if(name.equalsIgnoreCase("fireball")){
				e = l.getWorld().spawn(l, Fireball.class);
				Fireball fbg = (Fireball)e;
				fbg.setYield(0f);
				fbg.setIsIncendiary(false);
				fbg.setShooter(p);
				fbg.setDirection(p.getLocation().getDirection());
				fbg.setVelocity(p.getLocation().getDirection().multiply(speed));
			}else if(name.equalsIgnoreCase("snowball")){
				e = l.getWorld().spawn(l, Snowball.class);
				Snowball sb= (Snowball)e;
				sb.setShooter(p);
				sb.setVelocity(p.getLocation().getDirection().multiply(speed));
			}else if(name.equalsIgnoreCase("egg")){
				e = l.getWorld().spawn(l, Egg.class);
				Egg egg= (Egg)e;
				egg.setShooter(p);
				egg.setVelocity(p.getLocation().getDirection().multiply(speed));
			}else if(name.equalsIgnoreCase("enderpearl")){
				e = l.getWorld().spawn(l, EnderPearl.class);
				EnderPearl ep= (EnderPearl)e;
				ep.setShooter(p);
				ep.setVelocity(p.getLocation().getDirection().multiply(speed));
			}else if(name.equalsIgnoreCase("eyeofender")){
				e = l.getWorld().spawn(l, EnderSignal.class);
				EnderSignal es = (EnderSignal)e;
				es.setVelocity(p.getLocation().getDirection().multiply(speed));
			}
		}
		
		public static ArrayList<LivingEntity> getTargetsWithSpread(Player p, int range, boolean headshot, int spread, int accuracy){
			ArrayList<LivingEntity> lea = new ArrayList<LivingEntity>();
			Location loc = p.getEyeLocation();
			LivingEntity e =null;
			
			if(spread==0){
				e=getTarget(loc, range, headshot,accuracy);
				if(e!=null&&!lea.contains(e)){
					lea.add(e);
				}
				return lea;
			}
			for(int i=0; i<spread; i+=4){
				loc = p.getEyeLocation();
				loc.setYaw(loc.getYaw()+i);
				e = getTarget(loc, range, headshot,accuracy);
				if(e!=null&&!lea.contains(e))lea.add(e);
				loc = p.getEyeLocation();
				loc.setYaw(loc.getYaw()-i);
				e = getTarget(loc, range, headshot,accuracy);
				if(e!=null&&!lea.contains(e))lea.add(e);
				loc = p.getEyeLocation();
				loc.setPitch(loc.getPitch()+i);
				e = getTarget(loc, range, headshot,accuracy);
				if(e!=null&&!lea.contains(e))lea.add(e);
				loc = p.getEyeLocation();
				loc.setPitch(loc.getPitch()-i);
				e = getTarget(loc, range, headshot,accuracy);
				if(e!=null&&!lea.contains(e))lea.add(e);
			}
			return lea;
		}
		
		public static LivingEntity getTarget(Location loc, int range, boolean headshot, int accuracy) {
			Random rand = new Random();
			int random = rand.nextInt(101);
			LivingEntity target = null;
			BlockIterator bitr = new BlockIterator(loc,0d, range);
			Block b;
			Location l;
			int bx, by, bz;
			double ex, ey, ez;
			while (bitr.hasNext()) {
				b = bitr.next();
				bx = b.getX();
				by = b.getY();
				bz = b.getZ();
				Set<LivingEntity> entities = new HashSet<LivingEntity>();
				Location lb = new Location(b.getWorld(), bx, by, bz);
				if(!Util.isTransparent(lb.getBlock()))break;
				for (Entity e : Util.getNearbyEntities(lb, 0.5, 0.5, 0.5)) {
					if (e instanceof LivingEntity) {
						entities.add((LivingEntity) e);
					}
				}
				
				for (LivingEntity e : entities) {
					l = headshot==false ? e.getLocation() : e.getEyeLocation();
					ex = l.getX();
					ey = l.getY();
					ez = l.getZ();
					if(random>=accuracy){
						if(e instanceof Skeleton||e instanceof Zombie||e instanceof Blaze||e instanceof Player||e instanceof Creeper||e instanceof PigZombie||e instanceof Villager){
							if ((((bx - .5) <= ex) && (ex <= (bx + 1.5)))&&(((bz - .5) <= ez) && (ez <= (bz + 1.5)))&&(((by - 1) <= ey) && (ey <= (headshot==true?by:by+2.65)))) {
								target = e;
								break;
							}
						}else if(e instanceof Pig||e instanceof Sheep||e instanceof Cow||e instanceof MushroomCow||e instanceof Squid||e instanceof Wolf||e instanceof Spider){
							if ((((bx - .9) <= ex) && (ex <= (bx + 1.9)))&&(((bz - .9) <= ez) && (ez <= (bz + 1.9)))&&(((by - 1) <= ey) && (ey <= (headshot==true?by:by+1.3)))) {
								target = e;
								break;
							}
						}else if(e instanceof Chicken||e instanceof CaveSpider||e instanceof Slime){
							if ((((bx - .5) <= ex) && (ex <= (bx + 1.5)))&&(((bz - .5) <= ez) && (ez <= (bz + 1.5)))&&(((by - 1) <= ey) && (ey <= (headshot==true?by:by+1.2)))) {
								target = e;
								break;
							}
						}else{
							if ((((bx - .75) <= ex) && (ex <= (bx + 1.75)))&&(((bz - .75) <= ez) && (ez <= (bz + 1.75)))&&(((by - 1) <= ey) && (ey <= (headshot==true?by:by+2.55)))) {
								target = e;
								break;
							}
						}
					}
					
					
				}
			}
			return target;
		}
		
		@SuppressWarnings("deprecation")
		public static void removeAmmo(ArrayList<ItemStack> ammo ,Player p){
			if(ammo==null) return;
			HashMap<Integer, ? extends ItemStack> invAll = new HashMap<Integer, SpoutItemStack>();
			Inventory inv = p.getInventory();
			ItemStack ammoStack = null; 
			for(ItemStack theStack: ammo){
				invAll = inv.all(theStack.getTypeId());
				for(int j = 0; j<inv.getSize();j++){
					if(invAll.containsKey(j)){
						ItemStack hi = invAll.get(j);
						if(hi.getTypeId()==theStack.getTypeId()&&hi.getDurability()==theStack.getDurability()){
							ammoStack = hi;
							break;
						}
					}
				}
			}
			
			
			if(ammoStack == null){
				return;
			}
			
			if(ammoStack.getAmount() >1){
				ammoStack.setAmount(ammoStack.getAmount()-1);
			}else{
				inv.remove(ammoStack);
			}
			p.updateInventory();
		}

		public static boolean checkInvForAmmo(Player p, ArrayList<ItemStack> ammo) {
			if(ammo==null)return true;
			HashMap<Integer, ? extends ItemStack> invAll = new HashMap<Integer, SpoutItemStack>();
			Inventory inv = p.getInventory();
			for(ItemStack theStack : ammo){
				invAll = inv.all(theStack.getTypeId());
				for(int j = 0; j<inv.getSize();j++){
					if(invAll.containsKey(j)){
						ItemStack hi = invAll.get(j);
						if(hi.getTypeId()==theStack.getTypeId()&&hi.getDurability()==theStack.getDurability()){
							return true;
						}
					}
				}
			}
			return false;
		}
		
		public static int getAmmoCount(Player p, ArrayList<ItemStack> ammo){
			HashMap<Integer, ? extends ItemStack> invAll = new HashMap<Integer, SpoutItemStack>();
			int counter = 0;
			if(ammo==null)return counter;
			Inventory inv = p.getInventory();
			for(ItemStack theStack : ammo){
				invAll = inv.all(theStack.getTypeId());
				for(int j = 0; j<inv.getSize();j++){
					if(invAll.containsKey(j)){
						ItemStack hi = invAll.get(j);
						if(hi.getTypeId()==theStack.getTypeId()&&hi.getDurability()==theStack.getDurability()){
							counter+=hi.getAmount();
						}
					}
				}
			}
			return counter;
		}
		
		
		public static void performEffects(ArrayList<Effect> h, LivingEntity le, Player player, int range){
			Location loc;
			if(le!=null&&le!=player) {
				loc = le.getLocation();
			}else {
				loc = player.getTargetBlock(null, range).getLocation();
			}
			for(Effect eff : h){
				switch(eff.getType()){
					case 0:
						if(eff.hasEffect("explosion")){
							loc.getWorld().createExplosion(loc, (Integer) eff.getEffectValues("explosion").get(0));
						}
						if(eff.hasEffect("lightning")){
							loc.getWorld().strikeLightning(loc);
						}
						if(eff.hasEffect("smoke")){
							loc.getWorld().playEffect(loc, org.bukkit.Effect.SMOKE, (Integer) eff.getEffectValues("smoke").get(0));
						}
						if(eff.hasEffect("spawn")){
							Location l1 = loc;
							l1.setY(loc.getY()+1);
							loc.getWorld().spawnCreature(l1, EntityType.valueOf((String) eff.getEffectValues("spawn").get(0)));
						}
						if(eff.hasEffect("fire")){
							loc.getWorld().playEffect(loc, org.bukkit.Effect.MOBSPAWNER_FLAMES, (Integer) eff.getEffectValues("fire").get(0));
						}
						if(eff.hasEffect("place")){
							BlockIterator bi = new BlockIterator(player.getWorld(), player.getEyeLocation().toVector(), player.getEyeLocation().getDirection(), 0, range);
							Block last = null, b = null;
							boolean loop=true;
							while(bi.hasNext()&&loop){
								last = b;
								b = bi.next();
								if(!Util.isTransparent(b)){
									last.setTypeId((Integer) eff.getEffectValues("place").get(0));
									loop=false;
								}
							}
						}
						if(eff.hasEffect("break")){
							if(MaterialData.getBlock(loc.getBlock().getTypeId()).getHardness()<(Integer) eff.getEffectValues("break").get(0)){
								loc.getBlock().setTypeId(0);
							}
						}
						
						break;
					case 1:
						if(le!=null&&le!=player){
							if(eff.hasEffect("fire")){
								le.setFireTicks((Integer) eff.getEffectValues("fire").get(1));
							}
							if(eff.hasEffect("push")){
								Vector v = player.getLocation().getDirection();
								v.multiply((Double) eff.getEffectValues("push").get(0));
								le.setVelocity(v);
							}
							if(eff.hasEffect("draw")){
								Vector v = player.getLocation().getDirection();
								v.multiply((Double) eff.getEffectValues("draw").get(0)*-1);
								le.setVelocity(v);
							}
							for(int i=0;i<19;i++){
								if(eff.hasEffect("potion_"+i)){
									if(i==14||i==16)continue;
										CraftLivingEntity cle = (CraftLivingEntity)le;
										cle.getHandle().addEffect(new MobEffect((Integer)eff.getEffectValues("potion_"+i).get(0), (Integer)eff.getEffectValues("potion_"+i).get(1), (Integer)eff.getEffectValues("potion_"+i).get(2)));
								}
							}
						}
						break;
					case 2:
						if(eff.hasEffect("fire")){
							BlockIterator bi = new BlockIterator(player, range);
							while(bi.hasNext()){
								Block b = bi.next();
								b.getWorld().playEffect(b.getLocation(), org.bukkit.Effect.MOBSPAWNER_FLAMES, (Integer) eff.getEffectValues("fire").get(0));
							}
						}
						if(eff.hasEffect("explosion")){
							BlockIterator bi = new BlockIterator(player, range);
							boolean loop = true;
							while(bi.hasNext()&&loop){
								Block b = bi.next();
								if(Util.isTransparent(b))
								b.getWorld().createExplosion(b.getLocation(), (Integer) eff.getEffectValues("explosion").get(0));
								else loop=false;
							}
						}	
						if(eff.hasEffect("lightning")){
							BlockIterator bi = new BlockIterator(player, range);
							boolean loop=true;
							while(bi.hasNext()&&loop){
								Block b = bi.next();
								if(Util.isTransparent(b))
								b.getWorld().strikeLightning(b.getLocation());
								else loop=false;
							}
						}	
						if(eff.hasEffect("smoke")){
							BlockIterator bi = new BlockIterator(player, range);
							while(bi.hasNext()){
								Block b = bi.next();
								b.getWorld().playEffect(b.getLocation(), org.bukkit.Effect.SMOKE, (Integer) eff.getEffectValues("smoke").get(0));
							}
						}	
						if(eff.hasEffect("spawn")){
							BlockIterator bi = new BlockIterator(player, range);
							boolean loop=true;
							while(bi.hasNext()&&loop){
								Block b = bi.next();
								Location l1 = b.getLocation();
								l1.setY(loc.getY()+1);
								if(Util.isTransparent(b))
								b.getWorld().spawnCreature(l1, EntityType.valueOf((String) eff.getEffectValues("spawn").get(0)));
								else loop=false;
							}
						}	
						if(eff.hasEffect("place")){
							BlockIterator bi = new BlockIterator(player, range);
							boolean loop = true;
							while(bi.hasNext()&&loop){
								Block b = bi.next();
								if(Util.isTransparent(b))
								b.setTypeId((Integer) eff.getEffectValues("place").get(0));
								else loop=false;
							}
						}
						if(eff.hasEffect("break")){
							BlockIterator bi = new BlockIterator(player, range);
							boolean loop = true;
							while(bi.hasNext()&&loop){
								Block b = bi.next();
								if(MaterialData.getBlock(b.getTypeId()).getHardness()<(Integer) eff.getEffectValues("break").get(0)){
									b.setTypeId(0);
								}else{
									loop=false;
								}
							}
						}
						break;
						
				}
				
				
			}
		}
		
		public static void performRecoil(JavaPlugin plugin, Player p, float recoil){
			Task t1 = new Task(plugin, p, recoil){
				public void run() {
					Player p = (Player) this.getArg(0);
					Location l = p.getLocation();
					l.setPitch(l.getPitch() - this.getFloatArg(1)/2);
					p.teleport(l);
				}
			};
			t1.startRepeating(3, 1, false);
			Task t2 = new Task(plugin, t1){
				public void run(){
					Task t = (Task)this.getArg(0);
					t.stop();
				}
			};
			t2.startDelayed(5);
			Task t3 = new Task(plugin, p, recoil){
				public void run() {
					Player p = (Player) this.getArg(0);
					Location l = p.getLocation();
					l.setPitch(l.getPitch() + this.getFloatArg(1)/3);
					p.teleport(l);
				}
			};
			t3.startRepeating(6, 1, false);
			Task t4 = new Task(plugin, t3){
				public void run(){
					Task t = (Task)this.getArg(0);
					t.stop();
				}
			};
			t4.startDelayed(9);
		}
		
		public static void performKnockBack(Player p, float knockback){
			Location loc = p.getLocation();
			if(loc.getPitch()>5){
				loc.setPitch(0);
			}else if(loc.getPitch()<-5){
				loc.setPitch(0);
			}
			Vector pdir = Util.getDirection(loc);
			Vector v = pdir;
			v.setX(v.getX()*(knockback/100)*-1);
			v.setZ(v.getZ()*(knockback/100)*-1);
			p.setVelocity(v);
		}
		
		public static Gun getGunInHand(Player p) {
			ItemStack is = p.getItemInHand();
				
			if(holdsGun(p)){
				for(Gun g:GunsPlus.allGuns){
					SpoutItemStack sis = new SpoutItemStack(g);
					if(is.getTypeId()==sis.getTypeId()&&is.getDurability()==sis.getDurability()){
						return g;
					}
				}
			}
			return null;
		}
		
		public static Gun getGun(ItemStack item){
			for(Gun g:GunsPlus.allGuns){
				SpoutItemStack sis = new SpoutItemStack(g);
				if(item.getTypeId()==sis.getTypeId()&&item.getDurability()==sis.getDurability()){
					return g;
				}
			}
			return null;
		}
		
	/*	@SuppressWarnings("rawtypes")
		public static void zoomOut(Player p){
			//PotionEffect pe = new PotionEffect(PotionEffectType.SLOW, 0, 100);
			//p.addPotionEffect(pe, true);
			
			CraftPlayer cp = (CraftPlayer) p;
			SpoutPlayer sp = (SpoutPlayer) p;
			try {
	            Field field = EntityLiving.class.getDeclaredField("effects");
	            field.setAccessible(true);
	            HashMap effects = (HashMap)field.get(cp.getHandle());
	            effects.remove(2);
	            EntityPlayer player = cp.getHandle();
	            player.netServerHandler.sendPacket(new Packet42RemoveMobEffect(player.id, new MobEffect(2, 0, 0)));
	            cp.getHandle().getDataWatcher().watch(8, Integer.valueOf(0));
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
			sp.getMainScreen().removeWidget(plugin.zoomTextures.get(p));
			plugin.zoomTextures.remove(p);
		}
		
		@SuppressWarnings("rawtypes")
		public static void zoomIn(Player p, GenericTexture zTex,int  zoomfactor){
//			PotionEffect pe = new PotionEffect(PotionEffectType.SLOW, 24000, zoomfactor);
//			p.addPotionEffect(pe, true);
			
			
			CraftPlayer cp = (CraftPlayer) p;
			SpoutPlayer sp = (SpoutPlayer) p;
			cp.getHandle().addEffect(new MobEffect(2, 24000, zoomfactor));
			try{
				Field field;
			field = EntityLiving.class.getDeclaredField("effects");
	        field.setAccessible(true);
	        HashMap effects = (HashMap)field.get(cp.getHandle());
	        effects.remove(2);
			}catch(NoSuchFieldException e){
				e.printStackTrace();
			}catch(IllegalAccessException e1){
				e1.printStackTrace();
			}
			
			if(!(zTex==null)){
				GenericTexture t = zTex;
				t.setHeight(sp.getMainScreen().getHeight()).setWidth(sp.getMainScreen().getWidth());
				sp.getMainScreen().attachWidget(plugin, t);
				plugin.zoomTextures.put(p, t);
			}
		}*/
		
		public static boolean isGun(ItemStack i){
			for(Gun g:GunsPlus.allGuns){
				SpoutItemStack sis = new SpoutItemStack(g);
				if(i.getTypeId()==sis.getTypeId()&&i.getDurability()==sis.getDurability()){
					GunsPlus.log.info(i.getType().toString()+":"+sis.getType().toString());
					return true;
				}
			}
			return false;
		}
}
