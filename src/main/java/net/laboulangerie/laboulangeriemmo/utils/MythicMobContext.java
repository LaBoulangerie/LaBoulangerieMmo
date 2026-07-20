package net.laboulangerie.laboulangeriemmo.utils;

import java.util.Optional;

import net.laboulangerie.laboulangeriemmo.core.abilities.thehunter.MobVisualProfile;

public record MythicMobContext(String internalName, MobVisualProfile baseProfile,
        Optional<MobVisualProfile> disguiseProfile) {}
