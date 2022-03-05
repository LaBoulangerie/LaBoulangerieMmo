package net.laboulangerie.laboulangeriemmo.core.particles;

import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;

public class ArrowEffect extends Effect {

    public ArrowEffect(Entity arrow) {
        super(arrow);
        if (!(arrow instanceof Arrow))
            throw new IllegalArgumentException("This animation can only work on arrows");
    }

    @Override
    public void run() {
        world.spawnParticle(Particle.FLAME, entity.getLocation(), 0, 0, 0, 0, 8);
        if (((Arrow) entity).isInBlock() || ((Arrow) entity).getFireTicks() == 0 || ((Arrow) entity).isDead())
            cancel();
    }  
}
