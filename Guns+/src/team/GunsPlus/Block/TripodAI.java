package team.GunsPlus.Block;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import team.ApiPlus.Util.Task;
import team.ApiPlus.Util.Utils;
import team.GunsPlus.GunsPlus;
import team.GunsPlus.Enum.PlayerTarget;
import team.GunsPlus.Enum.Target;
import team.GunsPlus.Util.Util;

public class TripodAI{
	
	private TripodData td;
	private Task activity;
	private int counter = 0;
	private LivingEntity lastTarget;
	
	public TripodAI(TripodData td){
		this.td = td;
	}
	
	public void startAI(){
		activity = new Task(GunsPlus.plugin){
			public void run() {
				if(td.getDroppedGun()==null) return;
				if(counter >=  5){
					lastTarget = getNearestTarget();
					counter = 0;
				}
				if(lastTarget!=null){
					if(Util.canSee(td.getLocation(), lastTarget.getEyeLocation(), (Integer) td.getGun().getProperty("RANGE"))){
						aim();
						fire();
					}
				}
				counter++;
			}
		};
		activity.startTaskRepeating(1);
	}
	
	public void stopAI(){
		if(activity!=null){
			activity.stopTask();
		}
	}
	
	public void fire(){
		td.fire(td.getGun());
	}

	public void aim() {
			td.setLocation(Utils.setLookingAt(td.getLocation(), lastTarget.getEyeLocation()));
	}
	
	private LivingEntity getNearestTarget(){
		int r = (Integer) td.getGun().getProperty("RANGE");
		List<Entity> near = new ArrayList<Entity>(Utils.getNearbyEntities(td.getLocation(), r, r, r));  
		List<LivingEntity> les = new ArrayList<LivingEntity>();
		LivingEntity le = null;
		for(Target tar : td.getTargets()){
			for(Entity e : near){
				if(e.getType().equals(tar.getEntityType())&&e instanceof LivingEntity){
					if(tar instanceof PlayerTarget){
						if(((PlayerTarget)tar).getName().equals(((org.bukkit.entity.Player)e).getName())){
							les.add((LivingEntity)e);
						}
					}else{
						les.add((LivingEntity)e);
					}
				}
			}
		}
		if(les.isEmpty()) return le;
		le = les.get(0);
		for(LivingEntity l : les){
			if(l.getLocation().toVector().distance(td.getLocation().toVector())<le.getLocation().toVector().distance(td.getLocation().toVector())){
				le = l;
			}
		}
		return le;
	}
}
