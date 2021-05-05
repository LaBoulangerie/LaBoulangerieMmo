package fr.laboulangerie.laboulangeriemmo.player;

import fr.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import fr.laboulangerie.laboulangeriemmo.json.GsonSerializer;
import fr.laboulangerie.laboulangeriemmo.utils.FileUtils;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MmoPlayerManager {

    private GsonSerializer serializer;

    private File playersFolder;

    private Map<String, MmoPlayer> playersMap;

    public MmoPlayerManager(LaBoulangerieMmo laBoulangerieMmo) {
        this.serializer = laBoulangerieMmo.getSerializer();

        this.playersFolder = new File(laBoulangerieMmo.getDataFolder(), "players/");

        this.playersMap = new HashMap<>();
    }

    public void savePlayerData(Player player) {
        String uniqueId = player.getUniqueId().toString();
        Optional<MmoPlayer> mmoPlayerOptional = Optional.of(this.playersMap.get(uniqueId));

        if (mmoPlayerOptional.isPresent()) {
            String json = this.serializer.serialize(mmoPlayerOptional.get());
            FileUtils.save(new File(this.playersFolder, uniqueId+".json"), json);
        } else {
            MmoPlayer mmoPlayer = new MmoPlayer(player);
            this.playersMap.put(uniqueId, mmoPlayer);

            //c'est extremement hazardeux de faire comme ça xD si ça foire je le change par un truc plus solide
            //c'est plus marrant comme ça
            savePlayerData(player);
        }
    }

    public void loadPlayerData(Player player) {
        String uniqueId = player.getUniqueId().toString();
        String json = FileUtils.read(new File(this.playersFolder, uniqueId+".json"));
        if (!json.equals("")) {
            MmoPlayer mmoPlayer = (MmoPlayer) this.serializer.deserialize(json, MmoPlayer.class);
            this.playersMap.put(uniqueId, mmoPlayer);
        } else {
            MmoPlayer mmoPlayer = new MmoPlayer(player);
            this.playersMap.put(uniqueId, mmoPlayer);
        }
    }

    public MmoPlayer getPlayer(Player player) {
        return this.playersMap.get(player.getUniqueId().toString());
    }


}
