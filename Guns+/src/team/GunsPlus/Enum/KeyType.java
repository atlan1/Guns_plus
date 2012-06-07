package team.GunsPlus.Enum;

import org.getspout.spoutapi.keyboard.Keyboard;

public class KeyType {
	
	private int key;
	private boolean hold;

	public KeyType(String keyString, boolean h) {
		int key = (-1);
		if(keyString.equalsIgnoreCase("left"))
			key = (-2);
		else if(keyString.equalsIgnoreCase("right"))
			key = (-3);
		else if(keyString.equalsIgnoreCase("middle"))
			key = (-4);
		else
		    key = Keyboard.valueOf("KEY_"+keyString.toUpperCase()).getKeyCode();
		this.key = key;
		this.hold = h;
	}

	public boolean isHoldKey(){return hold;} 
	public int getKey(){return key;}
	
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof KeyType)) return false;
		KeyType kt = (KeyType) obj;
		return kt.getKey() == (this.getKey());
	}
}
