package team.GunsPlus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.inventory.SpoutItemStack;

import team.GunsPlus.Classes.Ammo;
import team.GunsPlus.Classes.Gun;

public class GunsPlus extends JavaPlugin {
	private String PRE = "[Guns+]";
	public final Logger log = Bukkit.getLogger();
	public boolean warnings = true;
	public boolean debug = false;

	public KeyType zoomKey = KeyType.RIGHT;
	public KeyType fireKey = KeyType.LEFT;
	public KeyType reloadKey = KeyType.LETTER("R");
	public boolean hudenabled = false;
	public int hudX = 0;
	public int hudY = 0;
	public String hudBackground = null;

	public File gunsFile;
	public FileConfiguration gunsConfig;
	public File ammoFile;
	public FileConfiguration ammoConfig;
	public File recipeFile;
	public FileConfiguration recipeConfig;
	public File generalFile;
	public FileConfiguration generalConfig;

	public List<Gun> allGuns = new ArrayList<Gun>();
	public List<Ammo> allAmmo = new ArrayList<Ammo>();

	@Override
	public void onDisable() {
		log.log(Level.INFO, PRE + " version " + getDescription().getVersion()
				+ " is now disabled.");
	}

	@Override
	public void onEnable() {
		init();

		if (debug)
			log.setLevel(Level.ALL);
		log.log(Level.INFO, PRE + " version " + getDescription().getVersion()
				+ " is now enabled.");
	}

	public void init() {
		config();
		performGeneral();
	}

	public void config() {
		gunsFile = new File(getDataFolder(), "guns.yml");
		ammoFile = new File(getDataFolder(), "ammo.yml");
		recipeFile = new File(getDataFolder(), "recipes.yml");
		generalFile = new File(getDataFolder(), "general.yml");
		try {
			firstRun();
		} catch (Exception e) {
			e.printStackTrace();
		}
		gunsConfig = new YamlConfiguration();
		ammoConfig = new YamlConfiguration();
		recipeConfig = new YamlConfiguration();
		generalConfig = new YamlConfiguration();
		loadYamls();
	}

	private void firstRun() {
		if (!gunsFile.exists()) {
			gunsFile.getParentFile().mkdirs();
			copy(getResource("guns.yml"), gunsFile);
		}
		if (!ammoFile.exists()) {
			ammoFile.getParentFile().mkdirs();
			copy(getResource("ammo.yml"), ammoFile);
		}
		if (!recipeFile.exists()) {
			recipeFile.getParentFile().mkdirs();
			copy(getResource("recipes.yml"), recipeFile);
		}
		if (!generalFile.exists()) {
			generalFile.getParentFile().mkdirs();
			copy(getResource("general.yml"), generalFile);
		}
	}

	private void copy(InputStream in, File file) {
		try {
			OutputStream out = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadYamls() {
		try {
			gunsConfig.load(gunsFile);
			ammoConfig.load(ammoFile);
			recipeConfig.load(recipeFile);
			generalConfig.load(generalFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void performGeneral() {
		try {
			warnings = generalConfig.getBoolean("show-warnings", true);
			debug = generalConfig.getBoolean("show-debug", false);

			hudenabled = generalConfig.getBoolean("hud.enabled", true);
			hudBackground = generalConfig.getString("hud.background",
					"http://dl.dropbox.com/u/44243469/GunPack/Textures/HUDBackground.png");
			hudX = generalConfig.getInt("hud.position.X", 20);
			hudY = generalConfig.getInt("hud.position.Y", 20);

			if (generalConfig.getBoolean("id-info-guns", true)) {
				log.log(Level.INFO, PRE
						+ " ------------  ID's of the guns: -----------------");
				if(allGuns.isEmpty()) log.log(Level.INFO, "EMPTY");
				for (Gun gun : allGuns) {
					log.log(Level.INFO, "ID of " + gun.getName() + ":"
							+ Material.FLINT.getId() + ":"
							+ new SpoutItemStack(gun).getDurability());
				}
			}
			if (generalConfig.getBoolean("id-info-ammo", true)) {
				log.log(Level.INFO, PRE
						+ " ------------  ID's of the ammo: -----------------");
				if(allAmmo.isEmpty()) log.log(Level.INFO, "EMPTY");
				for (Ammo ammo : allAmmo) {
					log.log(Level.INFO, "ID of " + ammo.getName() + ":"
							+ Material.FLINT.getId() + ":"
							+ new SpoutItemStack(ammo).getDurability());
				}
			}
			String z = generalConfig.getString("zoom", "right");
			if (z.startsWith("@")) {
				if (z.endsWith("_"))
					zoomKey = KeyType.HOLDLETTER(z.replace("@", ""));
				else
					zoomKey = KeyType.LETTER(z.replace("@", ""));
			} else if (z.startsWith("#")) {
				if (z.endsWith("_"))
					zoomKey = KeyType.HOLDNUMBER(z.replace("#", ""));
				else
					zoomKey = KeyType.NUMBER(z.replace("#", ""));
			} else {
				if (z.endsWith("_")) {
					switch (KeyType.getType(z)) {
					case RIGHT:
						zoomKey = KeyType.HOLDRIGHT;
						break;
					case LEFT:
						zoomKey = KeyType.HOLDLEFT;
						break;
					case RIGHTSHIFT:
						zoomKey = KeyType.HOLDRIGHTSHIFT;
						break;
					case LEFTSHIFT:
						zoomKey = KeyType.HOLDLEFTSHIFT;
						break;
					}
				} else {
					switch (KeyType.getType(z)) {
					case RIGHT:
						zoomKey = KeyType.RIGHT;
						break;
					case LEFT:
						zoomKey = KeyType.LEFT;
						break;
					case RIGHTSHIFT:
						zoomKey = KeyType.RIGHTSHIFT;
						break;
					case LEFTSHIFT:
						zoomKey = KeyType.LEFTSHIFT;
						break;
					}
				}
			}
			String f = generalConfig.getString("fire", "left");
			if (f.startsWith("@")) {
				if (f.endsWith("_"))
					zoomKey = KeyType.HOLDLETTER(f.replace("@", ""));
				else
					zoomKey = KeyType.LETTER(f.replace("@", ""));
			} else if (f.startsWith("#")) {
				if (f.endsWith("_"))
					zoomKey = KeyType.HOLDNUMBER(f.replace("#", ""));
				else
					zoomKey = KeyType.NUMBER(f.replace("#", ""));
			} else {
				if (f.endsWith("_")) {
					switch (KeyType.getType(f)) {
					case RIGHT:
						zoomKey = KeyType.HOLDRIGHT;
						break;
					case LEFT:
						zoomKey = KeyType.HOLDLEFT;
						break;
					case RIGHTSHIFT:
						zoomKey = KeyType.HOLDRIGHTSHIFT;
						break;
					case LEFTSHIFT:
						zoomKey = KeyType.HOLDLEFTSHIFT;
						break;
					}
				} else {
					switch (KeyType.getType(f)) {
					case RIGHT:
						zoomKey = KeyType.RIGHT;
						break;
					case LEFT:
						zoomKey = KeyType.LEFT;
						break;
					case RIGHTSHIFT:
						zoomKey = KeyType.RIGHTSHIFT;
						break;
					case LEFTSHIFT:
						zoomKey = KeyType.LEFTSHIFT;
						break;
					}
				}
			}
			String r = generalConfig.getString("reload", "@r");
			if (z.startsWith("@")) {
				if (r.endsWith("_"))
					zoomKey = KeyType.HOLDLETTER(r.replace("@", ""));
				else
					zoomKey = KeyType.LETTER(r.replace("@", ""));
			} else if (z.startsWith("#")) {
				if (r.endsWith("_"))
					zoomKey = KeyType.HOLDNUMBER(r.replace("#", ""));
				else
					zoomKey = KeyType.NUMBER(r.replace("#", ""));
			} else {
				if (r.endsWith("_")) {
					switch (KeyType.getType(r)) {
					case RIGHT:
						zoomKey = KeyType.HOLDRIGHT;
						break;
					case LEFT:
						zoomKey = KeyType.HOLDLEFT;
						break;
					case RIGHTSHIFT:
						zoomKey = KeyType.HOLDRIGHTSHIFT;
						break;
					case LEFTSHIFT:
						zoomKey = KeyType.HOLDLEFTSHIFT;
						break;
					}
				} else {
					switch (KeyType.getType(r)) {
					case RIGHT:
						zoomKey = KeyType.RIGHT;
						break;
					case LEFT:
						zoomKey = KeyType.LEFT;
						break;
					case RIGHTSHIFT:
						zoomKey = KeyType.RIGHTSHIFT;
						break;
					case LEFTSHIFT:
						zoomKey = KeyType.LEFTSHIFT;
						break;
					}
				}
			}
			if (zoomKey.getData().equalsIgnoreCase(fireKey.getData()) || fireKey.getData().equalsIgnoreCase(reloadKey.getData())
					|| reloadKey.getData().equalsIgnoreCase(zoomKey.getData())) {
				String message = (zoomKey.getData() + " " + fireKey.getData() + " " + reloadKey.getData());
				zoomKey = KeyType.RIGHT;
				fireKey = KeyType.LEFT;
				reloadKey = KeyType.LETTER("r");
				throw new Exception("Key's Duplicated: " + message);
			}

		} catch (Exception e) {
			if (warnings)
				log.log(Level.WARNING, PRE + "Config Error:" + e.getMessage());
			if (debug)
				e.printStackTrace();
		}

		// TODO: Transparent Materials
	}
}
