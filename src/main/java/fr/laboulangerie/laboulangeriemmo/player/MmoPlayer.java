package fr.laboulangerie.laboulangeriemmo.player;

import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Stream;

import javax.management.loading.PrivateClassLoader;

import com.google.common.base.Supplier;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.libs.org.apache.commons.codec.language.bm.Languages.SomeLanguages;
import org.bukkit.entity.Player;

import fr.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import fr.laboulangerie.laboulangeriemmo.core.particles.EffectRegistry;
import fr.laboulangerie.laboulangeriemmo.events.PlayerEarnsXpEvent;
import fr.laboulangerie.laboulangeriemmo.events.PlayerLevelUpEvent;
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
    
    public Integer getPalier(MmoPlayer player)
    {
    	
    	Integer palier = 0;
        palier = palier + player.getTalent("baking").getLevel(LaBoulangerieMmo.XP_MULTIPLIER);
        palier = palier + player.getTalent("fishing").getLevel(LaBoulangerieMmo.XP_MULTIPLIER);
        palier = palier + player.getTalent("mining").getLevel(LaBoulangerieMmo.XP_MULTIPLIER);
        palier = palier + player.getTalent("woodcutting").getLevel(LaBoulangerieMmo.XP_MULTIPLIER);
        palier = palier + player.getTalent("thehunter").getLevel(LaBoulangerieMmo.XP_MULTIPLIER);
        return palier;
    }
    public void useAbility(Abilities ability) {
        cooldownsHolder.startCooldown(ability);
        Player player = Bukkit.getPlayer(uniqueId);

        if (ability.shouldLog() == true) {
            EffectRegistry.playEffect(ability.getEffectName(), player);
            player.sendMessage("§eVous avez utilisé " + ability.toString() + ", cooldown de " + ability.getCooldown() + " " + ability.getCooldownUnit().toString().toLowerCase());
        }
    }

    public boolean canUseAbility(Abilities ability) {
        return cooldownsHolder.isCooldownElapsed(ability)
                && talents.get(ability.getParentTalent()).getLevel(LaBoulangerieMmo.XP_MULTIPLIER) >= ability.getRequiredLevel()
                && Bukkit.getPlayer(uniqueId).getGameMode() != GameMode.CREATIVE;
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
        if (getTalent(talentId).getLevel(LaBoulangerieMmo.XP_MULTIPLIER) >= 100) {
            return;
        }
        Bukkit.getPluginManager().callEvent(new PlayerEarnsXpEvent(amount, talentId, this));
        int oldLevel = getTalent(talentId).getLevel(LaBoulangerieMmo.XP_MULTIPLIER);

        xpCountdown.startCountDown(talentId, amount);
        getTalent(talentId).incrementXp(amount);
        int newLevel = getTalent(talentId).getLevel(LaBoulangerieMmo.XP_MULTIPLIER);
        if (oldLevel < newLevel) {
            Bukkit.getPluginManager().callEvent(new PlayerLevelUpEvent(getTalent(talentId), this));
            /*if (getPalier(this) == ???) {
            	do some stuff
            }*/
        }
    }
}
