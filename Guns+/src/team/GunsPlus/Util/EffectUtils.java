package team.GunsPlus.Util;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import team.ApiPlus.API.Effect.Effect;
import team.ApiPlus.API.Effect.EntityEffect;
import team.ApiPlus.API.Effect.LocationEffect;
import team.ApiPlus.API.Effect.Default.MoveEffect;
import team.GunsPlus.Enum.EffectSection;

public class EffectUtils {
	
	private static Location switchLocation(Effect e, Location shooter, Location target){
		Location ret = null;
		switch ((EffectSection)e.getEffectTarget()) {
			case TARGETLOCATION:
				ret = target;
				break;
			case SHOOTERLOCATION:
				ret = shooter;
				break;
			case TARGETENTITY:
				ret = target;
				break;
			case SHOOTER:
				ret = shooter;
				break;
			case FLIGHTPATH:
				break;
		}
		return ret;
	}
	
	private static LivingEntity switchEntity(Effect e, LivingEntity shooter, LivingEntity target){
		switch((EffectSection)e.getEffectTarget()){
			case TARGETENTITY:
				return target;
			case SHOOTER:
				return shooter;
			default:
				return null;
		}	
	}
	
	public static void performLocationEffect(LocationEffect e, Location shooter, Location target) {
		Location switched = switchLocation(e, shooter, target);
		if(switched!=null)
			new SphereEffect(switched, (Integer)((EffectSection) e.getEffectTarget()).getProperty("RADIUS"), e).start();
		else
			new FlightPathEffect(shooter, (Integer)((EffectSection) e.getEffectTarget()).getProperty("LENGTH"), e).start();
	}
	
	public static void performEntityEffect(EntityEffect e, LivingEntity shooter, LivingEntity target) {
		if(shooter.equals(target)&&(EffectSection)e.getEffectTarget() != EffectSection.SHOOTER) return;
		LivingEntity le = switchEntity(e, shooter, target);
		if(e instanceof MoveEffect){
			e.performEffect(le, shooter.getEyeLocation()); 
		}else
			e.performEffect(le);
	}
}
