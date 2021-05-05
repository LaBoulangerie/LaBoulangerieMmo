package fr.laboulangerie.laboulangeriemmo.player.ability;

import java.util.concurrent.TimeUnit;

public enum Abilities {

    FAST_MINE();


    private int requieredLevel;
    private TimeUnit cooldownUnit;
    private int cooldown;

    private Class<?> parentTalentClass;
    private AbilityExecutor executor;



}
