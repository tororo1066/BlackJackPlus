package advancement.serialization;

import advancement.data.DimensionType;
import advancement.data.EffectType;
import advancement.data.PotionType;
import advancement.display.FrameType;
import advancement.utility.KeyValue;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.*;
import net.md_5.bungee.chat.*;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.StructureType;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;

import java.util.List;
import java.util.Map;

public class ObjectSerializer {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .excludeFieldsWithoutExposeAnnotation()
            .registerTypeHierarchyAdapter(Material.class, new MaterialAdapter())
            .registerTypeHierarchyAdapter(EntityType.class, new EntityTypeAdapter())
            .registerTypeHierarchyAdapter(PotionType.class, new PotionTypeAdapter())
            .registerTypeHierarchyAdapter(DimensionType.class, new DimensionTypeAdapter())
            .registerTypeHierarchyAdapter(NamespacedKey.class, new NamespacedKeyAdapter())
            .registerTypeHierarchyAdapter(List.class, new ListAdapter())
            .registerTypeHierarchyAdapter(FrameType.class, new FrameTypeAdapter())
            .registerTypeHierarchyAdapter(Map.class, new MapAdapter())
            .registerTypeHierarchyAdapter(EffectType.class, new EffectTypeAdapter())
            .registerTypeHierarchyAdapter(KeyValue.class, new KeyValueAdapter())
            .registerTypeHierarchyAdapter(Biome.class, new BiomeAdapter())
            .registerTypeHierarchyAdapter(StructureType.class, new StructureTypeAdapter())
            .registerTypeHierarchyAdapter(GameMode.class, new GameModeAdapter())
            .registerTypeAdapter(BaseComponent.class, new ComponentSerializer())
            .registerTypeAdapter(TextComponent.class, new TextComponentSerializer())
            .registerTypeAdapter(TranslatableComponent.class, new TranslatableComponentSerializer())
            .registerTypeAdapter(KeybindComponent.class, new KeybindComponentSerializer())
            .registerTypeAdapter(ScoreComponent.class, new ScoreComponentSerializer())
            .registerTypeAdapter(SelectorComponent.class, new SelectorComponentSerializer())
            .registerTypeAdapter(Entity.class, new EntitySerializer())
            .registerTypeAdapter(Text.class, new TextSerializer())
            .registerTypeAdapter(Item.class, new ItemSerializer())
            .registerTypeAdapter(ItemTag.class, new ItemTag.Serializer())
            .create();

    public String serialize(Object object) {
        return GSON.toJson(object);
    }

}
