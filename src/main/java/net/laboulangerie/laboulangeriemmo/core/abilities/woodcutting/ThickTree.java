package net.laboulangerie.laboulangeriemmo.core.abilities.woodcutting;

import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.Event;

import net.laboulangerie.laboulangeriemmo.api.ability.AbilityArchetype;
import net.laboulangerie.laboulangeriemmo.api.ability.AbilityExecutor;
import net.laboulangerie.laboulangeriemmo.core.combo.ComboKey;
import net.laboulangerie.laboulangeriemmo.core.combo.KeyStreak;
import net.laboulangerie.laboulangeriemmo.events.ComboCompletedEvent;

public class ThickTree extends AbilityExecutor {

    public ThickTree(AbilityArchetype archetype) {
        super(archetype);
    }

    @Override
    public boolean shouldTrigger(Event baseEvent) {
        ComboCompletedEvent event = (ComboCompletedEvent) baseEvent;
        Block block = event.getPlayer().getTargetBlock(5);
        return new KeyStreak(ComboKey.RIGHT, ComboKey.RIGHT, ComboKey.LEFT)
                .match(event.getKeyStreak()) && block.getType() != null
                && block.getType() == Material.JUNGLE_SAPLING
                || block.getType() == Material.BIRCH_SAPLING
                || block.getType() == Material.SPRUCE_SAPLING;
    }

    @Override
    public void trigger(Event baseEvent, int level) {
        ComboCompletedEvent event = (ComboCompletedEvent) baseEvent;
        Block block = event.getPlayer().getTargetBlock(5);
        World world = block.getWorld();
        TreeType treeType = null;
        switch (block.getType()) {
            case SPRUCE_SAPLING:
                treeType = TreeType.MEGA_REDWOOD;
                break;
            case JUNGLE_SAPLING:
                treeType = TreeType.JUNGLE;
                break;
            case BIRCH_SAPLING:
                treeType = TreeType.TALL_BIRCH;
                break;
            default:
                event.getPlayer().sendMessage("Ceci n'est pas un sapling valable");
                return;
        }
        block.setType(Material.AIR);
        world.generateTree(block.getLocation(), treeType);
    }
}
