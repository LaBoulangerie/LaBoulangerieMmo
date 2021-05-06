package fr.laboulangerie.laboulangeriemmo.player.talent;

import fr.laboulangerie.laboulangeriemmo.player.ability.Abilities;

import java.util.Map;

public class Mining implements Talent {

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
