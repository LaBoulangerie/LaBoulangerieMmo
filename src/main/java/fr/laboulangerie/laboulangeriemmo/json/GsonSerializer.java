package fr.laboulangerie.laboulangeriemmo.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonSerializer {


    private Gson gson;

    public GsonSerializer() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .create();
    }

    public String serialize(GsonSerializable serializable) {
        return this.gson.toJson(serializable);
    }

    public GsonSerializable deserialize(String json, Class<? extends GsonSerializable> type) {
        return this.gson.fromJson(json, type);
    }
}
