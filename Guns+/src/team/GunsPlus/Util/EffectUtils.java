package team.GunsPlus.Util;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;
import org.getspout.spoutapi.material.MaterialData;
import team.GunsPlus.Enum.GunEffect;
import team.GunsPlus.Enum.GunEffectSection;
import team.GunsPlus.Enum.GunEffectType;
import team.GunsPlus.Manager.ConfigParser;

public class EffectUtils {
	
	private static Location switchLocation(GunEffect e, Location shooter, Location target){
		Location ret = null;
		switch ((GunEffectSection)e.getEffectSection()) {
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
				return null;
		}
		return ret;
	}
	
	private static LivingEntity switchEntity(GunEffect e, LivingEntity shooter, LivingEntity target){
		switch((GunEffectSection)e.getEffectSection()){
			case TARGETENTITY:
				return target;
			case SHOOTER:
				return shooter;
			default:
				return null;
		}	
	}
	
	private static List<Block> getTargetBlocks(GunEffect e, Location t){
		List<Block> targetBlocks = new ArrayList<Block>();
		if(!e.getEffectSection().getProperties().isEmpty()){
			targetBlocks = Util.getSphere(t, (Integer)
					e.getEffectSection().getProperty("RADIUS"));
		}
		if(targetBlocks.isEmpty()){
			 targetBlocks.add(t.getBlock());
		}
		return targetBlocks;
	}
	
	private static List<Block> getFlightPath(GunEffect e, Location direction, int maxrange){
		List<Block> blockList = new ArrayList<Block>();
		Integer range = (Integer) e.getEffectSection().getProperty("LENGTH");
		BlockIterator bitr = new BlockIterator(direction, 0d, range==null||range<0?maxrange:range);
		while(bitr.hasNext()){
			Block b = bitr.next();
			if(Util.isTransparent(b)&&!Util.isTripod(b)){
				blockList.add(b);
			}else break;
		}
		return blockList;
	}

	public static void breakEffect(GunEffect e, Location shooter, Location location, int range) {
		Location target = switchLocation(e, shooter, location);
		List<Block> targetBlocks = null;
		if(target!=null)
			targetBlocks = getTargetBlocks(e, target);
		else
			targetBlocks = getFlightPath(e, shooter, range);
		for(Block b : targetBlocks)
			breakBlock(b, e);
	}
	
	private static void  breakBlock(Block b, GunEffect eff){
		if(MaterialData.getBlock(b.getTypeId()).getHardness()<Float.valueOf(eff.getProperty("POTENCY").toString()).floatValue()&&!Util.isTripod(b)){
			 b.breakNaturally();
		}
	}

	public static void placeEffect(GunEffect e, Location shooter, Location location, int range) {
		Location target = switchLocation(e, shooter, location);
		List<Block> targetBlocks = null;
		if(target!=null)
			targetBlocks = getTargetBlocks(e, target);
		else
			targetBlocks = getFlightPath(e, shooter, range);
		for(Block b : targetBlocks)
			placeBlock(b, e);
	}
	
	private static void placeBlock(Block b , GunEffect eff) {
		 if(Util.isTransparent(b)&&!Util.isTripod(b)){
			 b.setTypeId(ConfigParser.parseItem((String)
			 eff.getProperty("BLOCK")).getTypeId());
		 }
	}

	public static void explosionEffect(GunEffect e, Location shooter, Location location, int range) {
		Location target = switchLocation(e, shooter, location);
		List<Block> targetBlocks = null;
		if(target!=null)
			targetBlocks = getTargetBlocks(e, target);
		else
			targetBlocks = getFlightPath(e, shooter, range);
		for(Block b : targetBlocks)
			explosion(b.getLocation(), e);
	}
	
	private static void explosion(Location l, GunEffect eff){
		if(Util.tntIsAllowedInRegion(l))
			 l.getWorld().createExplosion(l, (Integer)
			 eff.getProperty("SIZE"));
	}

	public static void lightningEffect(GunEffect e, Location shooter, Location location, int range) {
		Location target = switchLocation(e, shooter, location);
		List<Block> targetBlocks = null;
		if(target!=null)
			targetBlocks = getTargetBlocks(e, target);
		else
			targetBlocks = getFlightPath(e, shooter, range);
		for(Block b : targetBlocks)
			lightning(b.getLocation(), e);
	}
	
	private static void lightning(Location l, GunEffect eff){
		l.getWorld().strikeLightning(l);
	}

	public static void spawnEffect(GunEffect e, Location shooter, Location location, int range) {
		Location target = switchLocation(e, shooter, location);
		List<Block> targetBlocks = null;
		if(target!=null)
			targetBlocks = getTargetBlocks(e, target);
		else
			targetBlocks = getFlightPath(e, shooter, range);
		for(Block b : targetBlocks)
			spawn(b.getLocation(), e);
	}
	
	private static void spawn(Location l, GunEffect eff){
		Location l1 = l.clone();
		 l1.setY(l.getY()+1);
		 l1.getWorld().spawnCreature(l1, EntityType.valueOf((String)
		 eff.getProperty("ENTITY")));
	}
	
	public static void smokeEffect(GunEffect e, Location shooter, Location location, int range) {
		Location target = switchLocation(e, shooter, location);
		List<Block> targetBlocks = null;
		if(target!=null)
			targetBlocks = getTargetBlocks(e, target);
		else
			targetBlocks = getFlightPath(e, shooter, range);
		for(Block b : targetBlocks)
			smoke(b.getLocation(), e);
	}
	
	private static void smoke(Location l, GunEffect eff){
		l.getWorld().playEffect(l,org.bukkit.Effect.SMOKE ,
				 BlockFace.UP, (Integer) eff.getProperty("DENSITY"));
	}

	public static void potionEffect(GunEffect e, LivingEntity shooterEntity, LivingEntity le) {
		potion(switchEntity(e, shooterEntity, le), e);
	}
	
	private static void potion(LivingEntity le, GunEffect eff){
		le.addPotionEffect(new
		PotionEffect(PotionEffectType.getById(Integer.valueOf(eff.getProperty("ID").toString()).intValue()),
		Integer.valueOf(eff.getProperty("DURATION").toString()).intValue(),
		Integer.valueOf(eff.getProperty("STRENGTH").toString()).intValue()));	
	}

	public static void fireEffect(GunEffect e, LivingEntity shooterEntity, LivingEntity le, int range) {
		LivingEntity target = switchEntity(e, shooterEntity, le);
		if(target!=null){
			fire(target, e);
		}else{
			Location targetLoc = switchLocation(e, shooterEntity.getLocation(), le.getLocation());
			List<Block> targetBlocks = null;
			if(targetLoc!=null)
				targetBlocks = getTargetBlocks(e, targetLoc);
			else
				targetBlocks = getFlightPath(e, shooterEntity.getLocation(), range);
			for(Block b : targetBlocks)
				fire(b.getLocation(), e);
		}
	}
		
	private static void fire(LivingEntity le, GunEffect eff){
		le.setFireTicks(Integer.valueOf(eff.getProperty("DURATION").toString()).intValue());
	}
	
	private static void fire(Location l, GunEffect eff){
		l.getWorld().playEffect(l,
				 org.bukkit.Effect.MOBSPAWNER_FLAMES, null, (Integer)
				 eff.getProperty("STRENGTH"));
	}

	public static void drawEffect(GunEffect e, LivingEntity le, LivingEntity shooterEntity, Vector v) {
		move(v, switchEntity(e, shooterEntity, le), e);
	}
	
	public static void pushEffect(GunEffect e, LivingEntity le, LivingEntity shooterEntity, Vector v) {
		move(v, switchEntity(e, shooterEntity, le), e);
	}
	
	private static void move(Vector dir, LivingEntity l, GunEffect eff){
		int factor = 1;
		if(((GunEffectType)eff.getEffectType()).equals(GunEffectType.DRAW))
			factor = -1;
		dir.multiply(Double.valueOf(eff.getProperty("SPEED").toString()).doubleValue()*factor);
		l.setVelocity(dir);
	}
}
