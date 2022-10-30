package net.laboulangerie.laboulangeriemmo.api.player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.google.common.base.Supplier;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityArchetype;
import net.laboulangerie.laboulangeriemmo.api.talent.Talent;
import net.laboulangerie.laboulangeriemmo.api.talent.TalentArchetype;
import net.laboulangerie.laboulangeriemmo.core.PostProcessingEnabler;
import net.laboulangerie.laboulangeriemmo.core.XpCountDown;
import net.laboulangerie.laboulangeriemmo.core.particles.EffectRegistry;
import net.laboulangerie.laboulangeriemmo.events.PlayerEarnsXpEvent;
import net.laboulangerie.laboulangeriemmo.events.PlayerLevelUpEvent;
import net.laboulangerie.laboulangeriemmo.json.GsonSerializable;

public class MmoPlayer implements GsonSerializable, PostProcessingEnabler.PostProcessable {
    private transient FileConfiguration config = LaBoulangerieMmo.PLUGIN.getConfig();
    private transient XpCountDown xpCountdown;

    private UUID uniqueId;
    private String name;

    private HashMap<String, Talent> talents;
    private CooldownsHolder cooldownsHolder;
    private boolean hasEnabledCombo = true;

    public MmoPlayer(OfflinePlayer player) {
        uniqueId = player.getUniqueId();
        name = player.getName();

        talents = (HashMap<String, Talent>) LaBoulangerieMmo.talentsRegistry.generateTalentsDataHolder();

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

        for (String key : talents.keySet()) {
            palier += getTalent(key).getLevel();
        }

        return palier;
    }

    public void useAbility(AbilityArchetype ability, TalentArchetype talent) {
        cooldownsHolder.startCooldown(ability, talent.identifier);
        Player player = Bukkit.getPlayer(uniqueId);

        if (ability.shouldLog) {
            EffectRegistry.playEffect(ability.effect, player);

            List<TagResolver.Single> placeholders = Arrays.asList(
                Placeholder.parsed("ability", ability.displayName),
                Placeholder.parsed("cooldown", Integer.toString(ability.cooldown)),
                Placeholder.parsed("unit", ability.cooldownUnit.toString().toLowerCase()),
                Placeholder.parsed("talent", talent.displayName)
            );

            player.sendMessage(
                MiniMessage.miniMessage().deserialize(config.getString("lang.prefix"))
                    .append(MiniMessage.miniMessage().deserialize(config.getString("lang.messages.ability_log"),
                    TagResolver.resolver(placeholders))));
        }
    }

    public boolean canUseAbility(AbilityArchetype ability, String talentId) {
        return cooldownsHolder.isCooldownElapsed(ability, talentId)
            && talents.get(talentId).getLevel() >= ability.requiredLevel
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
        if (getTalent(talentId) == null) talents.put(talentId, new Talent(talentId));

        if (getTalent(talentId).getLevel() >= 100) {
            return;
        }
        Bukkit.getPluginManager().callEvent(new PlayerEarnsXpEvent(amount, talentId, this));
        int oldLevel = getTalent(talentId).getLevel();

        xpCountdown.startCountDown(talentId, amount);
        getTalent(talentId).incrementXp(amount);
        int newLevel = getTalent(talentId).getLevel();
        if (oldLevel < newLevel) {
            Bukkit.getPluginManager().callEvent(new PlayerLevelUpEvent(getTalent(talentId), this));
            /*
             * if (getPalier(this) == ???) {
             * do some stuff
             * }
             */
        }
    }
    public boolean hasEnabledCombo() {
        return hasEnabledCombo;
    }
    public void setEnableCombo(boolean enabled) {
        hasEnabledCombo = enabled;
    }
    public CooldownsHolder getCooldowns() {
        return cooldownsHolder;
    }

    public Town getTown() {
    	Resident resident = TownyUniverse.getInstance().getResident(uniqueId);
    	return resident.getTownOrNull();
    }

    public Nation getNation() {
    	Resident resident = TownyUniverse.getInstance().getResident(uniqueId);
    	return resident.getNationOrNull();
    }

    public static Integer getTownTotalLevel (Town town) {
		int townTotal = 0;
        for (final Resident resident :  town.getResidents()) {
            if (resident != null) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(resident.getUUID());
                MmoPlayer mmoPlayer = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getOfflinePlayer(offlinePlayer);
                townTotal += mmoPlayer.getPalier();
            }
        }
        return townTotal;
    }
    public static Integer getTownTalentLevel (Town town, String talentName) {
		int townTotal = 0;
        for (Resident resident : town.getResidents()) {
            if (resident != null) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(resident.getUUID());
                MmoPlayer mmoPlayer = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getOfflinePlayer(offlinePlayer);
                Talent talent = mmoPlayer.getTalent(talentName);
                townTotal += talent.getLevel();
            }
        }
        return townTotal;
    }
    public static Integer getNationTotalLevel (Nation nation) {
		int total = 0;
    	for (Town town :  nation.getTowns())
        	total += MmoPlayer.getTownTotalLevel(town);

    	return total;
    }
    public static Integer getNationTalentLevel (Nation nation, String talentName) {
		int total = 0;
    	for (Town town :  nation.getTowns())
        	total += MmoPlayer.getTownTalentLevel(town, talentName);

    	return total;
    }
    @Override
    public void postProcess() {
        HashMap<String, Talent> newTalents = (HashMap<String, Talent>) LaBoulangerieMmo.talentsRegistry.generateTalentsDataHolder();
        newTalents.entrySet().forEach(entry -> talents.merge(entry.getKey(), entry.getValue(), (newVal, oldVal) -> newVal));
    }
}
