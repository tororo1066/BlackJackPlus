package advancement.trigger;

import advancement.data.EntityData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.function.Consumer;

public class SummonedEntity implements Trigger {

    @Expose
    @SerializedName("entity")
    private EntityData entity;

    public void setEntity(Consumer<EntityData> consumer) {
        this.entity = new EntityData();
        consumer.accept(entity);
    }
}
