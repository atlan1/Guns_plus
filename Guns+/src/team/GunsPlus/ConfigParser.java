package team.GunsPlus;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.inventory.SpoutItemStack;

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
    
    public static int getProjectile(String name){
    	if(name.equalsIgnoreCase("eyeofender")){
    		return 1;
    	}else if(name.equalsIgnoreCase("eyeofender")){
    		return 2;
    	}else if(name.equalsIgnoreCase("enderpearl")){
    		return 3;
    	}else if(name.equalsIgnoreCase("egg")){
    		return 4;
    	}else if(name.equalsIgnoreCase("fireball")){
    		return 5;
    	}else if(name.equalsIgnoreCase("arrow")){
    		return 6;
    	}else if(name.equalsIgnoreCase("snowball")){
    		return 7;
    	}
    	return 0;
    }
}
