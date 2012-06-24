package team.GunsPlus.Manager;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.item.GenericCustomItem;

import team.ApiPlus.API.Effect.Effect;
import team.ApiPlus.Manager.EffectManager;
import team.GunsPlus.GunsPlus;
import team.GunsPlus.Block.Tripod;
import team.GunsPlus.Enum.EffectType;
import team.GunsPlus.Enum.EffectSection;
import team.GunsPlus.Enum.FireBehavior;
import team.GunsPlus.Enum.KeyType;
import team.GunsPlus.Item.Addition;
import team.GunsPlus.Item.Ammo;
import team.GunsPlus.Item.Gun;
import team.GunsPlus.Util.Util;

public class ConfigParser {

	public static List<ItemStack> parseItems(String s){
        List<ItemStack> result = new LinkedList<ItemStack>();
        
        String[] items = s.split(",");
        for (String item : items){
            ItemStack mat = parseItem(item.trim());
            if (mat != null)
                result.add(mat);
        }
        
        return result;
    }
    
    public static ItemStack parseItem(String item){
        if (item == null || item.equals(""))
            return null;
        
        String[] parts = item.split(":");
        if (parts.length == 1)
            return singleItem(parts[0]);
        if (parts.length == 2)
            return withDurability(parts[0], parts[1]);
        if(parts.length == 3)
        	return withAmount(parts[0], parts[1], parts[2]);
        
        return null;
    }
    
    private static ItemStack singleItem(String item){
    	SpoutItemStack custom = null;
        Material m = getMaterial(item);
        if(m==null){
			for(Ammo a:GunsPlus.allAmmo){
				if(a.getName().toString().equals(item)){
					custom = new SpoutItemStack(a);
				}
			}
			for(Gun g:GunsPlus.allGuns){
				if(((GenericCustomItem)g).getName().toString().equals(item)){
					custom = new SpoutItemStack((GenericCustomItem)g);
				}
			}
			for(Addition a : GunsPlus.allAdditions){
				if(a.getName().toString().equals(item)){
					custom = new SpoutItemStack(a);
				}
			}
			if(Tripod.tripodenabled&&GunsPlus.tripod.getName().equals(item)){
				custom = new SpoutItemStack(GunsPlus.tripod);
			}
        }
        if(custom==null){
        	if(m==null){
        		return null;
        	}else{
        		return new ItemStack(m);
        	}
        }else{
        	return new SpoutItemStack(custom);
        }
    }
    
    private static ItemStack withDurability(String item, String durab){
    	Material m = getMaterial(item);
        if (m == null)
            return null;
        SpoutItemStack sis = new SpoutItemStack(new ItemStack(m));
        if(durab.matches("[0-9]+")){
        	sis.setDurability(Short.parseShort(durab));
        }
        
        return sis;
    }
    
    private static ItemStack withAmount(String item, String durab, String amount){
    	Material m = getMaterial(item);
        if (m == null)
            return null;
        SpoutItemStack sis = new SpoutItemStack(new ItemStack(m, Integer.parseInt(amount)));
        if(durab.matches("[0-9]+")){
        	sis.setDurability(Short.parseShort(durab));
        }
        
        return sis;
    }
    
    private static Material getMaterial(String item){
        if (item.matches("[0-9]*"))
            return Material.getMaterial(Integer.parseInt(item));
        
        return Material.getMaterial(item.toUpperCase());
    }
    
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
    
    public static List<Effect> parseEffects(String path) throws Exception{
    	List<Effect> effects = new ArrayList<Effect>();
    	if(!ConfigLoader.gunsConfig.isConfigurationSection(path)||ConfigLoader.gunsConfig.getConfigurationSection(path).getKeys(false).isEmpty()) return effects;
    	for(String effect_: ConfigLoader.gunsConfig.getConfigurationSection(path).getKeys(false)){
    		String newpath=path+"."+effect_;
    		String type =  ConfigLoader.gunsConfig.getString(newpath+".type");
    		EffectType efftyp = EffectType.valueOf(type.toUpperCase());
    		EffectSection effsec = buildEffectTarget(newpath+".target");
    		if(Util.isAllowedInEffectSection(efftyp, effsec))
    			effects.add(buildEffect(effsec, efftyp, newpath));
    		else throw new Exception("The effect type "+efftyp.toString().toLowerCase()+" is not allowed to have the target "+effsec);
    	}
    	return effects;
    }
    
    private static EffectSection buildEffectTarget(String path) {
    	ArrayList<Map<?, ?>> args = new ArrayList<Map<?,?>>(ConfigLoader.gunsConfig.getMapList(path+".args"));
    	EffectSection effsec = EffectSection.valueOf(ConfigLoader.gunsConfig.getString(path+".type").toUpperCase());
    	if(args.isEmpty()||args==null) return effsec;
    	switch(effsec){
	    	case TARGETLOCATION:
	    		effsec.addProperty("RADIUS", searchKeyInMapList(args, "radius").get("radius"));
	    		break;
	    	case FLIGHTPATH:
	    		effsec.addProperty("LENGTH", searchKeyInMapList(args, "length").get("length"));
	    		break;
	    	case SHOOTERLOCATION:
	    		effsec.addProperty("RADIUS", searchKeyInMapList(args, "radius").get("radius"));
	    		break;
    	}
    	return effsec;
    }
    
    private static Effect buildEffect(EffectSection es, EffectType t, String path) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
    	Effect e = null;
    	ArrayList<Map<?, ?>> args = new ArrayList<Map<?,?>>(ConfigLoader.gunsConfig.getMapList(path+".args"));
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
		    		e =  EffectManager.getInstance().buildEffect(t.getEffectName(), parseItem((String)searchKeyInMapList(args, "block").get("block")).getType());
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
}
