package net.laboulangerie.laboulangeriemmo.core.particles;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;

public abstract class Effect extends BukkitRunnable {
    protected Entity entity = null;
    protected Location location;
    protected World world;
    /**
     * delay between each call of the run() method in the default implementation of startEffect()
     */
    protected int period = 1;

    public Effect(Entity entity) {
        super();
        this.entity = entity;
        location = entity.getLocation();
        world = location.getWorld();
    }

    public Effect(Location location) {
        super();
        this.location = location;
        world = location.getWorld();
    }

    public void startEffect() {
        run();
        runTaskTimerAsynchronously(LaBoulangerieMmo.PLUGIN, 0, period);
    }

    public void stopEffect() {
        cancel();
    }
}
