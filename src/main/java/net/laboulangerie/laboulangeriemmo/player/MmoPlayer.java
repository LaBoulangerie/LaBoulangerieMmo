package net.laboulangerie.laboulangeriemmo.player;

import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.google.common.base.Supplier;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.core.particles.EffectRegistry;
import net.laboulangerie.laboulangeriemmo.events.PlayerEarnsXpEvent;
import net.laboulangerie.laboulangeriemmo.events.PlayerLevelUpEvent;
import net.laboulangerie.laboulangeriemmo.json.GsonSerializable;
import net.laboulangerie.laboulangeriemmo.player.ability.Abilities;
import net.laboulangerie.laboulangeriemmo.player.talent.Talent;

public class MmoPlayer implements GsonSerializable {
    private transient FileConfiguration config = LaBoulangerieMmo.PLUGIN.getConfig();

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

    public Integer getPalier() {
        Integer palier = 0;

        for (String key : this.talents.keySet()) {
            palier += this.getTalent(key).getLevel(LaBoulangerieMmo.XP_MULTIPLIER);
        }

        return palier;
    }

    public void useAbility(Abilities ability) {
        cooldownsHolder.startCooldown(ability);
        Player player = Bukkit.getPlayer(uniqueId);

        if (ability.shouldLog() == true) {
            EffectRegistry.playEffect(ability.getEffectName(), player);

            HashMap<String, String> placeholders = new HashMap<>();
            placeholders.put("ability", ability.toString());
            placeholders.put("cooldown", Integer.toString(ability.getCooldown()));
            placeholders.put("unit", ability.getCooldownUnit().toString().toLowerCase());

            player.sendMessage(
                    MiniMessage.get().parse(this.config.getString("lang.prefix"))
                            .append(MiniMessage.get().parse(this.config.getString("lang.messages.ability_log"),
                                    placeholders)));
        }
    }

    public boolean canUseAbility(Abilities ability) {
        return cooldownsHolder.isCooldownElapsed(ability)
                && talents.get(ability.getParentTalent()).getLevel(LaBoulangerieMmo.XP_MULTIPLIER) >= ability
                        .getRequiredLevel()
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
            /*
             * if (getPalier(this) == ???) {
             * do some stuff
             * }
             */
        }
    }
}