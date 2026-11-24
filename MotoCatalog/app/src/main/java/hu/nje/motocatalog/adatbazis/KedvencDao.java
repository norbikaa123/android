
package hu.nje.motocatalog.adatbazis;

import java.util.List;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface KedvencDao {

    @Query("SELECT * FROM kedvencek ORDER BY marka, name")
    List<KedvencMotor> mind();

    @Query("SELECT COUNT(*) FROM kedvencek WHERE id = :id")
    int vanE(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void mentes(KedvencMotor motor);

    @Query("DELETE FROM kedvencek WHERE id = :id")
    void torles(String id);
}

