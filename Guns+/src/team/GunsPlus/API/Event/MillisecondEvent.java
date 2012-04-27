package team.GunsPlus.API.Event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MillisecondEvent extends Event{

	private static final HandlerList handlers = new HandlerList();
	private long milli;
	
    public HandlerList getHandlers() {
        return handlers;
    }
     
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public MillisecondEvent(long currmilli){
    	milli = currmilli;
    }
    
    public long getCurrentMillisecond(){
    	return milli;
    }

}
