
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import hu.nje.motocatalog.MainActivity;
import hu.nje.motocatalog.R;
import hu.nje.motocatalog.adatbazis.KedvencMotor;
import hu.nje.motocatalog.adatbazis.MotorAdatbazis;
import hu.nje.motocatalog.model.Motor;

public class KedvencekFragment extends Fragment {

    private final ExecutorService hatter = Executors.newSingleThreadExecutor();

    private MotorAdapter adapter;
    private TextView uzenet;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_lista, container, false);
        ((TextView) view.findViewById(R.id.listaCim)).setText("Kedvenc motorok");

        uzenet = view.findViewById(R.id.listaUzenet);

        RecyclerView lista = view.findViewById(R.id.motorRecycler);
        lista.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new MotorAdapter(motor ->
                ((MainActivity) requireActivity()).reszletekMegnyitasa(motor));

        lista.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        frissit();
    }

    private void frissit() {
        hatter.execute(() -> {

            List<KedvencMotor> mentett =
                    MotorAdatbazis.get(requireContext()).kedvencDao().mind();

            List<Motor> motorok = new ArrayList<>();

            for (KedvencMotor k : mentett) {
                motorok.add(k.motorrá());
            }

            if (isAdded()) requireActivity().runOnUiThread(() -> {
                adapter.frissit(motorok);
                uzenet.setText(motorok.isEmpty()
                        ? "Még nincs kedvenc motorod."
                        : motorok.size() + " kedvenc motor");
            });
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hatter.shutdown();
    }
}
