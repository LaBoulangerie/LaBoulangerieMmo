package net.laboulangerie.laboulangeriemmo.commands.talenttree;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityArchetype;
import net.laboulangerie.laboulangeriemmo.api.talent.Talent;
import net.laboulangerie.laboulangeriemmo.api.talent.TalentArchetype;

public class TalentTreeInv implements InventoryHolder {

    private static final int INVENTORY_COLUMNS = 9;
    private static final MiniMessage mm = MiniMessage.miniMessage();

    private final Inventory inventory;
    private ConfigurationSection config;
    private TagResolver mainResolver;
    private Player player;
    private Talent talent;
    private TalentArchetype talentArchetype;

    public TalentTreeInv(Player player, Talent talent) {
        this.config = LaBoulangerieMmo.PLUGIN.getConfig().getConfigurationSection("talent-tree");

        this.player = player;
        this.talent = talent;
        this.talentArchetype = LaBoulangerieMmo.talentsRegistry.getTalent(talent.getTalentId());
        this.mainResolver = getMainResolver();

        List<AbilityArchetype> abilities =
                talentArchetype.abilitiesArchetypes.values().stream().collect(Collectors.toList());

        Component title = mm.deserialize(config.getString("title"), mainResolver);
        this.inventory = Bukkit.getServer().createInventory(this, INVENTORY_COLUMNS * 6, title);

        // Set talent item at the top middle
        inventory.setItem(INVENTORY_COLUMNS / 2, getTalentItem());

        // Add all ability tiers
        int offset = Math.round((float) INVENTORY_COLUMNS / abilities.size());

        // TODO tho : Make a little algo that calculate space like CSS' space-between property
        int margin = (int) Math.floor(Math.floor((float) INVENTORY_COLUMNS / abilities.size()) / 2);

        // Starting from the third row
        int columnTop = INVENTORY_COLUMNS * 2 + margin;
        for (AbilityArchetype ability : abilities) {
            if (ability.hasTiers()) {
                int pos = columnTop;
                for (int i = 0; i < ability.tiers.size(); i++) {
                    inventory.setItem(pos, getAbilityItem(ability, i));
                    // Next row
                    pos += INVENTORY_COLUMNS;
                }
            } else {
                inventory.setItem(columnTop, getAbilityItem(ability, -1));
            }
            columnTop += offset;
        }
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    private TagResolver getAbilityTagResolver(AbilityArchetype ability, Integer tier) {
        List<TagResolver.Single> abilityResolvers = new ArrayList<>();
        abilityResolvers.add(Placeholder.unparsed("ability", ability.displayName));
        abilityResolvers.add(Placeholder.unparsed("tier", Integer.toString(tier + 1)));
        abilityResolvers.add(Placeholder.unparsed("description", ability.description));
        abilityResolvers.add(Placeholder.unparsed("instruction", ability.instruction));
        abilityResolvers.add(Placeholder.unparsed("cooldown-value", Integer.toString(ability.cooldown)));
        abilityResolvers.add(Placeholder.unparsed("cooldown-unit", ability.cooldownUnit.toString().toLowerCase()));

        int requiredLevel = ability.hasTiers() ? ability.getTier(tier) : ability.requiredLevel;
        abilityResolvers.add(Placeholder.unparsed("required-level", Integer.toString(requiredLevel)));

        String isUnlockedColor = talent.getLevel() >= requiredLevel ? "<green>" : "<red>";
        abilityResolvers.add(Placeholder.parsed("is-unlocked-color", isUnlockedColor));

        return TagResolver.resolver(abilityResolvers);
    }

    private TagResolver getMainResolver() {
        List<TagResolver.Single> mainResolvers = new ArrayList<>();
        mainResolvers.add(Placeholder.component("player", player.displayName()));
        mainResolvers.add(Placeholder.unparsed("talent", talent.getDisplayName()));
        mainResolvers.add(Placeholder.unparsed("level", Integer.toString(talent.getLevel())));
        mainResolvers.add(Placeholder.unparsed("xp", String.format("%.2f", talent.getXp())));
        return TagResolver.resolver(mainResolvers);
    }

    private ItemStack getAbilityItem(AbilityArchetype ability, Integer tier) {
        ConfigurationSection itemConfig = config.getConfigurationSection("ability-item");

        Material abilityMat = ability.displayItem;
        TagResolver abilityResolver = getAbilityTagResolver(ability, tier);

        int amount = ability.hasTiers() ? tier + 1 : 1;

        ItemStack abilityItem = getItemFromConfig(abilityMat, itemConfig,
                TagResolver.resolver(abilityResolver, mainResolver), ability.hasTiers());
        abilityItem.setAmount(amount);
        return abilityItem;
    }

    private ItemStack getTalentItem() {
        ConfigurationSection itemConfig = config.getConfigurationSection("talent-item");

        Material defaultTalentMat = Material.STICK;
        Material talentMat =
                talentArchetype.comboItems.isEmpty() ? defaultTalentMat : talentArchetype.comboItems.get(0);

        return getItemFromConfig(talentMat, itemConfig, mainResolver, false);
    }

    private ItemStack getItemFromConfig(Material material, ConfigurationSection itemConfig, TagResolver resolver,
            boolean hasTiers) {
        Component itemName = mm.deserialize(itemConfig.getString("name"), resolver).decoration(TextDecoration.ITALIC,
                TextDecoration.State.FALSE);

        if (itemConfig.isSet("with-tier") && hasTiers) {
            itemName = itemName.append(mm.deserialize(itemConfig.getString("with-tier"), resolver));
        }

        List<Component> loreComponents = itemConfig.getStringList("lore").stream()
                .map(s -> mm.deserialize(s, resolver).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE))
                .collect(Collectors.toList());

        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.displayName(itemName);
        itemMeta.lore(loreComponents);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        item.setItemMeta(itemMeta);

        return item;
    }

}
