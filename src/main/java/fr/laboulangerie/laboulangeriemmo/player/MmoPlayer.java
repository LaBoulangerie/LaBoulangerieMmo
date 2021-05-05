package fr.laboulangerie.laboulangeriemmo.player;

import fr.laboulangerie.laboulangeriemmo.json.GsonSerializable;
import fr.laboulangerie.laboulangeriemmo.player.talent.Talent;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class MmoPlayer implements GsonSerializable {

    private UUID uniqueId;
    private String name;

    private Set<Talent> talents;

    public MmoPlayer(Player player) {

    }
}
