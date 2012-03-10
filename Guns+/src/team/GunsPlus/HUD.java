package team.GunsPlus;

import java.util.HashMap;

import org.getspout.spoutapi.gui.GenericItemWidget;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericTexture;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.player.SpoutPlayer;

import team.GunsPlus.Classes.Gun;

public class HUD {
	
	public static HashMap<SpoutPlayer, HUD> playerHUD = new HashMap<SpoutPlayer, HUD>();
	
	public GunsPlus plugin;
	private GenericTexture backtex = new GenericTexture();
	private GenericItemWidget item = new GenericItemWidget();
	private GenericLabel label = new GenericLabel();
	
	
	public HUD(GunsPlus gp, int x, int y, String tex){
		this.plugin = gp;
		backtex = new GenericTexture(tex);
		item.setAnchor(WidgetAnchor.SCALE).setWidth(15).setHeight(15).setPriority(RenderPriority.Normal).setX(plugin.hudX+13).setY(plugin.hudY+5);
		label.setAnchor(WidgetAnchor.SCALE).setWidth(30).setHeight(15).setPriority(RenderPriority.High).setX(plugin.hudX+5).setY(plugin.hudY+37);
		backtex.setAnchor(WidgetAnchor.SCALE).setWidth(45).setHeight(50).setPriority(RenderPriority.High).setX(plugin.hudX).setY(plugin.hudY);
		backtex.setPriority(RenderPriority.High);
		label.setPriority(RenderPriority.High);
		item.setPriority(RenderPriority.High);
	}
	
	//TODO: getCounter() in Gun.java
	public void update(SpoutPlayer sp){
		if(GunUtils.holdsGun(sp)){
			backtex.setVisible(true);
			label.setVisible(true);
			item.setVisible(true);
			Gun g = GunUtils.getGun(sp.getItemInHand());
			SpoutItemStack i = new SpoutItemStack(g);
			item.setTypeId(i.getTypeId()).setData(i.getDurability());
			int count /*= g.getCounter(sp)*/ = 0;
			if(count<0)count = 0;
			int total = GunUtils.getAmmoCount(sp, g.getAmmo());
			if(total<0)total=0;
			if(total<count){
				count=total;
				total=0;
			}
			int notLoaded = total-count;
			if(notLoaded<0)notLoaded=0;
			label.setText(count+"/"+notLoaded);
		}else{
			backtex.setVisible(false);
			label.setVisible(false);
			item.setVisible(false);
		}
		sp.getMainScreen().attachWidgets(plugin, backtex, item, label);
	}
	
	public void stop(SpoutPlayer sp){
		if(HUD.playerHUD.containsKey(sp)){
			HUD.playerHUD.remove(sp);
		}
		sp.getMainScreen().removeWidgets(plugin);
	}
	
	public void start(SpoutPlayer sp){
		if(plugin.hudenabled&&Util.hasSpoutcraft(sp)&&sp.hasPermission("gunsplus.hud")){
			HUD.playerHUD.put(sp, this);
		}
	}

	public GenericTexture getBacktex() {
		return backtex;
	}

	public void setBacktex(GenericTexture backtex) {
		this.backtex = backtex;
	}

	public GenericItemWidget getItem() {
		return item;
	}

	public void setItem(GenericItemWidget item) {
		this.item = item;
	}

	public GenericLabel getLabel() {
		return label;
	}

	public void setLabel(GenericLabel label) {
		this.label = label;
	}

}
