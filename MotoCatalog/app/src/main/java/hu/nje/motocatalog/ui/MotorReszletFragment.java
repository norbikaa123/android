
package hu.nje.motocatalog.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import hu.nje.motocatalog.R;
import hu.nje.motocatalog.adatbazis.KedvencMotor;
import hu.nje.motocatalog.adatbazis.MotorAdatbazis;
import hu.nje.motocatalog.api.KepBetolto;
import hu.nje.motocatalog.model.Motor;

public class MotorReszletFragment extends Fragment {

    private final ExecutorService hatter = Executors.newSingleThreadExecutor();

    private Motor motor;
    private Button kedvencGomb;
    private boolean kedvenc;

    public static MotorReszletFragment ujPeldany(Motor motor) {

        MotorReszletFragment fragment = new MotorReszletFragment();
        Bundle b = new Bundle();

        b.putString("slug", motor.slug);
        b.putString("nev", motor.name);
        b.putString("marka", motor.marka);
        b.putString("kategoria", motor.category);
        b.putInt("cm3", motor.displacement);
        b.putString("kep", motor.thumbnail);

        if (motor.yearStart != null) b.putInt("evTol", motor.yearStart);
        if (motor.yearEnd != null) b.putInt("evIg", motor.yearEnd);

        fragment.setArguments(b);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reszlet, container, false);

        Bundle b = requireArguments();
        motor = new Motor();

        motor.slug = b.getString("slug");
        motor.name = b.getString("nev");
        motor.marka = b.getString("marka");
        motor.category = b.getString("kategoria");
        motor.displacement = b.getInt("cm3");
        motor.thumbnail = b.getString("kep");

        if (b.containsKey("evTol")) motor.yearStart = b.getInt("evTol");
        if (b.containsKey("evIg")) motor.yearEnd = b.getInt("evIg");

        ((TextView) view.findViewById(R.id.reszletNev))
                .setText(motor.teljesNev());

        String adatok = "Márka: " + motor.marka + "\nKategória: "
                + (motor.category == null ? "Nincs adat" : motor.category)
                + "\nHengerűrtartalom: " + motor.displacement + " cm³"
                + "\nGyártás kezdete: " + (motor.yearStart == null ? "Nincs adat" : motor.yearStart)
                + "\nGyártás vége: " + (motor.yearEnd == null ? "Nincs adat / jelenleg is gyártják" : motor.yearEnd);

        ((TextView) view.findViewById(R.id.reszletAdatok)).setText(adatok);

        KepBetolto.betolt(
                (ImageView) view.findViewById(R.id.reszletKep),
                motor.thumbnail
        );

        kedvencGomb = view.findViewById(R.id.kedvencGomb);
        kedvencGomb.setEnabled(false);

        hatter.execute(() -> {

            kedvenc = MotorAdatbazis.get(requireContext())
                    .kedvencDao().vanE(motor.azonosit()) > 0;

            if (isAdded()) requireActivity().runOnUiThread(() -> {
                gombSzoveg();
                kedvencGomb.setEnabled(true);
            });
        });

        kedvencGomb.setOnClickListener(v -> kedvencValtas());

        return view;
    }

    private void gombSzoveg() {
        kedvencGomb.setText(kedvenc
                ? "Eltávolítás a kedvencekből"
                : "Hozzáadás a kedvencekhez");
    }

    private void kedvencValtas() {

        kedvencGomb.setEnabled(false);

        hatter.execute(() -> {

            if (kedvenc) {
                MotorAdatbazis.get(requireContext())
                        .kedvencDao().torles(motor.azonosit());
            } else {
                MotorAdatbazis.get(requireContext())
                        .kedvencDao().mentes(KedvencMotor.motorbol(motor));
            }

            kedvenc = !kedvenc;

            if (isAdded()) requireActivity().runOnUiThread(() -> {

                gombSzoveg();
                kedvencGomb.setEnabled(true);

                Toast.makeText(requireContext(),
                        "Kedvencek frissítve", Toast.LENGTH_SHORT).show();
            });
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hatter.shutdown();
    }
}

 
