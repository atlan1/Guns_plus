package team.GunsPlus.Util;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

import team.GunsPlus.GunsPlus;
import team.GunsPlus.GunsPlusPlayer;
import team.GunsPlus.Item.Gun;

public class PlayerUtils {

	public static GunsPlusPlayer getPlayerBySpoutPlayer(SpoutPlayer sp){
		for(GunsPlusPlayer gp : GunsPlus.GunsPlusPlayers){
			if(gp.getPlayer().equals(sp)){
				return gp;
			}
		}
		return null;
	}
	
	public static GunsPlusPlayer getPlayerByName(String sp){
		for(GunsPlusPlayer gp : GunsPlus.GunsPlusPlayers){
			if(gp.getPlayer().getName().equals(sp)){
				return gp;
			}
		}
		return null;
	}
	
	public static Location getTargetBlock(SpoutPlayer sp, int range){
		BlockIterator bitr = new BlockIterator(sp, range);
		Location loc = null;
		int i = 0;
		while(bitr.hasNext()){
			if(i==range){
				loc = bitr.next().getLocation();
			}
			i++;
		}
		return loc;
	}
	
	public static boolean isDelayResetted(SpoutPlayer sp){
		GunsPlusPlayer gp = PlayerUtils.getPlayerBySpoutPlayer(sp);
		return gp.isDelayResetted();
	}
	
	public static boolean isReloadResetted(SpoutPlayer sp){
		GunsPlusPlayer gp = PlayerUtils.getPlayerBySpoutPlayer(sp);
		return gp.isReloadResetted();
	}
	
	public static boolean isOnDelayQueue(SpoutPlayer sp){
		GunsPlusPlayer gp = PlayerUtils.getPlayerBySpoutPlayer(sp);
		return gp.isOnDelayingQueue();
	}
	
	public static boolean isDelayed(SpoutPlayer sp){
		GunsPlusPlayer gp = PlayerUtils.getPlayerBySpoutPlayer(sp);
		return gp.isDelaying();
	}
	
	public static void setOnDelayQueue(SpoutPlayer sp){
		GunsPlusPlayer gp = PlayerUtils.getPlayerBySpoutPlayer(sp);
		gp.setOnDelayingQueue();
	}
	
	public static void setDelayed(SpoutPlayer sp){
		GunsPlusPlayer gp = PlayerUtils.getPlayerBySpoutPlayer(sp);
		gp.setDelaying();
	}
	
	public static void removeDelay(SpoutPlayer sp){
		GunsPlusPlayer gp = PlayerUtils.getPlayerBySpoutPlayer(sp);
		gp.resetDelay();
	}
	public static boolean isOnReloadQueue(SpoutPlayer sp){
		GunsPlusPlayer gp = PlayerUtils.getPlayerBySpoutPlayer(sp);
		return gp.isOnReloadingQueue();
	}
	
	public static boolean isReloading(SpoutPlayer sp){
		GunsPlusPlayer gp = PlayerUtils.getPlayerBySpoutPlayer(sp);
		return gp.isReloading();
	}
	
	public static void setOnReloadQueue(SpoutPlayer sp){
		GunsPlusPlayer gp = PlayerUtils.getPlayerBySpoutPlayer(sp);
		gp.setOnReloadingQueue();
	}
	
	public static void setReloading(SpoutPlayer sp){
		GunsPlusPlayer gp = PlayerUtils.getPlayerBySpoutPlayer(sp);
		gp.setReloading();
	}
	
	public static void removeReload(SpoutPlayer sp){
		GunsPlusPlayer gp = PlayerUtils.getPlayerBySpoutPlayer(sp);
		gp.resetReload();
	}
	
	public static int getFireCounter(SpoutPlayer sp, Gun g){
		GunsPlusPlayer gp = PlayerUtils.getPlayerBySpoutPlayer(sp);
		return gp.getFireCounter(g);
	}
	
	public static void resetFireCounter(SpoutPlayer sp, Gun g){
		GunsPlusPlayer gp = PlayerUtils.getPlayerBySpoutPlayer(sp);
		gp.resetFireCounter(g);
	}
	
	public static void setFireCounter(SpoutPlayer sp,Gun g, int value){
		GunsPlusPlayer gp = PlayerUtils.getPlayerBySpoutPlayer(sp);
		gp.setFireCounter(g, value);
	}
	
	public static boolean isZooming(SpoutPlayer sp){
		GunsPlusPlayer gp = PlayerUtils.getPlayerBySpoutPlayer(sp);
		return gp.isZooming();
	}
	
	public static void setZooming(SpoutPlayer sp, boolean b){
		GunsPlusPlayer gp = PlayerUtils.getPlayerBySpoutPlayer(sp);
		gp.setZooming(b);
	}
	
	public static void performRecoil(SpoutPlayer p, float recoil){
		Task t1 = new Task(GunsPlus.plugin, p, recoil){
			public void run() {
				SpoutPlayer p = (SpoutPlayer) this.getArg(0);
				Location l = p.getLocation();
				l.setPitch(l.getPitch() - this.getFloatArg(1)/2);
				p.teleport(l);
			}
		};
		t1.startTaskRepeating(3, 1, false);
		Task t2 = new Task(GunsPlus.plugin, t1){
			public void run(){
				Task t = (Task)this.getArg(0);
				t.stopTask();
			}
		};
		t2.startTaskDelayed(5);
		Task t3 = new Task(GunsPlus.plugin, p, recoil){
			public void run() {
				SpoutPlayer p = (SpoutPlayer) this.getArg(0);
				Location l = p.getLocation();
				l.setPitch(l.getPitch() + this.getFloatArg(1)/3);
				p.teleport(l);
			}
		};
		t3.startTaskRepeating(6, 1, false);
		Task t4 = new Task(GunsPlus.plugin, t3){
			public void run(){
				Task t = (Task)this.getArg(0);
				t.stopTask();
			}
		};
		t4.startTaskDelayed(9);
	}

	public static void performKnockBack(SpoutPlayer p, float knockback){
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

	public static boolean hasSpoutcraft(Player p) {
		SpoutPlayer sp = SpoutManager.getPlayer(p);
		if (sp.isSpoutCraftEnabled()) {
			return true;
		}
		return false;
	}

	public static void sendNotification(SpoutPlayer sp, String title,
			String text, ItemStack icon, int duration) {
		try {
			sp.sendNotification(title, text, icon, duration);
		} catch (Exception e) {
		}
	}
}
