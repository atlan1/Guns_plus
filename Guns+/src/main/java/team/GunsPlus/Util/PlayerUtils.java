package team.GunsPlus.Util;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

import team.ApiPlus.Util.Task;
import team.ApiPlus.Util.Utils;
import team.GunsPlus.GunsPlus;
import team.GunsPlus.GunsPlusPlayer;

public class PlayerUtils {

	public static GunsPlusPlayer getPlayerBySpoutPlayer(SpoutPlayer sp) {
		for(GunsPlusPlayer gp : GunsPlus.GunsPlusPlayers) {
			if(gp.getPlayer().equals(sp)) {
				return gp;
			}
		}
		return null;
	}

	public static GunsPlusPlayer getPlayerByName(String sp) {
		for(GunsPlusPlayer gp : GunsPlus.GunsPlusPlayers) {
			if(gp.getPlayer().getName().equals(sp)) {
				return gp;
			}
		}
		return null;
	}

	public static void performRecoil(SpoutPlayer p, float recoil) {
		Task t1 = new Task(GunsPlus.plugin, p, recoil) {
			public void run() {
				SpoutPlayer p = (SpoutPlayer) this.getArg(0);
				Location l = p.getLocation();
				l.setPitch(l.getPitch() - this.getFloatArg(1) / 2);
				p.teleport(l);
			}
		};
		t1.startTaskRepeating(3, 1, false);
		Task t2 = new Task(GunsPlus.plugin, t1) {
			public void run() {
				Task t = (Task) this.getArg(0);
				t.stopTask();
			}
		};
		t2.startTaskDelayed(5);
		Task t3 = new Task(GunsPlus.plugin, p, recoil) {
			public void run() {
				SpoutPlayer p = (SpoutPlayer) this.getArg(0);
				Location l = p.getLocation();
				l.setPitch(l.getPitch() + this.getFloatArg(1) / 3);
				p.teleport(l);
			}
		};
		t3.startTaskRepeating(6, 1, false);
		Task t4 = new Task(GunsPlus.plugin, t3) {
			public void run() {
				Task t = (Task) this.getArg(0);
				t.stopTask();
			}
		};
		t4.startTaskDelayed(9);
	}

	public static void performKnockBack(SpoutPlayer p, float knockback) {
		Location loc = p.getLocation();
		if(loc.getPitch() > 5) {
			loc.setPitch(0);
		} else if(loc.getPitch() < -5) {
			loc.setPitch(0);
		}
		Vector pdir = Utils.getDirection(loc);
		Vector v = pdir;
		v.setX(v.getX() * (knockback / 100) * -1);
		v.setZ(v.getZ() * (knockback / 100) * -1);
		p.setVelocity(v);
	}

	public static boolean hasSpoutcraft(Player p) {
		return SpoutManager.getPlayer(p).isSpoutCraftEnabled();
	}

	public static void sendNotification(SpoutPlayer sp, String title, String text, ItemStack icon, int duration) {
		if(!GunsPlus.notifications)
			return;
		if(title.length() > 26) {
			Util.warn("Too long notification. Check your item names.");
			title = title.replace(title.substring(25, title.length() - 1), "");
		}
		if(text.length() > 26) {
			Util.warn("Too long notification. Check your item names.");
			text = text.replace(text.substring(25, text.length() - 1), "");
		}
		sp.sendNotification(title, text, icon, duration);
	}
}
