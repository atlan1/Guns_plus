package team.GunsPlus.Enum;


import org.bukkit.entity.EntityType;

public enum Target {

	MONSTER(1, EntityType.BLAZE), ANIMAL(1, EntityType.COW), PLAYER(1, EntityType.PLAYER, "SirTyler");
	
	private int priority;
	private EntityType entity;
	
	private String data;
	
	private Target(int p, String n){
		setPriority(p);
		setEntity(n);
	}
	
	private Target(int p, EntityType e, String d){
		setPriority(p);
		setEntity(e);
		setData(d);
	}
	
	private Target(int p, EntityType e){
		setPriority(p);
		setEntity(e);
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public EntityType getEntity() {
		return entity;
	}

	public void setEntity(EntityType entity) {
		this.entity = entity;
	}
	
	public void setEntity(String n){
		EntityType e = EntityType.valueOf(n.toUpperCase());
		if(e!=null)
			entity = e;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
