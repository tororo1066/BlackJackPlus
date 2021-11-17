package advancement.serialization;

import advancement.data.PotionType;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class PotionTypeAdapter implements JsonSerializer<PotionType> {

    @Override
    public JsonElement serialize(PotionType src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src.getKey());
    }

}
