package team.GunsPlus.Enum;

import java.util.List;

public enum EffectSection {
	TARGETLOCATION(), TARGETENTITY(), SHOOTER(), SHOOTERLOCATION(), FLIGHTPATH(), UNDEFINED(), TARGETSPHERE(), SHOOTERSPHERE();
	
	private List<Object> data;
	
	public List<Object> getData(){
		return data;
	}
	
	public void setData(List<Object> data){
		this.data = data;
	}
}
