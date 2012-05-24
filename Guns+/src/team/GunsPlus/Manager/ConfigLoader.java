package team.GunsPlus.Manager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.CustomBlock;
import org.getspout.spoutapi.material.CustomItem;

import team.GunsPlus.GunsPlus;
import team.GunsPlus.Enum.Effect;
//import team.GunsPlus.Enum.FireBehavior;
import team.GunsPlus.Enum.KeyType;
import team.GunsPlus.Enum.Projectile;
import team.GunsPlus.Item.Addition;
import team.GunsPlus.Item.Ammo;
import team.GunsPlus.Item.Gun;
import team.GunsPlus.Util.GunUtils;
import team.GunsPlus.Util.Task;
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
		} catch (Exception e) {}
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
		} catch (Exception e) {}
	}

	private static void firstRun() {
		if(FileManager.create(gunsFile))
			FileManager.copy(GunsPlus.plugin.getResource("guns.yml"), ConfigLoader.gunsFile);
		if(FileManager.create(ammoFile))
			FileManager.copy(GunsPlus.plugin.getResource("ammo.yml"), ConfigLoader.ammoFile);
		if(FileManager.create(recipeFile))
			FileManager.copy(GunsPlus.plugin.getResource("recipes.yml"), ConfigLoader.recipeFile);
		if(FileManager.create(generalFile))
			FileManager.copy(GunsPlus.plugin.getResource("general.yml"), ConfigLoader.generalFile);
		if(FileManager.create(additionsFile))
			FileManager.copy(GunsPlus.plugin.getResource("additions.yml"), ConfigLoader.additionsFile);
		if(FileManager.create(dataFile))
			FileManager.copy(GunsPlus.plugin.getResource("data.dat"), dataFile);
	}
	
	public static void loadAdditions(){
		for(Object additionnode:additionsConfig.getKeys(false)){
			try{
				String name = additionnode.toString();
				String addtexture = additionsConfig.getString(additionnode+".add-texture");
				float randomfactor = (float) additionsConfig.getDouble(additionnode+".accuracy.random-factor");
				int spreadangleIN = 0;
				int spreadangleOUT = 0;
				int missingchanceIN = 0;
				int missingchanceOUT = 0;
				int critical =  additionsConfig.getInt(additionnode+".critical");
				int range =  additionsConfig.getInt(additionnode+".range");
				int damage =  additionsConfig.getInt(additionnode+".damage");
				int melee = additionsConfig.getInt(additionnode+".melee-damage");
				int reloadTime =   additionsConfig.getInt(additionnode+".reload-time");
				int shotDelay=  additionsConfig.getInt(additionnode+".shot-delay");
				int shotsBetweenReload =  additionsConfig.getInt(additionnode+".shots-between-reload");
				float recoil = (float) additionsConfig.getDouble(additionnode+".recoil");
				float weight = (float) additionsConfig.getDouble(additionnode+".weight");
				float knockback =  (float) additionsConfig.getDouble(additionnode+".knockback");
				float changedamage =  (float) additionsConfig.getDouble(additionnode+".damage-change");
				int zoomfactor =  additionsConfig.getInt(additionnode+".zoom-factor");
				int headShotDamage =  additionsConfig.getInt(additionnode+".head-shot-damage");
				int shotsoundvolume = additionsConfig.getInt(additionnode+".shot-sound.volume");
				int reloadsoundvolume = additionsConfig.getInt(additionnode+".reload-sound.volume");
				String shotSound = additionsConfig.getString(additionnode+".shot-sound.url");
				String reloadSound = additionsConfig.getString(additionnode+".reload-sound.url");
				String zoomTexture = additionsConfig.getString(additionnode+".zoom-texture");
				String spread_angle = additionsConfig.getString(additionnode+".accuracy.spread-angle");
				String missing_chance = additionsConfig.getString(additionnode+".accuracy.missing-chance");
				
				if(spread_angle!=null){
					String[] split = spread_angle.split("->");
					spreadangleIN=Integer.parseInt(split[1]);
					spreadangleOUT=Integer.parseInt(split[0]);
				}
				if(missing_chance!=null){
					String[] split = missing_chance.split("->");
					missingchanceIN=Integer.parseInt(split[1]);
					missingchanceOUT=Integer.parseInt(split[0]);
				}
				
//				ArrayList<EffectSection> effects = new ArrayList<EffectSection>(ConfigParser.getEffects(additionnode+".effects"));
//		TODO:
//				ArrayList<ItemStack> ammo =  new ArrayList<ItemStack>(ConfigParser.parseItems(gunsConfig.getString(additionnode+".ammo")));
				
				if(addtexture == null)
					throw new Exception (" Texture for "+name+" is missing or could not be found! Skipping!");
				
				Addition a = AdditionManager.buildAddition(GunsPlus.plugin, name, addtexture);
				if(weight!=0)
					AdditionManager.editNumberValue(a, "WEIGHT", weight);
				if(changedamage!=0)
					AdditionManager.editNumberValue(a, "CHANGEDAMAGE", changedamage);
				if(damage!=0)
					AdditionManager.editNumberValue(a, "DAMAGE", (float) damage);
				if(melee!=0)
					AdditionManager.editNumberValue(a, "MELEE", (float) melee);
				if(headShotDamage!=0)
					AdditionManager.editNumberValue(a, "HEADSHOTDAMAGE", (float) headShotDamage);
				if(zoomfactor!=0)
					AdditionManager.editNumberValue(a, "ZOOMFACTOR", (float) zoomfactor);
				if(critical!=0)
					AdditionManager.editNumberValue(a, "CRITICAL", (float) critical);
				if(range!=0)
					AdditionManager.editNumberValue(a, "RANGE", (float) range);
				if(randomfactor!=0)
					AdditionManager.editNumberValue(a, "RANDOMFACTOR", randomfactor);
				if(spreadangleOUT!=0)
					AdditionManager.editNumberValue(a, "SPREAD_OUT", (float) spreadangleOUT);
				if(spreadangleIN!=0)
					AdditionManager.editNumberValue(a, "SPREAD_IN", (float) spreadangleIN);
				if(missingchanceOUT!=0)
					AdditionManager.editNumberValue(a, "MISSING_OUT", (float) missingchanceOUT);
				if(missingchanceIN!=0)
					AdditionManager.editNumberValue(a, "MISSING_IN", (float) missingchanceIN);
				if(recoil!=0)
					AdditionManager.editNumberValue(a, "RECOIL", recoil);
				if(reloadTime!=0)
					AdditionManager.editNumberValue(a, "RELOADTIME", (float) reloadTime);
				if(shotsBetweenReload!=0)
					AdditionManager.editNumberValue(a, "SHOTSBETWEENRELOAD", (float) shotsBetweenReload);
				if(shotDelay!=0)
					AdditionManager.editNumberValue(a, "SHOTDELAY", (float) shotDelay);
				if(knockback!=0)
					AdditionManager.editNumberValue(a, "KNOCKBACK", knockback);
				if(shotsoundvolume!=0)
					AdditionManager.editNumberValue(a, "SHOTSOUNDVOLUME", (float) shotsoundvolume);
				if(reloadsoundvolume!=0)
					AdditionManager.editNumberValue(a, "RELOADSOUNDVOLUME", (float) reloadsoundvolume);
				if(zoomTexture!=null)
					AdditionManager.editStringValue(a, "ZOOMTEXTURE", zoomTexture);
				if(shotSound!=null)
					AdditionManager.editStringValue(a, "SHOTSOUND", shotSound);
				if(reloadSound!=null)
					AdditionManager.editStringValue(a, "RELOADSOUND", reloadSound);
				
				if(a.getNumberValues().isEmpty()&&a.getStringValues().isEmpty()){
					GunsPlus.allAdditions.remove(a);
					throw new Exception(" Could not find any values for addition "+name+"!");
				}
				
			}catch(Exception e){
				Util.warn("Config Error:" + e.getMessage());
				Util.debug(e);
			}
		}
		if(generalConfig.getBoolean("loaded-additions")==true){
			GunsPlus.log.log(Level.INFO, GunsPlus.PRE + " -------------- Additions loaded: ---------------");
			for(int k=0;k<GunsPlus.allAdditions.size();k++){
				GunsPlus.log.log(Level.INFO, "- "+GunsPlus.allAdditions.get(k).getName());
			}
		}
	}
	

	public static void loadAmmo() {
		Object[] ammoArray =  ammoConfig.getKeys(false).toArray();
		for(Object ammonode:ammoArray){
			try{
				YamlConfiguration defaultConfig = new YamlConfiguration();
				defaultConfig.load(GunsPlus.plugin.getResource("ammo.yml"));
				for(String node : defaultConfig.getConfigurationSection("SniperAmmo").getKeys(false)){
					if(ammoConfig.get(ammonode+"."+node)==null){
						Util.warn( "The node '"+node+"' in "+ammonode+" is missing or invalid!");
					}
				}
				
				String texture = ammoConfig.getString(ammonode+".texture");
				int damage = ammoConfig.getInt(ammonode+".damage", 0);
				
				String name = ammonode.toString();
				
				if(texture == null)
					throw new Exception(" Can't find texture url for "+ammonode+"! Skipping!");
				
				Ammo a = new Ammo(GunsPlus.plugin, name, texture, damage);
				GunsPlus.allAmmo.add(a);
			} catch (Exception e) {
				Util.warn("Config Error:" + e.getMessage());
				Util.debug(e);
			}
		}
		
		if(generalConfig.getBoolean("loaded-ammo")==true){
			GunsPlus.log.log(Level.INFO, GunsPlus.PRE + " -------------- Ammo loaded: ---------------");
			for(Ammo a : GunsPlus.allAmmo)GunsPlus.log.log(Level.INFO, "- "+a.getName());
		}		
	}
	
	public static void loadRecipes(){
		Object[] recipeKeys = recipeConfig.getKeys(false).toArray();
		for(Object key : recipeKeys){
			try{
				YamlConfiguration defaultConfig = new YamlConfiguration();
				defaultConfig.load(GunsPlus.plugin.getResource("recipes.yml"));
				for(String node : defaultConfig.getConfigurationSection("Sniper").getKeys(false)){
					if(recipeConfig.get(key+"."+node)==null){
						Util.warn( "The node '"+node+"' in the recipe for "+key+" is missing or invalid!");
					}
				}
				
				Object cm = null;
				if(Util.isGunsPlusMaterial(key.toString()))cm = Util.getGunsPlusMaterial(key.toString());
				else throw new Exception(GunsPlus.PRE + " Recipe output not found: "+key+"! Skipping!");
				int amount = recipeConfig.getInt(key.toString()+".amount");
				SpoutItemStack result = null;
				if(cm instanceof CustomItem){
					CustomItem ci = (CustomItem)cm;
					result = new SpoutItemStack(ci, amount);
				}
				else if(cm instanceof CustomBlock){
					CustomBlock cb = (CustomBlock) cm;
					result = new SpoutItemStack(cb, amount);
				}
				List<ItemStack> ingredients = ConfigParser.parseItems(recipeConfig.getString(key+".ingredients"));
				team.GunsPlus.Manager.RecipeManager.RecipeType type = team.GunsPlus.Manager.RecipeManager.RecipeType.valueOf(recipeConfig.getString(key+".type").toUpperCase());
				RecipeManager.addRecipe(type, ingredients, result);
			}catch (Exception e) {
				Util.warn("Config Error:" + e.getMessage());
				Util.debug(e);
			}
		}
	}

	public static void loadGuns() {
		Object[] gunsArray =  gunsConfig.getKeys(false).toArray();
		for(Object gunnode : gunsArray){
			try{
				String name = gunnode.toString();
				
				YamlConfiguration defaultConfig = new YamlConfiguration();
				defaultConfig.load(GunsPlus.plugin.getResource("guns.yml"));
				for(String node : defaultConfig.getConfigurationSection("Sniper").getKeys(false)){
					if(gunsConfig.get(name+"."+node)==null){
						Util.warn( "The node '"+node+"' in gun "+name+" is missing or invalid!");
					}
				}
				
				boolean hudenabled = gunsConfig.getBoolean(gunnode+".hud-enabled", true);
				boolean mountable = gunsConfig.getBoolean(gunnode+".mountable", true);
				boolean shootable = gunsConfig.getBoolean(gunnode+".shootable", true);
				float randomfactor = (float) gunsConfig.getDouble(gunnode+".accuracy.random-factor", 1.0);
				int spreadangleIN = 0;
				int spreadangleOUT = 0;
				int missingchanceIN = 0;
				int missingchanceOUT = 0;
				int critical =  gunsConfig.getInt((String) gunnode+".critical", 0);
				int range =  gunsConfig.getInt((String) gunnode+".range", 0);
				int damage =  gunsConfig.getInt((String) gunnode+".damage", 0);
				int melee = gunsConfig.getInt((String) gunnode+".melee-damage", 0);
				int reloadTime =   gunsConfig.getInt((String) gunnode+".reload-time", 0);
				int shotDelay=  gunsConfig.getInt((String) gunnode+".shot-delay", 0);
				int shotsBetweenReload =  gunsConfig.getInt((String) gunnode+".shots-between-reload", 0);
				float recoil = (float) gunsConfig.getDouble((String) gunnode+".recoil", 0);
				float weight = (float) gunsConfig.getDouble((String) gunnode+".weight", 0);
				float knockback =  (float) gunsConfig.getDouble((String) gunnode+".knockback", 1.0);
				float changedamage =  (float) gunsConfig.getDouble((String)gunnode+".damage-change", 0);
				int zoomfactor =  gunsConfig.getInt((String) gunnode+".zoom-factor", 0);
				int headShotDamage =  gunsConfig.getInt((String) gunnode+".head-shot-damage", 0);
				int shotsoundvolume = gunsConfig.getInt(gunnode+".shot-sound.volume", 50);
				int reloadsoundvolume = gunsConfig.getInt(gunnode+".reload-sound.volume", 50);
				
				String shotSound = gunsConfig.getString(gunnode+".shot-sound.url");
				String reloadSound = gunsConfig.getString(gunnode+".reload-sound.url");
				String zoomTexture = gunsConfig.getString(gunnode+".zoom-texture");
				String texture = gunsConfig.getString(gunnode+".texture");
//				FireBehavior fb = ConfigParser.parseFireBehavoir(gunnode+".fire-behavior");
				
				Projectile projectile = Projectile.valueOf(gunsConfig.getString(gunnode+".projectile.type"));
				projectile.setSpeed(gunsConfig.getDouble(gunnode+".projectile.speed", 1.0));
				
				String[] spread_angle = gunsConfig.getString(gunnode+".accuracy.spread-angle").split("->");
				if(spread_angle.length==2){
					spreadangleIN=Integer.parseInt(spread_angle[1]);
					spreadangleOUT=Integer.parseInt(spread_angle[0]);
				}
				String[] missing_chance = gunsConfig.getString(gunnode+".accuracy.missing-chance").split("->");
				if(missing_chance.length==2){
					missingchanceIN=Integer.parseInt(missing_chance[1]);
					missingchanceOUT=Integer.parseInt(missing_chance[0]);
				}
				
				ArrayList<Effect> effects = new ArrayList<Effect>(ConfigParser.parseEffects(gunnode+".effects"));
				
				ArrayList<ItemStack> ammo =  new ArrayList<ItemStack>();
				List<ItemStack> ammoStacks = ConfigParser.parseItems(gunsConfig.getString((String) gunnode+".ammo"));
				if(!(ammoStacks==null||ammoStacks.isEmpty())){
					ammo = new ArrayList<ItemStack>(ammoStacks);
				}
				
				ArrayList<Addition> adds = new ArrayList<Addition>(ConfigParser.parseAdditions(gunnode+".additions"));
				
				
				if(texture==null)
						throw new Exception(" Can't find texture url for "+gunnode+"!");
				
				Gun g = GunManager.buildNewGun(GunsPlus.plugin,name, texture);

				GunManager.editGunValue(g, "WEIGHT", weight);
				GunManager.editGunValue(g, "CHANGEDAMAGE", changedamage);
				GunManager.editGunValue(g, "DAMAGE", damage);
				GunManager.editGunValue(g, "MELEE", melee);
				GunManager.editGunValue(g, "HEADSHOTDAMAGE", headShotDamage);
				GunManager.editGunValue(g, "ZOOMFACTOR", zoomfactor);
				GunManager.editGunValue(g, "CRITICAL", critical);
				GunManager.editGunValue(g, "RANGE", range);
				GunManager.editGunValue(g, "RANDOMFACTOR", randomfactor);
				GunManager.editGunValue(g, "SPREAD_OUT", spreadangleOUT);
				GunManager.editGunValue(g, "SPREAD_IN", spreadangleIN);
				GunManager.editGunValue(g, "MISSING_OUT", missingchanceOUT);
				GunManager.editGunValue(g, "MISSING_IN", missingchanceIN);
				GunManager.editGunValue(g, "RECOIL", recoil);
				GunManager.editGunValue(g, "RELOADTIME", reloadTime);
				GunManager.editGunValue(g, "SHOTSBETWEENRELOAD", shotsBetweenReload);
				GunManager.editGunValue(g, "SHOTDELAY", shotDelay);
				GunManager.editGunValue(g, "KNOCKBACK", knockback);
				GunManager.editGunValue(g, "SHOTSOUNDVOLUME", shotsoundvolume);
				GunManager.editGunValue(g, "RELOADSOUNDVOLUME", reloadsoundvolume);
				GunManager.editGunResource(g, "ZOOMTEXTURE", zoomTexture);
				GunManager.editGunResource(g, "SHOTSOUND", shotSound);
				GunManager.editGunResource(g, "RELOADSOUND", reloadSound);
			 	GunManager.editAmmo(g, ammo);
				GunManager.editObject(g, "PROJECTILE", projectile);
				GunManager.editObject(g, "HUDENABLED", hudenabled);
				GunManager.editObject(g, "MOUNTABLE", mountable);
				GunManager.editObject(g, "SHOOTABLE", shootable);
//				GunManager.editObject(g, "FIREBEHAVIOR", fb);
				GunManager.editEffects(g, effects);
				
				//registering shapeless recipes for additions + building an extra gun for each addition
				for(Addition a: adds){
					List<ItemStack> listIngred = new ArrayList<ItemStack>();
						listIngred.add(new SpoutItemStack(a));
						listIngred.add(new SpoutItemStack(g));
					Gun addgun = GunManager.buildNewAdditionGun(GunsPlus.plugin, GunUtils.getFullGunName(g, a), texture, a, g);
					RecipeManager.addRecipe("shapeless", listIngred, new SpoutItemStack(addgun));
				}

			}catch(Exception e){
				Util.warn( "Config Error:" + e.getMessage());
				Util.debug(e);
			}
		}
		if(generalConfig.getBoolean("loaded-guns")==true){
			GunsPlus.log.log(Level.INFO, GunsPlus.PRE + " -------------- Guns loaded: ---------------");
			for(Gun g : GunsPlus.allGuns) GunsPlus.log.log(Level.INFO, "- "+g.getName());
		}
	}

	public static void loadGeneral() {
		try {
			YamlConfiguration defaultConfig = new YamlConfiguration();
			defaultConfig.load(GunsPlus.plugin.getResource("general.yml"));
			for(String node : defaultConfig.getKeys(true)){
				if(ConfigLoader.generalConfig.get(node)==null)
					Util.warn("The node '"+node+"' in general.yml is missing or invalid! Defaulting!");
			}
			
			
			GunsPlus.warnings = ConfigLoader.generalConfig.getBoolean("show-warnings", true);
			GunsPlus.debug = ConfigLoader.generalConfig.getBoolean("show-debug", false);
			GunsPlus.notifications = ConfigLoader.generalConfig.getBoolean("show-notifications", true);
			GunsPlus.autoreload = ConfigLoader.generalConfig.getBoolean("auto-reload", true);
			
			GunsPlus.tripodenabled = ConfigLoader.generalConfig.getBoolean("tripod.enabled", true);
			GunsPlus.tripodTexture = ConfigLoader.generalConfig.getString("tripod.texture", "http://dl.dropbox.com/u/44243469/GunPack/Textures/tripod.png");
			GunsPlus.maxtripodcount = ConfigLoader.generalConfig.getInt("tripod.max-count-per-player", -1);
			GunsPlus.forcezoom = ConfigLoader.generalConfig.getBoolean("tripod.force-zoom",  true);
			GunsPlus.tripodinvsize = ConfigLoader.generalConfig.getInt("tripod.inventory-size", 9);
			if(!((GunsPlus.tripodinvsize%9)==0)){
				Util.warn("Tripod inventory size has to be a multiple of 9!");
				GunsPlus.tripodinvsize = 9;
			}
			if(GunsPlus.tripodenabled == true){
				Task trecipe = new Task(GunsPlus.plugin){
					public void run(){
						if(GunsPlus.tripod!=null){
							SpoutItemStack result = new SpoutItemStack(GunsPlus.tripod, ConfigLoader.generalConfig.getInt("tripod.recipe.amount", 1));
							List<ItemStack> ingred = ConfigParser.parseItems(ConfigLoader.generalConfig.getString("tripod.recipe.ingredients", "blaze_rod, 0, blaze_rod, 0, cobblestone, 0, blaze_rod, 0, blaze_rod"));
							try {
								RecipeManager.addRecipe(team.GunsPlus.Manager.RecipeManager.RecipeType.valueOf(ConfigLoader.generalConfig.getString("tripod.recipe.type", "shaped").toUpperCase()), ingred, result);
							} catch (Exception e) {
								Util.debug(e);
								Util.warn("Config Error: "+e.getMessage());
							}
							this.stopTask();
						}
					}
				};
				trecipe.startTaskRepeating(5, false);
			}
			
			
			List<ItemStack> il = ConfigParser.parseItems(ConfigLoader.generalConfig.getString("transparent-materials"));
			for(int m=0;m<il.size();m++){
				GunsPlus.transparentMaterials.add(il.get(m).getType());
			}
			GunsPlus.hudenabled = ConfigLoader.generalConfig.getBoolean("hud.enabled", true);
			GunsPlus.hudBackground = ConfigLoader.generalConfig.getString("hud.background",
					"http://dl.dropbox.com/u/44243469/GunPack/Textures/HUDBackground.png");
			GunsPlus.hudX = ConfigLoader.generalConfig.getInt("hud.position.X", 20);
			GunsPlus.hudY = ConfigLoader.generalConfig.getInt("hud.position.Y", 20);
			
			String z = ConfigLoader.generalConfig.getString("zoom", "right");
			GunsPlus.zoomKey = ConfigParser.parseKeyType(z);
			if(GunsPlus.zoomKey==null) throw new Exception(" Could not parse zoom key!");

			String f = ConfigLoader.generalConfig.getString("fire", "left");
			GunsPlus.fireKey = ConfigParser.parseKeyType(f);
			if(GunsPlus.fireKey==null) throw new Exception(" Could not parse fire key!");

			String r = ConfigLoader.generalConfig.getString("reload", "@r");
			GunsPlus.reloadKey = ConfigParser.parseKeyType(r);
			if(GunsPlus.reloadKey==null) throw new Exception(" Could not parse reload key!");

			if (GunsPlus.zoomKey.getData().equalsIgnoreCase(GunsPlus.fireKey.getData()) || GunsPlus.fireKey.getData().equalsIgnoreCase(GunsPlus.reloadKey.getData())
					|| GunsPlus.reloadKey.getData().equalsIgnoreCase(GunsPlus.zoomKey.getData())) {
				String message = ("Zoom:" + GunsPlus.zoomKey.getData() + " Fire:" + GunsPlus.fireKey.getData() + " Reload:" + GunsPlus.reloadKey.getData());
				GunsPlus.zoomKey = KeyType.RIGHT;
				GunsPlus.fireKey = KeyType.LEFT;
				GunsPlus.reloadKey = KeyType.LETTER("r");
				throw new Exception("Key's Duplicated: " + message);
			}

		} catch (Exception e) {
			Util.warn( "Config Error:" + e.getMessage());
			Util.debug(e);}
	}
	
}
