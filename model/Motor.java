
package hu.nje.motocatalog.model;

import com.google.gson.annotations.SerializedName;

public class Motor {

    public String slug;
    public String name;
    public String category;
    public int displacement;

    @SerializedName("year_start") public Integer yearStart;
    @SerializedName("year_end") public Integer yearEnd;

    public String thumbnail;
    public String marka;

    public String teljesNev() {
        return marka + " " + name;
    }

    public String azonosit() {
        return marka + ":" + slug;
    }
}
