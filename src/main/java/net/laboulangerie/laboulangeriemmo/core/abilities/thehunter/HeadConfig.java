package net.laboulangerie.laboulangeriemmo.core.abilities.thehunter;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;

public class HeadConfig {
    private final String textureValue;
    private final String rarity;
    private final String condition;
    private final String displayName;
    private final Material material;

    public HeadConfig(String textureValue, String rarity, String condition, String displayName, String materialName) {
        this.textureValue = textureValue;
        this.rarity = rarity;
        this.condition = condition;
        this.displayName = displayName;
        this.material = materialName == null ? Material.PLAYER_HEAD : Material.matchMaterial(materialName);

        if (material == null || (!material.name().endsWith("_HEAD") && !material.name().endsWith("_SKULL")))
            throw new IllegalArgumentException("Invalid head material: " + materialName);
        if (material == Material.PLAYER_HEAD && (textureValue == null || textureValue.isEmpty()))
            throw new IllegalArgumentException("A PLAYER_HEAD requires a texture");
    }

    public String getTextureValue() {
        return textureValue;
    }

    public String getRarity() {
        return rarity;
    }

    public String getCondition() {
        return condition;
    }

    public String getDisplayName() {
        return displayName;
    }

    public float getRarityMultiplier() {
        return (float) LaBoulangerieMmo.PLUGIN.getConfig().getDouble("trophy-rarities." + rarity.toLowerCase(), 1.0);
    }

    /**
     * Extracts the texture URL from a base64-encoded texture value.
     * The base64 decodes to JSON like: {"textures":{"SKIN":{"url":"http://textures.minecraft.net/texture/..."}}}
     */
    private String extractTextureUrl(String base64Value) {
        try {
            String decoded = new String(Base64.getDecoder().decode(base64Value));
            JsonObject json = JsonParser.parseString(decoded).getAsJsonObject();
            return json.getAsJsonObject("textures")
                       .getAsJsonObject("SKIN")
                       .get("url")
                       .getAsString();
        } catch (Exception e) {
            LaBoulangerieMmo.PLUGIN.getLogger().warning("Failed to decode texture value: " + e.getMessage());
            return null;
        }
    }

    public ItemStack createHead() {
        ItemStack head = new ItemStack(material);
        ItemMeta meta = head.getItemMeta();

        if (material == Material.PLAYER_HEAD && meta instanceof SkullMeta skullMeta) {
            try {
                String textureUrl = extractTextureUrl(textureValue);
                if (textureUrl != null) {
                    UUID profileId = UUID.nameUUIDFromBytes(textureValue.getBytes(StandardCharsets.UTF_8));
                    PlayerProfile profile = Bukkit.createPlayerProfile(profileId);
                    PlayerTextures textures = profile.getTextures();

                    java.net.URL url = java.net.URI.create(textureUrl).toURL();
                    textures.setSkin(url);
                    profile.setTextures(textures);
                    skullMeta.setOwnerProfile(profile);
                }
            } catch (Exception e) {
                LaBoulangerieMmo.PLUGIN.getLogger().warning("Failed to set head texture: " + e.getMessage());
            }
        }

        if (displayName != null && !displayName.isEmpty()) {
            meta.setDisplayName("§b" + displayName);
        }

        head.setItemMeta(meta);
        return head;
    }
}
