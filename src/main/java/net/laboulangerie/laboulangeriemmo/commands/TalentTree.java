package net.laboulangerie.laboulangeriemmo.commands;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.AbstractMap.SimpleEntry;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityArchetype;
import net.laboulangerie.laboulangeriemmo.api.player.MmoPlayer;
import net.laboulangerie.laboulangeriemmo.api.talent.Talent;
import net.laboulangerie.laboulangeriemmo.api.talent.TalentArchetype;

public class TalentTree implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias,
            @NotNull String[] args) {
        Set<String> talentIdentifiers = LaBoulangerieMmo.talentsRegistry.getTalents().keySet();

        if (args.length != 1 || !talentIdentifiers.contains(args[0])) {
            sender.sendMessage("§4Veuillez spécifier un nom de talent.");
            return false;
        }
        String queriedTalent = args[0];

        ConfigurationSection talentTreeConfig =
                LaBoulangerieMmo.PLUGIN.getConfig().getConfigurationSection("talent-tree");

        Player player = (Player) sender;
        MmoPlayer mmoPlayer = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager().getPlayer(player);

        TalentArchetype talentArchetype = LaBoulangerieMmo.talentsRegistry.getTalent(queriedTalent);
        Talent talent = mmoPlayer.getTalent(queriedTalent);

        List<TagResolver.Single> resolvers = new ArrayList<>();
        resolvers.add(Placeholder.component("player", player.displayName()));
        resolvers.add(Placeholder.unparsed("talent", talent.getDisplayName()));

        List<AbilityArchetype> abilities =
                talentArchetype.abilitiesArchetypes.values().stream().collect(Collectors.toList());

        List<SimpleEntry<AbilityArchetype, Integer>> abilitiesTiers = new ArrayList<>();

        // Separate tiers from ability
        for (AbilityArchetype abilityArchetype : abilities) {
            if (abilityArchetype.hasTiers()) {
                for (int i = 0; i < abilityArchetype.tiers.size(); i++) {
                    abilitiesTiers.add(new SimpleEntry<>(abilityArchetype, i));
                }
            } else {
                abilitiesTiers.add(new SimpleEntry<>(abilityArchetype, -1));
            }
        }

        // Sort ability by level
        abilitiesTiers.sort(Comparator.comparingInt((entry) -> {
            AbilityArchetype abilityArchetype = entry.getKey();
            int tier = entry.getValue();

            if (tier == -1) return abilityArchetype.requiredLevel;
            return abilityArchetype.getTier(tier);
        }));

        int invColums = 9;
        Component invTitle = MiniMessage.miniMessage().deserialize(talentTreeConfig.getString("title"),
                TagResolver.resolver(resolvers));
        Inventory talentTreeInv = Bukkit.createInventory(player, invColums * 6, invTitle);

        // Set talent item in the top middle
        talentTreeInv.setItem(invColums / 2, getTalentItem(talentArchetype, talent));

        // Add all ability tiers, starting from the third row
        int location = invColums * 2;
        for (SimpleEntry<AbilityArchetype, Integer> e : abilitiesTiers) {
            talentTreeInv.setItem(location, getAbilityItemDescriptor(e.getKey(), e.getValue()));
            location++;
        }

        player.openInventory(talentTreeInv);
        return true;
    }

    ItemStack getAbilityItemDescriptor(AbilityArchetype ability, Integer tier) {
        ConfigurationSection itemConfig =
                LaBoulangerieMmo.PLUGIN.getConfig().getConfigurationSection("talent-tree.ability-item");

        Material abilityMat = ability.displayItem;

        List<TagResolver.Single> resolvers = new ArrayList<>();
        resolvers.add(Placeholder.unparsed("ability", ability.displayName));
        resolvers.add(Placeholder.unparsed("tier", Integer.toString(tier)));
        resolvers.add(Placeholder.unparsed("description", ability.description));
        resolvers.add(Placeholder.unparsed("cooldown-value", Integer.toString(ability.cooldown)));
        resolvers.add(Placeholder.unparsed("cooldown-unit", ability.cooldownUnit.toString().toLowerCase()));
        int requiredLevel = ability.hasTiers() ? ability.getTier(tier) : ability.requiredLevel;
        resolvers.add(Placeholder.unparsed("required-level", Integer.toString(requiredLevel)));

        return getItemFromConfig(abilityMat, itemConfig, resolvers);
    }

    ItemStack getTalentItem(TalentArchetype talentArchetype, Talent talent) {
        ConfigurationSection itemConfig =
                LaBoulangerieMmo.PLUGIN.getConfig().getConfigurationSection("talent-tree.talent-item");

        Material defaultTalentMat = Material.STICK;
        Material talentMat =
                talentArchetype.comboItems.isEmpty() ? defaultTalentMat : talentArchetype.comboItems.get(0);

        List<TagResolver.Single> resolvers = new ArrayList<>();
        resolvers.add(Placeholder.unparsed("talent", talent.getDisplayName()));
        resolvers.add(Placeholder.unparsed("level", Integer.toString(talent.getLevel())));
        resolvers.add(Placeholder.unparsed("xp", String.format("%.2f", talent.getXp())));

        return getItemFromConfig(talentMat, itemConfig, resolvers);
    }

    ItemStack getItemFromConfig(Material material, ConfigurationSection config, List<TagResolver.Single> resolvers) {
        Component itemName =
                MiniMessage.miniMessage().deserialize(config.getString("name"), TagResolver.resolver(resolvers))
                        .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);

        List<Component> loreComponents =
                config.getStringList("lore").stream()
                        .map(s -> MiniMessage.miniMessage().deserialize(s, TagResolver.resolver(resolvers))
                                .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE))
                        .collect(Collectors.toList());

        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.displayName(itemName);
        itemMeta.lore(loreComponents);

        item.setItemMeta(itemMeta);

        return item;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd,
            @NotNull String alias, @NotNull String[] args) {
        if (args.length != 1) return null;

        List<String> talentIdentifiers =
                LaBoulangerieMmo.talentsRegistry.getTalents().keySet().stream().collect(Collectors.toList());

        return talentIdentifiers;
    }


}
