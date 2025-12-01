package hu.bme.aut.menetrend.ui.detail;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import hu.bme.aut.menetrend.MainActivity;
import hu.bme.aut.menetrend.R;
import hu.bme.aut.menetrend.data.AppDatabase;
import hu.bme.aut.menetrend.data.Route;
import hu.bme.aut.menetrend.data.RouteDao;

public class RouteDetailFragment extends Fragment {

    private static final String ARG_ROUTE_ID = "arg_route_id";

    private long routeId;
    private Route route;
    private RouteDao dao;

    private ImageView ivTrain;
    private TextView tvStations;
    private TextView tvTimes;
    private TextView tvTransfers;
    private TextView tvOperator;
    private Button btnFavorite;
    private Button btnEdit;
    private Button btnDelete;
    private Button btnSearchAgain;

    public static RouteDetailFragment newInstance(long routeId) {
        RouteDetailFragment fragment = new RouteDetailFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ROUTE_ID, routeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            routeId = args.getLong(ARG_ROUTE_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_route_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ivTrain = view.findViewById(R.id.iv_train_detail);
        tvStations = view.findViewById(R.id.tv_detail_stations);
        tvTimes = view.findViewById(R.id.tv_detail_times);
        tvTransfers = view.findViewById(R.id.tv_detail_transfers);
        tvOperator = view.findViewById(R.id.tv_detail_operator);
        btnFavorite = view.findViewById(R.id.btn_toggle_favorite);
        btnEdit = view.findViewById(R.id.btn_edit_route);
        btnDelete = view.findViewById(R.id.btn_delete_route);
        btnSearchAgain = view.findViewById(R.id.btn_search_again);

        dao = AppDatabase.getInstance(requireContext()).routeDao();
        route = dao.getById(routeId);

        if (route == null) {
            Toast.makeText(getContext(), "Az útvonal nem található.", Toast.LENGTH_LONG).show();
            // Ha valamiért mégis null, lépjünk vissza az előző képernyőre
            if (getActivity() != null) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
            return;
        }

        refreshUi();

        btnFavorite.setOnClickListener(v -> toggleFavorite());
        btnEdit.setOnClickListener(v -> showEditDialog());
        btnDelete.setOnClickListener(v -> deleteRoute());
        btnSearchAgain.setOnClickListener(v -> searchAgain());
    }

    private void refreshUi() {
        tvStations.setText(route.fromStation + " → " + route.toStation);
        if (route.durationMinutes > 0 && route.departureTime != null && !route.departureTime.isEmpty()) {
            tvTimes.setText(route.departureTime + " - " + route.arrivalTime +
                    " (" + route.durationMinutes + " perc)");
        } else {
            tvTimes.setText(route.departureTime);
        }
        tvTransfers.setText("Átszállások: " + route.transfers);
        tvOperator.setText("Szolgáltató: " + route.operatorName);

        String imageToLoad = (route.imageUrl == null || route.imageUrl.isEmpty())
                ? "https://img.freepik.com/premium-vector/train-logo-silhouette-vector-file-train-black-icon_856335-2000.jpg"
                : route.imageUrl;

        Glide.with(this)
                .load(imageToLoad)
                .placeholder(R.drawable.ic_train_placeholder)
                .into(ivTrain);

        if (route.favorite) {
            btnFavorite.setText("Eltávolítás a kedvencekből");
        } else {
            btnFavorite.setText("Kedvencekhez adás");
        }
    }

    private void toggleFavorite() {
        route.favorite = !route.favorite;
        dao.update(route);
        refreshUi();
    }

    private void deleteRoute() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Törlés")
                .setMessage("Biztosan törölni szeretnéd ezt az útvonalat?")
                .setPositiveButton("Igen", (dialog, which) -> {
                    dao.delete(route);
                    Toast.makeText(getContext(), "Útvonal törölve.", Toast.LENGTH_LONG).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                })
                .setNegativeButton("Mégse", null)
                .show();
    }

    private void showEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Útvonal szerkesztése");

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

        etFrom.setText(route.fromStation);
        etTo.setText(route.toStation);
        etDeparture.setText(route.departureTime);
        etArrival.setText(route.arrivalTime);
        etDuration.setText(String.valueOf(route.durationMinutes));
        etTransfers.setText(String.valueOf(route.transfers));
        etOperator.setText(route.operatorName);
        etImageUrl.setText(route.imageUrl);

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

            int duration;
            int transfers;
            try {
                duration = durStr.isEmpty() ? 0 : Integer.parseInt(durStr);
                transfers = trStr.isEmpty() ? 0 : Integer.parseInt(trStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Menetidő és átszállások számát egész számként add meg!", Toast.LENGTH_LONG).show();
                return;
            }

            route.fromStation = from;
            route.toStation = to;
            route.departureTime = dep;
            route.arrivalTime = arr;
            route.durationMinutes = duration;
            route.transfers = transfers;
            route.operatorName = op;
            route.imageUrl = img;

            dao.update(route);
            refreshUi();
        });

        builder.setNegativeButton("Mégse", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private void searchAgain() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).openResultsFragment(
                    route.fromStation,
                    route.toStation,
                    route.transfers
            );
        }
    }
}
