package net.laboulangerie.laboulangeriemmo.core.protection;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class AllowAllTalentProtection implements TalentProtection {
    private final JavaPlugin plugin;
    private final String reason;

    public AllowAllTalentProtection() {
        this(null, "NOT_INITIALIZED");
    }

    public AllowAllTalentProtection(JavaPlugin plugin, String reason) {
        this.plugin = plugin;
        this.reason = reason;
    }

    @Override
    public boolean canBreak(Player player, Block block) {
        log("BREAK", block, null);
        return true;
    }

    @Override
    public boolean canPlace(Player player, Block block, Material material) {
        log("PLACE", block, material);
        return true;
    }

    @Override
    public boolean canIgnite(Block block) {
        log("IGNITE", block, Material.FIRE);
        return true;
    }

    private void log(String action, Block block, Material resultingMaterial) {
        if (plugin == null || !plugin.getConfig().getBoolean("minelet-protection-debug", false)) return;

        plugin.getLogger().info("[MineLetProtection] mode=PERMISSIVE action=" + action
                + " allowed=true reason=" + reason + location(block)
                + (resultingMaterial == null ? "" : " resultingMaterial=" + resultingMaterial));
    }

    private String location(Block block) {
        if (block == null) return "";
        return " world=" + block.getWorld().getName() + " x=" + block.getX()
                + " y=" + block.getY() + " z=" + block.getZ()
                + " currentMaterial=" + block.getType();
    }
}
