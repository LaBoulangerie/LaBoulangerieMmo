package fr.laboulangerie.laboulangeriemmo.player;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import fr.laboulangerie.laboulangeriemmo.json.GsonSerializable;
import fr.laboulangerie.laboulangeriemmo.player.ability.Abilities;
import fr.laboulangerie.laboulangeriemmo.player.talent.Baking;
import fr.laboulangerie.laboulangeriemmo.player.talent.Fishing;
import fr.laboulangerie.laboulangeriemmo.player.talent.Mining;
import fr.laboulangerie.laboulangeriemmo.player.talent.Talent;
import fr.laboulangerie.laboulangeriemmo.player.talent.WoodCutting;

public class MmoPlayer implements GsonSerializable {

    private UUID uniqueId;
    private String name;

    private HashMap<String, Talent> talents;
    private CooldownsHolder cooldownsHolder;

    public MmoPlayer(Player player) {
        this.uniqueId = player.getUniqueId();
        this.name = player.getName();

        talents = new HashMap<String, Talent>();
        talents.put("baking", new Baking());
        talents.put("fishing", new Fishing());
        talents.put("mining", new Mining());
        talents.put("woodcutting", new WoodCutting());

        cooldownsHolder = new CooldownsHolder();
    }

    public Talent getTalent(String talentName) {
        return talents.get(talentName);
    }

    public void useAbility(Abilities ability) {
        cooldownsHolder.startCooldown(ability);
    }

    public boolean canUseAbility(Abilities ability) {
        return cooldownsHolder.isCooldownElapsed(ability)
            && talents.get(ability.getParentTalent()).getLevel(0) >= ability.getRequiredLevel();
    }

    public String getName() {
        return name;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }
}
