package advancement.serialization;

import advancement.data.EffectType;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class EffectTypeAdapter implements JsonSerializer<EffectType> {

    @Override
    public JsonElement serialize(EffectType src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src.getKey());
    }

}
