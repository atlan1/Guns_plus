package team.GunsPlus.Manager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.CustomBlock;
import org.getspout.spoutapi.material.CustomItem;
import org.getspout.spoutapi.material.item.GenericCustomItem;

import team.ApiPlus.API.Effect.Effect;
import team.ApiPlus.API.Property.CollectionProperty;
import team.ApiPlus.API.Property.NumberProperty;
import team.ApiPlus.API.Property.ObjectProperty;
import team.ApiPlus.API.Property.StringProperty;
import team.ApiPlus.Manager.RecipeManager;
import team.ApiPlus.Util.ConfigUtil;
import team.ApiPlus.Util.FileUtil;
import team.ApiPlus.Util.Task;
import team.GunsPlus.GunsPlus;
import team.GunsPlus.Block.Tripod;
import team.GunsPlus.Enum.FireBehavior;
import team.GunsPlus.Enum.KeyType;
import team.GunsPlus.Enum.Projectile;
import team.GunsPlus.Item.Addition;
import team.GunsPlus.Item.Ammo;
import team.GunsPlus.Item.Gun;
import team.GunsPlus.Util.GunUtils;
import team.GunsPlus.Util.Util;

public class ConfigLoader {

	public static File generalFile;
	public static File gunsFile;
	public static File additionsFile;
	public static File ammoFile;
	public static File recipeFile;
	public static File dataFile;
	public static FileConfiguration recipeConfig;
	public static FileConfiguration ammoConfig;
	public static FileConfiguration additionsConfig;
	public static FileConfiguration gunsConfig;
	public static FileConfiguration generalConfig;
	public static FileConfiguration dataDB;

	public static void config() {
		gunsFile = new File(GunsPlus.plugin.getDataFolder(), "guns.yml");
		ammoFile = new File(GunsPlus.plugin.getDataFolder(), "ammo.yml");
		recipeFile = new File(GunsPlus.plugin.getDataFolder(), "recipes.yml");
		generalFile = new File(GunsPlus.plugin.getDataFolder(), "general.yml");
		additionsFile = new File(GunsPlus.plugin.getDataFolder(), "additions.yml");
		dataFile = new File(GunsPlus.plugin.getDataFolder(), "data.dat");
		try {
			firstRun();
		} catch(Exception e) {

		}
		gunsConfig = new YamlConfiguration();
		ammoConfig = new YamlConfiguration();
		recipeConfig = new YamlConfiguration();
		generalConfig = new YamlConfiguration();
		additionsConfig = new YamlConfiguration();
		dataDB = new YamlConfiguration();
		try {
			gunsConfig.load(gunsFile);
			ammoConfig.load(ammoFile);
			recipeConfig.load(recipeFile);
			generalConfig.load(generalFile);
			additionsConfig.load(additionsFile);
			dataDB.load(dataFile);
		} catch(Exception e) {
		}
	}

	private static void firstRun() throws IOException {
		if(FileUtil.create(gunsFile))
			FileUtil.copy(GunsPlus.plugin.getResource("guns.yml"), ConfigLoader.gunsFile);
		if(FileUtil.create(ammoFile))
			FileUtil.copy(GunsPlus.plugin.getResource("ammo.yml"), ConfigLoader.ammoFile);
		if(FileUtil.create(recipeFile))
			FileUtil.copy(GunsPlus.plugin.getResource("recipes.yml"), ConfigLoader.recipeFile);
		if(FileUtil.create(generalFile))
			FileUtil.copy(GunsPlus.plugin.getResource("general.yml"), ConfigLoader.generalFile);
		if(FileUtil.create(additionsFile))
			FileUtil.copy(GunsPlus.plugin.getResource("additions.yml"), ConfigLoader.additionsFile);
		if(FileUtil.create(dataFile))
			FileUtil.copy(GunsPlus.plugin.getResource("data.dat"), dataFile);
	}

	public static void loadAdditions() {
		for(Object additionnode : additionsConfig.getKeys(false)) {
			try {
				String name = additionnode.toString();
				if(!name.equalsIgnoreCase("name")) {
					String addtexture = additionsConfig.getString(additionnode + ".add-texture");
					String randomfactor = additionsConfig.getString(additionnode + ".accuracy.random-factor");
					String spreadangleIN = "0";
					String spreadangleOUT = "0";
					String missingchanceIN = "0";
					String missingchanceOUT = "0";
					String critical = additionsConfig.getString(additionnode + ".critical");
					String range = additionsConfig.getString(additionnode + ".range");
					String damage = additionsConfig.getString(additionnode + ".damage");
					String dur = additionsConfig.getString(additionnode + ".durability");
					String melee = additionsConfig.getString(additionnode + ".melee-damage");
					String reloadTime = additionsConfig.getString(additionnode + ".reload-time");
					String shotDelay = additionsConfig.getString(additionnode + ".shot-delay");
//					String shotsBetweenReload = additionsConfig.getString(additionnode + ".shots-between-reload");
					String recoil = additionsConfig.getString(additionnode + ".recoil");
					String weight = additionsConfig.getString(additionnode + ".weight");
					String knockback = additionsConfig.getString(additionnode + ".knockback");
					String changedamage = additionsConfig.getString(additionnode + ".damage-change");
					String zoomfactor = additionsConfig.getString(additionnode + ".zoom-factor");
					String headShotDamage = additionsConfig.getString(additionnode + ".head-shot-damage");
					String shotsoundvolume = additionsConfig.getString(additionnode + ".shot-sound.volume");
					String reloadsoundvolume = additionsConfig.getString(additionnode + ".reload-sound.volume");
					String shotSound = additionsConfig.getString(additionnode + ".shot-sound.url");
					String reloadSound = additionsConfig.getString(additionnode + ".reload-sound.url");
					String zoomTexture = additionsConfig.getString(additionnode + ".zoom-texture");
					String spread_angle = additionsConfig.getString(additionnode + ".accuracy.spread-angle");
					String missing_chance = additionsConfig.getString(additionnode + ".accuracy.missing-chance");
					String bullet_count = additionsConfig.getString(additionnode + ".accuracy.bullet-count");

					if(spread_angle != null) {
						String[] split = spread_angle.split("->");
						spreadangleIN = split[1];
						spreadangleOUT = split[0];
					}
					if(missing_chance != null) {
						String[] split = missing_chance.split("->");
						missingchanceIN = split[1];
						missingchanceOUT = split[0];
					}

					List<Effect> effects = new ArrayList<Effect>(ConfigParser.parseEffects(ConfigLoader.additionsConfig, name + ".effects"));

					if(addtexture == null)
						throw new Exception(" Texture for addition " + name + " is missing or could not be found! Skipping!");

					Addition a = ItemBuilder.buildAddition(GunsPlus.plugin, name, addtexture);
					if(weight != null)
						a.addProperty("WEIGHT", new NumberProperty(weight));
					if(changedamage != null)
						a.addProperty("CHANGEDAMAGE", new NumberProperty(changedamage));
					if(damage != null)
						a.addProperty("DAMAGE", new NumberProperty(damage));
					if(melee != null)
						a.addProperty("MELEE", new NumberProperty(melee));
					if(headShotDamage != null)
						a.addProperty("HEADSHOTDAMAGE", new NumberProperty(headShotDamage));
					if(zoomfactor != null)
						a.addProperty("ZOOMFACTOR", new NumberProperty(zoomfactor));
					if(critical != null)
						a.addProperty("CRITICAL", new NumberProperty(critical));
					if(range != null)
						a.addProperty("RANGE", new NumberProperty(range));
					if(randomfactor != null)
						a.addProperty("RANDOMFACTOR", new NumberProperty(randomfactor));
					if(dur != null)
						a.addProperty("DURABILITY", new NumberProperty(dur));
					if(bullet_count != null)
						a.addProperty("BULLETCOUNT", new NumberProperty(bullet_count));
					if(spreadangleOUT != null)
						a.addProperty("SPREAD_OUT", new NumberProperty(spreadangleOUT));
					if(spreadangleIN != null)
						a.addProperty("SPREAD_IN", new NumberProperty(spreadangleIN));
					if(missingchanceOUT != null)
						a.addProperty("MISSING_OUT", new NumberProperty(missingchanceOUT));
					if(missingchanceIN != null)
						a.addProperty("MISSING_IN", new NumberProperty(missingchanceIN));
					if(recoil != null)
						a.addProperty("RECOIL", new NumberProperty(recoil));
					if(reloadTime != null)
						a.addProperty("RELOADTIME", new NumberProperty(reloadTime));
//					if(shotsBetweenReload != null)
//						a.addProperty("SHOTSBETWEENRELOAD", new NumberProperty(shotsBetweenReload));
					if(shotDelay != null)
						a.addProperty("SHOTDELAY", new NumberProperty(shotDelay));
					if(knockback != null)
						a.addProperty("KNOCKBACK", new NumberProperty(knockback));
					if(shotsoundvolume != null)
						a.addProperty("SHOTSOUNDVOLUME", new NumberProperty(shotsoundvolume));
					if(reloadsoundvolume != null)
						a.addProperty("RELOADSOUNDVOLUME", new NumberProperty(reloadsoundvolume));
					if(zoomTexture != null)
						a.addProperty("ZOOMTEXTURE", new StringProperty(zoomTexture));
					if(shotSound != null)
						a.addProperty("SHOTSOUND", new StringProperty(shotSound));
					if(reloadSound != null)
						a.addProperty("RELOADSOUND", new StringProperty(reloadSound));
					if(!effects.isEmpty())
						a.addProperty("EFFECTS", new CollectionProperty<Effect>(effects));

					if(a.getProperties().isEmpty())
						Util.warn(" Could not find any properties for addition " + name + "!");
				}
			} catch(Exception e) {
				Util.warn("Config Error:" + e.getMessage());
				Util.debug(e);
			}
		}
	}

	public static void loadAmmo() {
		Object[] ammoArray = ammoConfig.getKeys(false).toArray();
		for(Object ammonode : ammoArray) {
			try {
				String name = ammonode.toString();
				if(!name.equalsIgnoreCase("name")) {
					String ammotexture = ammoConfig.getString(name + ".ammo-texture");
					String ammodur = ammoConfig.getString(name + ".ammo-durability", ""+1);
					String randomfactor = ammoConfig.getString(name + ".accuracy.random-factor");
					String spreadangleIN = "0";
					String spreadangleOUT = "0";
					String missingchanceIN = "0";
					String missingchanceOUT = "0";
					String critical = ammoConfig.getString(name + ".critical");
					String range = ammoConfig.getString(name + ".range");
					String damage = ammoConfig.getString(name + ".damage");
					String dur = ammoConfig.getString(name + ".durability");
					String bullet_count = ammoConfig.getString(name + ".accuracy.bullet-count");
					String melee = ammoConfig.getString(name + ".melee-damage");
					String reloadTime = ammoConfig.getString(name + ".reload-time");
					String shotDelay = ammoConfig.getString(name + ".shot-delay");
//					String shotsBetweenReload = ammoConfig.getString(name + ".shots-between-reload");
					String recoil = ammoConfig.getString(name + ".recoil");
					String weight = ammoConfig.getString(name + ".weight");
					String knockback = ammoConfig.getString(name + ".knockback");
					String changedamage = ammoConfig.getString(name + ".damage-change");
					String zoomfactor = ammoConfig.getString(name + ".zoom-factor");
					String headShotDamage = ammoConfig.getString(name + ".head-shot-damage");
					String shotsoundvolume = ammoConfig.getString(name + ".shot-sound.volume");
					String reloadsoundvolume = ammoConfig.getString(name + ".reload-sound.volume");
					String shotSound = ammoConfig.getString(name + ".shot-sound.url");
					String reloadSound = ammoConfig.getString(name + ".reload-sound.url");
					String zoomTexture = ammoConfig.getString(name + ".zoom-texture");
					String spread_angle = ammoConfig.getString(name + ".accuracy.spread-angle");
					String missing_chance = ammoConfig.getString(name + ".accuracy.missing-chance");

					if(spread_angle != null) {
						String[] split = spread_angle.split("->");
						spreadangleIN = split[1];
						spreadangleOUT = split[0];
					}
					if(missing_chance != null) {
						String[] split = missing_chance.split("->");
						missingchanceIN = split[1];
						missingchanceOUT = split[0];
					}

					List<Effect> effects = new ArrayList<Effect>(ConfigParser.parseEffects(ConfigLoader.ammoConfig, name + ".effects"));

					if(ammotexture == null)
						throw new Exception(" Texture for ammo " + name + " is missing or could not be found! Skipping!");

					Ammo a = (Ammo) ItemBuilder.buildAmmo(GunsPlus.plugin, name, ammotexture);
					a.addProperty("AMMODURABILITY", new NumberProperty(ammodur));
					if(weight != null)
						a.addProperty("WEIGHT", new NumberProperty(weight));
					if(changedamage != null)
						a.addProperty("CHANGEDAMAGE", new NumberProperty(changedamage));
					if(damage != null)
						a.addProperty("DAMAGE", new NumberProperty(damage));
					if(melee != null)
						a.addProperty("MELEE", new NumberProperty(melee));
					if(headShotDamage != null)
						a.addProperty("HEADSHOTDAMAGE", new NumberProperty(headShotDamage));
					if(zoomfactor != null)
						a.addProperty("ZOOMFACTOR", new NumberProperty(zoomfactor));
					if(critical != null)
						a.addProperty("CRITICAL", new NumberProperty(critical));
					if(range != null)
						a.addProperty("RANGE", new NumberProperty(range));
					if(randomfactor != null)
						a.addProperty("RANDOMFACTOR", new NumberProperty(randomfactor));
					if(dur != null)
						a.addProperty("DURABILITY", new NumberProperty(dur));
					if(bullet_count != null)
						a.addProperty("BULLETCOUNT", new NumberProperty(bullet_count));
					if(spreadangleOUT != null)
						a.addProperty("SPREAD_OUT", new NumberProperty(spreadangleOUT));
					if(spreadangleIN != null)
						a.addProperty("SPREAD_IN", new NumberProperty(spreadangleIN));
					if(missingchanceOUT != null)
						a.addProperty("MISSING_OUT", new NumberProperty(missingchanceOUT));
					if(missingchanceIN != null)
						a.addProperty("MISSING_IN", new NumberProperty(missingchanceIN));
					if(recoil != null)
						a.addProperty("RECOIL", new NumberProperty(recoil));
					if(reloadTime != null)
						a.addProperty("RELOADTIME", new NumberProperty(reloadTime));
//					if(shotsBetweenReload != null)
//						a.addProperty("SHOTSBETWEENRELOAD", new NumberProperty(shotsBetweenReload));
					if(shotDelay != null)
						a.addProperty("SHOTDELAY", new NumberProperty(shotDelay));
					if(knockback != null)
						a.addProperty("KNOCKBACK", new NumberProperty(knockback));
					if(shotsoundvolume != null)
						a.addProperty("SHOTSOUNDVOLUME", new NumberProperty(shotsoundvolume));
					if(reloadsoundvolume != null)
						a.addProperty("RELOADSOUNDVOLUME", new NumberProperty(reloadsoundvolume));
					if(zoomTexture != null)
						a.addProperty("ZOOMTEXTURE", new StringProperty(zoomTexture));
					if(shotSound != null)
						a.addProperty("SHOTSOUND", new StringProperty(shotSound));
					if(reloadSound != null)
						a.addProperty("RELOADSOUND", new StringProperty(reloadSound));
					if(!effects.isEmpty())
						a.addProperty("EFFECTS", new CollectionProperty<Effect>(effects));
				}
			} catch(Exception e) {
				Util.warn("Config Error:" + e.getMessage());
				Util.debug(e);
			}
		}
	}

	public static void loadRecipes() {
		Object[] recipeKeys = recipeConfig.getKeys(false).toArray();
		for(Object key : recipeKeys) {
			try {
				if(!((String) key).equalsIgnoreCase("name")) {
					YamlConfiguration defaultConfig = new YamlConfiguration();
					defaultConfig.load(GunsPlus.plugin.getResource("recipes.yml"));
					for(String node : defaultConfig.getConfigurationSection("Sniper").getKeys(false)) {

						if(recipeConfig.get(key + "." + node) == null) {
							Util.warn("The node '" + node + "' in the recipe for " + key + " is missing or invalid!");
						}
					}

					Object cm = null;
					if(Util.isGunsPlusMaterial(key.toString()))
						cm = Util.getGunsPlusMaterial(key.toString());
					else
						throw new Exception(GunsPlus.PRE + " Recipe output not found: " + key + "! Skipping!");
					int amount = recipeConfig.getInt(key.toString() + ".amount");
					SpoutItemStack result = null;
					if(cm instanceof CustomItem) {
						CustomItem ci = (CustomItem) cm;
						result = new SpoutItemStack(ci, amount);
					} else if(cm instanceof CustomBlock) {
						CustomBlock cb = (CustomBlock) cm;
						result = new SpoutItemStack(cb, amount);
					}
					List<ItemStack> ingredients = ConfigUtil.parseItems(recipeConfig.getString(key + ".ingredients"));
					RecipeManager.RecipeType type = RecipeManager.RecipeType.valueOf(recipeConfig.getString(key + ".type").toUpperCase());
					RecipeManager.addRecipe(type, ingredients, result);
				}
			} catch(Exception e) {
				Util.warn("Config Error:" + e.getMessage());
				Util.debug(e);
			}
		}
	}

	public static void loadGuns() {
		Object[] gunsArray = gunsConfig.getKeys(false).toArray();
		for(Object gunnode : gunsArray) {
			try {
				String name = gunnode.toString();
				if(!name.equalsIgnoreCase("name")) {
					YamlConfiguration defaultConfig = new YamlConfiguration();
					defaultConfig.load(GunsPlus.plugin.getResource("guns.yml"));
					for(String node : defaultConfig.getConfigurationSection("Sniper").getKeys(false)) {
						if(gunsConfig.get(name + "." + node) == null) {
							Util.warn("The node '" + node + "' in gun " + name + " is missing or invalid!");
						}
					}

					boolean hudenabled = gunsConfig.getBoolean(gunnode + ".hud-enabled", true);
					boolean mountable = gunsConfig.getBoolean(gunnode + ".mountable", true);
					boolean shootable = gunsConfig.getBoolean(gunnode + ".shootable", true);
					float randomfactor = (float) gunsConfig.getDouble(gunnode + ".accuracy.random-factor", 1.0);
					int spreadangleIN = 0;
					int spreadangleOUT = 0;
					int missingchanceIN = 0;
					int missingchanceOUT = 0;
					int critical = gunsConfig.getInt((String) gunnode + ".critical", 0);
					int bulletcount = gunsConfig.getInt((String) gunnode + ".accuracy.bullet-count", 1);
					int dur = gunsConfig.getInt((String) gunnode + ".durability", -1);
					int range = gunsConfig.getInt((String) gunnode + ".range", 0);
					int damage = gunsConfig.getInt((String) gunnode + ".damage", 0);
					int melee = gunsConfig.getInt((String) gunnode + ".melee-damage", 0);
					int reloadTime = gunsConfig.getInt((String) gunnode + ".reload-time", 0);
					int shotDelay = gunsConfig.getInt((String) gunnode + ".shot-delay", 0);
//					int shotsBetweenReload = gunsConfig.getInt((String) gunnode + ".shots-between-reload", 0);
					float recoil = (float) gunsConfig.getDouble((String) gunnode + ".recoil", 0);
					float weight = (float) gunsConfig.getDouble((String) gunnode + ".weight", 0);
					float knockback = (float) gunsConfig.getDouble((String) gunnode + ".knockback", 1.0);
					float changedamage = (float) gunsConfig.getDouble((String) gunnode + ".damage-change", 0);
					int zoomfactor = gunsConfig.getInt((String) gunnode + ".zoom-factor", 0);
					int headShotDamage = gunsConfig.getInt((String) gunnode + ".head-shot-damage", 0);
					int shotsoundvolume = gunsConfig.getInt(gunnode + ".shot-sound.volume", 50);
					int reloadsoundvolume = gunsConfig.getInt(gunnode + ".reload-sound.volume", 50);

					String shotSound = gunsConfig.getString(gunnode + ".shot-sound.url");
					String reloadSound = gunsConfig.getString(gunnode + ".reload-sound.url");
					String zoomTexture = gunsConfig.getString(gunnode + ".zoom-texture");
					String texture = gunsConfig.getString(gunnode + ".texture");
					FireBehavior fb = ConfigParser.parseFireBehavoir(gunnode + ".fire-behavior");

					Projectile projectile = Projectile.valueOf(gunsConfig.getString(gunnode + ".projectile.type").toUpperCase());
					projectile.setSpeed(gunsConfig.getDouble(gunnode + ".projectile.speed", 1.0));

					String[] spread_angle = gunsConfig.getString(gunnode + ".accuracy.spread-angle").split("->");
					if(spread_angle.length == 2) {
						spreadangleIN = Integer.parseInt(spread_angle[1]);
						spreadangleOUT = Integer.parseInt(spread_angle[0]);
					}
					String[] missing_chance = gunsConfig.getString(gunnode + ".accuracy.missing-chance").split("->");
					if(missing_chance.length == 2) {
						missingchanceIN = Integer.parseInt(missing_chance[1]);
						missingchanceOUT = Integer.parseInt(missing_chance[0]);
					}

					ArrayList<Effect> effects = new ArrayList<Effect>(ConfigParser.parseEffects(ConfigLoader.gunsConfig, gunnode + ".effects"));

					ArrayList<ItemStack> ammo = new ArrayList<ItemStack>();
					List<ItemStack> ammoStacks = ConfigUtil.parseItems(gunsConfig.getString((String) gunnode + ".ammo"));
					if(!(ammoStacks == null || ammoStacks.isEmpty())) {
						ammo = new ArrayList<ItemStack>(ammoStacks);
					}

					ArrayList<Addition> adds = new ArrayList<Addition>(ConfigParser.parseAdditions(gunnode + ".additions"));

					if(texture == null)
						throw new Exception(" Can't find texture url for " + gunnode + "!");

					Gun g = ItemBuilder.buildGun(GunsPlus.plugin, name, texture);

					g.addProperty("WEIGHT", new NumberProperty(weight));
					g.addProperty("CHANGEDAMAGE", new NumberProperty(changedamage));
					g.addProperty("DAMAGE", new NumberProperty(damage));
					g.addProperty("MELEE", new NumberProperty(melee));
					g.addProperty("HEADSHOTDAMAGE", new NumberProperty(headShotDamage));
					g.addProperty("ZOOMFACTOR", new NumberProperty(zoomfactor));
					g.addProperty("CRITICAL", new NumberProperty(critical));
					g.addProperty("RANGE", new NumberProperty(range));
					g.addProperty("RANDOMFACTOR", new NumberProperty(randomfactor));
					g.addProperty("SPREAD_OUT", new NumberProperty(spreadangleOUT));
					g.addProperty("SPREAD_IN", new NumberProperty(spreadangleIN));
					g.addProperty("MISSING_OUT", new NumberProperty(missingchanceOUT));
					g.addProperty("MISSING_IN", new NumberProperty(missingchanceIN));
					g.addProperty("RECOIL", new NumberProperty(recoil));
					g.addProperty("RELOADTIME", new NumberProperty(reloadTime));
//					g.addProperty("SHOTSBETWEENRELOAD", new NumberProperty(shotsBetweenReload));
					g.addProperty("SHOTDELAY", new NumberProperty(shotDelay));
					g.addProperty("KNOCKBACK", new NumberProperty(knockback));
					g.addProperty("SHOTSOUNDVOLUME", new NumberProperty(shotsoundvolume));
					g.addProperty("ZOOMTEXTURE", new StringProperty(zoomTexture));
					g.addProperty("SHOTSOUND", new StringProperty(shotSound));
					g.addProperty("RELOADSOUND", new StringProperty(reloadSound));
					g.addProperty("RELOADSOUNDVOLUME", new NumberProperty(reloadsoundvolume));
					g.addProperty("AMMO", new CollectionProperty<ItemStack>(ammo));
					g.addProperty("PROJECTILE", new ObjectProperty<Projectile>(projectile));
					g.addProperty("HUDENABLED", new ObjectProperty<Boolean>(hudenabled));
					g.addProperty("MOUNTABLE", new ObjectProperty<Boolean>(mountable));
					g.addProperty("SHOOTABLE", new ObjectProperty<Boolean>(shootable));
					g.addProperty("FIREBEHAVIOR", new ObjectProperty<FireBehavior>(fb));
					g.addProperty("EFFECTS", new CollectionProperty<Effect>(effects));
					g.addProperty("BULLETCOUNT", new NumberProperty(bulletcount));
					g.addProperty("DURABILITY", new NumberProperty(dur));
					// registering shapeless recipes for additions + building an
					// extra gun for each addition
					for(Addition a : adds) {
						List<ItemStack> listIngred = new ArrayList<ItemStack>();
						listIngred.add(new SpoutItemStack(a));
						listIngred.add(new SpoutItemStack((GenericCustomItem) g));
						Gun addgun = ItemBuilder.buildAdditionGun(GunsPlus.plugin, GunUtils.getFullGunName(g, a), texture, a, g);
						RecipeManager.addRecipe("shapeless", listIngred, new SpoutItemStack((GenericCustomItem) addgun));
					}
				}
			} catch(Exception e) {
				Util.warn("Config Error:" + e.getMessage());
				Util.debug(e);
			}
		}
	}

	public static void loadGeneral() {
		try {
			YamlConfiguration defaultConfig = new YamlConfiguration();
			defaultConfig.load(GunsPlus.plugin.getResource("general.yml"));
			for(String node : defaultConfig.getKeys(true)) {
				if(ConfigLoader.generalConfig.get(node) == null)
					Util.warn("The node '" + node + "' in general.yml is missing or invalid! Defaulting!");
			}

			GunsPlus.warnings = ConfigLoader.generalConfig.getBoolean("show-warnings", true);
			GunsPlus.warnings = ConfigLoader.generalConfig.getBoolean("check-for-updates", true);
			GunsPlus.debug = ConfigLoader.generalConfig.getBoolean("show-debug", false);
			GunsPlus.notifications = ConfigLoader.generalConfig.getBoolean("send-notifications", true);
			GunsPlus.autoreload = ConfigLoader.generalConfig.getBoolean("auto-reload", true);
			GunsPlus.useperms = ConfigLoader.generalConfig.getBoolean("use-permissions", true);
			GunsPlus.toolholding = ConfigLoader.generalConfig.getBoolean("tool-like-gun-holding", true);

			Util.info("The Tripod block is currently being improved and therefore disabled in this version of Guns+!");
			Tripod.tripodenabled = false;
			Tripod.tripodTexture = ConfigLoader.generalConfig.getString("tripod.texture", "http://dl.dropbox.com/u/44243469/GunPack/Textures/tripod.png");
			Tripod.maxtripodcount = ConfigLoader.generalConfig.getInt("tripod.max-count-per-player", -1);
			Tripod.forcezoom = ConfigLoader.generalConfig.getBoolean("tripod.force-zoom", true);
			Tripod.tripodinvsize = ConfigLoader.generalConfig.getInt("tripod.inventory-size", 9);
			Tripod.hardness = (float) ConfigLoader.generalConfig.getDouble("tripod.hardness", 2.0);
			if(!((Tripod.tripodinvsize % 9) == 0)) {
				Util.warn("Tripod inventory size has to be a multiple of 9!");
				Tripod.tripodinvsize = 9;
			}
			if(Tripod.tripodenabled == true) {
				Task trecipe = new Task(GunsPlus.plugin) {
					public void run() {
						if(GunsPlus.tripod != null) {
							SpoutItemStack result = new SpoutItemStack(GunsPlus.tripod, ConfigLoader.generalConfig.getInt("tripod.recipe.amount", 1));
							List<ItemStack> ingred = ConfigUtil.parseItems(ConfigLoader.generalConfig.getString("tripod.recipe.ingredients", "blaze_rod, 0, blaze_rod, 0, cobblestone, 0, blaze_rod, 0, blaze_rod"));
							try {
								RecipeManager.addRecipe(RecipeManager.RecipeType.valueOf(ConfigLoader.generalConfig.getString("tripod.recipe.type", "shaped").toUpperCase()), ingred, result);
							} catch(Exception e) {
								Util.debug(e);
								Util.warn("Config Error: " + e.getMessage());
							}
							this.stopTask();
						}
					}
				};
				trecipe.startTaskRepeating(5, false);
			}

			GunsPlus.hudenabled = ConfigLoader.generalConfig.getBoolean("hud.enabled", true);
			GunsPlus.hudBackground = ConfigLoader.generalConfig.getString("hud.background", "http://dl.dropbox.com/u/44243469/GunPack/Textures/HUDBackground.png");
			GunsPlus.hudX = ConfigLoader.generalConfig.getInt("hud.position.X", 20);
			GunsPlus.hudY = ConfigLoader.generalConfig.getInt("hud.position.Y", 20);

			String z = ConfigLoader.generalConfig.getString("zoom", "right");
			GunsPlus.zoomKey = ConfigParser.parseKeyType(z);
			if(GunsPlus.zoomKey == null)
				throw new Exception(" Could not parse zoom key!");

			String f = ConfigLoader.generalConfig.getString("fire", "left");
			GunsPlus.fireKey = ConfigParser.parseKeyType(f);
			if(GunsPlus.fireKey == null)
				throw new Exception(" Could not parse fire key!");

			String r = ConfigLoader.generalConfig.getString("reload", "R");
			GunsPlus.reloadKey = ConfigParser.parseKeyType(r);
			if(GunsPlus.reloadKey == null)
				throw new Exception(" Could not parse reload key!");

			if(GunsPlus.zoomKey.equals(GunsPlus.fireKey) || GunsPlus.fireKey.equals(GunsPlus.reloadKey) || GunsPlus.reloadKey.equals(GunsPlus.zoomKey)) {
				String message = ("Zoom:" + GunsPlus.zoomKey.getKey().toString() + " Fire:" + GunsPlus.fireKey.getKey().toString() + " Reload:" + GunsPlus.reloadKey.getKey().toString());
				GunsPlus.zoomKey = new KeyType("LEFT", false);
				GunsPlus.fireKey = new KeyType("RIGHT", false);
				GunsPlus.reloadKey = new KeyType("R", false);
				throw new Exception("Key's Duplicated: " + message);
			}
		} catch(Exception e) {
			Util.warn("Config Error:" + e.getMessage());
			Util.debug(e);
		}
	}

	public static boolean modify(FileConfiguration con) {
		String name = con.getString("Name");
		if(name.equalsIgnoreCase("guns")) {
			ConfigLoader.gunsConfig = con;
			ConfigLoader.loadGuns();
			return true;
		}
		if(name.equalsIgnoreCase("ammo")) {
			ConfigLoader.ammoConfig = con;
			ConfigLoader.loadAmmo();
			return true;
		}
		if(name.equalsIgnoreCase("additions")) {
			ConfigLoader.additionsConfig = con;
			ConfigLoader.loadAdditions();
			return true;
		}
		if(name.equalsIgnoreCase("recipes")) {
			ConfigLoader.recipeConfig = con;
			ConfigLoader.loadRecipes();
			return true;
		}
		return false;
	}
}
