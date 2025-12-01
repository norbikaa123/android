package hu.bme.aut.menetrend.ui.search;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;

import hu.bme.aut.menetrend.MainActivity;
import hu.bme.aut.menetrend.R;
import hu.bme.aut.menetrend.data.AppDatabase;
import hu.bme.aut.menetrend.data.Route;
import hu.bme.aut.menetrend.data.RouteDao;

public class SearchFragment extends Fragment {

    private EditText etFrom, etTo;
    private TextView tvDateTime, tvMaxTransfers;
    private SeekBar seekMaxTransfers;
    private Button btnPickDateTime, btnSearch;
    private CheckBox cbAddToFavorites;

    private Calendar selectedDateTime = Calendar.getInstance();
    private int maxTransfers = 2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        etFrom = view.findViewById(R.id.et_from);
        etTo = view.findViewById(R.id.et_to);
        tvDateTime = view.findViewById(R.id.tv_date_time);
        tvMaxTransfers = view.findViewById(R.id.tv_max_transfers);
        seekMaxTransfers = view.findViewById(R.id.seek_max_transfers);
        btnPickDateTime = view.findViewById(R.id.btn_pick_date_time);
        btnSearch = view.findViewById(R.id.btn_search);
        cbAddToFavorites = view.findViewById(R.id.cb_add_to_favorites);

        btnPickDateTime.setOnClickListener(v -> pickDateTime());

        seekMaxTransfers.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                maxTransfers = progress;
                tvMaxTransfers.setText("Max átszállások: " + maxTransfers);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        btnSearch.setOnClickListener(v -> {
            String from = etFrom.getText().toString().trim();
            String to = etTo.getText().toString().trim();

            if (from.isEmpty()) {
                etFrom.setError("Kötelező");
                return;
            }
            if (to.isEmpty()) {
                etTo.setError("Kötelező");
                return;
            }

            if (cbAddToFavorites.isChecked()) {
                saveSearchToFavorites(from, to, maxTransfers);
            }

            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openResultsFragment(from, to, maxTransfers);
            }
        });

        return view;
    }

    private void saveSearchToFavorites(String from, String to, int maxTransfers) {
        AppDatabase db = AppDatabase.getInstance(requireContext());
        RouteDao dao = db.routeDao();

        Route route = new Route();
        route.fromStation = from;
        route.toStation = to;

        String dateTimeText = tvDateTime.getText().toString();
        route.departureTime = dateTimeText != null ? dateTimeText : "";
        route.arrivalTime = "";
        route.durationMinutes = 0;
        route.transfers = maxTransfers;
        route.favorite = true;
        route.operatorName = "Mentett keresés";
        route.imageUrl = "https://img.freepik.com/premium-vector/train-logo-silhouette-vector-file-train-black-icon_856335-2000.jpg";

        dao.insert(route);
    }

    private void pickDateTime() {
        final Calendar now = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    selectedDateTime.set(Calendar.YEAR, year);
                    selectedDateTime.set(Calendar.MONTH, month);
                    selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    TimePickerDialog timePicker = new TimePickerDialog(
                            requireContext(),
                            (timeView, hourOfDay, minute) -> {
                                selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                selectedDateTime.set(Calendar.MINUTE, minute);
                                String text = dayOfMonth + "." + (month + 1) + ". " + year + " " +
                                        String.format("%02d:%02d", hourOfDay, minute);
                                tvDateTime.setText(text);
                            },
                            now.get(Calendar.HOUR_OF_DAY),
                            now.get(Calendar.MINUTE),
                            true);
                    timePicker.show();

                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }
}
