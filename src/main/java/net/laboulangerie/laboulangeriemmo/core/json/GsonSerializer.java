package net.laboulangerie.laboulangeriemmo.core.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.laboulangerie.laboulangeriemmo.api.player.CooldownsHolder;

public class GsonSerializer {

    private Gson gson;

    public GsonSerializer() {
        gson = new GsonBuilder().setPrettyPrinting().serializeNulls()
            .registerTypeAdapterFactory(new PostProcessingEnabler())
            .registerTypeAdapter(CooldownsHolder.class, new DateSerializer())
            .create();
    }

    public String serialize(GsonSerializable serializable) {
        return gson.toJson(serializable);
    }

    public GsonSerializable deserialize(String json, Class<? extends GsonSerializable> type)
            throws JsonSyntaxException {
        return gson.fromJson(json, type);
    }
}
