package team.GunsPlus.Item;

import java.util.HashMap;
import java.util.List;

import org.bukkit.block.Block;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.material.item.GenericCustomItem;
import org.getspout.spoutapi.player.SpoutPlayer;

import team.GunsPlus.GunsPlus;

import com.griefcraft.util.ProtectionFinder;
import com.griefcraft.util.matchers.DoorMatcher;
import com.griefcraft.util.matchers.DoubleChestMatcher;

public class Addition extends GenericCustomItem{
	
	private HashMap<String,Float> numbers = new HashMap<String,Float>();
	private HashMap<String, String> strings = new HashMap<String, String>();
	
	public Addition(GunsPlus gp, String n, String tex){
		super(gp, n, tex);
	}
	
	public boolean onItemInteract(SpoutPlayer player, SpoutBlock block,
			org.bukkit.block.BlockFace face) {
		try {
			if (GunsPlus.lwc != null) {
				GunsPlus.lwc.wrapPlayer(player.getPlayer());
				Block b;
				if (block != null)
					b = block;
				else
					return true;
				DoubleChestMatcher chestMatcher = new DoubleChestMatcher();
				DoorMatcher doorMatcher = new DoorMatcher();
				if (GunsPlus.lwc.getProtectionSet(b.getWorld(), b.getX(),
						b.getY(), b.getZ()).size() >= 2) {
					ProtectionFinder pf = new ProtectionFinder(GunsPlus.lwc);
					pf.addBlock(b);
					if (chestMatcher.matches(pf) || doorMatcher.matches(pf)) {
						List<Block> list = pf.getBlocks();
						for (Block bl : list) {
							if (!GunsPlus.lwc.canAccessProtection(player, bl)) {
								return false;
							}
						}
					}
				}
				if (!GunsPlus.lwc.canAccessProtection(player, b)) {
					return false;
				} else
					return true;
			} else
				return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	
	public String getString(String s) {
		return strings.containsKey(s)?strings.get(s):null;
	}

	public void setString(String name, String resource) {
		this.strings.put(name, resource);
	}
	
	public void setValue(String name, Float value) {
		numbers.put(name, value);
	}
	
	public void removeString(String name) {
		if(strings.containsKey(name)){
			this.strings.remove(name);}
	}
	
	public void removeValue(String name) {
		if(numbers.containsKey(name)){
			this.numbers.remove(name);}
	}
	
	public float getValue(String name) {
		return numbers.containsKey(name)?numbers.get(name):null;
	}
	
	public HashMap<String, Float> getNumberValues(){
		return numbers;
	}
	
	public HashMap<String, String> getStringValues(){
		return strings;
	}
	
	
}
