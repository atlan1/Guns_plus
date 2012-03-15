package team.Enum;

public enum EffectSection {
	TARGETLOCATION(true), TARGETENTITY(false), SHOOTER(false), SHOOTERLOCATION(true), FLIGHTPATH(true), UNDEFINED(true);
	
	private boolean isLocation;
	
	EffectSection(boolean b){
		this.setLocation(b);
	}

	public boolean isLocation() {
		return isLocation;
	}

	public void setLocation(boolean isLocation) {
		this.isLocation = isLocation;
	}
}
