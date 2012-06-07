package team.GunsPlus.Item;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.block.Block;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.material.item.GenericCustomItem;
import org.getspout.spoutapi.player.SpoutPlayer;

import team.ApiPlus.API.PropertyHolder;
import team.GunsPlus.GunsPlus;

import com.griefcraft.util.ProtectionFinder;
import com.griefcraft.util.matchers.DoorMatcher;
import com.griefcraft.util.matchers.DoubleChestMatcher;

public class Addition extends GenericCustomItem implements PropertyHolder{
	
	private Map<String, Object> properties = new HashMap<String, Object>();
	
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
	
	
	@Override
	public Object getProperty(String id) {
		return properties.get(id);
	}

	@Override
	public void addProperty(String id, Object property) {
		if(!properties.containsKey(id))
			properties.put(id, property);
	}

	@Override
	public Map<String, Object> getProperties() {
		return properties;
	}

	@Override
	public void setProperties(Map<String, Object> properties) {
		this.properties = new HashMap<String, Object>(properties);
	}

	@Override
	public void removeProperty(String id) {
		if(properties.containsKey(id))
			properties.remove(id);
	}

	@Override
	public void editProperty(String id, Object property) {
		if(properties.containsKey(id))
			properties.put(id, property);
	}

	public Map<String, Object> getNumberProperties() {
		Map<String, Object> values = new HashMap<String, Object>();
		for(Object o : new HashSet<Object>(properties.values())){
			if(o instanceof Float || o instanceof Integer || o instanceof Double || o instanceof Short){
				for (Entry<String, Object> entry : properties.entrySet()) {
			         if (o.equals(entry.getValue())) {
			             values.put(entry.getKey(), o);
			         }
			     }
			}
		}
		return values;
	}
	
	public Map<String, Object> getOverridableProperties() {
		Map<String, Object> values = new HashMap<String, Object>(properties);
		for(String s : new HashSet<String>(values.keySet())){
			for(String s2 : getNumberProperties().keySet()){
				if(s.equals(s2)){
					values.remove(s);
				}
			}
		}
		return values;
	}
}
