package fr.tchatat.gotoesig;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import fr.tchatat.gotoesig.models.TrajetCard;

public class TrajetAdapter extends RecyclerView.Adapter<TrajetAdapter.TrajetHolder> {

    Context c;
    ArrayList<TrajetCard> trajets = new ArrayList<TrajetCard>();
    private onClickInterface onClickInterface;

    public interface onClickInterface {
        void onItemClick(TrajetCard item);
    }


    public TrajetAdapter(Context c, ArrayList<TrajetCard> trajets, onClickInterface onClickInterface) {
        this.c = c;
        this.trajets = trajets;
        this.onClickInterface = onClickInterface;
    }

    @NonNull
    @Override
    public TrajetHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.trajetcardview, parent, false);
        return new TrajetHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrajetHolder holder, final int position) {
//        holder.bind(trajets.get(position), listener);
        Picasso.get().load(Uri.parse(trajets.get(position).getUser().getProfileImage())).into(holder.avatar);
        holder.proposer.setText(trajets.get(position).getUser().getPseudo());
        holder.depart.setText(trajets.get(position).getTrajet().getAdresse().toUpperCase());
        holder.date.setText(trajets.get(position).getTrajet().getDate()+" "+trajets.get(position).getTrajet().getHeure());
        holder.mode.setText(trajets.get(position).getTrajet().getMoyen());

        if(trajets.get(position).getTrajet().getContribution() > 0)
            holder.contribution.setText(trajets.get(position).getTrajet().getContribution()+"€");
        holder.places.setText(trajets.get(position).total()+"/"+trajets.get(position).getTrajet().getNombre()+" Places");
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickInterface.onItemClick(trajets.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return trajets.size();
    }




    public class TrajetHolder extends RecyclerView.ViewHolder{

        ImageView avatar;
        TextView depart,date, places, mode, contribution, proposer;
        CardView cardView;
        public TrajetHolder(@NonNull View itemView) {
            super(itemView);
            this.cardView = itemView.findViewById(R.id.ended_road_1);
            this.avatar = itemView.findViewById(R.id.avatar_user_1);
            this.depart= itemView.findViewById(R.id.myroads_current_departure_location_1);
            this.date = itemView.findViewById(R.id.myroads_current_departure_date_1);
            this.places = itemView.findViewById(R.id.myroads_current_departure_places_1);
            this.mode = itemView.findViewById(R.id.myroads_current_departure_mode_1);
            this.contribution = itemView.findViewById(R.id.myroads_current_departure_price_1);
            this.proposer = itemView.findViewById(R.id.myroads_current_departure_username_1);
        }
/*
        public void bind(final TrajetCard item, final TrajetAdapter.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }*/
/*
        @Override
        public void onClick(View v) {
            TrajetCard trajetCard = trajets.get(getAdapterPosition());
            Log.d("click", trajetCard.toString());
        }*/
    }

}
