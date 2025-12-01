package hu.bme.aut.menetrend.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.menetrend.R;
import hu.bme.aut.menetrend.data.Route;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.RouteViewHolder> {

    public interface OnRouteClickListener {
        void onRouteClick(Route route);
    }

    private final List<Route> routes = new ArrayList<>();
    private final OnRouteClickListener listener;

    public RouteAdapter(OnRouteClickListener listener) {
        this.listener = listener;
    }

    public void setRoutes(List<Route> newRoutes) {
        routes.clear();
        routes.addAll(newRoutes);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_route, parent, false);
        return new RouteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder holder, int position) {
        Route route = routes.get(position);
        holder.bind(route);
    }

    @Override
    public int getItemCount() {
        return routes.size();
    }

    class RouteViewHolder extends RecyclerView.ViewHolder {

        ImageView ivTrain;
        TextView tvStations;
        TextView tvTimes;
        TextView tvTransfers;

        RouteViewHolder(@NonNull View itemView) {
            super(itemView);
            ivTrain = itemView.findViewById(R.id.iv_train);
            tvStations = itemView.findViewById(R.id.tv_stations);
            tvTimes = itemView.findViewById(R.id.tv_times);
            tvTransfers = itemView.findViewById(R.id.tv_transfers);
        }

        void bind(Route route) {
            tvStations.setText(route.fromStation + " → " + route.toStation);
            if (route.durationMinutes > 0 && route.departureTime != null && !route.departureTime.isEmpty()) {
                tvTimes.setText(route.departureTime + " - " + route.arrivalTime +
                        " (" + route.durationMinutes + " perc)");
            } else {
                tvTimes.setText(route.departureTime);
            }
            tvTransfers.setText("Átszállások: " + route.transfers);

            String imageToLoad = (route.imageUrl == null || route.imageUrl.isEmpty())
                    ? "https://img.freepik.com/premium-vector/train-logo-silhouette-vector-file-train-black-icon_856335-2000.jpg"
                    : route.imageUrl;

            Glide.with(itemView.getContext())
                    .load(imageToLoad)
                    .placeholder(R.drawable.ic_train_placeholder)
                    .into(ivTrain);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRouteClick(route);
                }
            });
        }
    }
}
