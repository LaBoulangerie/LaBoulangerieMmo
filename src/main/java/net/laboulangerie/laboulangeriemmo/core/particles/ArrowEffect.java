package net.laboulangerie.laboulangeriemmo.core.particles;

import net.laboulangerie.laboulangeriemmo.core.thehunter.firebow.FireArrow;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class ArrowEffect extends Effect {

    public ArrowEffect(Entity arrow) {
        super(arrow);
        if (!(arrow instanceof Arrow))
            throw new IllegalArgumentException("This animation can only work on arrows");
    }

    private int helixAngle = 0;

    private FireArrow fireArrow = null;

    @Override
    public void run() {
        for(FireArrow fa : FireArrow.fireArrow) {
            if (fa.getArrow() == entity) {
                fireArrow = fa;
            }
        }

        if (fireArrow != null && fireArrow.getAbilityLevel() == 3) {
            world.spawnParticle(Particle.FIREWORKS_SPARK, entity.getLocation(), 0, 0, 0, 0, 8);
            createHelix(fireArrow.getArrow().getLocation(), 3, 0.4f);
        }

        if (fireArrow != null && fireArrow.getAbilityLevel() == 2) {
            createHelix(fireArrow.getArrow().getLocation(), 2, 0.3f);
        }

        if (fireArrow == null || fireArrow.getAbilityLevel() == 1)
            world.spawnParticle(Particle.FLAME, entity.getLocation(), 0, 0, 0, 0, 8);
        if (((Arrow) entity).isInBlock() || ((Arrow) entity).getFireTicks() == 0 || ((Arrow) entity).isDead())
            cancel();
    }

    private void createHelix(Location location, int branches, float radius) {
        Location tmpLocation = location.clone();

        for (int i = 0; i < 3; i++) {
            for (int j = branches; j > 0; j--) {
                helixAngle += 5;

                final double angle = (2 * Math.PI * j / branches) + helixAngle;
                final double x = Math.cos(angle) * radius;
                final double y = Math.sin(angle) * radius;

                Vector v = rotateAroundAxisX(new Vector(x, y, 0), tmpLocation.getPitch());
                v = rotateAroundAxisY(v, tmpLocation.getYaw());

                final Location temp = tmpLocation.clone().add(v);

                tmpLocation.getWorld().spawnParticle(Particle.FLAME, (float) temp.getX(), (float) temp.getY(), (float) temp.getZ(), 0);
            }
            tmpLocation = tmpLocation.subtract(tmpLocation.getDirection());
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
