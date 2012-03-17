package team.GunsPlus.Manager;

import java.util.List;

import me.znickq.furnaceapi.SpoutFurnaceRecipe;
import me.znickq.furnaceapi.SpoutFurnaceRecipes;

import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.inventory.SpoutShapedRecipe;
import org.getspout.spoutapi.inventory.SpoutShapelessRecipe;
import org.getspout.spoutapi.material.MaterialData;

public class RecipeManager {
	public static void addShapedRecipe(List<ItemStack> ingredients, ItemStack result){
		char[] name = {'a','b','c',
						'd','e','f',
						'g','h','i'};
		int i = 0;
		SpoutShapedRecipe x = new SpoutShapedRecipe(result);
		x.shape("abc","def","ghi");
		for(ItemStack item : ingredients) {
			if(item.getTypeId()==0){
				i++;
				continue;
			}
			SpoutItemStack ingred = new SpoutItemStack(item);
			x.setIngredient(name[i], MaterialData.getMaterial(ingred.getTypeId(),(short)ingred.getDurability()));
			i++;
		}
		SpoutManager.getMaterialManager().registerSpoutRecipe(x);
	}
	
	public static void addShapelessRecipe(List<ItemStack> ingredients, ItemStack result){
		SpoutShapelessRecipe x = new SpoutShapelessRecipe(result);
		for(ItemStack item : ingredients){
			SpoutItemStack ingred = new SpoutItemStack(item);
			x.addIngredient(MaterialData.getMaterial(ingred.getTypeId(),ingred.getDurability()));
		}
		SpoutManager.getMaterialManager().registerSpoutRecipe(x);
	}
	
	public static void addFurnaceRecipe(ItemStack input, ItemStack result){
		SpoutFurnaceRecipe x = new SpoutFurnaceRecipe(new SpoutItemStack(input), new SpoutItemStack(result));
		SpoutFurnaceRecipes.registerSpoutRecipe(x);
	}
}
