

package hu.nje.motocatalog.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import hu.nje.motocatalog.MainActivity;
import hu.nje.motocatalog.R;
import hu.nje.motocatalog.api.ApiKliens;
import hu.nje.motocatalog.api.MotorValasz;
import hu.nje.motocatalog.model.Motor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KeresesFragment extends Fragment {

    private MotorAdapter adapter;
    private TextView uzenet;
    private Call<MotorValasz> aktualisKeres;

    // A keresőoldal létrehozása
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_kereses, container, false);

        EditText markaMezo = view.findViewById(R.id.keresMarka);
        EditText szovegMezo = view.findViewById(R.id.keresSzoveg);
        uzenet = view.findViewById(R.id.keresUzenet);

        RecyclerView lista = view.findViewById(R.id.keresLista);
        lista.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new MotorAdapter(motor ->
                ((MainActivity) requireActivity()).reszletekMegnyitasa(motor));

        lista.setAdapter(adapter);

        // Keresés gomb
        view.findViewById(R.id.keresGomb).setOnClickListener(v -> {

            String marka = markaMezo.getText().toString().trim().toLowerCase(Locale.ROOT);
            String szoveg = szovegMezo.getText().toString().trim().toLowerCase(Locale.ROOT);

            // alapértelmezett ducatti van megadva
            if (marka.isEmpty()) {
                marka = "ducati";
            }

            keres(marka, szoveg);
        });

        return view;
    }

    // Motorok keresése
    private void keres(String marka, String szoveg) {

        if (aktualisKeres != null) {
            aktualisKeres.cancel();
        }

        uzenet.setText("Motorok keresése...");
        adapter.frissit(new ArrayList<>());

        aktualisKeres = ApiKliens.letrehoz().motorok(marka, 100,
                "Bearer " + ApiKliens.API_KEY, ApiKliens.API_SECRET);

        aktualisKeres.enqueue(new Callback<MotorValasz>() {

            @Override
            public void onResponse(@NonNull Call<MotorValasz> call,
                                   @NonNull Response<MotorValasz> response) {

                if (!isAdded() || getView() == null || call != aktualisKeres) return;

                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().data != null) {

                    List<Motor> talalatok = new ArrayList<>();

                    for (Motor motor : response.body().data) {

                        motor.marka = marka;

                        String nev = motor.name == null ? "" : motor.name.toLowerCase(Locale.ROOT);
                        String kat = motor.category == null ? "" : motor.category.toLowerCase(Locale.ROOT);
                        String azonosito = motor.slug == null ? "" : motor.slug.toLowerCase(Locale.ROOT);

                        if (szoveg.isEmpty() || nev.contains(szoveg)
                                || kat.contains(szoveg) || azonosito.contains(szoveg)) {
                            talalatok.add(motor);
                        }
                    }

                    adapter.frissit(talalatok);

                    if (talalatok.isEmpty()) {
                        uzenet.setText("Nincs találat.");
                    } else {
                        uzenet.setText(talalatok.size() + " találat.");
                    }

                } else {
                    uzenet.setText("Nem sikerült a keresés.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<MotorValasz> call,
                                  @NonNull Throwable t) {

                if (isAdded() && getView() != null && call == aktualisKeres
                        && !call.isCanceled()) {
                    uzenet.setText("Hálózati hiba.");
                }
            }
        });
    }

    // oldal bezáráskor keresés leállit
    @Override
    public void onDestroyView() {
        if (aktualisKeres != null) {
            aktualisKeres.cancel();
        }
        super.onDestroyView();
    }
}

