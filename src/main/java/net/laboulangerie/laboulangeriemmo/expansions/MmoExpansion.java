package net.laboulangerie.laboulangeriemmo.expansions;

import java.util.List;
import java.util.Locale;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.api.player.MmoPlayer;
import net.laboulangerie.laboulangeriemmo.api.player.MmoPlayerManager;
import net.laboulangerie.laboulangeriemmo.api.talent.Talent;

public class MmoExpansion extends PlaceholderExpansion {

    private MmoPlayerManager mmoPlayerManager;

    public MmoExpansion() {
        mmoPlayerManager = LaBoulangerieMmo.PLUGIN.getMmoPlayerManager();
    }

    @Override
    public String getAuthor() {
        return "La Boulangerie";
    }

    @Override
    public String getIdentifier() {
        return "lbmmo";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on
                     // reload
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        MmoPlayer mmoPlayer = mmoPlayerManager.getOfflinePlayer(player);

        if (params.equalsIgnoreCase("palier")) {
            return Integer.toString(mmoPlayer.getPalier());
        }

        if (params.equalsIgnoreCase("palier_colored")) {
            FileConfiguration config = LaBoulangerieMmo.PLUGIN.getConfig();
            List<String> colors = config.getStringList("palier-coloration");

            int maxTalentLevel = config.getInt("max-talent-level", 100);
            int talentSize = LaBoulangerieMmo.talentsRegistry.getTalents().size();

            Integer maxPalier = maxTalentLevel * talentSize;
            Integer palier = mmoPlayer.getPalier();

            float progress = ((float) palier) / maxPalier;

            return String.format(Locale.US, "<transition:" + String.join(":", colors) + ":%.2f>%d</transition>",
                    progress, palier);
        }

        String talentId = params.split("_")[0];
        Talent talent = mmoPlayer.getTalent(talentId);

        if (params.equals("town_total_level")) {
            return Integer.toString(MmoPlayer.getTownTotalLevel(mmoPlayer.getTown()));
        }

        if (params.equals("nation_total_level")) {
            return Integer.toString(MmoPlayer.getNationTotalLevel(mmoPlayer.getNation()));
        }

        if (talent == null) {
            return null;
        }

        if (params.equals(talentId + "_xp")) {
            return Double.toString(talent.getXp());
        }

        if (params.equals(talentId + "_level")) {
            return Integer.toString(talent.getLevel());
        }

        if (params.equals(talentId + "_xp_in_level")) {
            return Double.toString(talent.getXp() - talent.getLevelXp());
        }

        if (params.equals(talentId + "_xp_to_next_lvl")) {
            return Double.toString(talent.getXpToNextLevel());
        }

        if (params.equals(talentId + "_name")) {
            return talent.getDisplayName();
        }

        return null; // Placeholder is unknown by the Expansion
    }
}
