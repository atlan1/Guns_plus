package team.GunsPlus;

import java.util.List;

import me.znickq.furnaceapi.SpoutFurnaceRecipe;
import me.znickq.furnaceapi.SpoutFurnaceRecipes;

import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.inventory.SpoutShapedRecipe;
import org.getspout.spoutapi.inventory.SpoutShapelessRecipe;

public class RecipeManager {
	public static void addShapedRecipe(List<ItemStack> ingredients, ItemStack result){
		char[] name = {'a','b','c',
						'd','e','f',
						'g','h','i'};
		int i = 0;
		SpoutShapedRecipe x = new SpoutShapedRecipe(result);
		for(ItemStack item : ingredients) {
			x.setIngredient(name[i], new SpoutItemStack(item).getMaterial());
			i++;
		}
		x.shape("abcdefghi");
		SpoutManager.getMaterialManager().registerSpoutRecipe(x);
	}
	
	public static void addShapelessRecipe(List<ItemStack> ingredients, ItemStack result){
		SpoutShapelessRecipe x = new SpoutShapelessRecipe(result);
		for(ItemStack item : ingredients){
			x.addIngredient(new SpoutItemStack(item).getMaterial());
		}
		SpoutManager.getMaterialManager().registerSpoutRecipe(x);
	}
	
	public static void addFurnaceRecipe(ItemStack input, ItemStack result){
		SpoutFurnaceRecipe x = new SpoutFurnaceRecipe(new SpoutItemStack(input), new SpoutItemStack(result));
		SpoutFurnaceRecipes.registerSpoutRecipe(x);
	}
}
