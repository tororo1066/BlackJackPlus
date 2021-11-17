package advancement.trigger;

import advancement.data.DamageData;
import advancement.data.EntityData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.function.Consumer;

public class PlayerHurtEntity implements Trigger {

    @Expose
    @SerializedName("damage")
    private DamageData damage;

    @Expose
    @SerializedName("entity")
    private EntityData entity;

    public void setDamage(Consumer<DamageData> consumer) {
        this.damage = new DamageData();
        consumer.accept(damage);
    }

    public void setEntity(Consumer<EntityData> consumer) {
        this.entity = new EntityData();
        consumer.accept(entity);
    }

 }
