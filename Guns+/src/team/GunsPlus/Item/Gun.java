package team.GunsPlus.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.material.item.GenericCustomTool;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.griefcraft.util.ProtectionFinder;
import com.griefcraft.util.matchers.DoorMatcher;
import com.griefcraft.util.matchers.DoubleChestMatcher;

import team.GunsPlus.GunsPlus;
import team.GunsPlus.Enum.Effect;

public class Gun extends GenericCustomTool {

	private ArrayList<ItemStack> ammo = new ArrayList<ItemStack>();
	private HashMap<String, Float> values = new HashMap<String, Float>();
	private HashMap<String, String> resources = new HashMap<String, String>();
	private HashMap<String, Object> objects = new HashMap<String, Object>();
	private ArrayList<Effect> effects = new ArrayList<Effect>();

	public Gun(Plugin plugin, String name, String texture) {
		super(plugin, name, texture);
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

	public ArrayList<ItemStack> getAmmo() {
		return ammo;
	}

	public void setAmmo(ArrayList<ItemStack> ammo) {
		ArrayList<ItemStack> list = new ArrayList<ItemStack>(ammo);
		this.ammo = list;
	}

	public String getResource(String s) {
		return resources.containsKey(s) ? resources.get(s) : null;
	}

	public void setResource(String name, String resource) {
		this.resources.put(name, resource);
	}

	public Object getObject(String s) {
		return objects.containsKey(s) ? objects.get(s) : null;
	}

	public void setObject(String name, Object o) {
		this.objects.put(name, o);
	}

	public void setValue(String name, Float value) {
		values.put(name, value);
	}

	public float getValue(String name) {
		return values.containsKey(name) ? values.get(name) : 0;
	}

	public void addEffect(Effect es) {
		effects.add(es);
	}

	public void removeEffect(Effect es) {
		if (effects.contains(es))
			effects.remove(es);
	}

	public ArrayList<Effect> getEffects() {
		return effects;
	}

	public void setEffects(ArrayList<Effect> effects) {
		ArrayList<Effect> list = new ArrayList<Effect>(effects);
		this.effects = list;
	}

	public HashMap<String, String> getResources() {
		return resources;
	}

	public HashMap<String, Object> getObjects() {
		return objects;
	}

	public void setResources(HashMap<String, String> resources) {
		HashMap<String, String> list = new HashMap<String, String>(resources);
		this.resources = list;
	}

	public void setObjects(HashMap<String, Object> objects) {
		HashMap<String, Object> list = new HashMap<String, Object>(objects);
		this.objects = list;
	}

	public HashMap<String, Float> getValues() {
		return values;
	}

	public void setValues(HashMap<String, Float> values) {
		HashMap<String, Float> list = new HashMap<String, Float>(values);
		this.values = list;
	}
}
