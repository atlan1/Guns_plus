package team.GunsPlus.Enum;

import org.bukkit.entity.EntityType;

public class PlayerTarget implements Target{

	private String name = "SirTyler";
	
	public PlayerTarget(String name) {
		this.name = name;
	}
	
	@Override
	public EntityType getEntityType() {
		return EntityType.PLAYER;
	}
	
	public String getName() {
		return name;
	}
}
