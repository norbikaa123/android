
package hu.nje.motocatalog.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Header;

// motorok lekerese apibol
public interface MotorApi {

    // Motorok lekérése a megadott márka alapján
    @GET("api/v1/motos/makes/{marka}/models")
    Call<MotorValasz> motorok(
            @Path("marka") String marka,
            @Query("limit") int limit,
            @Header("Authorization") String authorization,
            @Header("X-Api-Secret") String secret);
}

