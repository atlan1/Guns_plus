package team.GunsPlus.API.Classes;

/**ó
 * Created with IntelliJ IDEA.
 * User: DreTaX
 * Date: 2013.06.17.
 * Time: 16:18
 * To change this template use File | Settings | File Templates.
 */

import java.util.ArrayList;
import java.util.List;
import team.ApiPlus.API.Operator;
import team.GunsPlus.Item.Gun;

public abstract class Shooter
        implements Operator
{
    private boolean fireing = false;
    private Boolean reloading = null;
    private Boolean delaying = null;
    private List<Gun> canShoot = new ArrayList();

    public void resetReload() {
        this.reloading = null;
    }

    public void resetDelay() {
        this.delaying = null;
    }

    public boolean isReloading() {
        return (this.reloading != null) && (this.reloading.booleanValue());
    }

    public boolean isOnReloadingQueue() {
        return (this.reloading != null) && (!this.reloading.booleanValue());
    }

    public boolean isOnDelayingQueue() {
        return (this.delaying != null) && (!this.delaying.booleanValue());
    }

    public boolean isDelayResetted() {
        return this.delaying == null;
    }

    public boolean isReloadResetted() {
        return this.reloading == null;
    }

    public void setReloading() {
        this.reloading = Boolean.valueOf(true);
    }

    public void setOnReloadingQueue() {
        this.reloading = Boolean.valueOf(false);
    }

    public boolean isDelaying() {
        return (this.delaying != null) && (this.delaying.booleanValue());
    }

    public void setDelaying() {
        this.delaying = Boolean.valueOf(true);
    }

    public void setOnDelayingQueue() {
        this.delaying = Boolean.valueOf(false);
    }

    public boolean isFireing() {
        return this.fireing;
    }

    public void setFireing(boolean f) {
        this.fireing = f;
    }

    public Boolean getCanShoot(Gun g)
    {
        return Boolean.valueOf(this.canShoot.contains(g));
    }

    public void setCanShoot(Gun g, boolean can) {
        if ((can) && (!this.canShoot.contains(g)))
            this.canShoot.add(g);
        else if ((!can) && (this.canShoot.contains(g)))
            this.canShoot.remove(g);
    }

    public abstract void reload(Gun paramGun, boolean paramBoolean);

    public abstract void delay(Gun paramGun);

    public abstract void fire(Gun paramGun);
}