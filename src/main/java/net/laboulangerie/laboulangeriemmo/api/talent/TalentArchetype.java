package net.laboulangerie.laboulangeriemmo.api.talent;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;

import net.laboulangerie.laboulangeriemmo.api.ability.AbilityArchetype;

public class TalentArchetype {
    public String displayName;
    public String identifier;
    public Map<String, AbilityArchetype> abilitiesArchetypes = new HashMap<>();
    /**
     * The item the player must be holding to accept a combo for this talent,
     * null means no limitation.
     */
    public Material comboItem = null;
}
