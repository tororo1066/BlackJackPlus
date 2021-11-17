package advancement.serialization;

import advancement.display.FrameType;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class FrameTypeAdapter implements JsonSerializer<FrameType> {

    @Override
    public JsonElement serialize(FrameType src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getName());
    }

}
