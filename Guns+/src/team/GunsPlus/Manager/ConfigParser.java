package team.GunsPlus.Manager;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import team.ApiPlus.API.Effect.Effect;
import team.ApiPlus.API.Effect.EffectType;
import team.ApiPlus.Manager.EffectManager;
import team.ApiPlus.Util.ConfigUtil;
import team.GunsPlus.GunsPlus;
import team.GunsPlus.Effect.EffectTargetImpl;
import team.GunsPlus.Effect.EffectTargetType;
import team.GunsPlus.Enum.Animal;
import team.GunsPlus.Enum.FireBehavior;
import team.GunsPlus.Enum.KeyType;
import team.GunsPlus.Enum.Monster;
import team.GunsPlus.Enum.PlayerTarget;
import team.GunsPlus.Enum.Target;
import team.GunsPlus.Enum.TargetType;
import team.GunsPlus.Item.Addition;
import team.GunsPlus.Util.Util;

public class ConfigParser {
    
    public static KeyType parseKeyType(String string) throws Exception{
    	boolean hold = false;
    	String keyname = "";
    	if(string.endsWith("_")){
    		hold = true;
    		string = string.replace("_", "");
    	}
    	if(string.matches("[0-9a-zA-Z]*")){
    		keyname = string;
    	}else throw new Exception(" Key contains invalid characters: "+ string);
    	return new KeyType(keyname, hold);
    }
    
    public static FireBehavior parseFireBehavoir(String node){
    	try{
    		return FireBehavior.valueOf(ConfigLoader.gunsConfig.getString(node, "single").toUpperCase());
    	}catch(Exception e){
    		Util.warn(e.getMessage());
    		Util.debug(e);
    	}
    	return null;
    }
    
    public static List<Effect> parseEffects(FileConfiguration config, String path) throws Exception{
    	List<Effect> effects = new ArrayList<Effect>();
    	if(!config.isConfigurationSection(path)||config.getConfigurationSection(path).getKeys(false).isEmpty()) return effects;
    	for(String effect_: config.getConfigurationSection(path).getKeys(false)){
    		String newpath=path+"."+effect_;
    		String type =  config.getString(newpath+".type");
    		EffectType efftyp = EffectType.valueOf(type.toUpperCase());
    		EffectTargetImpl efftar = buildEffectTarget(config, newpath+".target");
    		if(Util.isAllowedWithTarget(efftyp, efftar))
    			effects.add(buildEffect(config, efftar, efftyp, newpath));
    		else throw new Exception("The effect type "+efftyp.toString().toLowerCase()+" is not allowed to have the target "+efftar);
    	}
    	return effects;
    }
    
    private static EffectTargetImpl buildEffectTarget(FileConfiguration f, String path) {
    	ArrayList<Map<?, ?>> args = new ArrayList<Map<?,?>>(f.getMapList(path+".args"));
    	EffectTargetType ett = EffectTargetType.valueOf(f.getString(path+".type").toUpperCase());
    	EffectTargetImpl efftar = new EffectTargetImpl(ett);
    	if(args.isEmpty()||args==null) return efftar;
    	switch(efftar.getType()){
	    	case TARGETLOCATION:
	    		efftar.addProperty("RADIUS", searchKeyInMapList(args, "radius").get("radius"));
	    		break;
	    	case FLIGHTPATH:
	    		efftar.addProperty("LENGTH", searchKeyInMapList(args, "length").get("length"));
	    		break;
	    	case SHOOTERLOCATION:
	    		efftar.addProperty("RADIUS", searchKeyInMapList(args, "radius").get("radius"));
	    		break;
    	}
    	return efftar;
    }
    
    private static Effect buildEffect(FileConfiguration config, EffectTargetImpl es, EffectType t, String path) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
    	Effect e = null;
    	ArrayList<Map<?, ?>> args = new ArrayList<Map<?,?>>(config.getMapList(path+".args"));
    		switch(t){
		    	case EXPLOSION:
		    		e =  EffectManager.getInstance().buildEffect(t.getEffectName(), searchKeyInMapList(args, "size").get("size")); 
		    		break;
		    	case LIGHTNING:
		    		e =  EffectManager.getInstance().buildEffect(t.getEffectName()); 
		    		break;
		    	case MOVE:
		    		e =  EffectManager.getInstance().buildEffect(t.getEffectName(), searchKeyInMapList(args, "speed").get("speed"), searchKeyInMapList(args, "direction").get("direction"));
		    		break;
		    	case POTION:
		    		e =  EffectManager.getInstance().buildEffect(t.getEffectName(), searchKeyInMapList(args, "id").get("id"), searchKeyInMapList(args, "duration").get("duration"), searchKeyInMapList(args, "strength").get("strength") );
		    		break;
		    	case SPAWN:
		    		e =  EffectManager.getInstance().buildEffect(t.getEffectName(), searchKeyInMapList(args, "entity").get("entity"));
		    		break;
		    	case PLACE:
		    		e =  EffectManager.getInstance().buildEffect(t.getEffectName(), ConfigUtil.parseItem((String)searchKeyInMapList(args, "block").get("block")).getType());
		    		break;
		    	case BREAK:
		    		e =  EffectManager.getInstance().buildEffect(t.getEffectName(), searchKeyInMapList(args, "potency").get("potency"));
		    		break;
		    	case PARTICLE:
		    		e =  EffectManager.getInstance().buildEffect(t.getEffectName(), searchKeyInMapList(args, "type").get("type"), searchKeyInMapList(args, "amount").get("amount"), searchKeyInMapList(args, "gravity").get("gravity"), searchKeyInMapList(args, "max-age").get("max-age"), searchKeyInMapList(args, "scale").get("scale"));
		    		break;
		    	case BURN:
		    		e =  EffectManager.getInstance().buildEffect(t.getEffectName(), searchKeyInMapList(args, "duration").get("duration"));
		    		break;
    	}
    		e.setEffectTarget(es);
    	return e;
    }
    
    private static Map<?, ?> searchKeyInMapList(List<Map<?,?>> maplist, String key){
    	for(Map<?, ?> map : maplist){
    		if(map.containsKey(key)){
    			return map;
    		}
    	}
    	return null;
    }
    
    public static ArrayList<Addition> parseAdditions(String path){
    	ArrayList<Addition> adds = new ArrayList<Addition>();
    	String string = ConfigLoader.gunsConfig.getString(path);
    	if(string!=null){
	    	String[] split = string.split(",");
	    	for(String splitString : split){
	    		for(Addition a : GunsPlus.allAdditions){
		    		if(a.getName().equalsIgnoreCase(splitString.trim())){
		    			adds.add(a);
		    		}
		    	}
	    	}
    	}
    	return adds;
    }
    
	
	public static Target parseTarget(String type, String subtype) {
		Target t = null;
		if(type.equals(TargetType.Monster.name())){
			t = Monster.valueOf(subtype.toUpperCase());
		}else if(type.equals(TargetType.Animal.name())){
			t =  Animal.valueOf(subtype.toUpperCase());
		}else if(type.equals(TargetType.Player.name())){
			t = new  PlayerTarget(subtype);
		}
		
		return t;
	}
}
