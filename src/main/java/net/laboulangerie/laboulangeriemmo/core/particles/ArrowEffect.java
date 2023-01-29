package net.laboulangerie.laboulangeriemmo.core.particles;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import net.laboulangerie.laboulangeriemmo.core.abilities.thehunter.firebow.FireArrow;

public class ArrowEffect extends Effect {

    public ArrowEffect(Entity arrow) {
        super(arrow);
        if (!(arrow instanceof AbstractArrow))
            throw new IllegalArgumentException("This animation can only work on arrows");
    }

    private float helixAngle = 0;

    private FireArrow fireArrow = null;

    @Override
    public void run() {
        for(FireArrow fa : FireArrow.fireArrow) {
            if (fa.getArrow() == entity) {
                fireArrow = fa;
            }
        }

        if (fireArrow != null && fireArrow.getAbilityLevel() == 3) {
            world.spawnParticle(Particle.LAVA, entity.getLocation(), 0, 0, 0, 0, 8);
            createHelix(fireArrow.getArrow().getLocation(), 3, 0.5f);
        }

        if (fireArrow != null && fireArrow.getAbilityLevel() == 2) {
            world.spawnParticle(Particle.FIREWORKS_SPARK, entity.getLocation(), 0, 0, 0, 0, 8);
            createHelix(fireArrow.getArrow().getLocation(), 2, 0.5f);
        }

        if (fireArrow == null || fireArrow.getAbilityLevel() == 1)
            world.spawnParticle(Particle.FLAME, entity.getLocation(), 0, 0, 0, 0, 8);
        if (((AbstractArrow) entity).isInBlock() || ((AbstractArrow) entity).getFireTicks() == 0 || ((AbstractArrow) entity).isDead())
            cancel();
    }

    private void createHelix(Location location, int branches, float radius) {
        helixAngle += 0.3f;
        for (int i = branches; i > 0; i--) {

            final double angle = (2 * Math.PI * i / branches) + helixAngle;
            final double x = Math.cos(angle) * radius;
            final double y = Math.sin(angle) * radius;

            Vector v = rotateAroundAxisX(new Vector(x, y, 0), location.getPitch());
            v = rotateAroundAxisY(v, location.getYaw());

            final Location temp = location.clone().add(v);

            location.getWorld().spawnParticle(Particle.FLAME, (float) temp.getX(), (float) temp.getY(), (float) temp.getZ(), 0);
        }
    }

    private Vector rotateAroundAxisX(Vector v, double angle) {
        angle = Math.toRadians(angle);

        final double cos = Math.cos(angle);
        final double sin = Math.sin(angle);
        final double y = v.getY() * cos - v.getZ() * sin;
        final double z = v.getY() * sin + v.getZ() * cos;
        return v.setY(y).setZ(z);
    }

    private Vector rotateAroundAxisY(Vector v, double angle) {
        angle = -angle;
        angle = Math.toRadians(angle);

        final double cos = Math.cos(angle);
        final double sin = Math.sin(angle);
        final double x = v.getX() * cos + v.getZ() * sin;
        final double z = v.getX() * -sin + v.getZ() * cos;
        return v.setX(x).setZ(z);
    }
}
