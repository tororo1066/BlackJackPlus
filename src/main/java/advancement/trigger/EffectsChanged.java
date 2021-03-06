package advancement.trigger;

import advancement.data.EffectData;
import advancement.data.EffectType;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class EffectsChanged implements Trigger {

    @Expose
    @SerializedName("effects")
    private Map<EffectType, EffectData> effects;

    /**
     * Add effect to required effects
     *
     * @param effectType Type of effect
     * @param consumer Modifiers of effect
     */
    public void addEffect(EffectType effectType, Consumer<EffectData> consumer) {
        if (this.effects == null) {
            this.effects = new HashMap<>();
        }

        EffectData effect = new EffectData();
        consumer.accept(effect);

        this.effects.put(effectType, effect);
    }

}
