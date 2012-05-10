package team.GunsPlus.Enum;

public enum FireBehavior {
	SINGLE(0,0), AUTOMATIC(0,0);
	
	private FireBurst fb;
	
	private FireBehavior(int x, int y){
		fb = new FireBurst(x, y);
	}
	
	private FireBehavior(FireBurst f){
		fb = f;
	}
	
	public FireBurst getBurst(){return fb;}
	public void setBurst(FireBurst f){fb = f;}
	
	public static FireBehavior SINGLE(int x, int y){
		FireBehavior f =  FireBehavior.SINGLE;
		f.setBurst(new FireBurst(x, y));
		return f;
	}
	
	public static FireBehavior AUTOMATIC(int x, int y){
		FireBehavior f =  FireBehavior.AUTOMATIC;
		f.setBurst(new FireBurst(x, y));
		return f;
	}
	
}
