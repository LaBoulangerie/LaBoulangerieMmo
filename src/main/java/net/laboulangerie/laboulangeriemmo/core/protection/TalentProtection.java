package net.laboulangerie.laboulangeriemmo.core.protection;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface TalentProtection {
    boolean canBreak(Player player, Block block);

    boolean canPlace(Player player, Block block, Material material);

    boolean canIgnite(Block block);
}
