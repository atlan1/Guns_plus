package team.GunsPlus;


import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.getspout.spoutapi.event.input.KeyPressedEvent;
import org.getspout.spoutapi.event.input.KeyReleasedEvent;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.keyboard.Keyboard;
import org.getspout.spoutapi.player.SpoutPlayer;

import team.GunsPlus.Classes.Gun;

public class GunsPlusListener implements Listener{

	public GunsPlus plugin;
	
	public GunsPlusListener(GunsPlus instance){
		plugin = instance;
	}
	
	@EventHandler(priority=EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent e){
		Player p = e.getPlayer();
		if(!Util.hasSpoutcraft(p)) return;
		SpoutPlayer sp = (SpoutPlayer) p;
		if(!GunUtils.holdsGun(sp)) return;
		
		Action a = e.getAction();
		Gun g = GunUtils.getGunInHand(sp);
		switch(a){
			case RIGHT_CLICK_AIR:
				if(plugin.zoomKey.equals(KeyType.RIGHT)) g.zoom(sp);
				if(plugin.reloadKey.equals(KeyType.RIGHT)) g.reload(sp);
				if(plugin.fireKey.equals(KeyType.RIGHT)) g.fire(sp);
				if(sp.isSneaking()&&plugin.zoomKey.equals(KeyType.RIGHTSHIFT)) g.zoom(sp);
				if(sp.isSneaking()&&plugin.reloadKey.equals(KeyType.RIGHTSHIFT)) g.reload(sp);
				if(sp.isSneaking()&&plugin.fireKey.equals(KeyType.RIGHTSHIFT)) g.fire(sp);
				break;
			case RIGHT_CLICK_BLOCK:
				if(plugin.zoomKey.equals(KeyType.RIGHT)) g.zoom(sp);
				if(plugin.reloadKey.equals(KeyType.RIGHT)) g.reload(sp);
				if(!Util.isLWC(e.getClickedBlock())) if(plugin.fireKey.equals(KeyType.RIGHT)) g.fire(sp);
				if(sp.isSneaking()&&plugin.zoomKey.equals(KeyType.RIGHTSHIFT)) g.zoom(sp);
				if(sp.isSneaking()&&plugin.reloadKey.equals(KeyType.RIGHTSHIFT)) g.reload(sp);
				if(!Util.isLWC(e.getClickedBlock())) if(sp.isSneaking()&&plugin.fireKey.equals(KeyType.RIGHTSHIFT)) g.fire(sp);
				break;
			case LEFT_CLICK_AIR:
				if(plugin.zoomKey.equals(KeyType.LEFT)) g.zoom(sp);
				if(plugin.reloadKey.equals(KeyType.LEFT)) g.reload(sp);
				if(plugin.fireKey.equals(KeyType.LEFT)) g.fire(sp);
				if(sp.isSneaking()&&plugin.zoomKey.equals(KeyType.LEFTSHIFT)) g.zoom(sp);
				if(sp.isSneaking()&&plugin.reloadKey.equals(KeyType.LEFTSHIFT)) g.reload(sp);
				if(sp.isSneaking()&&plugin.fireKey.equals(KeyType.LEFTSHIFT)) g.fire(sp);
				break;
			case LEFT_CLICK_BLOCK:
				if(plugin.zoomKey.equals(KeyType.LEFT)) g.zoom(sp);
				if(plugin.reloadKey.equals(KeyType.LEFT)) g.reload(sp);
				if(!Util.isLWC(e.getClickedBlock())) if(plugin.fireKey.equals(KeyType.LEFT)) g.fire(sp);
				if(sp.isSneaking()&&plugin.zoomKey.equals(KeyType.LEFTSHIFT)) g.zoom(sp);
				if(sp.isSneaking()&&plugin.reloadKey.equals(KeyType.LEFTSHIFT)) g.reload(sp);
				if(!Util.isLWC(e.getClickedBlock())) if(sp.isSneaking()&&plugin.fireKey.equals(KeyType.LEFTSHIFT)) g.fire(sp);
				break;
		}
	}
	
	
	@EventHandler(priority=EventPriority.NORMAL)
	public void onKeyPressed(KeyPressedEvent e){
		SpoutPlayer sp = e.getPlayer();
		if(!GunUtils.holdsGun(sp)) return;
		
		Gun g = GunUtils.getGunInHand(sp);
		Keyboard key = e.getKey();
		String keyString = key.toString().split("_")[1].toLowerCase();
		ScreenType st = e.getScreenType();
		
		if((plugin.zoomKey.equals(KeyType.LETTER)||plugin.zoomKey.equals(KeyType.NUMBER)||plugin.zoomKey.equals(KeyType.HOLDNUMBER)||plugin.zoomKey.equals(KeyType.HOLDLETTER))&&plugin.zoomKey.getData().equalsIgnoreCase(keyString)&&st.toString().equalsIgnoreCase("GAME_SCREEN")){
			g.zoom(sp);
		}else if((plugin.reloadKey.equals(KeyType.LETTER)||plugin.reloadKey.equals(KeyType.NUMBER)||plugin.reloadKey.equals(KeyType.HOLDNUMBER)||plugin.reloadKey.equals(KeyType.HOLDLETTER))&&plugin.reloadKey.getData().equalsIgnoreCase(keyString)&&st.toString().equalsIgnoreCase("GAME_SCREEN")){
			g.reload(sp);
		}else if((plugin.fireKey.equals(KeyType.LETTER)||plugin.fireKey.equals(KeyType.NUMBER)||plugin.fireKey.equals(KeyType.HOLDNUMBER)||plugin.fireKey.equals(KeyType.HOLDLETTER))&&plugin.fireKey.getData().equalsIgnoreCase(keyString)&&st.toString().equalsIgnoreCase("GAME_SCREEN")){
			g.fire(sp);
		}
	}
	
	@EventHandler(priority=EventPriority.NORMAL)
	public void onKeyReleased(KeyReleasedEvent e){
		SpoutPlayer sp = e.getPlayer();
		if(!GunUtils.holdsGun(sp)) return;
		
		Gun g = GunUtils.getGunInHand(sp);
		Keyboard key = e.getKey();
		String keyString = key.toString().split("_")[1].toLowerCase();
		ScreenType st = e.getScreenType();
		
		if((plugin.zoomKey.equals(KeyType.HOLDNUMBER)||plugin.zoomKey.equals(KeyType.HOLDLETTER))&&plugin.zoomKey.getData().equalsIgnoreCase(keyString)&&st.toString().equalsIgnoreCase("GAME_SCREEN")){
			g.zoom(sp);
		}else if((plugin.reloadKey.equals(KeyType.HOLDNUMBER)||plugin.reloadKey.equals(KeyType.HOLDLETTER))&&plugin.reloadKey.getData().equalsIgnoreCase(keyString)&&st.toString().equalsIgnoreCase("GAME_SCREEN")){
			//stop reloading ?
		}else if((plugin.fireKey.equals(KeyType.HOLDNUMBER)||plugin.fireKey.equals(KeyType.HOLDLETTER))&&plugin.fireKey.getData().equalsIgnoreCase(keyString)&&st.toString().equalsIgnoreCase("GAME_SCREEN")){
			//stop fireing ?
		}
	}
	
	@EventHandler(priority=EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent e){
		if(!Util.hasSpoutcraft(e.getPlayer())) {
			e.getPlayer().sendMessage(ChatColor.GRAY + "This server is running Guns+");
			e.getPlayer().sendMessage(ChatColor.GRAY + "By " + plugin.getDescription().getAuthors());
			return;
		}
		SpoutPlayer sp = (SpoutPlayer) e.getPlayer();
		HUD hud = new HUD(plugin, plugin.hudX, plugin.hudY, plugin.hudBackground);
		hud.start(sp);
		GunsPlus.fireCounter.put(sp, 0);
		sp.sendNotification(ChatColor.GRAY + "Guns+", ChatColor.DARK_GREEN + "By " + plugin.getDescription().getAuthors(), Material.SULPHUR);
	}
}
