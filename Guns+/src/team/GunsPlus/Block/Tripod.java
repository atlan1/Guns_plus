package team.GunsPlus.Block;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.MaterialData;
import org.getspout.spoutapi.material.block.GenericCustomBlock;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.getspout.spoutapi.sound.SoundEffect;

import team.GunsPlus.GunsPlus;
import team.GunsPlus.Manager.TripodDataHandler;
import team.GunsPlus.Util.PlayerUtils;
import team.GunsPlus.Util.Task;

public class Tripod extends GenericCustomBlock {

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
		Location l = new Location(w, x, y, z);
		if (le instanceof SpoutPlayer) {
			SpoutPlayer sp = (SpoutPlayer) le;
			if(l.getBlock().getRelative(BlockFace.DOWN).getTypeId()==0||l.getBlock().getRelative(BlockFace.DOWN).isLiquid()){
				remove(l);
				return;
			}
			if (TripodDataHandler.getIdsByPlayers(sp.getName()).size() < GunsPlus.maxtripodcount
					|| GunsPlus.maxtripodcount < 0) {
				TripodData td = new TripodData(
						PlayerUtils.getPlayerBySpoutPlayer(sp), l);
				TripodDataHandler.save(td);
				GunsPlus.allTripodBlocks.add(td);
			} else {
				if (GunsPlus.notifications)
					sp.sendNotification(ChatColor.RED
							+ "You reached the maximum", ChatColor.RED
							+ "amount of Tripods!", new SpoutItemStack(this),
							2000);
				remove(l);
			}
		}
	}
	
	private void remove(Location l){
		Task removeBlock = new Task(GunsPlus.plugin, l){
			public void run(){
				Location l = (Location)this.getArg(0);
				l.getWorld().dropItemNaturally(l, new SpoutItemStack(GunsPlus.tripod));
				l.getBlock().breakNaturally();
			}
		};
		removeBlock.startTaskDelayed(1);
	}
}