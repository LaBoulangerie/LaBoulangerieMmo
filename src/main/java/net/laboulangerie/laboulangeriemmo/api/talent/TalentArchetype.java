package net.laboulangerie.laboulangeriemmo.api.talent;

import java.util.HashMap;
import java.util.Map;

import net.laboulangerie.laboulangeriemmo.api.ability.AbilityArchetype;

public class TalentArchetype {
    public String displayName;
    public String identifier;
    public Map<String, AbilityArchetype> abilitiesArchetypes = new HashMap<>();
}
