package net.laboulangerie.laboulangeriemmo.api.player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Stream;

import com.google.gson.JsonSyntaxException;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import net.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import net.laboulangerie.laboulangeriemmo.core.json.GsonSerializer;
import net.laboulangerie.laboulangeriemmo.utils.FileUtils;

public class MmoPlayerManager {

    private GsonSerializer serializer;

    private File playersFolder;

    private Map<String, MmoPlayer> playersMap;

    public MmoPlayerManager() {
        serializer = LaBoulangerieMmo.PLUGIN.getSerializer();

        playersFolder = new File(LaBoulangerieMmo.PLUGIN.getDataFolder(), "players/");

        playersMap = new HashMap<>();

        Bukkit.getOnlinePlayers().stream().forEach(p -> loadPlayerData(p));
    }

    public void savePlayerData(Player player) {
        String uniqueId = player.getUniqueId().toString();
        Optional<MmoPlayer> mmoPlayerOptional = Optional.of(playersMap.get(uniqueId));

        if (mmoPlayerOptional.isPresent()) {
            String json = serializer.serialize(mmoPlayerOptional.get());
            FileUtils.save(new File(playersFolder, uniqueId + ".json"), json);
        } else {
            MmoPlayer mmoPlayer = new MmoPlayer(player);
            playersMap.put(uniqueId, mmoPlayer);

            // c'est extremement hazardeux de faire comme ça xD si ça foire je le change par
            // un truc plus solide
            // c'est plus marrant comme ça
            savePlayerData(player);
        }
    }

    public void loadPlayerData(OfflinePlayer player) {
        String uniqueId = player.getUniqueId().toString();

        try {
            File file = new File(playersFolder, uniqueId + ".json");
            if (!file.exists()) {
                MmoPlayer mmoPlayer = new MmoPlayer(player);
                playersMap.put(uniqueId, mmoPlayer);
                return;
            }
            String json = FileUtils.read(file);

            if (!json.equals("")) {
                MmoPlayer mmoPlayer = (MmoPlayer) serializer.deserialize(json, MmoPlayer.class);
                playersMap.put(uniqueId, mmoPlayer);
            } else {
                MmoPlayer mmoPlayer = new MmoPlayer(player);
                playersMap.put(uniqueId, mmoPlayer);
            }
        } catch (JsonSyntaxException e) {
            LaBoulangerieMmo.PLUGIN.getLogger().log(Level.SEVERE, "Failed to load player data, disabling plugin to preserve saves's integrity.");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(LaBoulangerieMmo.PLUGIN);
        }
    }

    public MmoPlayer getPlayer(Player player) {
        return playersMap.get(player.getUniqueId().toString());
    }

    public MmoPlayer getOfflinePlayer(OfflinePlayer player) {
        MmoPlayer mmoPlayer = playersMap.get(player.getUniqueId().toString());
        if (mmoPlayer == null) {
            loadPlayerData(player);
            mmoPlayer = playersMap.get(player.getUniqueId().toString());
        }
        return mmoPlayer;
    }

    public void savePlayersData() {
        Bukkit.getOnlinePlayers().stream().forEach(p -> savePlayerData(p));
    }

    public Stream<MmoPlayer> stream() {
        return playersMap.values().stream();
    }
}
