
package hu.nje.motocatalog.model;

import com.google.gson.annotations.SerializedName;

// motor adatok apiból
public class Motor {

    // A motor alapadatai
    public String slug;
    public String name;
    public String category;
    public int displacement;

    // Az API-ban más néven szereplő mezők
    @SerializedName("year_start") public Integer yearStart;
    @SerializedName("year_end") public Integer yearEnd;

    // motor képe márkája
    public String thumbnail;
    public String marka;

    // motor teljes neve
    public String teljesNev() {
        return marka + " " + name;
    }

    // Egyedi azonosító készit
    public String azonosit() {
        return marka + ":" + slug;
    }
}
