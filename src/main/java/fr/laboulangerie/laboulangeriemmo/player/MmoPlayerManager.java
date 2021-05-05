package fr.laboulangerie.laboulangeriemmo.player;

import fr.laboulangerie.laboulangeriemmo.LaBoulangerieMmo;
import fr.laboulangerie.laboulangeriemmo.json.GsonSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MmoPlayerManager {

    private GsonSerializer serializer;

    private Map<UUID, MmoPlayer> playersMap;

    public MmoPlayerManager(LaBoulangerieMmo laBoulangerieMmo) {
        this.serializer = laBoulangerieMmo.getSerializer();

        this.playersMap = new HashMap<>();
    }



}
