package net.laboulangerie.laboulangeriemmo.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import net.laboulangerie.laboulangeriemmo.core.PostProcessingEnabler;

public class GsonSerializer {

    private Gson gson;

    public GsonSerializer() {
        gson = new GsonBuilder().setPrettyPrinting().serializeNulls()
                .registerTypeAdapterFactory(new PostProcessingEnabler()).create();
    }

    public String serialize(GsonSerializable serializable) {
        return gson.toJson(serializable);
    }

    public GsonSerializable deserialize(String json, Class<? extends GsonSerializable> type)
            throws JsonSyntaxException {
        return gson.fromJson(json, type);
    }
}
