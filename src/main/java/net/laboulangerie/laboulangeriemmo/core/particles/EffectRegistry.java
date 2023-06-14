package net.laboulangerie.laboulangeriemmo.core.particles;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;

public class EffectRegistry {
    private static Map<String, Class<? extends Effect>> particles =
            new HashMap<String, Class<? extends Effect>>();

    public static void registerParticlesEffects() {
        particles.put("default", HelixEffect.class);
        particles.put("trail", TrailEffect.class);
        particles.put("arrow", ArrowEffect.class);
        particles.put("invisible-particles", InvisibleParticles.class);
        particles.put("stun", StunEffect.class);
    }

    public static Effect getNewEffect(String effectName, Entity entity)
            throws IllegalArgumentException {
        Class<? extends Effect> effectAsClass = particles.get(effectName);
        if (effectAsClass == null)
            throw new IllegalArgumentException("Effect " + effectName + " doesn't exists!");

        try {
            return effectAsClass.getConstructor(Entity.class).newInstance(entity);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            LaBoulangerieMmo.PLUGIN.getLogger().severe(
                    "Tried to instantiate an Effect with an invalid argument for the constructor!");
            e.printStackTrace();
            return null;
        }
    }

    public static Effect getNewEffect(String effectName, Location location)
            throws IllegalArgumentException {
        Class<? extends Effect> effectAsClass = particles.get(effectName);
        if (effectAsClass == null)
            throw new IllegalArgumentException("Effect " + effectName + "doesn't exists!");

        try {
            return effectAsClass.getConstructor(Location.class).newInstance(location);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            LaBoulangerieMmo.PLUGIN.getLogger().severe(
                    "Tried to instantiate an Effect with an invalid argument for the constructor!");
            e.printStackTrace();
            return null;
        }
    }

    public static void playEffect(String effectName, Entity entity) {
        try {
            getNewEffect(effectName, entity).startEffect();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public static void playEffect(String effectName, Location location) {
        try {
            getNewEffect(effectName, location).startEffect();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
