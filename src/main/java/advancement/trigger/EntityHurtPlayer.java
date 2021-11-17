package advancement.trigger;

import advancement.data.DamageData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.function.Consumer;

public class EntityHurtPlayer implements Trigger {

    @Expose
    @SerializedName("damage")
    private DamageData damage;

    /**
     * Configuration damage settings
     *
     * @param consumer Consumer for damage configuration
     */
    public void setDamage(Consumer<DamageData> consumer) {
        this.damage = new DamageData();
        consumer.accept(damage);
    }

}
