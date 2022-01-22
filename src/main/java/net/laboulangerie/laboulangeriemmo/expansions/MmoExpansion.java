package net.laboulangerie.laboulangeriemmo.expansions;

import org.bukkit.OfflinePlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.player.MmoPlayer;
import net.laboulangerie.laboulangeriemmo.player.MmoPlayerManager;
import net.laboulangerie.laboulangeriemmo.player.talent.Talent;

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

        String talentId = params.split("_")[0];
        Talent talent = mmoPlayer.getTalent(talentId);

        if (talent == null) {
            return null;
        }

        if (params.equals(talentId + "_xp")) {
            return Double.toString(talent.getXp());
        }

        if (params.equals(talentId + "_level")) {
            return Integer.toString(talent.getLevel(LaBoulangerieMmo.XP_MULTIPLIER));
        }

        if (params.equals(talentId + "_xp_in_level")) {
            return Double.toString(talent.getXp() - talent.getLevelXp(LaBoulangerieMmo.XP_MULTIPLIER));
        }

        if (params.equals(talentId + "_xp_to_next_lvl")) {
            return Double.toString(talent.getXpToNextLevel(LaBoulangerieMmo.XP_MULTIPLIER));
        }

        if (params.equals(talentId + "_name")) {
            return talent.getDisplayName();
        }

        return null; // Placeholder is unknown by the Expansion
    }
}