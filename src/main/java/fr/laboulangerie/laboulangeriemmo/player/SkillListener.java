package fr.laboulangerie.laboulangeriemmo.player;

import java.util.Set;
import java.util.function.BiConsumer;

import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import fr.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;

public class SkillListener implements Listener {


    private LaBoulangerieMmo laBoulangerieMmo;

    public SkillListener(LaBoulangerieMmo laBoulangerieMmo) {
        this.laBoulangerieMmo = laBoulangerieMmo;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        MmoPlayer player = laBoulangerieMmo.getMmoPlayerManager().getPlayer(event.getPlayer());
        giveReward(event.getPlayer(), GrindingCategory.BREAK, event.getBlock().getType().toString(), (talentName, amount) -> player.getTalent(talentName).incrementXp(amount));
    }

    private void giveReward(Player player, GrindingCategory category, String identifier, BiConsumer<String, Double> consumer) {
        if (player.getGameMode() == GameMode.CREATIVE) return;
        Set<String> keys = laBoulangerieMmo.getConfig().getConfigurationSection("talent-grinding").getKeys(false);

        keys.stream().forEach(talentName -> {
            ConfigurationSection section = laBoulangerieMmo.getConfig().getConfigurationSection("talent-grinding." + talentName + "." + category.toString());
            if (section == null) return;

            if (section.getKeys(false).contains(identifier)) {
                consumer.accept(talentName, section.getDouble(identifier));
            }
        });
    }
}
