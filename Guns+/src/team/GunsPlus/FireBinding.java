package team.GunsPlus;

import java.util.HashMap;
import java.util.Map;

import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.event.input.KeyBindingEvent;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.keyboard.BindingExecutionDelegate;
import org.getspout.spoutapi.player.SpoutPlayer;

import team.ApiPlus.Util.Task;
import team.ApiPlus.API.Property.*;
import team.GunsPlus.Enum.FireBehavior;
import team.GunsPlus.Enum.KeyType;
import team.GunsPlus.Item.Gun;
import team.GunsPlus.Util.GunUtils;
import team.GunsPlus.Util.PlayerUtils;

public class FireBinding implements BindingExecutionDelegate {

	private GunsPlus plugin;
	private static Map<SpoutPlayer, Task> autoFire = new HashMap<SpoutPlayer, Task>();

	public FireBinding(GunsPlus p, KeyType kt) {
		plugin = p;
		SpoutManager.getKeyBindingManager().registerBinding("Fire", kt.getKey(), "If pressed guns will fire.", this, plugin);
	}

	@Override
	public void keyPressed(KeyBindingEvent ev) {
		if(ev.getScreenType().equals(ScreenType.GAME_SCREEN)) {
			SpoutPlayer sp = ev.getPlayer();
			if(GunUtils.holdsGun(sp)) {
				Gun g = GunUtils.getGunInHand(sp);
				@SuppressWarnings("unchecked")
				FireBehavior f = ((ObjectProperty<FireBehavior>) g.getProperty("FIREBEHAVIOR")).getValue();
				if(f.equals(FireBehavior.SINGLE)) {
					PlayerUtils.getPlayerBySpoutPlayer(sp).fire(g);
				} else if(f.equals(FireBehavior.AUTOMATIC)) {
					Task task = new Task(GunsPlus.plugin, PlayerUtils.getPlayerBySpoutPlayer(sp), g) {
						public void run() {
							GunsPlusPlayer gp = (GunsPlusPlayer) this.getArg(0);
							Gun g = (Gun) this.getArg(1);
							gp.fire(g);
						}
					};
					task.startTaskRepeating(((NumberProperty) g.getProperty("SHOTDELAY")).getValue().longValue(), false);
					autoFire.put(sp, task);
				}
			}
		}
	}

	@Override
	public void keyReleased(KeyBindingEvent ev) {
//		if(ev.getScreenType().equals(ScreenType.GAME_SCREEN)) {
			SpoutPlayer sp = ev.getPlayer();
			if(GunUtils.holdsGun(sp)) {
				if(autoFire.containsKey(sp)) {
					autoFire.get(sp).stopTask();
					autoFire.remove(sp);
				}
			}
		}
//	}
}
