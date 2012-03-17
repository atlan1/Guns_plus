package team.GunsPlus;


import org.getspout.spoutapi.gui.GenericItemWidget;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericTexture;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.player.SpoutPlayer;

import team.GunsPlus.Item.Gun;

public class HUD {
	
	
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
	
	public void update(SpoutPlayer sp){
		if(GunUtils.holdsGun(sp)&&GunUtils.isHudEnabled(GunUtils.getGunInHand(sp))){
			backtex.setVisible(true);
			label.setVisible(true);
			item.setVisible(true);
			Gun g = GunUtils.getGun(sp.getItemInHand());
			SpoutItemStack i = new SpoutItemStack(g);
			item.setTypeId(i.getTypeId()).setData(i.getDurability());
			int count = Util.getFireCounter(sp);
			int total = GunUtils.getAmmoCount(sp, g.getAmmo());
			int mag = (int) g.getValue("SHOTSBETWEENRELOAD");
			if(count<0)count = 0;
			if(total<0)total=0;
			if(mag<0)mag=0;
			int current;
			if(total<=mag)
				current = total;
			else
				current= (mag-count);
			if(total==0) current=0;
			label.setText(current+"/"+total);
		}else{
			backtex.setVisible(false);
			label.setVisible(false);
			item.setVisible(false);
		}
		sp.getMainScreen().attachWidgets(plugin, backtex, item, label);
	}
	
	public void stop(SpoutPlayer sp){
		if(GunsPlus.playerHUD.containsKey(sp)){
			GunsPlus.playerHUD.remove(sp);
		}
		sp.getMainScreen().removeWidgets(plugin);
	}
	
	public void start(SpoutPlayer sp){
		if(plugin.hudenabled&&(sp.hasPermission("gunsplus.hud")||sp.isOp())){
			GunsPlus.playerHUD.put(sp, this);
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
