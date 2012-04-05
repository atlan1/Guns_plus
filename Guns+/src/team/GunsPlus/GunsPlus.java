package team.GunsPlus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.morematerials.morematerials.Main;
import net.morematerials.morematerials.SmpManager;
import net.morematerials.morematerials.SmpPackage;
import net.morematerials.morematerials.materials.SMCustomBlock;
import net.morematerials.morematerials.materials.SMCustomItem;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.gui.GenericTexture;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.CustomBlock;
import org.getspout.spoutapi.material.CustomItem;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.griefcraft.lwc.LWC;
import com.griefcraft.lwc.LWCPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import team.GunsPlus.Block.Tripod;
import team.GunsPlus.Block.TripodData;
import team.GunsPlus.Enum.KeyType;
import team.GunsPlus.Enum.Projectile;
import team.GunsPlus.Gui.HUD;
import team.GunsPlus.Item.Ammo;
import team.GunsPlus.Item.Gun;
import team.GunsPlus.Manager.AdditionManager;
import team.GunsPlus.Manager.ConfigParser;
import team.GunsPlus.Manager.GunManager;
import team.GunsPlus.Manager.RecipeManager;

public class GunsPlus extends JavaPlugin {
	public static String PRE = "[Guns+]";
	
	public static LWC lwc;
	public static WorldGuardPlugin wg;
	
	public static Logger log = Logger.getLogger("Minecraft");;
	
	public static List<SpoutPlayer> inZoom = new ArrayList<SpoutPlayer>();
	public static HashMap<SpoutPlayer, Boolean> reloading = new HashMap<SpoutPlayer, Boolean>();
	public static HashMap<SpoutPlayer, Boolean> delaying = new HashMap<SpoutPlayer, Boolean>();
	public static HashMap<SpoutPlayer, Integer> fireCounter = new HashMap<SpoutPlayer, Integer>();
	
	public static HashMap<SpoutPlayer, GenericTexture> zoomTextures = new HashMap<SpoutPlayer, GenericTexture>();
	public static HashMap<SpoutPlayer, HUD> playerHUD = new HashMap<SpoutPlayer, HUD>();
	
	public static boolean warnings = true;
	public static boolean debug = false;
	public static boolean notifications = true;
	public static boolean autoreload = true;
	public KeyType fireKey = KeyType.LEFT;
	public KeyType reloadKey = KeyType.LETTER("R");
	public boolean hudenabled = false;
	public int hudX = 0;
	public int hudY = 0;
	public String hudBackground = null;
	public KeyType zoomKey = KeyType.RIGHT;
	
	public File gunsFile;
	public static FileConfiguration gunsConfig;
	public File additionsFile;
	public static FileConfiguration additionsConfig;
	public File ammoFile;
	public static FileConfiguration ammoConfig;
	public File recipeFile;
	public static FileConfiguration recipeConfig;
	public File generalFile;
	public static FileConfiguration generalConfig;

	public static List<Gun> allGuns = new ArrayList<Gun>();
	public static List<Ammo> allAmmo = new ArrayList<Ammo>();
	public static List<Addition> allAdditions = new ArrayList<Addition>();
	public static List<SMCustomItem> allMoreMaterialsItems = new ArrayList<SMCustomItem>();
	public static List<CustomBlock> allMoreMaterialsBlocks = new ArrayList<CustomBlock>();
	public static List<Material> transparentMaterials = new ArrayList<Material>();
	
	public static List<TripodData> allTripodBlocks = new ArrayList<TripodData>();

	public static String tripodTexture = "http://dl.dropbox.com/u/44243469/GunPack/Textures/landmine.png";

	@Override
	public void onDisable() {
		log.log(Level.INFO, PRE + " version " + getDescription().getVersion()
				+ " is now disabled.");
	}

	@Override
	public void onEnable() {
		new VersionChecker(this,"http://dev.bukkit.org/server-mods/guns/files.rss");
		new Tripod(this, tripodTexture);
		config();
		init();
		
		if(debug) log.setLevel(Level.ALL);
			
		Plugin spout = getServer().getPluginManager().getPlugin("Spout");
		Plugin lwcPlugin = getServer().getPluginManager().getPlugin("LWC");
		Plugin furnaceAPI = getServer().getPluginManager().getPlugin("FurnaceAPI");
		Plugin worldguard = getServer().getPluginManager().getPlugin("WorldGuardPlugin");
		Plugin mm = getServer().getPluginManager().getPlugin("MoreMaterials");
		if(spout != null) {
		    log.log(Level.INFO, PRE+" Plugged into Spout!");
		}else{
			this.setEnabled(false);
		}
		if(lwcPlugin != null) {
		    lwc = ((LWCPlugin) lwcPlugin).getLWC();
		    log.log(Level.INFO, PRE+" Plugged into LWC!");
		}
		if(furnaceAPI != null) {
			 log.log(Level.INFO, PRE+" Plugged into FurnaceAPI!");
		}
		if(worldguard != null) {
			wg = (WorldGuardPlugin) worldguard;
			log.log(Level.INFO, PRE+" Plugged into WorldGuard!");
		}
		//just experimental, hook into mm and provide the custom items in gun recipes
		if(mm != null) {
			Main moremats = (Main) mm;
			SmpManager smpManager = moremats.getSmpManager();
			if(smpManager!=null)
				try {
					Field f1 = SmpManager.class.getDeclaredField("smpPackages");
					f1.setAccessible(true);
					System.out.println(moremats+"|"+f1);
					@SuppressWarnings("unchecked")
					HashMap<String, SmpPackage> packages = (HashMap<String, SmpPackage>)f1.get(smpManager);
					for(SmpPackage smp:packages.values()){
						Field f2 = SmpPackage.class.getDeclaredField("customItemsList");
						f2.setAccessible(true);
						@SuppressWarnings("unchecked")
						HashMap<String, SMCustomItem> items = (HashMap<String, SMCustomItem>)f2.get(smp);
						for(SMCustomItem ci : items.values()) allMoreMaterialsItems.add(ci);
						Field f3 = SmpPackage.class.getDeclaredField("customItemsList");
						f3.setAccessible(true);
						@SuppressWarnings("unchecked")
						HashMap<String, SMCustomBlock> blocks = (HashMap<String, SMCustomBlock>)f3.get(smp);
						for(SMCustomBlock cb : blocks.values()) allMoreMaterialsBlocks.add(cb);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			log.log(Level.INFO, PRE+" Plugged into MoreMaterials!");
		}
		
		Bukkit.getPluginManager().registerEvents(new GunsPlusListener(this), this);
		getCommand("guns+").setExecutor(new CommandEx(this));
		log.log(Level.INFO, PRE + " version " + getDescription().getVersion()+ " is now enabled.");
	}

	public void init() {
		performGeneral();
		loadAdditions();
		loadAmmo();
		loadGuns();
		loadRecipes();
		Util.printCustomIDs();
		updateHUD();
		updateTripods();
	}
	
	public void loadAdditions(){
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
				
				Addition a = AdditionManager.buildAddition(this, name, addtexture);
				if(weight!=0)
					AdditionManager.editNumberValue(a, "WEIGHT", weight);
				if(changedamage!=0)
					AdditionManager.editNumberValue(a, "CHANGEDAMAGE", changedamage);
				if(damage!=0)
					AdditionManager.editNumberValue(a, "DAMAGE", (float) damage);
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
					allAdditions.remove(a);
					throw new Exception(" Could not find any values for addition "+name+"!");
				}
				
			}catch(Exception e){
				if (warnings)
					log.log(Level.WARNING, PRE + "Config Error:" + e.getMessage());
				if (debug)
					e.printStackTrace();
			}
		}
		if(generalConfig.getBoolean("loaded-additions")==true){
			log.log(Level.INFO, PRE + " -------------- Additions loaded: ---------------");
			for(int k=0;k<allAdditions.size();k++){
				log.log(Level.INFO, "- "+allAdditions.get(k).getName());
			}
		}
	}
	

	public void loadAmmo() {
		Object[] ammoArray =  ammoConfig.getKeys(false).toArray();
		for(Object ammonode:ammoArray){
			try{
				String texture = ammoConfig.getString(ammonode+".texture");
				int damage = ammoConfig.getInt(ammonode+".damage", 0);
				
				String name = ammonode.toString();
				
				if(texture == null)
					throw new Exception(" Can't find texture url for "+ammonode+"! Skipping!");
				
				Ammo a = new Ammo(this, name, texture, damage);
				allAmmo.add(a);
			} catch (Exception e) {
				if (warnings)
					log.log(Level.WARNING, PRE + "Config Error:" + e.getMessage());
				if (debug)
					e.printStackTrace();
			}
		}
		
		if(generalConfig.getBoolean("loaded-ammo")==true){
			log.log(Level.INFO, PRE + " -------------- Ammo loaded: ---------------");
			for(Ammo a : allAmmo)log.log(Level.INFO, "- "+a.getName());
		}		
	}
	
	public void loadRecipes(){
		Object[] recipeKeys = recipeConfig.getKeys(false).toArray();
		for(Object key : recipeKeys){
			try{
				CustomItem ci = null;
				if(Util.isGunsPlusItem(key.toString()))ci = Util.getGunsPlusItem(key.toString());
				else throw new Exception(PRE + " Recipe output not found: "+key+"! Skipping!");
				int amount = recipeConfig.getInt(key.toString()+".amount");
				SpoutItemStack result = new SpoutItemStack(ci, amount);
				List<ItemStack> ingredients = ConfigParser.parseItems(recipeConfig.getString(key+".ingredients"));
				if(recipeConfig.getString(key+".type").equalsIgnoreCase("shaped")){
					if(ingredients.size()!=9) throw new Exception(PRE + " Wrong number of ingredients in shaped recipe for: "+key+"! Skipping!");
					RecipeManager.addShapedRecipe(ingredients, result);
				}else if(recipeConfig.getString(key+".type").equalsIgnoreCase("shapeless")){
					RecipeManager.addShapelessRecipe(ingredients, result);
				}else if(recipeConfig.getString(key+".type").equalsIgnoreCase("furnace")){
					RecipeManager.addFurnaceRecipe(ingredients.get(0), result);
				}
			}catch (Exception e) {
				if (warnings)
					log.log(Level.WARNING, PRE + "Config Error:" + e.getMessage());
				if (debug)
					e.printStackTrace();
			}
		}
	}

	public void loadGuns() {
		Object[] gunsArray =  gunsConfig.getKeys(false).toArray();
		for(Object gunnode : gunsArray){
			try{
				String name = gunnode.toString();
				boolean hudenabled = gunsConfig.getBoolean(gunnode+".hud-enabled", true);
				float randomfactor = (float) gunsConfig.getDouble(gunnode+".accuracy.random-factor", 1.0);
				int spreadangleIN = 0;
				int spreadangleOUT = 0;
				int missingchanceIN = 0;
				int missingchanceOUT = 0;
				int critical =  gunsConfig.getInt((String) gunnode+".critical", 0);
				int range =  gunsConfig.getInt((String) gunnode+".range", 0);
				int damage =  gunsConfig.getInt((String) gunnode+".damage", 0);
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
				
				ArrayList<Effect> effects = new ArrayList<Effect>(ConfigParser.getEffects(gunnode+".effects"));
				
				ArrayList<ItemStack> ammo =  new ArrayList<ItemStack>();
				List<ItemStack> ammoStacks = ConfigParser.parseItems(gunsConfig.getString((String) gunnode+".ammo"));
				if(!(ammoStacks==null||ammoStacks.isEmpty())){
					ammo = new ArrayList<ItemStack>(ammoStacks);
				}
				
				ArrayList<Addition> adds = new ArrayList<Addition>(ConfigParser.getAdditions(gunnode+".additions"));
				
				
				if(texture==null)
						throw new Exception(" Can't find texture url for "+gunnode+"!");
				
				//CREATING GUN
				Gun g = GunManager.buildNewGun(this,name, texture);

				GunManager.editGunValue(g, "WEIGHT", weight);
				GunManager.editGunValue(g, "CHANGEDAMAGE", changedamage);
				GunManager.editGunValue(g, "DAMAGE", damage);
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
				GunManager.editEffects(g, effects);
				
				//registering shapeless recipes for additions&&building an extra gun for each addition
				for(Addition a: adds){
					List<ItemStack> listIngred = new ArrayList<ItemStack>();
						listIngred.add(new SpoutItemStack(a));
						listIngred.add(new SpoutItemStack(g));
					Gun addgun = GunManager.buildNewAdditionGun(this, GunUtils.getGunNameWithAdditions(g, a), texture, a, g);
					RecipeManager.addShapelessRecipe(listIngred, new SpoutItemStack(addgun));
				}

			}catch(Exception e){
				if (warnings)
					log.log(Level.WARNING, PRE + "Config Error:" + e.getMessage());
				if (debug)
					e.printStackTrace();
			}
		}
		if(generalConfig.getBoolean("loaded-guns")==true){
			log.log(Level.INFO, PRE + " -------------- Guns loaded: ---------------");
			for(Gun g : allGuns) log.log(Level.INFO, "- "+g.getName());
		}
	}

	public void config() {
		gunsFile = new File(getDataFolder(), "guns.yml");
		ammoFile = new File(getDataFolder(), "ammo.yml");
		recipeFile = new File(getDataFolder(), "recipes.yml");
		generalFile = new File(getDataFolder(), "general.yml");
		additionsFile = new File(getDataFolder(), "additions.yml");
		try {
			firstRun();
		} catch (Exception e) {
			e.printStackTrace();
		}
		gunsConfig = new YamlConfiguration();
		ammoConfig = new YamlConfiguration();
		recipeConfig = new YamlConfiguration();
		generalConfig = new YamlConfiguration();
		additionsConfig = new YamlConfiguration();
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
		if (!additionsFile.exists()) {
			additionsFile.getParentFile().mkdirs();
			copy(getResource("additions.yml"), additionsFile);
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
			additionsConfig.load(additionsFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updateHUD(){
		Task update = new Task(this){
			public void run(){
				Set<SpoutPlayer> ps = GunsPlus.playerHUD.keySet();
				for(SpoutPlayer sp:ps){
					GunsPlus.playerHUD.get(sp).update(sp);
				}
			}
		};
		update.startRepeating(5);
	}
	
	
	public void updateTripods(){
		Task update = new Task(this){
			public void run(){
				for(TripodData td: GunsPlus.allTripodBlocks){
					td.update();
				}
			}
		};
		update.startRepeating(20);
	}

	public void performGeneral() {
		try {
			warnings = generalConfig.getBoolean("show-warnings", true);
			debug = generalConfig.getBoolean("show-debug", false);
			notifications = generalConfig.getBoolean("show-notifications", true);
			autoreload = generalConfig.getBoolean("auto-reload", true);
			
			List<ItemStack> il = ConfigParser.parseItems(generalConfig.getString("transparent-materials"));
			for(int m=0;m<il.size();m++){
				transparentMaterials.add(il.get(m).getType());
			}

			hudenabled = generalConfig.getBoolean("hud.enabled", true);
			hudBackground = generalConfig.getString("hud.background",
					"http://dl.dropbox.com/u/44243469/GunPack/Textures/HUDBackground.png");
			hudX = generalConfig.getInt("hud.position.X", 20);
			hudY = generalConfig.getInt("hud.position.Y", 20);
			
			String z = generalConfig.getString("zoom", "right");
			zoomKey = ConfigParser.getKeyType(z);
			if(zoomKey==null) throw new Exception(" Could not parse zoom key!");
			
			String f = generalConfig.getString("fire", "left");
			fireKey = ConfigParser.getKeyType(f);
			if(fireKey==null) throw new Exception(" Could not parse fire key!");
			
			String r = generalConfig.getString("reload", "@r");
			reloadKey = ConfigParser.getKeyType(r);
			if(reloadKey==null) throw new Exception(" Could not parse reload key!");
			
			if (zoomKey.getData().equalsIgnoreCase(fireKey.getData()) || fireKey.getData().equalsIgnoreCase(reloadKey.getData())
					|| reloadKey.getData().equalsIgnoreCase(zoomKey.getData())) {
				String message = ("Zoom:" + zoomKey.getData() + " Fire:" + fireKey.getData() + " Reload:" + reloadKey.getData());
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
	}

	public void resetFields() {
		GunsPlus.allGuns = new ArrayList<Gun>();
		GunsPlus.allAmmo = new ArrayList<Ammo>();
		GunsPlus.allAdditions = new ArrayList<Addition>();
		GunsPlus.transparentMaterials = new ArrayList<Material>();
		GunsPlus.zoomTextures = new HashMap<SpoutPlayer, GenericTexture>();
	}
}
