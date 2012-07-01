package team.GunsPlus.Enum;

public enum TargetType {
	Monster(Monster.class), Animal(Animal.class), Player(PlayerTarget.class);
	
	private Class<? extends Target> targetClass;
	
	private TargetType(Class<? extends Target> c) {
		this.targetClass = c;
	}
	
	public Class<? extends Target> getTargetClass() {
		return targetClass;
	}
	
	public static TargetType getTargetType(Class< ? extends Target> c ) {
		if(c.equals(Monster.class)){
			return TargetType.Monster;
		}else if(c.equals(Animal.class)){
			return TargetType.Animal;
		}else if(c.equals(PlayerTarget.class)){
			return TargetType.Player;
		}
		return null;
	}
}
