package hu.bme.aut.menetrend.ui.results;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import hu.bme.aut.menetrend.MainActivity;
import hu.bme.aut.menetrend.R;
import hu.bme.aut.menetrend.data.AppDatabase;
import hu.bme.aut.menetrend.data.Route;
import hu.bme.aut.menetrend.data.RouteDao;
import hu.bme.aut.menetrend.ui.adapter.RouteAdapter;

public class ResultsFragment extends Fragment {

    private static final String ARG_FROM = "arg_from";
    private static final String ARG_TO = "arg_to";
    private static final String ARG_MAX_TRANSFERS = "arg_max_transfers";

    private String from;
    private String to;
    private int maxTransfers;

    private RecyclerView rvRoutes;
    private RouteAdapter adapter;
    private Spinner spinnerSort;
    private EditText etFilter;

    private final List<Route> allRoutes = new ArrayList<>();

    public static ResultsFragment newInstance(String from, String to, int maxTransfers) {
        ResultsFragment fragment = new ResultsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FROM, from);
        args.putString(ARG_TO, to);
        args.putInt(ARG_MAX_TRANSFERS, maxTransfers);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            from = args.getString(ARG_FROM);
            to = args.getString(ARG_TO);
            maxTransfers = args.getInt(ARG_MAX_TRANSFERS, 0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_results, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rvRoutes = view.findViewById(R.id.rv_routes);
        spinnerSort = view.findViewById(R.id.spinner_sort);
        etFilter = view.findViewById(R.id.et_filter);

        rvRoutes.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RouteAdapter(route -> {
            if (getActivity() instanceof MainActivity) {
                // Gondoskodunk róla, hogy legyen adatbázis rekord ehhez az útvonalhoz
                RouteDao dao = AppDatabase.getInstance(requireContext()).routeDao();
                long id = route.id;
                if (id <= 0) {
                    id = dao.insert(route);
                    route.id = id;
                }
                ((MainActivity) getActivity()).openRouteDetailFragment(id);
            }
        });
        rvRoutes.setAdapter(adapter);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.sort_options,
                android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(spinnerAdapter);

        spinnerSort.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                applyFilterAndSort();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) { }
        });

        etFilter.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override public void afterTextChanged(Editable s) {
                applyFilterAndSort();
            }
        });

        // Random járatok generálása a megadott feltételek alapján
        generateRandomRoutes();
    }

    private void generateRandomRoutes() {
        allRoutes.clear();

        Random random = new Random();

        String[] operators = new String[] { "MÁV", "GYSEV", "RailJet" };
        String[] imageUrls = new String[] {
                "https://img.freepik.com/premium-vector/train-logo-silhouette-vector-file-train-black-icon_856335-2000.jpg",
                "https://img.freepik.com/premium-vector/train-logo-silhouette-vector-file-train-black-icon_856335-2000.jpg",
                "https://img.freepik.com/premium-vector/train-logo-silhouette-vector-file-train-black-icon_856335-2000.jpg"
        };

        int routeCount = 10;

        for (int i = 0; i < routeCount; i++) {
            Route r = new Route();
            r.fromStation = from;
            r.toStation = to;

            int depHour = random.nextInt(24);
            int depMinute = random.nextInt(12) * 5;
            r.departureTime = String.format("%02d:%02d", depHour, depMinute);

            r.durationMinutes = 30 + random.nextInt(271);

            int totalMinutes = depHour * 60 + depMinute + r.durationMinutes;
            int arrHour = (totalMinutes / 60) % 24;
            int arrMinute = totalMinutes % 60;
            r.arrivalTime = String.format("%02d:%02d", arrHour, arrMinute);

            if (maxTransfers > 0) {
                r.transfers = random.nextInt(maxTransfers + 1);
            } else {
                r.transfers = 0;
            }

            r.favorite = false;
            r.operatorName = operators[random.nextInt(operators.length)];
            r.imageUrl = imageUrls[random.nextInt(imageUrls.length)];

            // id-t nem állítunk be kézzel, az adatbázis autoGenerate fogja kiosztani szükség esetén

            allRoutes.add(r);
        }

        applyFilterAndSort();
    }

    private void applyFilterAndSort() {
        String filter = etFilter.getText().toString().trim().toLowerCase();

        List<Route> filtered = new ArrayList<>();
        for (Route r : allRoutes) {
            if (filter.isEmpty()) {
                filtered.add(r);
            } else {
                String text = (r.fromStation + " " + r.toStation + " " +
                        r.departureTime + " " + r.arrivalTime).toLowerCase();
                if (text.contains(filter)) {
                    filtered.add(r);
                }
            }
        }

        int sortIndex = spinnerSort.getSelectedItemPosition();
        Comparator<Route> comparator;
        if (sortIndex == 0) {
            comparator = (a, b) -> a.departureTime.compareTo(b.departureTime);
        } else if (sortIndex == 1) {
            comparator = (a, b) -> a.arrivalTime.compareTo(b.arrivalTime);
        } else {
            comparator = (a, b) -> Integer.compare(a.transfers, b.transfers);
        }
        Collections.sort(filtered, comparator);

        adapter.setRoutes(filtered);
    }
}
