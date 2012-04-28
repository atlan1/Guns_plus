package team.GunsPlus.Util;

import org.bukkit.Bukkit;

import team.GunsPlus.API.Event.MillisecondEvent;

public class OnMillisecond implements Runnable{

	public void run(){
		while(true){
			try {
				synchronized (this){
					this.wait(1);
				}
				Bukkit.getServer().getPluginManager().callEvent(new MillisecondEvent());
			} catch (InterruptedException e) {
				Util.debug(e);
			}
		}
	}
}
