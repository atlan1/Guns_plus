package me.atlan1.GunPack;

import me.atlan1.GunPack.Classes.*;
import me.atlan1.GunPack.Util;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.inventory.SpoutShapedRecipe;
import org.getspout.spoutapi.inventory.SpoutShapelessRecipe;
import org.getspout.spoutapi.material.CustomItem;
import org.getspout.spoutapi.material.MaterialData;

public class GunPack extends JavaPlugin{
	
	public static GunPack plugin;
	public Util util;
	public MaterialParser mparser;
	public final String PREFIX = "[GunPack]";
	
	/*
	 * ***TO-DO***
	 * ---NEWS---
	 * 1. Better fire rate...
	 * 2. Combine Accuracy and Bullet Spread
	 * 3. Ranged damages
	 * 4. Improve bullet hits, option to define bullet number
	 * 5. Placeable Guns: make guns mountable on a tripod and make tripod-gun combination
	 * 6. Explosion damage protect
	 * 7. Gun weight to specify the walk speed
	 * 8. Disable HUD for specific guns
	 * 9. Hook into certain plugins: Citizens, Chestshop, MoreMaterials
	 * 
	 * ---FIX---
	 * 1. Permissions ?
	 * 2. Sound texture precache -> seems to be fixed on latest spout update
	 * 3. HUD should overlay the zoomtexture
	 * 4. make shorter smoke trail; less density
	 * 5. Bypassing reload time if you switch weapons #
	 * 6. Reload command  #
	 * 7. Fireballs, Projectiles #
	 */
	
	//DATA STORAGE SECTION
	public HashMap<Player, Boolean> reload = new HashMap<Player, Boolean>();
	public HashMap<Player, Boolean> delay = new HashMap<Player, Boolean>();
	public HashMap<Player, Boolean> zoom = new HashMap<Player, Boolean>();
	public ArrayList<Material> globalTransparentMats = new ArrayList<Material>();
	public HashMap<Player, HashMap<String, Widget>> hudElements = new HashMap<Player, HashMap<String, Widget>>();
	public HashMap<Player, Widget> zoomTextures = new HashMap<Player, Widget>();
	public HashMap<Player, Task> hudPlayers = new HashMap<Player, Task>();
	public ArrayList<Gun> allGuns = new ArrayList<Gun>();
	public ArrayList<Ammo> allCustomAmmo = new ArrayList<Ammo>();
	
	// GENERAL SETTINGS
	public int zoomtype = 2;
	public String zoomkey = "left";
	public int shottype = 1;
	public String shotkey = "right";
	public int rtype = 4;
	public String rkey = "KEY_R";
	public boolean hudenabled = false;
	public int hudX = 0;
	public int hudY = 0;
	public String hudBackground = null;
	
	//configuration files
	 public File gunsFile;
	 public FileConfiguration gunsConfig;
	 public File ammoFile;
	 public FileConfiguration ammoConfig;
	 public File recipeFile;
	 public FileConfiguration recipeConfig;
	 public File generalFile;
	 public FileConfiguration generalConfig;
	
	 //basically for debugging
	public final Logger log = Logger.getLogger("Minecraft");
	
	
	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile= getDescription();
		log.info(PREFIX+" version " + pdfFile.getVersion()+ " is now disabled.");
	}
	
	@Override
	public void onEnable() {
		util = new Util(this);
		mparser = new MaterialParser(this);
		
		init();
		
		this.getServer().getPluginManager().registerEvents(new GunPackListener(this), this);
		log.info(PREFIX+" version " + this.getDescription().getVersion()+ " is now enabled.");
	}
	
	public void init(){
		config();
		
		loadAmmo();
		
		loadGuns();
		
		loadRecipes();
		
		performGeneral();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String CommandLabel, String[] args){
		if(CommandLabel.equalsIgnoreCase("GunPack")||CommandLabel.equalsIgnoreCase("gp")){
			if(args[0].equalsIgnoreCase("reload")){
				
				if(sender instanceof Player){
					if(((Player)sender).hasPermission("gunpack.reload")){
						sender.sendMessage(ChatColor.GREEN+PREFIX+" Configuration files reloaded!");
						config();
						this.allGuns = new ArrayList<Gun>();
						this.allCustomAmmo = new ArrayList<Ammo>();
						this.zoomTextures = new HashMap<Player, Widget>();
						this.hudElements = new HashMap<Player, HashMap<String, Widget>>();
						this.globalTransparentMats = new ArrayList<Material>();
						loadAmmo();
						loadGuns();
						loadRecipes();
						performGeneral();
					}
				}else{
					sender.sendMessage(PREFIX+" Configuration files reloaded!");
					config();
					this.allGuns = new ArrayList<Gun>();
					this.allCustomAmmo = new ArrayList<Ammo>();
					this.zoomTextures = new HashMap<Player, Widget>();
					this.hudElements = new HashMap<Player, HashMap<String, Widget>>();
					this.globalTransparentMats = new ArrayList<Material>();
					loadAmmo();
					loadGuns();
					loadRecipes();
					performGeneral();
				}
				
			}
			return true;
		}
		return false;
	}
	
	public void performGeneral()
	{
		if(generalConfig.isBoolean("hud.enabled")){
			hudenabled = generalConfig.getBoolean("hud.enabled");
		}else{
			if(generalConfig.getBoolean("show-warnings"))
				log.info(PREFIX+" Can't find HUD enabled value, disableing!");
			hudenabled = false;
		}
		if(generalConfig.isString("hud.background")){
			hudBackground = generalConfig.getString("hud.background");
		}else{
			if(generalConfig.getBoolean("show-warnings"))
				log.info(PREFIX+" Can't find HUD background url!");
		}
		if(generalConfig.isInt("hud.position.X")&&generalConfig.isInt("hud.position.Y")){
			hudY = generalConfig.getInt("hud.position.Y");
			hudX = generalConfig.getInt("hud.position.X");
		}else{
			if(generalConfig.getBoolean("show-warnings"))
				log.info(PREFIX+" Can't find x/y value for HUD, disableing!");
			hudenabled = false;
		}
		
		if(generalConfig.getBoolean("id-info-guns")==true){
			log.info(PREFIX+"------------  ID's of the guns: -----------------");
			for(int i=0;i<allGuns.size();i++){
				log.info("ID of "+allGuns.get(i).getName()+": "+Material.FLINT.getId()+":"+new SpoutItemStack(allGuns.get(i)).getDurability());
			}
		}
		
		
		if(generalConfig.getBoolean("id-info-ammo")==true){
			log.info(PREFIX+"------------  ID's of the ammo: -----------------");
			for(int i=0;i<allCustomAmmo.size();i++){
				log.info("ID of "+allCustomAmmo.get(i).getName()+": "+Material.FLINT.getId()+":"+new SpoutItemStack(allCustomAmmo.get(i)).getDurability());
			}
		}
		String z = generalConfig.getString("zoom");
		String s = generalConfig.getString("shot");
		String r = generalConfig.getString("reload");
		
		
		if(z.equalsIgnoreCase("right")){
			zoomtype=0;
			zoomkey="right";
		}
		else if(z.equalsIgnoreCase("left")){
			zoomtype=1;
			zoomkey="left";
		}
		else if(z.equalsIgnoreCase("right+shift")){
			zoomtype=2;
			zoomkey="right+shift";
		}
		else if(z.equalsIgnoreCase("left+shift")){
			zoomtype=3;
			zoomkey="left+shift";
		}
		else if(!(z.equalsIgnoreCase("right")&&z.equalsIgnoreCase("left")&&z.equalsIgnoreCase("left+shift")&&z.equalsIgnoreCase("right+shift"))&&z.matches("[a-zA-Z0-9]_")){
			if(z.endsWith("_")){
				zoomtype=5;
			}else{
				zoomtype=4;
			}
			zoomkey="KEY_"+z.substring(0, z.length()-1);
		}else{
			if(generalConfig.getBoolean("show-warnings"))
			log.info(PREFIX+" Your value for zooming is not available. Setting to default...");
		}
		
		if(s.equalsIgnoreCase("right")){
			shottype=0;
			shotkey="right";
		}
		else if(s.equalsIgnoreCase("left")){
			shottype=1;
			shotkey="left";
		}
		else if(s.equalsIgnoreCase("right+shift")){
			shottype=2;
			shotkey="right+shift";
		}
		else if(s.equalsIgnoreCase("left+shift")){
			shottype=3;
			shotkey="left+shift";
		}
		else if(!(s.equalsIgnoreCase("right")&&s.equalsIgnoreCase("left")&&s.equalsIgnoreCase("left+shift")&&s.equalsIgnoreCase("right+shift"))&&s.matches("[a-zA-Z0-9]")){
			shottype=4;
			shotkey="KEY_"+s;
		}else{
			if(generalConfig.getBoolean("show-warnings"))
			log.info(PREFIX+" Your value for shooting is not available. Setting to default...");
		}
		
		if(r.equalsIgnoreCase("right")){
			rtype=0;
			rkey="right";
		}
		else if(r.equalsIgnoreCase("left")){
			rtype=1;
			rkey="left";
		}
		else if(r.equalsIgnoreCase("right+shift")){
			rtype=2;
			rkey="right+shift";
		}
		else if(r.equalsIgnoreCase("left+shift")){
			rtype=3;
			rkey="left+shift";
		}
		else if(!(r.equalsIgnoreCase("right")&&r.equalsIgnoreCase("left")&&r.equalsIgnoreCase("left+shift")&&r.equalsIgnoreCase("right+shift"))&&r.matches("[a-zA-Z0-9]")){
			rtype=4;
			rkey="KEY_"+r;
		}else{
			if(generalConfig.getBoolean("show-warnings"))
			log.info(PREFIX+" Your value for reloading is not available. Setting to default...");
		}
		
		if(zoomkey.equals(shotkey)||shotkey.equals(rkey)||rkey.equals(zoomkey)){
			if(generalConfig.getBoolean("show-warnings"))
			log.info(PREFIX+" You can't use the same kombination for zooming and shooting! Setting to default...");
			shottype=0;
			shotkey="right";
			rtype=4;
			rkey="KEY_R";
			zoomtype=1;
			zoomkey="left";
		}
		
		List<ItemStack> il = MaterialParser.parseItems(generalConfig.getString("transparent-materials"));
		for(int m=0;m<il.size();m++){
			this.globalTransparentMats.add(il.get(m).getType());
		}
		
	}
	
	
	public Effect getEffects(String path, int type){
		ConfigurationSection cs = gunsConfig.getConfigurationSection(path);
		Object[] effectsArray = cs.getKeys(false).toArray();
		Effect e = new Effect(type);
		for(Object o : effectsArray){
			if(o.toString().toLowerCase().equals("explosion")&&(type==0||type==2)){
				e.addValue(o.toString().toLowerCase(), 0, gunsConfig.getInt(path+"."+(String)o+".size"));
			}else if(o.toString().toLowerCase().equals("lightning")&&(type==0||type==2)){
				e.addEffect(o.toString().toLowerCase());
			}else if(o.toString().toLowerCase().equals("smoke")&&(type==0||type==2)){
				e.addValue(o.toString().toLowerCase(), 0, gunsConfig.getInt(path+"."+(String)o+".density"));
			}else if(o.toString().toLowerCase().equals("spawn")&&(type==0||type==2)){
				e.addValue(o.toString().toLowerCase(), 0, gunsConfig.getString(path+"."+(String)o+".mob"));
			}else if(o.toString().toLowerCase().equals("push")&&type==1){
				e.addValue(o.toString().toLowerCase(), 0, gunsConfig.getDouble(path+"."+(String)o+".speed"));
			}else if(o.toString().toLowerCase().equals("fire")){
				if(type==1){
					e.addValue(o.toString().toLowerCase(), 1, gunsConfig.getInt(path+"."+(String)o+".duration"));
				}else{
					e.addValue(o.toString().toLowerCase(), 0, gunsConfig.getInt(path+"."+(String)o+".strength"));
				}
			}else if(o.toString().toLowerCase().equals("draw")&&type==1){
				e.addValue(o.toString().toLowerCase(), 0, gunsConfig.getDouble(path+"."+(String)o+".speed"));
			}else if(o.toString().toLowerCase().equals("place")&&(type==0||type==2)){
				e.addValue(o.toString().toLowerCase(), 0, MaterialParser.parseItem(gunsConfig.getString(path+"."+(String)o+".block")).getTypeId());
			}else if(o.toString().toLowerCase().equals("break")&&(type==0||type==2)){
				e.addValue(o.toString().toLowerCase(), 0, gunsConfig.getInt(path+"."+(String)o+".potency"));
			}else if(o.toString().toLowerCase().startsWith("potion")&&type==1){
				String[] p = o.toString().split("_");
				if(p.length<2){
					if(generalConfig.getBoolean("show-warnings"))
					log.info(PREFIX+" Can't find potion effect id. Skipping effect!");
					continue;
				}
				e.addValue(o.toString().toLowerCase(), 0, Integer.parseInt(p[1]));
				e.addValue(o.toString().toLowerCase(), 1, gunsConfig.getInt(path+"."+(String)o+".strength"));
				e.addValue(o.toString().toLowerCase(), 2, gunsConfig.getInt(path+"."+(String)o+".duration"));
			}else{
				if(generalConfig.getBoolean("show-warnings"))
				log.info(PREFIX+" Effect "+o.toString()+" does not exist!");
			}
		}
		return e;
	}
	
	//loading gun items
	public void loadGuns(){
		Set<String> gunsKeys = gunsConfig.getKeys(false);
		Object[] gunsArray =  gunsKeys.toArray();
		for(int i = 0;i<gunsArray.length;i++){
			// DEFAULT_VALUES
			int accIn=0;
			int accOut=0;
			int critical = 0;
			int range = 100;
			int damage = 10;
			int reloadTime = 0;
			int shotDelay=0;
			int SBR = 10;
			int recoil = 0;
			int knockback = 0;
			int zoomfactor = 0;
			int HSD = 10;
			int spread = 0;
			double projectileSpeed = 0;
			String shotSound = null;
			String reloadSound = null;
			String zoomTexture = null;
			String texture = null;
			String projectile = null;
			ArrayList<Effect> effects = new ArrayList<Effect>();
			ArrayList<ItemStack> ammo = new ArrayList<ItemStack>();
			
			
			
			// GETTING EFFECTS
			ConfigurationSection cs = gunsConfig.getConfigurationSection(gunsArray[i]+".effects");
			if(cs==null){
				if(generalConfig.getBoolean("show-warnings"))
				log.info(PREFIX+" Can't find any effects for the "+gunsArray[i]+"!");
			}else{
				Object[] effectsArray = cs.getKeys(false).toArray();
				
				for(Object o : effectsArray){
					Effect e = null;
					if(o.toString().equalsIgnoreCase("targetBlock")){
						e = getEffects(gunsArray[i]+".effects.targetBlock", 0);
					}else if(o.toString().equalsIgnoreCase("targetEntity")){
						e = getEffects(gunsArray[i]+".effects.targetEntity", 1);
					}else if(o.toString().equalsIgnoreCase("flightPath")){
						e = getEffects(gunsArray[i]+".effects.flightPath", 2);
					}
					if(e==null)
					{
						if(generalConfig.getBoolean("show-warnings"))
						log.info(PREFIX+" Could not load effect in "+gunsArray[i]+"! Skipping!");
						continue;
					}
					effects.add(e);
				}
			}
			
			//GETTING AMMO
			if(gunsConfig.getString((String) gunsArray[i]+".ammo")==null||!gunsConfig.isString((String) gunsArray[i]+".ammo")){
				if(generalConfig.getBoolean("show-warnings"))
				log.info(PREFIX+" Can't find any assigned ammo for "+gunsArray[i]);
			}else{
				ammo = new ArrayList<ItemStack>(MaterialParser.parseItems(gunsConfig.getString((String) gunsArray[i]+".ammo")));
				if(ammo.isEmpty()){
					if(generalConfig.getBoolean("show-warnings"))
					log.info(PREFIX+" Can't find any valid ammo for "+gunsArray[i]);
				}
			}
			
			//GETTING ACCURACY
			if(gunsConfig.getString((String) gunsArray[i]+".accuracy")==null||!gunsConfig.isString((String) gunsArray[i]+".accuracy")){
				if(generalConfig.getBoolean("show-warnings"))
				log.info(PREFIX+" Can't find accuracy values for "+gunsArray[i]+"!");
			}else{
				String[] split = gunsConfig.getString(gunsArray[i]+".accuracy").split("->");
				if(split.length==2){
					accIn=Integer.parseInt(split[1]);
					accOut=Integer.parseInt(split[0]);
				}else{
					if(generalConfig.getBoolean("show-warnings"))
					log.info(PREFIX+" Can't find TWO accuracy values for "+gunsArray[i]+"!");
				}
			}
			
			//GETTING DAMAGE
			if(!gunsConfig.isInt((String) gunsArray[i]+".damage")){
				if(generalConfig.getBoolean("show-warnings"))
				log.info(PREFIX+" Can't find damage value for "+gunsArray[i]+"! Skipping!");
				continue;
			}else{
				damage = gunsConfig.getInt((String) gunsArray[i]+".damage");
			}
			
			//GETTING PROJECTILESPEED
			if(!gunsConfig.isDouble((String) gunsArray[i]+".projectile.speed")){
				if(generalConfig.getBoolean("show-warnings"))
				log.info(PREFIX+" Can't find projectile speed value for "+gunsArray[i]+"!");
			}else{
				projectileSpeed = gunsConfig.getDouble((String) gunsArray[i]+".projectile.speed");
			}
			
			//GETTING CRITICAL
			if(!gunsConfig.isInt((String) gunsArray[i]+".critical")){
				if(generalConfig.getBoolean("show-warnings"))
				log.info(PREFIX+" Can't find critical value for "+gunsArray[i]+"!");
			}else{
				critical = gunsConfig.getInt((String) gunsArray[i]+".critical");
			}
			
			//GETTING RANGE
			if(!gunsConfig.isInt((String) gunsArray[i]+".range")){
				if(generalConfig.getBoolean("show-warnings"))
				log.info(PREFIX+" Can't find range value for "+gunsArray[i]+"! Skipping!");
				continue;
			}else{
				range = gunsConfig.getInt((String) gunsArray[i]+".range");
			}
			
			//GETTING RELOADTIME
			if(!gunsConfig.isInt((String) gunsArray[i]+".reloadTime")){
				if(generalConfig.getBoolean("show-warnings"))
				log.info(PREFIX+" Can't find reload time value for "+gunsArray[i]+"! Skipping!");
				continue;
			}else{
				reloadTime = gunsConfig.getInt((String) gunsArray[i]+".reloadTime");
			}
			
			//GETTING SHOTDELAY
			if(!gunsConfig.isInt((String) gunsArray[i]+".shotDelay")){
				if(generalConfig.getBoolean("show-warnings"))
				log.info(PREFIX+" Can't find shot delay value for "+gunsArray[i]+"! Skipping!");
				continue;
			}else{
				shotDelay = gunsConfig.getInt((String) gunsArray[i]+".shotDelay");
			}
			
			//GETTING SHOTSBETWEENRELOAD
			if(!gunsConfig.isInt((String) gunsArray[i]+".shotsBetweenReload")){
				if(generalConfig.getBoolean("show-warnings"))
				log.info(PREFIX+" Can't find shots between reload value for "+gunsArray[i]+"! Skipping!");
				continue;
			}else{
				SBR = gunsConfig.getInt((String) gunsArray[i]+".shotsBetweenReload");
			}
			
			//GETTING RECOIL
			if(!gunsConfig.isInt((String) gunsArray[i]+".recoil")){
				if(generalConfig.getBoolean("show-warnings"))
				log.info(PREFIX+" Can't find recoil value for "+gunsArray[i]+"! Skipping!");
			}else{
				recoil = gunsConfig.getInt((String) gunsArray[i]+".recoil");
			}
			
			//GETTING KNOCKBACK
			if(!gunsConfig.isInt((String) gunsArray[i]+".knockback")){
				if(generalConfig.getBoolean("show-warnings"))
				log.info(PREFIX+" Can't find knockback value for "+gunsArray[i]+"! Skipping!");
			}else{
				knockback = gunsConfig.getInt((String) gunsArray[i]+".knockback");
			}
			
			//GETTING ZOOMFACTOR
			if(!gunsConfig.isInt((String) gunsArray[i]+".zoomfactor")){
				if(generalConfig.getBoolean("show-warnings"))
				log.info(PREFIX+" Can't find zoomfactor value for "+gunsArray[i]+"! Skipping!");
			}else{
				zoomfactor = gunsConfig.getInt((String) gunsArray[i]+".zoomfactor");
			}
			
			//GETTING HEADSHOTDAMAGE
			if(!gunsConfig.isInt((String) gunsArray[i]+".headShotDamage")){
				if(generalConfig.getBoolean("show-warnings"))
				log.info(PREFIX+" Can't find recoil value for "+gunsArray[i]+"!");
			}else{
				HSD = gunsConfig.getInt((String) gunsArray[i]+".headShotDamage");
			}
			
			//GETTING SPREAD
			if(!gunsConfig.isInt((String) gunsArray[i]+".spread")){
				if(generalConfig.getBoolean("show-warnings"))
				log.info(PREFIX+" Can't find spread value for "+gunsArray[i]+"!");
			}else{
				spread = gunsConfig.getInt((String) gunsArray[i]+".spread");
			}
			
			//GETTING SHOTSOUND
			if(gunsConfig.getString((String)gunsArray[i]+".shotSound")==null||!gunsConfig.isString((String) gunsArray[i]+".shotSound")){
				if(generalConfig.getBoolean("show-warnings"))
				log.info(PREFIX+" Can't find shot sound url for "+gunsArray[i]+"!");
			}else{
				shotSound = gunsConfig.getString((String) gunsArray[i]+".shotSound");
			}
			
			//GETTING RELOADSOUND
			if(gunsConfig.getString((String)gunsArray[i]+".reloadSound")==null||!gunsConfig.isString((String) gunsArray[i]+".reloadSound")){
				if(generalConfig.getBoolean("show-warnings"))
				log.info(PREFIX+" Can't find reload sound url for "+gunsArray[i]+"!");
			}else{
				reloadSound = gunsConfig.getString((String) gunsArray[i]+".reloadSound");
			}
			
			//GETTING TEXTURE
			if(gunsConfig.getString((String)gunsArray[i]+".texture")==null||!gunsConfig.isString((String) gunsArray[i]+".texture")){
				if(generalConfig.getBoolean("show-warnings"))
				log.info(PREFIX+" Can't find texture url for "+gunsArray[i]+"! Skipping!");
				continue;
			}else{
				texture = gunsConfig.getString((String) gunsArray[i]+".texture");
			}
			
			//GETTING ZOOMTEXTURE
			if(gunsConfig.getString((String)gunsArray[i]+".zoomTexture")==null||!gunsConfig.isString((String) gunsArray[i]+".zoomTexture")){
				if(generalConfig.getBoolean("show-warnings"))
				log.info(PREFIX+" Can't find zoom texture url for "+gunsArray[i]+"!");
			}else{
				zoomTexture = gunsConfig.getString((String) gunsArray[i]+".zoomTexture");
			}
			 
			//GETTING PROJECTILE
			if(gunsConfig.getString((String)gunsArray[i]+".projectile.type")==null||!gunsConfig.isString((String) gunsArray[i]+".projectile.type")){
				if(generalConfig.getBoolean("show-warnings"))
				log.info(PREFIX+" Can't find projectile type for "+gunsArray[i]+"!");
			}else{
				projectile = gunsConfig.getString((String) gunsArray[i]+".projectile.type");
			}
			
			Gun g = new Gun(this,
							damage,
							range,
							reloadTime,
							shotDelay,
							SBR,
							recoil,
							knockback,
							ammo,
							zoomfactor,
							shotSound,
							reloadSound,
							HSD,
							spread,
							accIn,
							accOut,
							critical,
							zoomTexture,
							projectile,
							projectileSpeed,
							effects,
							this,
							texture,
							(String)gunsArray[i]);
			allGuns.add(g);
		}
		if(generalConfig.getBoolean("loaded-guns")==true){
			log.info("["+this.getDescription().getName()+"] -------------- Guns loaded: ---------------");
			for(int k=0;k<allGuns.size();k++){
				System.out.print("- "+allGuns.get(k).getName());
			}
		}
	}
	
	//loading ammo items
	public void loadAmmo(){
		Set<String> ammoKeys = ammoConfig.getKeys(false);
		Object[] ammoArray =  ammoKeys.toArray();
		for(int i = 0;i<ammoArray.length;i++){
			if((String) ammoArray[i]+".texture"==null||!ammoConfig.isString((String) ammoArray[i]+".texture")){
				log.info(PREFIX+" Can't find texture url for "+ammoArray[i]+"! Skipping!");
				continue;
			}
			Ammo a = new Ammo(this, ammoArray[i].toString(), ammoConfig.getString((String) ammoArray[i]+".texture"));
			allCustomAmmo.add(a);
		}
		if(generalConfig.getBoolean("loaded-ammo")==true){
			log.info(PREFIX+"-------------- Ammo loaded: ---------------");
			for(int k=0;k<allCustomAmmo.size();k++){
				System.out.print("- "+allCustomAmmo.get(k).getName());
			}
		}
	}
	
	
	//loading recipes
	public void loadRecipes(){
		Set<String> recipeKeys = recipeConfig.getKeys(false);
		Object[] recipeArray =  recipeKeys.toArray();
		for(int i = 0;i<recipeArray.length;i++){
			CustomItem r = null;
			for(int j = 0;j<allGuns.size();j++){
				if(allGuns.get(j).getName().equals(recipeArray[i].toString()))
					r=allGuns.get(j);
			}
			for(int k = 0;k<allCustomAmmo.size();k++){
				if(allCustomAmmo.get(k).getName().equals(recipeArray[i].toString()))
					r=allCustomAmmo.get(k);
			}
			if(r==null){
				log.info(PREFIX+" Recipe output not found: "+recipeArray[i]+"! Skipping!");
				continue;
			}
			if(recipeConfig.getInt(recipeArray[i]+".amount")<0){
				log.info(PREFIX+" Amount of "+recipeArray[i]+"'s can't be smaller than 0! Skipping!");
			}
			SpoutItemStack result = new SpoutItemStack(r, recipeConfig.getInt(recipeArray[i]+".amount"));
			if(recipeConfig.getString(recipeArray[i]+".type").equalsIgnoreCase("shaped")){
				SpoutShapedRecipe ssr = new SpoutShapedRecipe(result);
				
				char[] chars = {'a','b','c','d','e','f','g','h','i'};
				int charcounter = 0;
				String s =recipeConfig.getString(recipeArray[i]+".ingredients");
				
				List<ItemStack> lis = MaterialParser.parseItems(s);
				if(lis.size()<9){
					log.info(PREFIX+" Too low ingredients for recipe of "+recipeArray[i]+"! Skipping!");
					continue;
				}
				
				for(int l=0;l<9;l++){
					if(lis.get(l).getTypeId()==0)chars[l]=' ';
				}
				String r1 = chars[0]+""+chars[1]+""+chars[2];
				String r2 = chars[3]+""+chars[4]+""+chars[5];
				String r3 = chars[6]+""+chars[7]+""+chars[8];
				ssr.shape(r1, r2, r3);
			
				for(int l=0;l<lis.size();l++){
					if(chars[charcounter]==' '){
						charcounter++;
						continue;
					}
					ssr.setIngredient(chars[charcounter],MaterialData.getMaterial(lis.get(l).getTypeId(),(short) lis.get(l).getDurability())); 
					charcounter++;
				}
				SpoutManager.getMaterialManager().registerSpoutRecipe(ssr);
				
			}else if(recipeConfig.getString(recipeArray[i]+".type").equalsIgnoreCase("shapeless")){
				SpoutShapelessRecipe ssl = new SpoutShapelessRecipe(result);
				String s =recipeConfig.getString(recipeArray[i]+".ingredients");
				List<ItemStack> lis = MaterialParser.parseItems(s);
				for(int l=0;l<lis.size();l++){
					ssl.addIngredient(MaterialData.getMaterial(lis.get(l).getTypeId(),(short) lis.get(l).getDurability())); 
				}
				SpoutManager.getMaterialManager().registerSpoutRecipe(ssl);
			}
		}
	}
	
	
	//loading the config....
	 public void config(){
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
	    if(!gunsFile.exists()){
	    	gunsFile.getParentFile().mkdirs();
	        copy(getResource("guns.yml"), gunsFile);
	    }
	    if(!ammoFile.exists()){
	    	ammoFile.getParentFile().mkdirs();
	        copy(getResource("ammo.yml"), ammoFile);
	    }
	    if(!recipeFile.exists()){
	    	recipeFile.getParentFile().mkdirs();
	        copy(getResource("recipes.yml"), recipeFile);
	    }
	    if(!generalFile.exists()){
	    	generalFile.getParentFile().mkdirs();
	        copy(getResource("general.yml"), generalFile);
	    }
	}
	private void copy(InputStream in, File file) {
	    try {
	        OutputStream out = new FileOutputStream(file);
	        byte[] buf = new byte[1024];
	        int len;
	        while((len=in.read(buf))>0){
	            out.write(buf,0,len);
	        }
	        out.close();
	        in.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*public void saveYamls() {
	    try {
	        gunsConfig.save(gunsFile);
	        ammoConfig.save(ammoFile);
	        recipeConfig.save(recipeFile);
	        generalConfig.save(generalFile);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}*/
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
}