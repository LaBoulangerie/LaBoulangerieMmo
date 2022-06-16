package net.laboulangerie.laboulangeriemmo.api.talent;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityArchetype;

public class TalentsRegistry {
    private HashMap<String, TalentArchetype> talentsArchetypes;

    public TalentArchetype getTalent(String identifier) {
        return talentsArchetypes.get(identifier);
    }
    public HashMap<String, TalentArchetype> getTalents() {
        return talentsArchetypes;
    }
    /**
     * Add a talent to the TalentsRegistry
     * @param archetype
     * @return false if the {@link net.laboulangerie.laboulangeriemmo.api.talent.TalentArchetype TalentArchetype}
     * is invalid (e.g: talent already exists or references a non-existent ability).
     * Returns true otherwise
     */
    public boolean addTalent(TalentArchetype archetype) {
        if (validateTalent(archetype)) {
            talentsArchetypes.put(archetype.identifier, archetype);
            return true;
        }
        return false;
    }

    /**
     * Removes the matching element
     * @param archetype element to be removed from the list if present
     * @return the talent that was removed
     */
    public TalentArchetype removeTalent(TalentArchetype archetype) {
        return talentsArchetypes.remove(archetype.identifier);
    }

    public void init() {
        talentsArchetypes = new HashMap<>();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(LaBoulangerieMmo.PLUGIN.getDataFolder(), "config.yml"));

        if (config.getBoolean("enable-talents", false) && config.isSet("talents")) {
            LaBoulangerieMmo.PLUGIN.getLogger().info("Loading talents...");

            //Go through all talents
            for (String identifier : config.getConfigurationSection("talents").getKeys(false)) {
                try {
                    if (talentsArchetypes.containsKey(identifier)) {
                        warnTalent(identifier, "A talent with identifier '"+ identifier +"' already exists!");
                        continue;
                    }
                    if (config.getString("talents." + identifier + ".display_name") == null) {
                        warnTalent(identifier, "Field 'display_name' missing or null.");
                        continue;
                    }
                    TalentArchetype talent = new TalentArchetype();
                    talent.identifier = identifier;
                    talent.displayName = config.getString("talents." + identifier + ".display_name");

                    if (config.isSet("talents." + identifier + ".combo_items")) {
                        try {
                            talent.comboItems = config.getStringList("talents." + identifier + ".combo_items")
                                .stream().map(Material::valueOf).collect(Collectors.toList());
                        } catch (Exception e) {
                            LaBoulangerieMmo.PLUGIN.getLogger().warning("Unable to parse optional field 'combo_item of talent '"+ identifier+"', ignoring the field!");
                        }
                    }

                    if (config.isSet("talents." + identifier + ".abilities")) {
                        ConfigurationSection abilities = config.getConfigurationSection("talents." + identifier + ".abilities");
                        /**
                         * Key is the field's name in the config and the value is
                         * its name in AbilityArchetype
                         */
                        Map<String, String> requiredFields = Map.of("display_name", "displayName", "cooldown", "cooldown", "unit", "cooldownUnit", "level", "requiredLevel", "effect", "effect", "log", "shouldLog");

                        for (String abilityId : abilities.getKeys(false)) { //go through all abilities
                            if  (talent.abilitiesArchetypes.containsKey(abilityId)) {
                                warnAbility(identifier, abilityId, "An ability with this identifier already exists!");
                                continue;
                            }
                            AbilityArchetype abilityArchetype = new AbilityArchetype();
                            ConfigurationSection ability = abilities.getConfigurationSection(abilityId);
            
                            if (ability == null) {// Appends if the path lead to a list (talking by experience)
                                warnAbility(identifier, abilityId, "Configuration node is of wrong type!");
                                continue;
                            }
                            abilityArchetype.identifier = abilityId;
                            AtomicReference<Boolean> fieldsMissing = new AtomicReference<>(false);

                            requiredFields.keySet().stream().forEach(fieldName -> {
                                if (!ability.isSet(fieldName)) {
                                    warnAbility(identifier, abilityId, "Field '"+fieldName+"' is missing!");
                                    fieldsMissing.set(true);
                                    return;
                                }
                                try { //Dynamically assign values to each field of the AbilityArchetype
                                    Class<? extends AbilityArchetype> abilityClass = abilityArchetype.getClass();
                                    Field classField = abilityClass.getField(requiredFields.get(fieldName));

                                    Object value = ability.get(fieldName);
                                    if (fieldName.equals("unit")) value = TimeUnit.valueOf((String) value);

                                    classField.set(abilityArchetype, value);
                                }catch(Exception e) {
                                    LaBoulangerieMmo.PLUGIN.getLogger().severe("§4Error when trying to set field '"+ fieldName +"' of ability '"+ abilityId +"' from talent '"+ identifier +"', value's type might be invalid!");
                                    e.printStackTrace();
                                }
                            });

                            if (fieldsMissing.get()) continue;
                        
                            if (ability.isSet("tiers")) {
                                try {
                                    abilityArchetype.tiers = ability.getIntegerList("tiers");
                                } catch (Exception e) {
                                    LaBoulangerieMmo.PLUGIN.getLogger().warning("Error when trying to set optional field 'tiers' for ability '"+abilityId+"' of talent '"+identifier+"'! Ignoring tiers for this ability.");
                                    e.printStackTrace();
                                }
                            }
                            talent.abilitiesArchetypes.put(abilityId, abilityArchetype);
                        }
                    }
                    addTalent(talent);
                } catch (Exception e) {
                    LaBoulangerieMmo.PLUGIN.getLogger().warning("Unable to load talent \"" + identifier + "\" from configuration!");
                    e.printStackTrace();
                }
            }
        }else {
            LaBoulangerieMmo.PLUGIN.getLogger().info("Talents disabled or not present in the configuration.");
        }
    }

    /**
     * Generates a Map containing an instance of {@link net.laboulangerie.laboulangeriemmo.api.talent.Talent Talent}
     * for each registered {@link net.laboulangerie.laboulangeriemmo.api.talent.TalentArchetype TalentArchetype}.
     * Used internally to update data hold by {@link net.laboulangerie.laboulangeriemmo.api.player.MmoPlayer MmoPlayer}
     * @return keys are talents' identifiers
     */
    public Map<String, Talent> generateTalentsDataHolder() {
        Map<String, Talent> talents = new HashMap<>();
        talentsArchetypes.values().stream()
            .forEach(archetype -> talents.put(archetype.identifier, new Talent(archetype.identifier)));
        return talents;
    }
    private boolean validateTalent(TalentArchetype talent) {
        if (talent.identifier.contains("/")) { // "/" is forbidden because it is used to reference abilities in CooldownsHolder (talent/ability)
            warnTalent(talent.identifier, "It contains a forbidden character: '/'");
            return false;
        }
        if (talentsArchetypes.containsKey(talent.identifier)) {
            warnValidation(talent.identifier, "Talent with this identifier already exists");
            return false;
        }
        AtomicReference<Boolean> noErrors = new AtomicReference<>(true);

        talent.abilitiesArchetypes.keySet().forEach(abilityId -> {
            if (!LaBoulangerieMmo.abilitiesRegistry.exists(abilityId)) {
                warnValidation(talent.identifier, "Talent refers to inexistent ability '"+ abilityId +"'!");
                noErrors.set(false);
            }
        });
        return noErrors.get();
    }

    private void warnValidation(String talent, String cause) {
        LaBoulangerieMmo.PLUGIN.getLogger().warning("Unable to validate talent '"+ talent +"': "+ cause);
    }
    private void warnTalent(String talent, String cause) {
        LaBoulangerieMmo.PLUGIN.getLogger().warning("Unable to load talent '" + talent + "': " + cause);
    }
    private void warnAbility(String talent, String ability, String cause) {
        LaBoulangerieMmo.PLUGIN.getLogger().warning("Unable to load ability '" +ability+ "' of talent '"+talent+"': "+cause);
    }
}
