package team.GunsPlus.Enum;

import org.getspout.spoutapi.keyboard.Keyboard;

public class KeyType {
	
	private Keyboard key;
	private boolean hold;

	public  KeyType(String keyString, boolean h) {
		Keyboard key = null;
		if(keyString.equalsIgnoreCase("left"))
			key = Keyboard.MOUSE_LEFT;
		else if(keyString.equalsIgnoreCase("right"))
			key = Keyboard.MOUSE_RIGHT;
		else if(keyString.equalsIgnoreCase("middle"))
			key = Keyboard.MOUSE_MIDDLE;
		else
		    key = Keyboard.valueOf("KEY_"+keyString.toUpperCase());
		this.key = key;
		this.hold = h;
	}

	public boolean isHoldKey(){return hold;} 
	public Keyboard getKey(){return key;}
	
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof KeyType)) return false;
		KeyType kt = (KeyType) obj;
		return kt.getKey().equals(this.getKey());
	}
}
