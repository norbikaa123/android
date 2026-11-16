
package hu.nje.motocatalog.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiKliens {

    // Az API eléréséhez szükséges kulcsok
    public static final String API_KEY = "ci_bfdda7421a6a853c22ef117532c1c8bfd69ef749b10c438da6718308";
    public static final String API_SECRET = "a22c5f52e07558de74ae38f6843b371bf35beb319399fb671713bcc00a8202aa";

    // api kommunikacio letrehozása
    public static MotorApi letrehoz() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://carimagesapi.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(MotorApi.class);
    }
}
