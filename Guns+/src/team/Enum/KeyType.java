package team.Enum;

public enum KeyType {
	RIGHT("Right"),LEFT("Left"),
	RIGHTSHIFT("Right_Shift"),LEFTSHIFT("Left_Shift"),
	LETTER("A"),NUMBER("0"),
	HOLDRIGHT("Right:HOLD"),HOLDLEFT("Left:HOLD"),
	HOLDRIGHTSHIFT("Right_Shift:HOLD"),HOLDLEFTSHIFT("Left_Shift:HOLD"),
	HOLDLETTER("A:HOLD"),HOLDNUMBER("0:HOLD");
	
	private String data;
	
	private KeyType(String data) {
		this.data = data;
	}
	
	public String getData() {
		return data;
	}
	public void setData(String input) {
		this.data = input;
	}
	
	public static KeyType getType(String input) {
		KeyType got = null;
		if(input.equalsIgnoreCase("right")) {
			got = RIGHT;
		} else if(input.equalsIgnoreCase("left")) {
			got = LEFT;
		} else if(input.equalsIgnoreCase("left_shift") || input.equalsIgnoreCase("leftshift")) {
			got = LEFTSHIFT;
		} else if(input.equalsIgnoreCase("right_shift") || input.equalsIgnoreCase("rightshift")) {
			got = RIGHTSHIFT;
		} else if(input.matches("[a-zA-Z]")) {
			got = LETTER(input);
		} else if(input.matches("[0-9]")) {
			got = NUMBER(input);
		}
		return got;
	}

	public static KeyType LETTER(String input) {
		KeyType l = KeyType.LETTER;
		l.setData(input);
		return l;
	}
	public static KeyType HOLDLETTER(String input) {
		KeyType l = KeyType.HOLDLETTER;
		l.setData(input);
		return l;
	}
	public static KeyType NUMBER(String input) {
		KeyType n = KeyType.NUMBER;
		n.setData(input);
		return n;
	}
	public static KeyType HOLDNUMBER(String input) {
		KeyType n = KeyType.HOLDNUMBER;
		n.setData(input);
		return n;
	}
}
