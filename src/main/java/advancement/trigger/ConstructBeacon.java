package advancement.trigger;

import advancement.data.Range;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ConstructBeacon implements Trigger {

    @Expose
    @SerializedName("level")
    private Range<Integer> level;

    /**
     * Set the required tier of the updated beacon structure.
     *
     * @param minimum Minimum level
     * @param maximum Maximum level
     */
    public void setLevel(int minimum, int maximum) {
        this.level = new Range<>(minimum, maximum);
    }

    /**
     * Set the required tier of the updated beacon structure.
     *
     * @param level Level required
     */
    public void setLevel(int level) {
        this.level = new Range<>(level);
    }
}
