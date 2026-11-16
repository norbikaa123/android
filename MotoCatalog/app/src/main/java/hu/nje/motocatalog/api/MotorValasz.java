
package hu.nje.motocatalog.api;

import java.util.List;
import hu.nje.motocatalog.model.Motor;

// Az API-tól kapott válasz adatait tárolja
public class MotorValasz {

    // A lekért motorok listája
    public List<Motor> data;

    // összes találat száma
    public int total;
}

