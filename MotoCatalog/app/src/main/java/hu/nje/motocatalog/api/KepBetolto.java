
package hu.nje.motocatalog.api;

import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

public class KepBetolto {

    // A motor képének betöltése
    public static void betolt(ImageView kep, String url) {

        // API-azonosítók hozzáadása
        GlideUrl glideUrl = new GlideUrl(url, new LazyHeaders.Builder()
                .addHeader("Authorization", "Bearer " + ApiKliens.API_KEY)
                .addHeader("X-Api-Secret", ApiKliens.API_SECRET)
                .build());

        // A kép betöltése és megjelenítése
        Glide.with(kep)
                .load(glideUrl)
                .into(kep);
    }
}
