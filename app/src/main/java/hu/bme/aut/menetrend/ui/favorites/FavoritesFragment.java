package hu.bme.aut.menetrend.ui.favorites;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import hu.bme.aut.menetrend.MainActivity;
import hu.bme.aut.menetrend.R;
import hu.bme.aut.menetrend.data.AppDatabase;
import hu.bme.aut.menetrend.data.Route;
import hu.bme.aut.menetrend.data.RouteDao;
import hu.bme.aut.menetrend.ui.adapter.RouteAdapter;

public class FavoritesFragment extends Fragment {

    private RecyclerView rvFavorites;
    private RouteAdapter adapter;
    private AppDatabase db;
    private RouteDao dao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rvFavorites = view.findViewById(R.id.rv_favorites);
        rvFavorites.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new RouteAdapter(route -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openRouteDetailFragment(route.id);
            }
        });
        rvFavorites.setAdapter(adapter);

        db = AppDatabase.getInstance(requireContext());
        dao = db.routeDao();

        FloatingActionButton fab = view.findViewById(R.id.fab_add_route);
        fab.setOnClickListener(v -> showEditDialog(null));

        loadFavorites();
    }

    private void loadFavorites() {
        List<Route> favorites = dao.getFavorites();
        adapter.setRoutes(favorites);
    }

    private void showEditDialog(@Nullable Route routeToEdit) {
        boolean isEdit = routeToEdit != null;

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(isEdit ? "Útvonal szerkesztése" : "Új útvonal");

        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_route_edit, null, false);

        EditText etFrom = dialogView.findViewById(R.id.et_edit_from);
        EditText etTo = dialogView.findViewById(R.id.et_edit_to);
        EditText etDeparture = dialogView.findViewById(R.id.et_edit_departure);
        EditText etArrival = dialogView.findViewById(R.id.et_edit_arrival);
        EditText etDuration = dialogView.findViewById(R.id.et_edit_duration);
        EditText etTransfers = dialogView.findViewById(R.id.et_edit_transfers);
        EditText etOperator = dialogView.findViewById(R.id.et_edit_operator);
        EditText etImageUrl = dialogView.findViewById(R.id.et_edit_image_url);

        if (isEdit) {
            etFrom.setText(routeToEdit.fromStation);
            etTo.setText(routeToEdit.toStation);
            etDeparture.setText(routeToEdit.departureTime);
            etArrival.setText(routeToEdit.arrivalTime);
            etDuration.setText(String.valueOf(routeToEdit.durationMinutes));
            etTransfers.setText(String.valueOf(routeToEdit.transfers));
            etOperator.setText(routeToEdit.operatorName);
            etImageUrl.setText(routeToEdit.imageUrl);
        }

        builder.setView(dialogView);

        builder.setPositiveButton("Mentés", (dialog, which) -> {
            String from = etFrom.getText().toString().trim();
            String to = etTo.getText().toString().trim();
            String dep = etDeparture.getText().toString().trim();
            String arr = etArrival.getText().toString().trim();
            String durStr = etDuration.getText().toString().trim();
            String trStr = etTransfers.getText().toString().trim();
            String op = etOperator.getText().toString().trim();
            String img = etImageUrl.getText().toString().trim();

            if (from.isEmpty() || to.isEmpty()) {
                Toast.makeText(getContext(), "Honnan és Hová mezők kötelezőek.", Toast.LENGTH_LONG).show();
                return;
            }

            int duration = 0;
            int transfers = 0;
            try {
                if (!durStr.isEmpty()) {
                    duration = Integer.parseInt(durStr);
                }
                if (!trStr.isEmpty()) {
                    transfers = Integer.parseInt(trStr);
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Menetidő és átszállások számát egész számként add meg!", Toast.LENGTH_LONG).show();
                return;
            }

            if (isEdit) {
                routeToEdit.fromStation = from;
                routeToEdit.toStation = to;
                routeToEdit.departureTime = dep;
                routeToEdit.arrivalTime = arr;
                routeToEdit.durationMinutes = duration;
                routeToEdit.transfers = transfers;
                routeToEdit.operatorName = op;
                routeToEdit.imageUrl = img;
                routeToEdit.favorite = true;
                dao.update(routeToEdit);
            } else {
                Route route = new Route();
                route.fromStation = from;
                route.toStation = to;
                route.departureTime = dep;
                route.arrivalTime = arr;
                route.durationMinutes = duration;
                route.transfers = transfers;
                route.operatorName = op.isEmpty() ? "Mentett keresés" : op;
                route.imageUrl = img;
                route.favorite = true;
                dao.insert(route);
            }

            loadFavorites();
        });

        builder.setNegativeButton("Mégse", (dialog, which) -> dialog.dismiss());

        builder.show();
    }
}
