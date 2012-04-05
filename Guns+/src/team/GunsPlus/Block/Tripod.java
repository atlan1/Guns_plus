package team.GunsPlus.Block;


import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.MaterialData;
import org.getspout.spoutapi.material.block.GenericCustomBlock;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.getspout.spoutapi.sound.SoundEffect;

import team.GunsPlus.GunsPlus;

public class Tripod extends GenericCustomBlock{
	
	public Tripod(GunsPlus plugin, String texture) {
		super(plugin, "Tripod", false);
		this.setHardness(MaterialData.cobblestone.getHardness());
		this.setLightLevel(MaterialData.cobblestone.getLightLevel());
		this.setItemDrop(new SpoutItemStack(this, 1));
		this.setStepSound(SoundEffect.WOOD);
		this.setBlockDesign(new TripodDesign(plugin, texture));
	}

	@Override
	public void onBlockPlace(World w, int x, int y, int z, LivingEntity le) {
		Location l = new Location(w, x,y,z);
		if(le instanceof SpoutPlayer){
			TripodData td = new TripodData((SpoutPlayer)le, l);
			GunsPlus.allTripodBlocks.add(td);
		}
	}
}
