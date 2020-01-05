package fr.tchatat.gotoesig.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import fr.tchatat.gotoesig.R;
import fr.tchatat.gotoesig.models.AvisTrajetCard;

public class AvisAdapter extends RecyclerView.Adapter<AvisAdapter.AvisHolder> {

    Context c;
    ArrayList<AvisTrajetCard> avis = new ArrayList<AvisTrajetCard>();



    public AvisAdapter(Context c, ArrayList<AvisTrajetCard> avis) {
        this.c = c;
        this.avis = avis;
    }

    @NonNull
    @Override
    public AvisHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.avistrajet, parent, false);
        return new AvisHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AvisHolder holder, final int position) {

        Picasso.get().load(Uri.parse(avis.get(position).getUser().getProfileImage())).into(holder.image_avis);
        holder.message_avis.setText(avis.get(position).getAvis().getMessage());
        holder.date_avis.setText("Avis laissé le "+avis.get(position).getAvis().getDate());
    }

    @Override
    public int getItemCount() {
        return avis.size();
    }




    public class AvisHolder extends RecyclerView.ViewHolder{

        ImageView image_avis;
        TextView date_avis, message_avis;
        public AvisHolder(@NonNull View itemView) {
            super(itemView);
            this.image_avis = itemView.findViewById(R.id.image_avis);
            this.message_avis = itemView.findViewById(R.id.message_avis);
            this.date_avis= itemView.findViewById(R.id.date_avis);

        }
    }

}
