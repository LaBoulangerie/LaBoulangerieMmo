package net.laboulangerie.laboulangeriemmo.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
import org.bukkit.inventory.ItemFlag;
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


        int invColums = 9;
        Component invTitle = MiniMessage.miniMessage().deserialize(talentTreeConfig.getString("title"),
                TagResolver.resolver(resolvers));
        Inventory talentTreeInv = Bukkit.createInventory(player, invColums * 6, invTitle);

        // Set talent item in the top middle
        talentTreeInv.setItem(invColums / 2, getTalentItem(talentArchetype, talent));

        // Add all ability tiers
        int offset = Math.round((float) invColums / abilities.size());
        // If it works it works
        // TODO tho : Make a little algo that calculate space like CSS' space-between property
        // Something like :
        // N°items | Row
        // 1 | ----x----
        // 2 | ---x-x---
        // 3 | --x-x-x--
        // 4 | -x-x-x-x-
        // 5 | -x-xxx-x- this is were it begins to be tricky
        // 6 | -xxx-xxx-
        // 7 | -xxxxxxx-
        // ...
        int margin = (int) Math.floor(Math.floor((float) invColums / abilities.size()) / 2);
        System.out.println(offset);
        System.out.println(margin);

        // Starting from the third row
        int columnTop = invColums * 2 + margin;
        for (AbilityArchetype ability : abilities) {
            if (ability.hasTiers()) {
                int pos = columnTop;
                for (int i = 0; i < ability.tiers.size(); i++) {
                    talentTreeInv.setItem(pos, getAbilityItemDescriptor(ability, i, talent));
                    // Next row
                    pos += invColums;
                }
            } else {
                talentTreeInv.setItem(columnTop, getAbilityItemDescriptor(ability, -1, talent));
            }
            columnTop += offset;
        }

        player.openInventory(talentTreeInv);
        return true;
    }

    ItemStack getAbilityItemDescriptor(AbilityArchetype ability, Integer tier, Talent talent) {
        ConfigurationSection itemConfig =
                LaBoulangerieMmo.PLUGIN.getConfig().getConfigurationSection("talent-tree.ability-item");

        Material abilityMat = ability.displayItem;

        List<TagResolver.Single> resolvers = new ArrayList<>();
        resolvers.add(Placeholder.unparsed("ability", ability.displayName));
        resolvers.add(Placeholder.unparsed("tier", Integer.toString(tier + 1)));
        resolvers.add(Placeholder.unparsed("description", ability.description));
        resolvers.add(Placeholder.unparsed("cooldown-value", Integer.toString(ability.cooldown)));
        resolvers.add(Placeholder.unparsed("cooldown-unit", ability.cooldownUnit.toString().toLowerCase()));
        int requiredLevel = ability.hasTiers() ? ability.getTier(tier) : ability.requiredLevel;
        resolvers.add(Placeholder.unparsed("required-level", Integer.toString(requiredLevel)));
        String isUnlockedColor = talent.getLevel() >= requiredLevel ? "<green>" : "<red>";
        resolvers.add(Placeholder.parsed("is-unlocked-color", isUnlockedColor));

        int amount = ability.hasTiers() ? tier + 1 : 1;
        ItemStack abilityItem = getItemFromConfig(abilityMat, itemConfig, resolvers, ability.hasTiers());
        abilityItem.setAmount(amount);
        return abilityItem;
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

        return getItemFromConfig(talentMat, itemConfig, resolvers, false);
    }

    ItemStack getItemFromConfig(Material material, ConfigurationSection config, List<TagResolver.Single> resolvers,
            boolean hasTiers) {
        Component itemName =
                MiniMessage.miniMessage().deserialize(config.getString("name"), TagResolver.resolver(resolvers))
                        .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);

        if (config.isSet("with-tier") && hasTiers) {
            itemName = itemName.append(MiniMessage.miniMessage().deserialize(config.getString("with-tier"),
                    TagResolver.resolver(resolvers)));
        }

        List<Component> loreComponents =
                config.getStringList("lore").stream()
                        .map(s -> MiniMessage.miniMessage().deserialize(s, TagResolver.resolver(resolvers))
                                .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE))
                        .collect(Collectors.toList());

        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.displayName(itemName);
        itemMeta.lore(loreComponents);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

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
