
package hu.nje.motocatalog.adatbazis;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import hu.nje.motocatalog.model.Motor;

@Entity(tableName = "kedvencek")
public class KedvencMotor {

    @PrimaryKey @NonNull public String id = "";

    public String slug;
    public String name;
    public String marka;
    public String category;
    public int displacement;
    public Integer yearStart;
    public Integer yearEnd;
    public String thumbnail;

    
    public static KedvencMotor motorbol(Motor m) {
        KedvencMotor k = new KedvencMotor();

        k.id = m.azonosit();
        k.slug = m.slug;
        k.name = m.name;
        k.marka = m.marka;
        k.category = m.category;
        k.displacement = m.displacement;
        k.yearStart = m.yearStart;
        k.yearEnd = m.yearEnd;
        k.thumbnail = m.thumbnail;

        return k;
    }

   
    public Motor motorrá() {
        Motor m = new Motor();

        m.slug = slug;
        m.name = name;
        m.marka = marka;
        m.category = category;
        m.displacement = displacement;
        m.yearStart = yearStart;
        m.yearEnd = yearEnd;
        m.thumbnail = thumbnail;

        return m;
    }
}

