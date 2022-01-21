package fr.laboulangerie.laboulangeriemmo.expansions;

import fr.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import fr.laboulangerie.laboulangeriemmo.player.MmoPlayer;
import fr.laboulangerie.laboulangeriemmo.player.MmoPlayerManager;
import fr.laboulangerie.laboulangeriemmo.player.talent.Talent;

import org.bukkit.OfflinePlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

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

        // TODO Temporaire, trouver un moyen de synchroniser les talents dans tout le
        // plugin (config ?)
        String[] talents = new String[] { "baking", "fishing", "mining", "woodcutting", "thehunter" };

        for (String talentId : talents) {
            if (params.startsWith(talentId + "_")) {
                Talent talent = mmoPlayer.getTalent(talentId);

                if (params.endsWith("xp")) {
                    return Double.toString(talent.getXp());
                }

                if (params.endsWith("level")) {
                    return Integer.toString(talent.getLevel(LaBoulangerieMmo.XP_MULTIPLIER));
                }

                if (params.endsWith("xp_to_next_lvl")) {
                    return Double.toString(talent.getXpToNextLevel(LaBoulangerieMmo.XP_MULTIPLIER));
                }

                if (params.endsWith("name")) {
                    return talent.getDisplayName();
                }
            }
        }

        return null; // Placeholder is unknown by the Expansion
    }
}