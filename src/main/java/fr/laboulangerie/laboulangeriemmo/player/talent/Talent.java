package fr.laboulangerie.laboulangeriemmo.player.talent;

import fr.laboulangerie.laboulangeriemmo.player.ability.Abilities;

import java.util.Map;

public interface Talent {


    int getXp();

    Map<Abilities, Long> getAbilities();

    void incrementXp(int amount);

    default int getLevel(double multiplier) {
        return (int)Math.round(multiplier*Math.sqrt(this.getXp()));
    }
}
