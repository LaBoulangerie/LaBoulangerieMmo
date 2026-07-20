package net.laboulangerie.laboulangeriemmo.core.protection;

import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import fr.minelet.api.MineLetAPI;
import fr.minelet.api.protection.IProtectionService;
import fr.minelet.api.protection.ProtectionRequest;
import fr.minelet.api.protection.ProtectionResult;

public final class MineLetTalentProtection implements TalentProtection {
    private final JavaPlugin plugin;
    private final IProtectionService protection;
    private final String version;

    public MineLetTalentProtection(JavaPlugin plugin, IProtectionService protection) {
        this(plugin, protection, "unknown");
    }

    private MineLetTalentProtection(JavaPlugin plugin, IProtectionService protection, String version) {
        this.plugin = plugin;
        this.protection = protection;
        this.version = version;
    }

    public static MineLetTalentProtection create(JavaPlugin plugin) {
        if (!MineLetAPI.isAvailable()) return null;
        return new MineLetTalentProtection(plugin, MineLetAPI.getProtection(), String.valueOf(MineLetAPI.getVersion()));
    }

    public String getVersion() {
        return version;
    }

    @Override
    public boolean canBreak(Player player, Block block) {
        return safelyCheck("BREAK", block, null,
                () -> protection.check(ProtectionRequest.breakBlock(player, block)));
    }

    @Override
    public boolean canPlace(Player player, Block block, Material material) {
        return safelyCheck("PLACE", block, material,
                () -> protection.check(ProtectionRequest.placeBlock(player, block, material)));
    }

    @Override
    public boolean canIgnite(Block block) {
        return safelyCheck("IGNITE", block, Material.FIRE,
                () -> protection.check(ProtectionRequest.igniteBlock(block)));
    }

    private boolean safelyCheck(String requestedAction, Block block, Material resultingMaterial,
            ProtectionCheck check) {
        try {
            ProtectionResult result = check.result();
            if (debugEnabled()) {
                plugin.getLogger().info("[MineLetProtection] mode=ACTIVE requestedAction=" + requestedAction
                        + " allowed=" + result.allowed() + " action=" + result.action()
                        + " flag=" + result.flagKey() + " claimed=" + result.claimed()
                        + " hamlet=" + result.hamletId() + " denial=" + result.denialMessageKey()
                        + location(block)
                        + (resultingMaterial == null ? "" : " resultingMaterial=" + resultingMaterial));
            }
            return result.allowed();
        } catch (RuntimeException exception) {
            plugin.getLogger().log(Level.WARNING,
                    "MineLet protection check failed; the terrain modification was denied", exception);
            return false;
        }
    }

    private String location(Block block) {
        if (block == null) return "";
        return " world=" + block.getWorld().getName() + " x=" + block.getX()
                + " y=" + block.getY() + " z=" + block.getZ()
                + " currentMaterial=" + block.getType();
    }

    private boolean debugEnabled() {
        return plugin.getConfig() != null
                && plugin.getConfig().getBoolean("minelet-protection-debug", false);
    }

    @FunctionalInterface
    private interface ProtectionCheck {
        ProtectionResult result();
    }
}
