package team.GunsPlus.Util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import team.GunsPlus.API.Event.MillisecondEvent;

public class MillisecondTask implements Listener{

	private static Map<UUID, Task> tasklist = new HashMap<UUID, Task>();
	
	@EventHandler
	public void onMilli(MillisecondEvent e){
		for(UUID id : new HashSet<UUID>(tasklist.keySet())){
			Task t = tasklist.get(id);
			switch(t.getType()){
				case DELAYED:
					if(e.getCurrentMillisecond()-t.getStartTime()>t.getTimeDiff()){
						t.getTask().run();
						tasklist.remove(id);
					}
					break;
				case REPEATING:
					if(e.getCurrentMillisecond()-t.getStartTime()>t.getTimeDiff()){
						t.getTask().run();
					}
					t.setStartTime(System.currentTimeMillis());
					break;
			}
		}
	}
	
	public static UUID startTask(Runnable task, TaskType tt, long time){
		UUID id = UUID.randomUUID();
		tasklist.put(id, new Task(task,tt,System.currentTimeMillis(),time));
		return id;
	}
	
	public static UUID startTask(Runnable r, String name, long time){
		return startTask(r, TaskType.valueOf(name.toUpperCase()), time);
	}
	
	public static void stopTask(UUID id){
		tasklist.remove(id);
	}
	
	public static boolean isRunning(UUID id){
		if(tasklist.containsKey(id))
			return true;
		else
			return false;
	}
	
	static enum TaskType{
		DELAYED(), REPEATING();
	}
	
	public static class Task{
		private long starttime;
		private long timediff;
		private Runnable task;
		private TaskType tasktype;
		
		private Task(Runnable task, TaskType tasktype,  long starttime, long timediff){
			this.tasktype = tasktype;
			this.task = task;
			this.starttime = starttime;
			this.timediff = timediff;
		}
		
		private long getStartTime(){
			return starttime;
		}
		
		private void setStartTime(long starttime){
			this.starttime = starttime;
		}
		
		private long getTimeDiff(){
			return timediff;
		}
		
		private Runnable getTask(){
			return task;
		}
		
		private TaskType getType(){
			return tasktype;
		}
	}
}
