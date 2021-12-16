package fr.laboulangerie.laboulangeriemmo.player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.google.gson.JsonSyntaxException;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import fr.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import fr.laboulangerie.laboulangeriemmo.json.GsonSerializer;
import fr.laboulangerie.laboulangeriemmo.utils.FileUtils;

public class MmoPlayerManager {

    private GsonSerializer serializer;

    private File playersFolder;

    private Map<String, MmoPlayer> playersMap;

    public MmoPlayerManager() {
        this.serializer = LaBoulangerieMmo.PLUGIN.getSerializer();

        this.playersFolder = new File(LaBoulangerieMmo.PLUGIN.getDataFolder(), "players/");

        this.playersMap = new HashMap<>();

        Bukkit.getOnlinePlayers().stream().forEach(p -> loadPlayerData(p));
    }

    public void savePlayerData(Player player) {
        String uniqueId = player.getUniqueId().toString();
        Optional<MmoPlayer> mmoPlayerOptional = Optional.of(this.playersMap.get(uniqueId));

        if (mmoPlayerOptional.isPresent()) {
            String json = this.serializer.serialize(mmoPlayerOptional.get());
            FileUtils.save(new File(this.playersFolder, uniqueId + ".json"), json);
        } else {
            MmoPlayer mmoPlayer = new MmoPlayer(player);
            this.playersMap.put(uniqueId, mmoPlayer);

            //c'est extremement hazardeux de faire comme ça xD si ça foire je le change par un truc plus solide
            //c'est plus marrant comme ça
            savePlayerData(player);
        }
    }

    public void loadPlayerData(OfflinePlayer player) {
        String uniqueId = player.getUniqueId().toString();

        try {
            File file = new File(this.playersFolder, uniqueId + ".json");
            if (!file.exists()) throw new JsonSyntaxException("hacky");
            String json = FileUtils.read(file);

            if (!json.equals("")) {
                MmoPlayer mmoPlayer = (MmoPlayer) this.serializer.deserialize(json, MmoPlayer.class);
                this.playersMap.put(uniqueId, mmoPlayer);
            }
        } catch (JsonSyntaxException e) {
            MmoPlayer mmoPlayer = new MmoPlayer(player);
            this.playersMap.put(uniqueId, mmoPlayer);
        }
    }

    public MmoPlayer getPlayer(Player player) {
        return this.playersMap.get(player.getUniqueId().toString());
    }

    public MmoPlayer getOfflinePlayer(OfflinePlayer player) {
        MmoPlayer mmoPlayer = this.playersMap.get(player.getUniqueId().toString());
        if (mmoPlayer == null) {
            loadPlayerData(player);
            mmoPlayer = this.playersMap.get(player.getUniqueId().toString());
        }
        return mmoPlayer;
    }

    public void savePlayersData() {
        Bukkit.getOnlinePlayers().stream().forEach(p -> savePlayerData(p));
    }
}
