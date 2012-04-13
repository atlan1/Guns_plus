package team.GunsPlus.Enum;

public enum EffectSection {
	TARGETLOCATION(), TARGETENTITY(), SHOOTER(), SHOOTERLOCATION(), FLIGHTPATH(), UNDEFINED(), TARGETSPHERE(), SHOOTERSPHERE();
	
	private int data;
	
	public int getData(){
		return data;
	}
	
	public void setData(int data){
		this.data = data;
	}
}
