package team.GunsPlus.Util;

import org.bukkit.Bukkit;

import team.GunsPlus.API.Event.MillisecondEvent;

public class OnMillisecond implements Runnable{

	private long lastmilli;
	
	public void run(){
		lastmilli = System.currentTimeMillis();
		while(true){
			if(System.currentTimeMillis()>=lastmilli+1){
				Bukkit.getServer().getPluginManager().callEvent(new MillisecondEvent(System.currentTimeMillis()));
				lastmilli = System.currentTimeMillis();
			}
		}
	}
}
