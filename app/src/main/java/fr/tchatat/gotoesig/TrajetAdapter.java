package fr.tchatat.gotoesig;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import fr.tchatat.gotoesig.models.TrajetCard;

public class TrajetAdapter extends RecyclerView.Adapter<TrajetHolder> {

    Context c;
    ArrayList<TrajetCard> trajets = new ArrayList<TrajetCard>();

    public TrajetAdapter(Context c, ArrayList<TrajetCard> trajets) {
        this.c = c;
        this.trajets = trajets;
    }

    @NonNull
    @Override
    public TrajetHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.trajetcardview, parent, false);
        return new TrajetHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrajetHolder holder, int position) {
        Picasso.get().load(Uri.parse(trajets.get(position).getUser().getProfileImage())).into(holder.avatar);
        holder.proposer.setText(trajets.get(position).getUser().getPseudo());
        holder.depart.setText(trajets.get(position).getTrajet().getAdresse());
        holder.date.setText(trajets.get(position).getTrajet().getDate());
        holder.mode.setText(trajets.get(position).getTrajet().getMoyen());
        if(trajets.get(position).getTrajet().getContribution() > 0)
            holder.contribution.setText(trajets.get(position).getTrajet().getContribution()+"€");
        holder.places.setText(trajets.get(position).total()+"/"+trajets.get(position).getTrajet().getNombre()+" Places");

    }

    @Override
    public int getItemCount() {
        return trajets.size();
    }
}
