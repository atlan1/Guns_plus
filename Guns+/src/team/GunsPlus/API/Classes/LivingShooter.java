package team.GunsPlus.API.Classes;

/*
 * Created with IntelliJ IDEA.
 * User: DreTaX
 * Date: 2013.06.17.
 * Time: 16:19
 * To change this template use File | Settings | File Templates.
 */
import org.bukkit.entity.LivingEntity;

public abstract class LivingShooter extends Shooter
{
    public abstract LivingEntity getLivingEntity();
}