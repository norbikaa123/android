package hu.bme.aut.menetrend.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RouteDao {

    @Query("SELECT * FROM routes WHERE favorite = 1")
    List<Route> getFavorites();

    @Query("SELECT * FROM routes WHERE id = :id LIMIT 1")
    Route getById(long id);

    @Insert
    long insert(Route route);

    @Update
    void update(Route route);

    @Delete
    void delete(Route route);
}
