package team.GunsPlus;


import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.event.input.KeyPressedEvent;
import org.getspout.spoutapi.event.input.KeyReleasedEvent;
import org.getspout.spoutapi.keyboard.Keyboard;
import org.getspout.spoutapi.player.SpoutPlayer;

import team.GunsPlus.Classes.Gun;
import team.GunsPlus.Classes.Task;

public class GunsPlusListener implements Listener{

	

	GunsPlus plugin;
	
	public GunsPlusListener (GunsPlus g){
		plugin = g;
	}
	
	@EventHandler(priority=EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent e){
		Player p = e.getPlayer();
		Action a = e.getAction();
		//CHECK IF THE PLAYER IS HOLDING A GUN
		if(plugin.util.holdsGun(p)){
			Gun g = plugin.util.getGunInHand(p);
			//SWITCH THE THREE BUTTON TYPES AND PERFORM THE MATCHING ACTION
			switch(plugin.zoomtype) {
				case 0:
					if(a.equals(Action.RIGHT_CLICK_AIR)||a.equals(Action.RIGHT_CLICK_BLOCK))g.zoom(p);break;
				case 1:
					if(a.equals(Action.LEFT_CLICK_AIR)||a.equals(Action.LEFT_CLICK_BLOCK))g.zoom(p);break;
				case 2:
					if((a.equals(Action.RIGHT_CLICK_AIR)||a.equals(Action.RIGHT_CLICK_BLOCK))&&p.isSneaking())g.zoom(p);break;
				case 3:
					if((a.equals(Action.LEFT_CLICK_AIR)||a.equals(Action.LEFT_CLICK_BLOCK))&&p.isSneaking())g.zoom(p);break;
			}
			switch(plugin.shottype) {
				case 0:
					if(a.equals(Action.RIGHT_CLICK_AIR)||a.equals(Action.RIGHT_CLICK_BLOCK))g.shot(p);break;
				case 1:
					if(a.equals(Action.LEFT_CLICK_AIR)||a.equals(Action.LEFT_CLICK_BLOCK))g.shot(p);break;
				case 2:
					if((a.equals(Action.RIGHT_CLICK_AIR)||a.equals(Action.RIGHT_CLICK_BLOCK))&&p.isSneaking())g.shot(p);break;
				case 3:
					if((a.equals(Action.LEFT_CLICK_AIR)||a.equals(Action.LEFT_CLICK_BLOCK))&&p.isSneaking())g.shot(p);break;
			}
			switch(plugin.rtype) {
				case 0:
					if(a.equals(Action.RIGHT_CLICK_AIR)||a.equals(Action.RIGHT_CLICK_BLOCK))g.reload(p);break;
				case 1:
					if(a.equals(Action.LEFT_CLICK_AIR)||a.equals(Action.LEFT_CLICK_BLOCK))g.reload(p);break;
				case 2:
					if((a.equals(Action.RIGHT_CLICK_AIR)||a.equals(Action.RIGHT_CLICK_BLOCK))&&p.isSneaking())g.reload(p);break;
				case 3:
					if((a.equals(Action.LEFT_CLICK_AIR)||a.equals(Action.LEFT_CLICK_BLOCK))&&p.isSneaking())g.reload(p);break;
			}
		}
	}
	//ZOOMS OUT AND REMOVES THE PLAYER FROM THE RELOAD HASHMAP
	@EventHandler(priority=EventPriority.NORMAL)
	public void onPlayerRespawn(PlayerRespawnEvent e){
		if(plugin.zoom.containsKey(e.getPlayer())){
			plugin.util.zoomOut(e.getPlayer());
		}else if(plugin.reload.containsKey(e.getPlayer())){
			plugin.reload.remove(e.getPlayer());
		}
	}
	
	//ZOOMS OUT IF THE PLAYER TOGGLES SHIFT, WAS SNEAKING BEFORE AND HAD THE ZOOMKEY 2 OR 3 (2=RIGHT+SHIFT, 3=LEFT+SHIFT)
	@EventHandler(priority=EventPriority.NORMAL)
	public void  onPlayerToggleSneak(PlayerToggleSneakEvent e){
		if((plugin.zoomtype==2||plugin.zoomtype==3)&&e.getPlayer().isSneaking()&&plugin.zoom.containsKey(e.getPlayer())&&plugin.util.holdsGun(e.getPlayer())){
			Gun g = plugin.util.getGunInHand(e.getPlayer());
			g.zoom(e.getPlayer());
		}
	}
	
	//ZOOMS OUT IF THE PLAYER CHANGES THE HELD ITEM
	@EventHandler(priority=EventPriority.NORMAL)
	public void  onItemHeldChange(PlayerItemHeldEvent e){
		
		if(plugin.zoom.containsKey(e.getPlayer())){
			plugin.util.zoomOut(e.getPlayer());
		}
	}
	
	//START DRAWING THE HUD IF THE PLAYER HAS THE PERMISSIONS AND HAS SPOUTCRAFT
	@EventHandler(priority=EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent e){
		if(plugin.hudenabled&&SpoutManager.getPlayer(e.getPlayer()).isSpoutCraftEnabled()){
			Task t  = new Task (plugin, (SpoutPlayer)e.getPlayer()){
				public void run(){
					SpoutPlayer p = (SpoutPlayer) this.getArg(0);
					if(p.hasPermission("gunpack.hud")){
						plugin.util.updateHUD(p);
					}
				}
			};
			t.startRepeating(5);
			plugin.hudPlayers.put(e.getPlayer(), t);
		}
		
	}
	
	//REMOVE THE PLAYER FROM THE HUD HASHMAP
	@EventHandler(priority=EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent e){
		if(plugin.hudPlayers.containsKey(e.getPlayer())){
			plugin.hudPlayers.get(e.getPlayer()).stop();
			plugin.hudPlayers.remove(e.getPlayer());
		}
	}
	
	//ZOOMS OUT IF THE PLAYER ENTERS A BED
	@EventHandler(priority=EventPriority.NORMAL)
	public void  onPlayerBedEnter(PlayerBedEnterEvent e){
		if(plugin.zoom.containsKey(e.getPlayer())){
			plugin.util.zoomOut(e.getPlayer());
		}
	}
	
	//ZOOMS OUT IF THE PLAYER ENTERS A PORTAL
	@EventHandler(priority=EventPriority.NORMAL)
	public void  onPlayerPortal(PlayerPortalEvent e){
		if(plugin.zoom.containsKey(e.getPlayer())){
			plugin.util.zoomOut(e.getPlayer());
		}
	}
	
	//CHECK THE THREE KEY TYPES AND PERFORM THE MATCHING ACTION
	@EventHandler(priority=EventPriority.NORMAL)
	public void onKeyPressedEvent(KeyPressedEvent e){
		SpoutPlayer p = e.getPlayer();
		Keyboard k = e.getKey();
		//CHECKS IF THE PLAYER IS HOLDING A GUN AND IS IN GAME
		if(plugin.util.holdsGun(p)&&e.getScreenType().toString().equals("GAME_SCREEN")){
			Gun g = plugin.util.getGunInHand(p);
			//TYPE 4 MEANS A CUSTOM KEY
			if(plugin.zoomtype==4&&k.toString().equalsIgnoreCase(plugin.zoomkey)){
				g.zoom(p);
			}else if(plugin.shottype==4&&k.toString().equalsIgnoreCase(plugin.shotkey)){
				g.shot(p);
			}else if(plugin.rtype==4&&k.toString().equalsIgnoreCase(plugin.rkey)){
				g.reload(p);
			}else if(plugin.zoomtype==5&&k.toString().equalsIgnoreCase(plugin.zoomkey)){
				g.zoom(p);
			}
		}
	}
	
	//IF THE ZOOMKEY IS A TOGGLE (SPECIFIED BY A _ BEHIND THE ZOOMKEY IN THE CONFIG), ZOOM OUT ON RELEASE
	@EventHandler(priority=EventPriority.NORMAL)
	public void onKeyReleasedEvent(KeyReleasedEvent e){
		SpoutPlayer p = e.getPlayer();
		Keyboard k = e.getKey();
		if(plugin.util.holdsGun(p)){
			Gun g = plugin.util.getGunInHand(p);
			if(plugin.zoomtype==5&&k.toString().equalsIgnoreCase(plugin.zoomkey)){
				g.zoom(p);
			}
		}
	}
}
