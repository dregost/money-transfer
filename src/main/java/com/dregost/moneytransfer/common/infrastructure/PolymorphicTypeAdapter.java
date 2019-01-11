package com.dregost.moneytransfer.common.infrastructure;

import com.google.gson.*;
import lombok.*;

import java.lang.reflect.Type;

@AllArgsConstructor(staticName = "of")
public class PolymorphicTypeAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T> {
    private static final String TYPE_PROPERTY = "type";
    private static final String DATA_PROPERTY = "data";

    private final Gson gson;

    @Override
    public JsonElement serialize(final T object,
                                 final Type type,
                                 final JsonSerializationContext jsonSerializationContext) {
        val result = new JsonObject();
        val typeName = object.getClass().getName();
        result.addProperty(TYPE_PROPERTY, typeName);
        val data = gson.toJsonTree(object);
        result.add(DATA_PROPERTY, data);
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T deserialize(final JsonElement jsonElement,
                         final Type type,
                         final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        val jsonObject = jsonElement.getAsJsonObject();
        val jsonPrimitive = (JsonPrimitive) jsonObject.get(TYPE_PROPERTY);
        val className = jsonPrimitive.getAsString();

        try {
            val cls = Class.forName(className);
            return (T) gson.fromJson(jsonObject.get(DATA_PROPERTY), cls);
        } catch(Exception e) {
            throw new JsonParseException(e.getMessage());
        }
    }
}
