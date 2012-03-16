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
    
    public static List<EffectType> getEffects(String path){
    	List<EffectType> effects = new ArrayList<EffectType>();
    	for(String effectsection: GunsPlus.gunsConfig.getConfigurationSection(path).getKeys(false)){
    		EffectSection effsec = EffectSection.valueOf(effectsection.toUpperCase());
    		for(String effecttype : GunsPlus.gunsConfig.getConfigurationSection(path+"."+effectsection).getKeys(false)){
    			EffectType efftyp = EffectType.valueOf(effecttype.toUpperCase());
    			efftyp.setSection(effsec);
    			if(buildEffect(efftyp, path+"."+effectsection+"."+effecttype))
    				effects.add(efftyp);
    		}
    	}
    	return effects;
    }
    
    private static boolean buildEffect(EffectType efftyp , String path){
    	if(!Util.isAllowedInEffectSection(efftyp, efftyp.getSection())) return false;
    	switch(efftyp){
	    	case EXPLOSION:
	    		efftyp.addArgument("SIZE", GunsPlus.gunsConfig.getInt(path+".size"));
	    		break;
	    	case LIGHTNING:
	    		break;
	    	case SMOKE:
	    		efftyp.addArgument("DENSITY", GunsPlus.gunsConfig.getInt(path+".density"));
	    		break;
	    	case FIRE:
	    		if(efftyp.getSection().equals(EffectSection.SHOOTER)||efftyp.getSection().equals(EffectSection.TARGETENTITY))
	    			efftyp.addArgument("DURATION", GunsPlus.gunsConfig.getInt(path+".duration"));
	    		else
	    			efftyp.addArgument("STRENGTH", GunsPlus.gunsConfig.getInt(path+".strength"));
	    		break;
	    	case PUSH:
	    		efftyp.addArgument("SPEED", GunsPlus.gunsConfig.getInt(path+".speed"));
	    		break;
	    	case DRAW:
	    		efftyp.addArgument("SPEED", GunsPlus.gunsConfig.getInt(path+".speed"));
	    		break;
	    	case POTION:
	    		efftyp.addArgument("ID", GunsPlus.gunsConfig.getInt(path+".id"));
	    		efftyp.addArgument("DURATION", GunsPlus.gunsConfig.getInt(path+".duration"));
	    		efftyp.addArgument("STRENGTH", GunsPlus.gunsConfig.getInt(path+".strength"));
	    		break;
	    	case SPAWN:
	    		efftyp.addArgument("ENTITY", GunsPlus.gunsConfig.getInt(path+".entity"));
	    		break;
	    	case PLACE:
	    		efftyp.addArgument("BLOCK", GunsPlus.gunsConfig.getInt(path+".block"));
	    		break;
	    	case BREAK:
	    		efftyp.addArgument("POTENCY", GunsPlus.gunsConfig.getInt(path+".potency"));
	    		break;
    	}
    	return true;
    }
}
