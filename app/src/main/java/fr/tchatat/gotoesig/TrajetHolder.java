package fr.tchatat.gotoesig;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import fr.tchatat.gotoesig.models.Trajet;
import fr.tchatat.gotoesig.models.TrajetCard;


public class TrajetHolder extends RecyclerView.ViewHolder {

    ImageView avatar;
    TextView depart,date, places, mode, contribution, proposer;

    public TrajetHolder(@NonNull View itemView) {
        super(itemView);

        this.avatar = itemView.findViewById(R.id.avatar_user_1);
        this.depart= itemView.findViewById(R.id.myroads_current_departure_location_1);
        this.date = itemView.findViewById(R.id.myroads_current_departure_date_1);
        this.places = itemView.findViewById(R.id.myroads_current_departure_places_1);
        this.mode = itemView.findViewById(R.id.myroads_current_departure_mode_1);
        this.contribution = itemView.findViewById(R.id.myroads_current_departure_price_1);
        this.proposer = itemView.findViewById(R.id.myroads_current_departure_username_1);
    }

    public void bind(final TrajetCard item, final TrajetAdapter.OnItemClickListener listener) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                listener.onItemClick(item);
            }
        });
    }
}
