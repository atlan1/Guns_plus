package team.GunsPlus.Gui;

import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.GenericRadioButton;
import org.getspout.spoutapi.gui.Screen;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.player.SpoutPlayer;

import team.GunsPlus.GunsPlus;
import team.GunsPlus.Block.TripodData;

public class TripodPopup extends GenericPopup{

	private TripodData data;
	private GenericLabel title = new GenericLabel("Tripod Menu");
	private GenericRadioButton auto = new GenericRadioButton("automatic");
	private GenericRadioButton manu = new GenericRadioButton("manual");
	
	private GenericButton apply = new GenericButton("apply");
	private GenericLabel targets = new GenericLabel("Targets:");
	private GenericButton add = new GenericButton("+");
	
	public TripodPopup(GunsPlus plugin, TripodData td){
		setData(td);
		
		title.setAnchor(WidgetAnchor.SCALE);
		title.setX(100).setY(20);
		title.setWidth(50).setHeight(15);
		
		targets.setAlign(WidgetAnchor.SCALE);
		targets.setX(120).setY(35);
		
		auto.setAnchor(WidgetAnchor.SCALE);
		auto.setX(10).setY(30);
		auto.setWidth(30).setHeight(15);
		
		manu.setAnchor(WidgetAnchor.SCALE);
		manu.setX(10).setY(50);
		manu.setWidth(30).setHeight(15);
		
		auto.setGroup(1);
		manu.setGroup(1);
		
		apply.setAnchor(WidgetAnchor.SCALE);
		apply.setX(350).setY(100);
		apply.setWidth(30).setHeight(15);
		
		add.setAnchor(WidgetAnchor.SCALE);
		add.setX(300).setY(46);
		add.setWidth(10).setHeight(10);

		this.attachWidgets(plugin, title, auto, manu, apply, add, targets);
		
		manu.setSelected(true);
	}
	
	public void resize(Screen s){
		int w = s.getWidth();
		int h = s.getHeight();
		title.setX(w/2);
		targets.setX((w/3)*2);
		apply.setX(w-10);
		apply.setY(h-10);
		add.setX((w/3)*2);
	}

	public void attach(SpoutPlayer sp){
		resize(sp.getMainScreen());
		sp.getMainScreen().attachPopupScreen(this);
	}
	
	public void close(SpoutPlayer sp){
		if(sp.getMainScreen().getActivePopup().equals(this)){
			sp.getMainScreen().getActivePopup().close();
		}
	}

	public TripodData getData() {
		return data;
	}

	public void setData(TripodData data) {
		this.data = data;
	}
}
