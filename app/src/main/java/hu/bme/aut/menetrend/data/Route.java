package hu.bme.aut.menetrend.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "routes")
public class Route {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String fromStation;
    public String toStation;

    public String departureTime;
    public String arrivalTime;

    public int durationMinutes;
    public int transfers;

    public String operatorName;
    public String imageUrl;

    public boolean favorite;
}
