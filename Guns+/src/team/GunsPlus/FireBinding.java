package team.GunsPlus;

import java.util.HashMap;
import java.util.Map;

import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.event.input.KeyBindingEvent;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.keyboard.BindingExecutionDelegate;
import org.getspout.spoutapi.player.SpoutPlayer;
import team.GunsPlus.Enum.FireBehavior;
import team.GunsPlus.Enum.KeyType;
import team.GunsPlus.Item.Gun;
import team.GunsPlus.Util.GunUtils;
import team.GunsPlus.Util.PlayerUtils;
import team.GunsPlus.Util.Task;

public class FireBinding implements BindingExecutionDelegate{

	private GunsPlus plugin;
	private Map<SpoutPlayer, Task> autoFire = new HashMap<SpoutPlayer, Task>();
	
	public FireBinding(GunsPlus p, KeyType kt){
		plugin = p;
		SpoutManager.getKeyBindingManager().registerBinding("Fire", kt.getKey(), "If pressed guns will fire.", this, plugin);
	}
	
	@Override
	public void keyPressed(KeyBindingEvent ev) {
		if(ev.getScreenType().equals(ScreenType.GAME_SCREEN)){
			SpoutPlayer sp = ev.getPlayer();
			if(GunUtils.holdsGun(sp)){
				Gun g = GunUtils.getGunInHand(sp);
				FireBehavior f = (FireBehavior)g.getProperty("FIREBEHAVIOR");
				if(f.equals(FireBehavior.SINGLE)){
					PlayerUtils.getPlayerBySpoutPlayer(sp).fire(g);
				}else if(f.equals(FireBehavior.AUTOMATIC)){
					Task task = new Task(GunsPlus.plugin, sp, g){
						public void run(){
							SpoutPlayer sp = (SpoutPlayer) this.getArg(0);
							Gun g = (Gun) this.getArg(1);
							PlayerUtils.getPlayerBySpoutPlayer(sp).fire(g);
						}
					};
					task.startTaskRepeating(((Number) g.getProperty("SHOTDELAY")).longValue(), false);
					autoFire.put(sp, task);
				}
			}
		}
	}

	@Override
	public void keyReleased(KeyBindingEvent ev) {
		if(ev.getScreenType().equals(ScreenType.GAME_SCREEN)){
			SpoutPlayer sp = ev.getPlayer();
			if(GunUtils.holdsGun(sp)){
				if(autoFire.containsKey(sp)){
					autoFire.get(sp).stopTask();
					autoFire.remove(sp);
				}
			}
		}
	}

}
