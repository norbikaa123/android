package hu.bme.aut.menetrend.ui.search;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import hu.bme.aut.menetrend.R;
import hu.bme.aut.menetrend.ui.adapter.RouteAdapter;
import java.util.Arrays;

public class SearchFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);

        RecyclerView rv = v.findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(new RouteAdapter(Arrays.asList("Demo 1", "Demo 2")));

        return v;
    }
}
