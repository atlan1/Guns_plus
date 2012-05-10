package team.GunsPlus.Enum;

public class FireBurst {
	
	private int burstTimes;
	private int burstDelay;
	
	public FireBurst(int x, int y){
		this.burstTimes=x;
		this.burstDelay=y;
	}
	
	public int getShots(){return burstTimes;}
	public int getDelay(){return burstDelay;}
}
