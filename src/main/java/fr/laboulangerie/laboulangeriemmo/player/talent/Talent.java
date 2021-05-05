package fr.laboulangerie.laboulangeriemmo.player.talent;

import java.util.List;

public interface Talent {


    int getXp();

    List<Ability> getAbilities();

    void incrementXp(int amount);

    default int getLevel(double multiplier) {
        return (int)Math.round(multiplier*Math.sqrt(this.getXp()));
    }
}
