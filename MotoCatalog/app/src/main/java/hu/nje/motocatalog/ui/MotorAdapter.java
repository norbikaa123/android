
package hu.nje.motocatalog.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import hu.nje.motocatalog.R;
import hu.nje.motocatalog.api.KepBetolto;
import hu.nje.motocatalog.model.Motor;

// A motorok megjelenitese listaban
public class MotorAdapter extends RecyclerView.Adapter<MotorAdapter.MotorSor> {

    // kattintas kezelés
    public interface Kattintas {
        void megnyomva(Motor motor);
    }

    // A listában megjelenő motorok
    private final List<Motor> motorok = new ArrayList<>();
    private final Kattintas kattintas;

    public MotorAdapter(Kattintas kattintas) {
        this.kattintas = kattintas;
    }

    // uj motorok
    public void frissit(List<Motor> ujMotorok) {
        motorok.clear();
        motorok.addAll(ujMotorok);
        notifyDataSetChanged();
    }

    // motor soranak kinezete
    @NonNull
    @Override
    public MotorSor onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View sor = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sor_motor, parent, false);

        return new MotorSor(sor);
    }

    // motor neve adata képének beallitasa
    @Override
    public void onBindViewHolder(@NonNull MotorSor holder, int position) {
        Motor motor = motorok.get(position);

        holder.nev.setText(motor.teljesNev());

        holder.adat.setText((motor.category == null
                ? "Ismeretlen kategória" : motor.category)
                + " • " + motor.displacement + " cm³");

        KepBetolto.betolt(holder.kep, motor.thumbnail);

        // kattintas vezerles
        holder.itemView.setOnClickListener(v -> kattintas.megnyomva(motor));
    }

    // hany motor legyen listaban
    @Override
    public int getItemCount() {
        return motorok.size();
    }

    // motor elemeinek tarolasa
    static class MotorSor extends RecyclerView.ViewHolder {

        TextView nev, adat;
        ImageView kep;

        MotorSor(View item) {
            super(item);

            nev = item.findViewById(R.id.sorNev);
            adat = item.findViewById(R.id.sorAdat);
            kep = item.findViewById(R.id.sorKep);
        }
    }
}
