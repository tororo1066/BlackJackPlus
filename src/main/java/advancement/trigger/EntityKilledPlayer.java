package advancement.trigger;

import advancement.data.DamageType;
import advancement.data.EntityData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.function.Consumer;

public class EntityKilledPlayer implements Trigger {

    @Expose
    @SerializedName("entity")
    private EntityData entity;

    @Expose
    @SerializedName("killing_blow")
    private DamageType killingBlow;

    public void setEntity(Consumer<EntityData> consumer) {
        this.entity = new EntityData();
        consumer.accept(entity);
    }

    public void setKillingBlow(Consumer<DamageType> consumer) {
        this.killingBlow = new DamageType();
        consumer.accept(killingBlow);
    }

}
