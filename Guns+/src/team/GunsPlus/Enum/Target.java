package team.GunsPlus.Enum;


import org.bukkit.entity.EntityType;

import team.GunsPlus.GunsPlus;

public enum Target {

	MONSTER(1, Monster.CREEPER), ANIMAL(1, Animal.COW), PLAYER(1, Player.Player);
	
	public enum Monster{
		CREEPER(), SKELETON(), SPIDER(), GIANT(), ZOMBIE(), SLIME(), GHAST(), PIGZOMBIE(), ENDERMAN(), CAVESPIDER(), SILVERFISH(), BLAZE(), MAGMACUBE(), ENDERDRAGON();
	}
	
	public enum Animal{
		PIG(), SHEEP(), COW(), CHICKEN(), SQUID(), WOLF(), MUSHROOMCOW(), SNOWMAN(), OCELOT(), IRONGOLEM(), VILLAGER();
	}
	
	public enum Player{
		Player("");
		private String name;
		private Player(String n){name = n;}
		public String getName(){return name;}
		public String setName(String n){name = n; return name;}
	}
	
	private int priority;
	private Enum<?> entity;
	
	private Target(int p, String n){
		setPriority(p);
		setEntity(n);
	}
	
	private Target(int p, Enum<?> e){
		setPriority(p);
		setEntity(e);
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public Enum<?> getEntity() {
		return entity;
	}
	
	public EntityType getRealEntity() {
		EntityType e = null;
		if(entity instanceof Monster){
			switch((Monster)entity){
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
		}else if(entity instanceof Animal){
			switch((Animal)entity){
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
		}else if(entity instanceof Player){
				e = EntityType.PLAYER;
		}
		return e;
	}
	
	public static Target getTarget(String type, String name, int priority){
		Target t = null;
		try{
			t = Target.valueOf(type);
			t.setEntity(name);
			t.setPriority(priority);
		}catch(Exception e){
			if(GunsPlus.debug)
				e.printStackTrace();
		}
		return t;
	}
	
	

	public void setEntity(Enum<?> entity) {
		if(entity instanceof Monster||entity instanceof Animal||entity instanceof Player)
			this.entity = entity;
	}
	
	public void setEntity(String n){
		Enum<?> ent = null;
		try{
			ent = Monster.valueOf(n.toUpperCase());
			entity = ent;
		}catch(Exception e1){
			try{
				ent = Animal.valueOf(n.toUpperCase());
				entity = ent;
			}catch(Exception e2){
				ent = Player.Player;
				((Player)ent).setName(n);
				entity = ent;
			}
		}
	}
}
