package team.old.GunsPlus.Classes;

import java.util.HashMap;
import java.util.Set;



public class Effect {
	private HashMap<String, HashMap<Integer , Object>> effects = new HashMap<String, HashMap<Integer , Object>>();
	private int type;
	
	public Effect(int type){
		this.type=type;
	}

	public void addEffect(String effect){
		this.effects.put(effect, null);
	}
	public void removeEffect(String effect){
		this.effects.remove(effect);
	}
	public void addValue(String eff, int id, Object o){
		if(this.effects.get(eff)!=null){
			this.effects.get(eff).put(id, o);
		}else{
			HashMap<Integer, Object> hm 	= new HashMap<Integer, Object>();
			hm.put(id, o);
			this.effects.put(eff, hm);
		}
		
	}
	public HashMap<Integer, Object> getEffectValues(String eff){
		return effects.containsKey(eff)?effects.get(eff):null;
	}
	public String getEffect(String eff){
		return effects.containsKey(eff)?eff:null;
	}
	public Set<String> getEffectNames(){
		return  effects.keySet();
	}
	public boolean hasEffect(String eff){
		return effects.containsKey(eff)?true:false;
	}
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
