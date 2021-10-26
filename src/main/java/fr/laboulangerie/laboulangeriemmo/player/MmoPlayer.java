package fr.laboulangerie.laboulangeriemmo.player;

import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Stream;

import com.google.common.base.Supplier;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import fr.laboulangerie.laboulangeriemmo.core.ParticleEffect;
import fr.laboulangerie.laboulangeriemmo.events.PlayerEarnsXpEvent;
import fr.laboulangerie.laboulangeriemmo.json.GsonSerializable;
import fr.laboulangerie.laboulangeriemmo.player.ability.Abilities;
import fr.laboulangerie.laboulangeriemmo.player.talent.Talent;

public class MmoPlayer implements GsonSerializable {

    private UUID uniqueId;
    private String name;

    private HashMap<String, Talent> talents;
    private CooldownsHolder cooldownsHolder;
    private transient XpCountDown xpCountdown;

    public MmoPlayer(OfflinePlayer player) {
        this.uniqueId = player.getUniqueId();
        this.name = player.getName();

        talents = new HashMap<String, Talent>();
        talents.put("baking", new Talent("baking", "Boulanger"));
        talents.put("fishing", new Talent("fishing", "Pêcheur"));
        talents.put("mining", new Talent("mining", "Mineur"));
        talents.put("woodcutting", new Talent("woodcutting", "Bûcheron"));
        talents.put("thehunter", new Talent("thehunter", "Chasseur"));

        cooldownsHolder = new CooldownsHolder();
        xpCountdown = new XpCountDown(this);
        xpCountdown.start();
    }

    public MmoPlayer() {
        xpCountdown = new XpCountDown(this);
        xpCountdown.start();
        
    }

    public Talent getTalent(String talentName) {
        return talents.get(talentName);
    }

    public void useAbility(Abilities ability) {
        cooldownsHolder.startCooldown(ability);
        Player player = Bukkit.getPlayer(uniqueId);

        if (ability.shouldLog() == true) {
            new ParticleEffect().createHelix(player);
            player.sendMessage("§eVous avez utilisé " + ability.toString() + ", cooldown de " + ability.getCooldown() +" "+ ability.getCooldownUnit().toString().toLowerCase());
        }
    }
        

    public boolean canUseAbility(Abilities ability) {
        return cooldownsHolder.isCooldownElapsed(ability)
            && talents.get(ability.getParentTalent()).getLevel(0.2) >= ability.getRequiredLevel();
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
    public void incrementXp(String talentId, double amount) {
        Bukkit.getPluginManager().callEvent(new PlayerEarnsXpEvent(amount, talentId, this));
        xpCountdown.startCountDown(talentId, amount);
        getTalent(talentId).incrementXp(amount);
    }
}
