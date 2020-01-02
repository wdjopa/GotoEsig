package fr.tchatat.gotoesig;

import android.content.Context;
import android.net.Uri;
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
    ArrayList<TrajetCard> trajets;

    public TrajetAdapter(Context c, ArrayList<TrajetCard> trajets) {
        this.c = c;
        this.trajets = trajets;
    }

    @NonNull
    @Override
    public TrajetHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.trajetcardview, null);

        return new TrajetHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull TrajetHolder holder, int position) {
        Picasso.get().load(Uri.parse(trajets.get(position).getUser().getProfileImage())).into(holder.avatar);
        holder.proposer.setText(trajets.get(position).getUser().getPseudo());
        holder.depart.setText(trajets.get(position).getTrajet().getAdresse());
        holder.date.setText(trajets.get(position).getTrajet().getDate());
        holder.places.setText(trajets.get(position).getTrajet().getNombre()+"/"+trajets.get(position).getUsers().size());
        holder.mode.setText(trajets.get(position).getTrajet().getMoyen());
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
