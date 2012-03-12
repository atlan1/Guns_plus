package team.GunsPlus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.gui.GenericTexture;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.CustomItem;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.griefcraft.lwc.LWC;
import com.griefcraft.lwc.LWCPlugin;

import team.GunsPlus.Classes.Ammo;
import team.GunsPlus.Classes.Gun;

public class GunsPlus extends JavaPlugin {
	public static String PRE = "[Guns+]";
	
	public static LWC lwc;
	public final static Logger log = Bukkit.getLogger();
	public final GunManager gm = new GunManager(this);
	
	public static boolean warnings = true;
	public static boolean debug = false;
	public static boolean notifications = true;
	public static boolean autoreload = true;

	public KeyType zoomKey = KeyType.RIGHT;
	public static List<SpoutPlayer> inZoom = new ArrayList<SpoutPlayer>();
	public static HashMap<SpoutPlayer, Boolean> reloading = new HashMap<SpoutPlayer, Boolean>();
	public static HashMap<SpoutPlayer, Boolean> delaying = new HashMap<SpoutPlayer, Boolean>();
	public static HashMap<SpoutPlayer, GenericTexture> zoomTextures = new HashMap<SpoutPlayer, GenericTexture>();
	public static HashMap<SpoutPlayer, Integer> fireCounter = new HashMap<SpoutPlayer, Integer>();
	public KeyType fireKey = KeyType.LEFT;
	public KeyType reloadKey = KeyType.LETTER("R");
	public boolean hudenabled = false;
	public int hudX = 0;
	public int hudY = 0;
	public String hudBackground = null;

	public File gunsFile;
	public static FileConfiguration gunsConfig;
	public File ammoFile;
	public static FileConfiguration ammoConfig;
	public File recipeFile;
	public static FileConfiguration recipeConfig;
	public File generalFile;
	
	
	public static FileConfiguration generalConfig;

	public static List<Gun> allGuns = new ArrayList<Gun>();
	public static List<Ammo> allAmmo = new ArrayList<Ammo>();
	public static List<Material> transparentMaterials = new ArrayList<Material>();

	@Override
	public void onDisable() {
		log.log(Level.INFO, PRE + " version " + getDescription().getVersion()
				+ " is now disabled.");
	}

	@Override
	public void onEnable() {
		init();
		Bukkit.getPluginManager().registerEvents(new GunsPlusListener(this), this);
		if (debug)
			log.setLevel(Level.ALL);
		log.log(Level.INFO, PRE + " version " + getDescription().getVersion()
				+ " is now enabled.");
		Plugin lwcPlugin = getServer().getPluginManager().getPlugin("LWC");
		Plugin furnaceAPI = getServer().getPluginManager().getPlugin("FurnaceAPI");
		if(lwcPlugin != null) {
		    lwc = ((LWCPlugin) lwcPlugin).getLWC();
		    log.log(Level.FINE, "Plugged into LWC!");
		}
		if(furnaceAPI != null) {
			 log.log(Level.FINE, "Plugged into FurnaceAPI!");
		}
	}

	public void init() {
		config();
		performGeneral();
		loadAmmo();
		loadGuns();
		loadRecipes();
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
		updateHUD();
	}

	public void loadAmmo() {
		Object[] ammoArray =  ammoConfig.getKeys(false).toArray();
		for(int i = 0;i<ammoArray.length;i++){
			try{
				String texture = ammoConfig.getString(ammoArray[i]+".texture");
				String name = ammoArray[i].toString();
				
				if(texture == null)
					throw new Exception(" Can't find texture url for "+ammoArray[i]+"! Skipping!");
				
				Ammo a = new Ammo(this, name, texture);
				allAmmo.add(a);
			} catch (Exception e) {
				if (warnings)
					log.log(Level.WARNING, PRE + "Config Error:" + e.getMessage());
				if (debug)
					e.printStackTrace();
			}
		}
		
		if(generalConfig.getBoolean("loaded-ammo")==true){
			log.log(Level.FINE, PRE + " -------------- Ammo loaded: ---------------");
			for(int k=0;k<allAmmo.size();k++){
				log.log(Level.FINE, "- "+allAmmo.get(k).getName());
			}
		}		
	}
	
	public void loadRecipes(){
		Object[] recipeKeys = recipeConfig.getKeys(false).toArray();
		for(Object key : recipeKeys){
			try{
				CustomItem ci = null;
				if(Util.isGunsPlusItem(key.toString()))ci = Util.getGunsPlusItem(key.toString());
				else throw new Exception(PRE + " Recipe output not found: "+key+"! Skipping!");
				int amount = recipeConfig.getInt(key+".amount");
				SpoutItemStack result = new SpoutItemStack(ci, amount);
				List<ItemStack> ingredients = ConfigParser.parseItems(recipeConfig.getString(key+".ingredients"));
				if(recipeConfig.getString(key+".type").equalsIgnoreCase("shaped")){
					if(ingredients.size()!=9) throw new Exception(PRE + " Wrong number of ingredients in recipe for: "+key+"! Skipping!");
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
		for(int i = 0;i<gunsArray.length;i++){
			try{
				String name = gunsArray[i].toString();
				int accuracyIn = 0;
				int accuracyOut = 0;
				int critical =  gunsConfig.getInt((String) gunsArray[i]+".critical", 0);
				int range =  gunsConfig.getInt((String) gunsArray[i]+".range", 0);
				int damage =  gunsConfig.getInt((String) gunsArray[i]+".damage", 0);
				int  reloadTime =   gunsConfig.getInt((String) gunsArray[i]+".reloadTime", 0);
				int shotDelay=  gunsConfig.getInt((String) gunsArray[i]+".shotDelay", 0);
				int shotsBetweenReload =  gunsConfig.getInt((String) gunsArray[i]+".shotsBetweenReload", 0);
				float recoil = gunsConfig.getInt((String) gunsArray[i]+".recoil", 0);
				float knockback =  gunsConfig.getInt((String) gunsArray[i]+".knockback", 0);
				int zoomfactor =  gunsConfig.getInt((String) gunsArray[i]+".zoomfactor", 0);
				int headShotDamage =  gunsConfig.getInt((String) gunsArray[i]+".headShotDamage", 0);
				int spread =  gunsConfig.getInt((String) gunsArray[i]+".spread", 0);
				float projectileSpeed =  (float) gunsConfig.getDouble((String) gunsArray[i]+".projectile.speed", 0);
				String shotSound = gunsConfig.getString(gunsArray[i]+".shotSound");
				String reloadSound = gunsConfig.getString(gunsArray[i]+".reloadSound");
				String zoomTexture = gunsConfig.getString(gunsArray[i]+".zoomTexture");
				String texture = gunsConfig.getString(gunsArray[i]+".texture");
				String projectile = gunsConfig.getString(gunsArray[i]+".projectile.type");
				int projectiletype = 0;
				
				ArrayList<EffectType> effects = new ArrayList<EffectType>();
				
				effects = ConfigParser.getEffects(gunsArray[i]+".effects");
				
				ArrayList<ItemStack> ammo =  new ArrayList<ItemStack>(ConfigParser.parseItems(gunsConfig.getString((String) gunsArray[i]+".ammo")));
				
				if(ammo.isEmpty()){
						throw new Exception(" Can't find any valid ammo for "+gunsArray[i]);
				}
				
				String[] split = gunsConfig.getString(gunsArray[i]+".accuracy").split("->");
				if(split.length==2){
					accuracyIn=Integer.parseInt(split[1]);
					accuracyOut=Integer.parseInt(split[0]);
				}else{
					throw new Exception(" Can't find TWO accuracy values for "+gunsArray[i]+"!");
				}
				
				if(shotSound==null){
					throw new Exception(" Can't find shot sound url for "+gunsArray[i]+"!");
				}
				
				if(reloadSound==null){
						throw new Exception(" Can't find reload sound url for "+gunsArray[i]+"!");
				}
				
				if(texture==null){
						throw new Exception(" Can't find texture url for "+gunsArray[i]+"!");
				}
				
				if(zoomTexture==null){
						throw new Exception(" Can't find zoom texture url for "+gunsArray[i]+"!");
				}
				 
				if(projectile==null){
						throw new Exception(" Can't find projectile type for "+gunsArray[i]+"!");
				}
				projectiletype = ConfigParser.getProjectile(projectile);
				
				//CREATING GUN
				Gun g = gm.buildNewGun(name, texture);
				gm.editGunValue(g, "DAMAGE", damage);
				gm.editGunValue(g, "HEADSHOTDAMAGE", headShotDamage);
				gm.editGunValue(g, "ZOOMFACTOR", zoomfactor);
				gm.editGunValue(g, "CRITICAL", critical);
				gm.editGunValue(g, "RANGE", range);
				gm.editGunValue(g, "ACCURACYOUT", accuracyOut);
				gm.editGunValue(g, "ACCURACYIN", accuracyIn);
				gm.editGunValue(g, "RECOIL", recoil);
				gm.editGunValue(g, "RELOADTIME", reloadTime);
				gm.editGunValue(g, "SHOTSBETWEENRELOAD", shotsBetweenReload);
				gm.editGunValue(g, "SHOTDELAY", shotDelay);
				gm.editGunValue(g, "SPREAD", spread);
				gm.editGunValue(g, "KNOCKBACK", knockback);
				gm.editGunValue(g, "projectileSpeed", projectileSpeed);
				gm.editGunValue(g, "PROJECTILETYPE", projectiletype);
				gm.editGunResource(g, "ZOOMTEXTURE", zoomTexture);
				gm.editGunResource(g, "SHOTSOUND", shotSound);
				gm.editGunResource(g, "RELOADSOUND", reloadSound);
				for(EffectType et : effects) gm.editGunEffect(g, et);
				allGuns.add(g);
			}catch(Exception e){
				if (warnings)
					log.log(Level.WARNING, PRE + "Config Error:" + e.getMessage());
				if (debug)
					e.printStackTrace();
			}
		}
		if(generalConfig.getBoolean("loaded-guns")==true){
			log.log(Level.FINE, PRE + " -------------- Guns loaded: ---------------");
			for(int k=0;k<allGuns.size();k++){
				log.log(Level.FINE, "- "+allGuns.get(k).getName());
			}
		}
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
	
	public void updateHUD(){
		Task update = new Task(this){
			public void run(){
				Set<SpoutPlayer> ps = HUD.playerHUD.keySet();
				for(SpoutPlayer sp:ps){
					HUD.playerHUD.get(sp).update(sp);
				}
			}
		};
		update.startRepeating(5);
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
}
