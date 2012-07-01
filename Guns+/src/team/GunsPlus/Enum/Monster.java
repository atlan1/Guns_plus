package team.GunsPlus.Enum;

import org.bukkit.entity.EntityType;

public enum Monster implements Target{
	CREEPER(), SKELETON(), SPIDER(), GIANT(), ZOMBIE(), SLIME(), GHAST(), PIGZOMBIE(), ENDERMAN(), CAVESPIDER(), SILVERFISH(), BLAZE(), MAGMACUBE(), ENDERDRAGON();

	@Override
	public EntityType getEntityType() {
		EntityType e = null;
		switch(this){
			case CREEPER:
				e = EntityType.CREEPER;
				break;
			case SKELETON:
				e = EntityType.SKELETON;
				break;
			case SPIDER:
				e = EntityType.SPIDER;
				break;
			case GHAST:
				e = EntityType.GHAST;
				break;
			case BLAZE:
				e = EntityType.BLAZE;
				break;
			case ZOMBIE:
				e = EntityType.ZOMBIE;
				break;
			case SLIME:
				e = EntityType.SLIME;
				break;
			case PIGZOMBIE:
				e = EntityType.PIG_ZOMBIE;
				break;
			case MAGMACUBE:
				e = EntityType.MAGMA_CUBE;
				break;
			case SILVERFISH:
				e = EntityType.SILVERFISH;
				break;
			case CAVESPIDER:
				e = EntityType.CAVE_SPIDER;
				break;
			case ENDERMAN:
				e = EntityType.ENDERMAN;
				break;
			case ENDERDRAGON:
				e = EntityType.ENDER_DRAGON;
				break;
		}
		return e;
	}
}
