
package hu.nje.motocatalog;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import hu.nje.motocatalog.model.Motor;
import hu.nje.motocatalog.ui.KedvencekFragment;
import hu.nje.motocatalog.ui.KeresesFragment;
import hu.nje.motocatalog.ui.MotorListaFragment;
import hu.nje.motocatalog.ui.MotorReszletFragment;

public class MainActivity extends AppCompatActivity {

    private Toolbar fejlec;

    // Az alkalmazás indulásakor fut le
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // A felső fejléc beállítása
        fejlec = findViewById(R.id.fejlec);
        setSupportActionBar(fejlec);

        // A képernyő felső és alsó sávjának figyelembevétele
        View teljesOldal = findViewById(R.id.teljesOldal);

        ViewCompat.setOnApplyWindowInsetsListener(teljesOldal, (v, insets) -> {
            androidx.core.graphics.Insets savok =
                    insets.getInsets(WindowInsetsCompat.Type.statusBars()
                            | WindowInsetsCompat.Type.navigationBars());

            v.setPadding(0, savok.top, 0, savok.bottom);
            return insets;
        });

        // Oldalváltáskor frissítjük a fejlécet
        getSupportFragmentManager()
                .addOnBackStackChangedListener(this::fejlecFrissites);

        // Első indításkor a motorlistát nyitjuk meg
        if (savedInstanceState == null) {
            megnyit(new MotorListaFragment(), false);
        }

        getSupportFragmentManager().executePendingTransactions();
        fejlecFrissites();
    }

    // A hárompontos menü létrehozása
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // A menüpontokra és a vissza gombra kattintás kezelése
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            vissza();
            return true;
        }

        // A kiválasztott menüpont alapján oldalt váltunk
        if (id == R.id.menu_motorok) {
            megnyit(new MotorListaFragment(), false);
        } else if (id == R.id.menu_kereses) {
            megnyit(new KeresesFragment(), false);
        } else if (id == R.id.menu_kedvencek) {
            megnyit(new KedvencekFragment(), false);
        } else {
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    // Visszalépés az előző oldalra
    private void vissza() {

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            megnyit(new MotorListaFragment(), false);
        }
    }

    // A fejléc címének és vissza gombjának beállítása
    private void fejlecFrissites() {

        ActionBar bar = getSupportActionBar();
        if (bar == null) return;

        Fragment f = getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainer);

        boolean lista = f instanceof MotorListaFragment;
        boolean reszlet = f instanceof MotorReszletFragment;

        // A főoldalon nem jelenik meg a vissza gomb
        bar.setDisplayHomeAsUpEnabled(!lista);
        bar.setHomeButtonEnabled(!lista);

        // A részletes oldalon más címet jelenítünk meg
        bar.setTitle(reszlet ? "Motor adatai" : "MotoCatalog");
    }

    // A kiválasztott motor részletes oldalának megnyitása
    public void reszletekMegnyitasa(Motor motor) {
        megnyit(MotorReszletFragment.ujPeldany(motor), true);
    }

    // Az alkalmazás oldalai közötti váltás
    private void megnyit(Fragment fragment, boolean visszaLehetLepni) {

        FragmentManager fm = getSupportFragmentManager();

        // Ha nem kell visszalépési lehetőség, töröljük az előző oldalakat
        if (!visszaLehetLepni) {
            fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        // Az új oldal megjelenítése
        androidx.fragment.app.FragmentTransaction tr = fm.beginTransaction()
                .replace(R.id.fragmentContainer, fragment);

        // A részletes oldalról vissza tudunk lépni
        if (visszaLehetLepni) {
            tr.addToBackStack(null);
        }

        tr.commit();
        fm.executePendingTransactions();

        fejlecFrissites();
    }
}
