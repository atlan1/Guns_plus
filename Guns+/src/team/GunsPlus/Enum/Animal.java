package team.GunsPlus.Enum;

import org.bukkit.entity.EntityType;

public enum Animal implements Target{
	PIG(), SHEEP(), COW(), CHICKEN(), SQUID(), WOLF(), MUSHROOMCOW(), SNOWMAN(), OCELOT(), IRONGOLEM(), VILLAGER();
	
	@Override
	public EntityType getEntityType() {
		EntityType e = null;
		switch(this){
			case PIG:
				e = EntityType.PIG;
				break;
			case SHEEP:
				e = EntityType.SHEEP;
				break;
			case COW:
				e = EntityType.COW;
				break;
			case CHICKEN:
				e = EntityType.CHICKEN;
				break;
			case SQUID:
				e = EntityType.SQUID;
				break;
			case WOLF:
				e = EntityType.WOLF;
				break;
			case IRONGOLEM:
				e = EntityType.IRON_GOLEM;
				break;
			case OCELOT:
				e = EntityType.OCELOT;
				break;
			case SNOWMAN:
				e = EntityType.SNOWMAN;
				break;
			case VILLAGER:
				e = EntityType.VILLAGER;
				break;
			case MUSHROOMCOW:
				e = EntityType.MUSHROOM_COW;
				break;
		}
		return e;
	}
}
