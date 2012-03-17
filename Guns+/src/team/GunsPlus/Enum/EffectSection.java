package team.GunsPlus.Enum;

import java.util.ArrayList;

public enum EffectSection {
	TARGETLOCATION(), TARGETENTITY(), SHOOTER(), SHOOTERLOCATION(), FLIGHTPATH(), UNDEFINED();
	
	private ArrayList<EffectType> effects = new ArrayList<EffectType>();
	
	public void addEffect(EffectType et){
		effects.add(et);
	}
	
	public void removeEffect(EffectType et){
		if(effects.contains(et)){
			effects.remove(et);
		}
	}
	
	public ArrayList<EffectType> getEffects(){
		return effects;
	}
}
