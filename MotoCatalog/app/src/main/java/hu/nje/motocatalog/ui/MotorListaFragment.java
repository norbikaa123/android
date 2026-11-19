
package hu.nje.motocatalog.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import hu.nje.motocatalog.MainActivity;
import hu.nje.motocatalog.R;
import hu.nje.motocatalog.api.ApiKliens;
import hu.nje.motocatalog.api.MotorValasz;
import hu.nje.motocatalog.model.Motor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MotorListaFragment extends Fragment {

    private MotorAdapter adapter;
    private TextView uzenet;
    private final List<Motor> motorok = new ArrayList<>();

    // A főoldalon megjelenő márkák
    private final String[] markak = {"ducati", "honda", "yamaha", "kawasaki"};

    // A főoldal létrehozása
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_lista, container, false);

        uzenet = view.findViewById(R.id.listaUzenet);

        RecyclerView lista = view.findViewById(R.id.motorRecycler);
        lista.setLayoutManager(new LinearLayoutManager(requireContext()));

        // A motorlista beállítása
        adapter = new MotorAdapter(motor ->
                ((MainActivity) requireActivity()).reszletekMegnyitasa(motor));

        lista.setAdapter(adapter);

        betolt();

        return view;
    }

    // Motorok lekérése az API-ból
    private void betolt() {

        uzenet.setText("Motorok betöltése...");
        motorok.clear();

        // 4márkából 40et jelenitsen meg
        for (String marka : markak) {

            ApiKliens.letrehoz().motorok(marka, 40,
                            "Bearer " + ApiKliens.API_KEY, ApiKliens.API_SECRET)
                    .enqueue(new Callback<MotorValasz>() {

                        // jó válasz feldolgozása
                        @Override
                        public void onResponse(@NonNull Call<MotorValasz> call,
                                               @NonNull Response<MotorValasz> response) {

                            if (!isAdded() || getView() == null) return;

                            if (response.isSuccessful()
                                    && response.body() != null
                                    && response.body().data != null) {

                                for (Motor motor : response.body().data) {
                                    motor.marka = marka.substring(0, 1).toUpperCase()
                                            + marka.substring(1);

                                    motorok.add(motor);
                                }

                                adapter.frissit(new ArrayList<>(motorok));
                                uzenet.setText("");
                            }
                        }

                        // Ha nem sikerült a lekérés
                        @Override
                        public void onFailure(@NonNull Call<MotorValasz> call,
                                              @NonNull Throwable t) {

                            if (isAdded() && getView() != null && motorok.isEmpty()) {
                                uzenet.setText("Nem sikerült motorokat betölteni.");
                            }
                        }
                    });
        }
    }
}
