package advancement.trigger;

import advancement.data.LocationData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.function.Consumer;

public class HeroOfTheVillage implements Trigger {

    @Expose
    @SerializedName("location")
    private LocationData location;

    public void setLocation(Consumer<LocationData> consumer) {
        this.location = new LocationData();
        consumer.accept(location);
    }
}
