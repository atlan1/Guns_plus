package team.GunsPlus.Block;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import team.GunsPlus.GunsPlus;
import team.GunsPlus.Enum.Target;
import team.GunsPlus.Util.Task;
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
				if(counter ==  5){
					lastTarget = getNearestTarget((int) td.getGun().getValue("RANGE"), td.getTargets());
					counter = 0;
				}
				if(lastTarget!=null){
					aim();
					fire();
				}
				counter++;
			}
		};
		activity.startMSTaskRepeating(5);
	}
	
	public void stopAI(){
		if(activity!=null)
			activity.stopMSTask();
	}
	
	public void fire(){
		td.fire(td.getGun());
	}

	public void aim() {
			td.setLocation(Util.setLookingAt(td.getLocation(), lastTarget.getEyeLocation()));
	}
	
	private LivingEntity getNearestTarget(int r, List<Target> t){
		List<Entity> near = td.getDroppedGun().getNearbyEntities(r, r, r);
		List<LivingEntity> les = new ArrayList<LivingEntity>();
		LivingEntity le = null;
		for(Target tar : t){
			for(Entity e : near){
				if(e.getType().equals(tar.getRealEntity())&&e instanceof LivingEntity){
					if(e.getType().equals(EntityType.PLAYER)){
						if(((Target.Player)tar.getEntity()).getName().equals(((org.bukkit.entity.Player)e).getName())){
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
