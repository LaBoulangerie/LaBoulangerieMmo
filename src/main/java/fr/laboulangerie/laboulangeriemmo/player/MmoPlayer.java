package fr.laboulangerie.laboulangeriemmo.player;

import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Stream;

import com.google.common.base.Supplier;

import org.bukkit.entity.Player;

import fr.laboulangerie.laboulangeriemmo.json.GsonSerializable;
import fr.laboulangerie.laboulangeriemmo.player.ability.Abilities;
import fr.laboulangerie.laboulangeriemmo.player.talent.Talent;

public class MmoPlayer implements GsonSerializable {

    private UUID uniqueId;
    private String name;

    private HashMap<String, Talent> talents;
    private CooldownsHolder cooldownsHolder;

    public MmoPlayer(Player player) {
        this.uniqueId = player.getUniqueId();
        this.name = player.getName();

        talents = new HashMap<String, Talent>();
        talents.put("baking", new Talent("baking", "Boulanger"));
        talents.put("fishing", new Talent("fishing", "Pêcheur"));
        talents.put("mining", new Talent("mining", "Mineur"));
        talents.put("woodcutting", new Talent("woodcutting", "Bûcheron"));

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

    public Supplier<Stream<Talent>> streamTalents() {
        return () -> talents.values().stream();
    }
}
