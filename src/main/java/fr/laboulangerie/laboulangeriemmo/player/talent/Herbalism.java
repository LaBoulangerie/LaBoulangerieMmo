package fr.laboulangerie.laboulangeriemmo.player.talent;

import java.util.Map;

import fr.laboulangerie.laboulangeriemmo.player.ability.Abilities;

public class Herbalism implements Talent {

    private int xp;

    @Override
    public int getXp() {
        return 0;
    }

    @Override
    public Map<Abilities, Long> getAbilities() {
        return null;
    }

    @Override
    public void incrementXp(int amount) {

    }
}
