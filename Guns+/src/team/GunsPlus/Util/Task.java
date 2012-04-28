package team.GunsPlus.Util;

import java.util.UUID;

import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;


public class Task implements Runnable {
    private JavaPlugin plugin;
    private Object[] arguments;
    private int taskID = 0;
    private UUID taskUUID = null;

    public static Task create(JavaPlugin plugin, Object... arguments) {
        return new Task(plugin, arguments);
    }
    public Task(JavaPlugin plugin, Object... arguments) {
        this.plugin = plugin;
        this.arguments = arguments;
    }

    public JavaPlugin getPlugin() {
        return this.plugin;
    }
    public Server getServer() {
        return this.plugin.getServer();
    }
    public Object getArg(int index) {
        return arguments[index];
    }
    public int getIntArg(int index) {
        return (Integer) getArg(index);
    }
    public long getLongArg(int index) {
        return (Long) getArg(index);
    }
    public float getFloatArg(int index) {
        return (Float) getArg(index);
    }
    public double getDoubleArg(int index) {
        return (Double) getArg(index);
    }
    public String getStringArg(int index) {
        return (String) getArg(index);
    }

    public void run() {

    }

    public boolean isTickTaskQueued() {
        return this.getServer().getScheduler().isQueued(this.taskID);
    }
    public boolean isTickTaskRunning() {
        return this.getServer().getScheduler().isCurrentlyRunning(this.taskID);
    }
    public void stopTickTask() {
        this.getServer().getScheduler().cancelTask(this.taskID);
    }
    public void stopMSTask(){
    	MillisecondTask.stopTask(taskUUID);
    }
    public boolean isMSTaskRunning(){
    	return MillisecondTask.isRunning(taskUUID);
    }
    public void startMSTask(){
    	startMSTaskDelayed(0);
    }
    public void startMSTaskDelayed(long delay){
    	MillisecondTask.startTask(this, "delayed", delay);
    }
    public void startMSTaskRepeating(long interval){
    	MillisecondTask.startTask(this, "repeating", interval);
    }
    public void startTickTask() {
        startTickTask(false);
    }
    public void startTickTask(boolean Async) {
        startTickTaskDelayed(0, Async);
    }
    public void startTickTaskDelayed(long tickDelay) {
        startTickTaskDelayed(tickDelay, false);
    }
    public void startTickTaskDelayed(long tickDelay, boolean Async) {
        if (Async) {
            this.taskID = this.getServer().getScheduler().scheduleAsyncDelayedTask(this.plugin, this, tickDelay);
        } else {
            this.taskID = this.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, this, tickDelay);
        }
    }
    public void startTickTaskRepeating(long tickInterval) {
        startTickTaskRepeating(tickInterval, false);
    }
    public void startTickTaskRepeating(long tickInterval, boolean Async) {
        startTickTaskRepeating(0, tickInterval, Async);
    }
    public void startTickTaskRepeating(long tickDelay, long tickInterval, boolean Async) {
        if (Async) {
            this.taskID = this.getServer().getScheduler().scheduleAsyncRepeatingTask(this.plugin, this, tickDelay, tickInterval);
        } else {
            this.taskID = this.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, this, tickDelay, tickInterval);
        }
    }
}
