package advancement.trigger;

import advancement.data.ItemData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.function.Consumer;

public class FilledBucket implements Trigger {

    @Expose
    @SerializedName("item")
    private ItemData item;

    public void setItem(Consumer<ItemData> consumer) {
        this.item = new ItemData();
        consumer.accept(item);
    }

}
