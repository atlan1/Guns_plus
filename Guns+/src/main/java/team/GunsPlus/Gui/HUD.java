package team.GunsPlus.Gui;

import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.gui.GenericItemWidget;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericTexture;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.item.GenericCustomItem;
import org.getspout.spoutapi.player.SpoutPlayer;

import team.ApiPlus.API.Property.CollectionProperty;
import team.GunsPlus.GunsPlus;
import team.GunsPlus.GunsPlusPlayer;
import team.GunsPlus.Item.Gun;
import team.GunsPlus.Util.GunUtils;
import team.GunsPlus.Util.PlayerUtils;
import team.GunsPlus.Util.Util;

public class HUD {

	public GunsPlus plugin;
	private GenericTexture backtex = new GenericTexture();
	private GenericItemWidget item = new GenericItemWidget();
	private GenericLabel label = new GenericLabel();

	public HUD(GunsPlus gp, int x, int y, String tex) {
		this.plugin = gp;
		backtex = new GenericTexture(tex);
		item.setAnchor(WidgetAnchor.SCALE).setWidth(15).setHeight(15).setPriority(RenderPriority.Normal).setX(GunsPlus.hudX + 13).setY(GunsPlus.hudY + 5);
		label.setAnchor(WidgetAnchor.SCALE).setWidth(30).setHeight(15).setPriority(RenderPriority.High).setX(GunsPlus.hudX + 5).setY(GunsPlus.hudY + 37);
		backtex.setAnchor(WidgetAnchor.SCALE).setWidth(45).setHeight(50).setPriority(RenderPriority.High).setX(GunsPlus.hudX).setY(GunsPlus.hudY);
		backtex.setPriority(RenderPriority.High);
		label.setPriority(RenderPriority.High);
		item.setPriority(RenderPriority.High);
	}

	@SuppressWarnings("unchecked")
	public void update(SpoutPlayer sp) {
		if(GunUtils.holdsGun(sp) && GunUtils.isHudEnabled(GunUtils.getGunInHand(sp))) {
			GunsPlusPlayer gp = PlayerUtils.getPlayerBySpoutPlayer(sp);
			backtex.setVisible(true);
			label.setVisible(true);
			item.setVisible(true);
			Gun g = GunUtils.getGun(sp.getItemInHand());
			ArrayList<ItemStack> ammoTypes = (ArrayList<ItemStack>) ((CollectionProperty<ItemStack>)g.getProperty("AMMO")).getValue();
//			Inventory inv = sp.getInventory();
//			Ammo a = GunUtils.getFirstCustomAmmo(inv, ammoTypes);
//			SpoutItemStack sis = GunUtils.getFirstCustomAmmoStack(inv, ammoTypes); 
			SpoutItemStack i = new SpoutItemStack((GenericCustomItem) g);
			item.setTypeId(i.getTypeId()).setData(i.getDurability());
//			int count = PlayerUtils.getPlayerBySpoutPlayer(sp).getFireCounter(g); old way
//			int count = 0;
//			if(a!=null) count = sis.getEnchantmentLevel(SpoutEnchantment.DURABILITY);
			int total = gp.getTotalAmmo(g);
			if(Util.enteredTripod(sp))
				total = GunUtils.getAmmoCount(Util.getTripodDataOfEntered(sp).getInventory(), ammoTypes);
//			int mag = ((NumberProperty) g.getProperty("SHOTSBETWEENRELOAD")).getValue().intValue();
//			int mag = a!=null?sis.getEnchantmentLevel(SpoutEnchantment.MAX_DURABILITY):1;
//			System.out.println("MAG: "+mag+"; TOTAL: "+total+"; COUNT: "+count);
//			if(count < 0)
//				count = 0;
//			if(total < 0)
//				total = 0;
//			if(mag < 0)
//				mag = 0;
			int current = gp.getCurrentClipSize(g);
			int mag = gp.getMaxClipSize(g);
			if(total <= mag)
				current = total;
			if(total == 0)
				current = 0;
			label.setText(current + "|" + (total-current));
		} else {
			backtex.setVisible(false);
			label.setVisible(false);
			item.setVisible(false);
		}
		sp.getMainScreen().attachWidgets(plugin, backtex, item, label);
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
