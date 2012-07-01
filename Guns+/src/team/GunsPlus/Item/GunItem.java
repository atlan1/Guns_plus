package team.GunsPlus.Item;

import java.util.HashSet;

import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

import team.ApiPlus.API.PropertyHolder;
import team.ApiPlus.API.Type.ItemTypeEffectPlusProperty;
import team.GunsPlus.Util.GunUtils;
import team.GunsPlus.Util.Shooter;

public class GunItem extends ItemTypeEffectPlusProperty implements Gun{

	public GunItem(Plugin plugin, String name, String texture) {
		super(plugin, name, texture);
	}

	@SuppressWarnings("unchecked")
	@Override
	public  void performEffects(Object... args) {
		GunUtils.performEffects((Shooter)args[0], (HashSet<LivingEntity>)args[1], (PropertyHolder) args[2], this);
	}
	
}
