
package hu.nje.motocatalog.adatbazis;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {KedvencMotor.class}, version = 1, exportSchema = false)
public abstract class MotorAdatbazis extends RoomDatabase {

    public abstract KedvencDao kedvencDao();

    private static MotorAdatbazis peldany;

    public static synchronized MotorAdatbazis get(Context context) {

        if (peldany == null) {
            peldany = Room.databaseBuilder(context.getApplicationContext(),
                    MotorAdatbazis.class, "motorok.db").build();
        }

        return peldany;
    }
}
