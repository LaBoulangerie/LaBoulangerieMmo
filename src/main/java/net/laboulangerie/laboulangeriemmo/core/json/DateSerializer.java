package net.laboulangerie.laboulangeriemmo.core.json;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.laboulangerie.laboulangeriemmo.api.player.CooldownsHolder;

public class DateSerializer implements JsonSerializer<CooldownsHolder>, JsonDeserializer<CooldownsHolder> {

    @Override
    public JsonElement serialize(CooldownsHolder cooldownsHolder, Type type, JsonSerializationContext jsonCtx) {
        JsonObject obj = new JsonObject();
        JsonObject cooldownsJson = new JsonObject();
        for (Entry<String, Date> entry : cooldownsHolder.getCooldowns().entrySet()) {
            cooldownsJson.addProperty(entry.getKey(), entry.getValue().getTime());
        }
        obj.add("cooldowns", cooldownsJson);
        return obj;
    }

    @Override
    public CooldownsHolder deserialize(JsonElement json, Type type, JsonDeserializationContext jsonCtx) throws JsonParseException {
        CooldownsHolder holder = new CooldownsHolder();
        HashMap<String, Date> map = new HashMap<>();

        if (json.getAsJsonObject().has("cooldowns")) {
            JsonObject cooldownsJson = json.getAsJsonObject().get("cooldowns").getAsJsonObject();
            boolean shouldAttemptDateConversion = true;

            for (Entry<String, JsonElement> entry : cooldownsJson.asMap().entrySet()) {
                // Date serialization format changed somewhere between java 17 and 21
                if (shouldAttemptDateConversion && entry.getValue().isJsonPrimitive() && entry.getValue().getAsJsonPrimitive().isString()) {
                    SimpleDateFormat formater = new SimpleDateFormat("MMM dd, yyyy, hh:mm:ss a", Locale.US);

                    try {
                        map.put(entry.getKey(), formater.parse(entry.getValue().getAsString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    shouldAttemptDateConversion = false;
                    map.put(entry.getKey(), Date.from(Instant.ofEpochMilli(entry.getValue().getAsLong())));
                }
            }
        }

        holder.setCooldowns(map);
        return holder;
    }
}
