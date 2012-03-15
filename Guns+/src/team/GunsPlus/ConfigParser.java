package team.GunsPlus;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.inventory.SpoutItemStack;

import team.Enum.EffectSection;
import team.Enum.EffectType;
import team.Enum.KeyType;

public class ConfigParser {

	public static List<ItemStack> parseItems(String s)
    {
        List<ItemStack> result = new LinkedList<ItemStack>();
        
        String[] items = s.split(",");
        for (String item : items)
        {
            ItemStack mat = parseItem(item.trim());
            if (mat != null)
                result.add(mat);
        }
        
        return result;
    }
    
    public static ItemStack parseItem(String item)
    {
        if (item == null || item.equals(""))
            return null;
        
        String[] parts = item.split(":");
        if (parts.length == 1)
            return singleItem(parts[0]);
        if (parts.length == 2)
            return withDurability(parts[0], parts[1]);
        
        return null;
    }
    
    private static ItemStack singleItem(String item)
    {
    	SpoutItemStack custom = null;
        Material m = getMaterial(item);
        if(m==null){
			for(int j = 0;j<GunsPlus.allAmmo.size();j++){
				if(GunsPlus.allAmmo.get(j).getName().toString().equals(item)){
					custom = new SpoutItemStack(GunsPlus.allAmmo.get(j));
				}
			}
			for(int i = 0;i<GunsPlus.allGuns.size();i++){
				GunsPlus.log.log(Level.WARNING, GunsPlus.allGuns+"");
				if(GunsPlus.allGuns.get(i).getName().toString().equals(item)){
					custom = new SpoutItemStack(GunsPlus.allGuns.get(i));
				}
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
    
    private static ItemStack withDurability(String item, String durab)
    {
    	Material m = getMaterial(item);
        if (m == null)
            return null;
        SpoutItemStack sis = new SpoutItemStack(new ItemStack(m));
        if(durab.matches("[0-9]+")){
        	sis.setDurability(Short.parseShort(durab));
        }
        
        return sis;
    }
    
    private static Material getMaterial(String item)
    {
        if (item.matches("[0-9]*"))
            return Material.getMaterial(Integer.parseInt(item));
        
        return Material.getMaterial(item.toUpperCase());
    }
    
    public static KeyType getKeyType(String string){
    	KeyType key = null;
    	if (string.startsWith("@")) {
			if (string.endsWith("_"))
				key = KeyType.HOLDLETTER(string.replace("@", ""));
			else
				key = KeyType.LETTER(string.replace("@", ""));
		} else if (string.startsWith("#")) {
			if (string.endsWith("_"))
				key = KeyType.HOLDNUMBER(string.replace("#", ""));
			else
				key = KeyType.NUMBER(string.replace("#", ""));
		} else {
			if (string.endsWith("_")) {
				switch (KeyType.getType(string)) {
				case RIGHT:
					key = KeyType.HOLDRIGHT;
					break;
				case LEFT:
					key = KeyType.HOLDLEFT;
					break;
				case RIGHTSHIFT:
					key = KeyType.HOLDRIGHTSHIFT;
					break;
				case LEFTSHIFT:
					key = KeyType.HOLDLEFTSHIFT;
					break;
				}
			} else {
				switch (KeyType.getType(string)) {
				case RIGHT:
					key = KeyType.RIGHT;
					break;
				case LEFT:
					key = KeyType.LEFT;
					break;
				case RIGHTSHIFT:
					key = KeyType.RIGHTSHIFT;
					break;
				case LEFTSHIFT:
					key = KeyType.LEFTSHIFT;
					break;
				}
			}
		}
    	return key;
    }
    
    public static ArrayList<EffectType> getEffects(String path){
    	ArrayList<EffectType> effects = new ArrayList<EffectType>();
    	Object[] keys = GunsPlus.gunsConfig.getConfigurationSection(path).getKeys(false).toArray();
    	System.out.println(""+keys.length);
    	for(Object section:keys){
    		if(!GunsPlus.gunsConfig.isConfigurationSection(path+"."+section.toString()))continue;
    		Object[] effKeys = GunsPlus.gunsConfig.getConfigurationSection(path+"."+section.toString()).getKeys(false).toArray();
    		try{
	    		for(Object effectNode : effKeys){
	    			EffectSection es = EffectSection.valueOf(section.toString().toUpperCase());
	    			if(es!=null){
	    				EffectType et = getEffect(path+"."+section+"."+effectNode,effectNode.toString(), es);
	    				System.out.println(et+"<>"+et.getSection());
		    			effects.add(et);
		    		}else throw new Exception(" Unknown effect section "+section);
		    	}
    		}catch(Exception e){
				if (GunsPlus.warnings)
					GunsPlus.log.log(Level.WARNING, GunsPlus.PRE + "Config Error:" + e.getMessage());
				if (GunsPlus.debug)
					e.printStackTrace();
			}
    		System.out.println(effects.get(effects.size()-1<0?0:effects.size()-1)+"§"+effects.get(effects.size()-1<0?0:effects.size()-1).getSection());	
    	}
    	System.out.println("------------------");
    	for(EffectType eff : effects) System.out.println(eff+"||"+eff.getSection());
		return effects;
    }
    
    private static EffectType getEffect(String path, String node, EffectSection es){
    	EffectType et = null;
    	et = EffectType.valueOf(node.toUpperCase());
    	System.out.println(""+es);
    	et.setSection(es);
    	switch(et){
    	case EXPLOSION:
    		if(es.isLocation())
    			et.addArgument("SIZE", GunsPlus.gunsConfig.getInt(path+".size"));
    		else return null;
    		break;
    	case SMOKE:
    		if(es.isLocation())
    			et.addArgument("DENSITY", GunsPlus.gunsConfig.getInt(path+".density"));
    		else return null;
    		break;
    	case FIRE:
    		if(es.isLocation())
    			et.addArgument("STRENGTH", GunsPlus.gunsConfig.getInt(path+".strength"));
    		else et.addArgument("DURATION", GunsPlus.gunsConfig.getInt(path+".duration"));
    	case SPAWN:
    		if(es.isLocation())
    			et.addArgument("ENTITY", GunsPlus.gunsConfig.getInt(path+".entity"));
    		else return null;
    		break;
    	case PUSH:
    		if(!es.isLocation())
    			et.addArgument("SPEED", GunsPlus.gunsConfig.getDouble(path+".speed"));
    		else return null;
    		break;
    	case DRAW:
    		if(!es.isLocation())
    			et.addArgument("SPEED", GunsPlus.gunsConfig.getDouble(path+".speed"));
    		else return null;
    		break;
    	case BREAK:
    		et = EffectType.BREAK;
    		if(es.isLocation())
    			et.addArgument("POTENCY", GunsPlus.gunsConfig.getInt(path+".potency"));
    		else return null;
    		break;
    	case PLACE:
    		if(es.isLocation())
    			et.addArgument("BLOCK", GunsPlus.gunsConfig.getString(path+".block"));
    		else return null;
    		break;
    	case POTION:
    		if(!es.isLocation()){
    			et.addArgument("ID", GunsPlus.gunsConfig.getInt(path+".id"));
    			et.addArgument("DURATION", GunsPlus.gunsConfig.getInt(path+".duration"));
    			et.addArgument("STRENGTH", GunsPlus.gunsConfig.getInt(path+".strength"));
    		}else return null;
    		break;
    	case LIGHTNING:
    		if(es.isLocation())
    			et = EffectType.LIGHTNING;
    		else return null;
    		break;
    	}
    	System.out.println(""+et+"\\/"+et.getSection());
		return et;
    }
}
