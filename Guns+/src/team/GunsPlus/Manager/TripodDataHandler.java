package team.GunsPlus.Manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.material.item.GenericCustomItem;

import team.ApiPlus.Util.ConfigUtil;
import team.ApiPlus.Util.Task;
import team.GunsPlus.GunsPlus;
import team.GunsPlus.Block.TripodData;
import team.GunsPlus.Enum.Target;
import team.GunsPlus.Enum.TargetType;
import team.GunsPlus.Item.Gun;
import team.GunsPlus.Util.GunUtils;
import team.GunsPlus.Util.Util;

public class TripodDataHandler {

	private static boolean stopLoading = false;
	private static int nextID;

	public static void denyLoading() {
		stopLoading = true;
	}

	public static void allowLoading() {
		stopLoading = false;
	}

	public static TripodData load(int id) {
		if(stopLoading)
			return null;
		try {
			FileConfiguration db = ConfigLoader.dataDB;
			if(id > getNextId() - 1)
				return null;
			ConfigurationSection cs = db.getConfigurationSection(id + "");
			String player = cs.getString("player");
			UUID world = UUID.fromString(cs.getString("loc.world"));
			Location l = cs.getVector("loc.coords").toLocation(Bukkit.getWorld(world));
			Gun g = GunUtils.getGun(cs.getString("gun"));
			boolean auto = cs.getBoolean("auto");
			boolean working = cs.getBoolean("work");
			ConfigurationSection tars = db.getConfigurationSection(id + ".targets");
			Set<Target> targets = new HashSet<Target>();
			if(tars != null && !tars.getKeys(false).isEmpty()) {
				for(int i = 0; i < tars.getKeys(false).size(); i++) {
					String[] type_name = tars.getString("" + i).split("-");
					if(type_name.length == 2)
						targets.add(ConfigParser.parseTarget(type_name[0], type_name[1]));
				}
			}
			TripodData td = new TripodData(player, l, g, new HashSet<Target>(targets));
			ConfigurationSection inventory = db.getConfigurationSection(id + ".inventory");
			if(inventory != null && !inventory.getKeys(false).isEmpty()) {
				for(int j = 0; j < td.getInventory().getSize(); j++) {
					td.getInventory().setItem(j, ConfigUtil.parseItem(inventory.getString("" + j)));
				}
			}
			td.setAutomatic(auto);
			Task t = new Task(GunsPlus.plugin, td, working) {
				public void run() {
					TripodData td = (TripodData) this.getArg(0);
					boolean working = (Boolean) this.getArg(1);
					td.setWorking(working);
				}
			};
			t.startTaskDelayed(40);

			GunsPlus.allTripodBlocks.add(td);
			Block b = td.getLocation().getBlock();
			SpoutBlock sb = (SpoutBlock) b;
			sb.setCustomBlock(GunsPlus.tripod);
			return td;
		} catch(Exception e) {
			Util.debug(e);
		}
		return null;
	}

	public static void loadIds(Set<Integer> ids) {
		for(Integer id : ids) {
			load(id);
		}
	}

	public static Set<String> getAllRegisteredPlayers() {
		FileConfiguration db = ConfigLoader.dataDB;
		Set<String> players = new HashSet<String>();
		for(int i = 0; i < getNextId(); i++) {
			players.add(db.getString(i + ".player"));
		}
		return players;
	}

	public static Set<Integer> getIdsByWorld(World w) {
		Set<Integer> ids = new HashSet<Integer>();
		FileConfiguration db = ConfigLoader.dataDB;
		for(int i = 0; i < getNextId(); i++) {
			if(Bukkit.getWorld(UUID.fromString(db.getString(i + ".loc.world"))).equals(w)) {
				ids.add(i);
			}
		}
		return ids;
	}

	public static Set<Integer> getIdsByPlayers(String player) {
		Set<Integer> ids = new HashSet<Integer>();
		FileConfiguration db = ConfigLoader.dataDB;
		for(int i = 0; i < getNextId(); i++) {
			if(db.getString(i + ".player").equals(player)) {
				ids.add(i);
			}
		}
		return ids;
	}

	public static void loadAll() {
		for(int i = 0; i < getNextId(); i++) {
			load(i);
		}
	}

	public static int getNextId() {
		return nextID;
	}

	public static boolean removeId(int id) {
		FileConfiguration db = ConfigLoader.dataDB;
		if(!(id > getNextId() - 1))
			db.set(id + "", null);
		else
			return false;
		--nextID;
		for(int i = 0; i < getNextId() + 1; i++) {
			if(i > id) {
				ConfigurationSection temp = db.getConfigurationSection(i + "");
				db.set(i + "", null);
				db.set(i - 1 + "", temp);
			}
		}
		return true;
	}

	public static void addId() {
		FileConfiguration db = ConfigLoader.dataDB;
		db.createSection("" + getNextId());
		++nextID;
	}

	public static int getId(Location l) {
		FileConfiguration db = ConfigLoader.dataDB;
		int id = -1;
		for(int i = 0; i < getNextId(); i++) {
			if(Bukkit.getWorld(UUID.fromString(db.getString(i + ".loc.world"))).equals(l.getWorld()) && db.getVector(i + ".loc.coords").equals(l.toVector())) {
				id = i;
				return id;
			}
		}
		return id;
	}

	public static void saveAll() {
		for(TripodData td : GunsPlus.allTripodBlocks) {
			TripodDataHandler.save(td);
		}
	}

	public static boolean save(TripodData td) {
		try {
			FileConfiguration db = ConfigLoader.dataDB;
			if(td.getOwner() == null)
				return false;
			ConfigurationSection cs = null;
			if(getId(td.getLocation()) != -1) {
				cs = db.getConfigurationSection("" + getId(td.getLocation()));
			} else {
				addId();
				cs = db.getConfigurationSection("" + (getNextId() - 1));
			}
			cs.set("player", td.getOwnername());
			cs.set("loc.coords", td.getLocation().toVector());
			cs.set("loc.world", td.getLocation().getWorld().getUID().toString());
			if(td.getGun() != null)
				cs.set("gun", ((GenericCustomItem) td.getGun()).getName());
			cs.set("auto", td.isAutomatic());
			cs.set("work", td.isWorking());
			cs.createSection("targets");
			List<Target> tar = new ArrayList<Target>(td.getTargets());
			int j = 0;
			for(Target t : tar) {
				cs.set("targets." + j, TargetType.getTargetType(t.getClass()).name() + "-" + (Util.isPlayerTarget(t) ? ((team.GunsPlus.Enum.PlayerTarget) t).getName() : t.toString()));
				j++;
			}
			cs.createSection("inventory");
			for(int k = 0; k < td.getInventory().getSize(); k++) {
				ItemStack item = td.getInventory().getItem(k);
				cs.set("inventory." + k, item != null ? item.getTypeId() + ":" + item.getDurability() + ":" + item.getAmount() : null);
			}
		} catch(Exception e) {
			Util.debug(e);
			return false;
		}
		return true;
	}

	public static void setNextId(int size) {
		nextID = size;
	}
}
