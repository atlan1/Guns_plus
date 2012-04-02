package team.GunsPlus.Block;

import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.MaterialData;
import org.getspout.spoutapi.material.block.GenericCustomBlock;
import org.getspout.spoutapi.sound.SoundEffect;

import team.GunsPlus.GunsPlus;

public class Tripod extends GenericCustomBlock{

	
	public Tripod(GunsPlus plugin) {
		super(plugin, "Tripod", false);
		this.setHardness(MaterialData.cobblestone.getHardness());
		this.setLightLevel(MaterialData.cobblestone.getLightLevel());
		this.setItemDrop(new SpoutItemStack(this, 1));
		this.setStepSound(SoundEffect.WOOD);
		this.setBlockDesign(new TripodDesign(plugin));
	}
	
	


}
