package fr.tchatat.gotoesig.models;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class TrajetCard implements Parcelable {
    private Trajet trajet;
    private User user;
    private int nombre;

    public TrajetCard( User user,Trajet trajet) {
        this.trajet = trajet;
        this.user = user;
    }
    public TrajetCard( User user,Trajet trajet, int nombre) {
        this.trajet = trajet;
        this.user = user;
        this.nombre = nombre;
    }
    public TrajetCard() {}

    public Trajet getTrajet() {
        return trajet;
    }

    public void setTrajet(Trajet trajet) {
        this.trajet = trajet;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getNombre() {
        return nombre;
    }

    public void setNombre(int nombre) {
        this.nombre = nombre;
    }

    protected TrajetCard(Parcel in) {
        trajet = in.readParcelable(Trajet.class.getClassLoader());
        user = in.readParcelable(User.class.getClassLoader());
        nombre = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(trajet, flags);
        dest.writeParcelable(user, flags);
        dest.writeInt(nombre);
    }

    public static final Creator<TrajetCard> CREATOR = new Creator<TrajetCard>() {
        @Override
        public TrajetCard createFromParcel(Parcel in) {
            return new TrajetCard(in);
        }

        @Override
        public TrajetCard[] newArray(int size) {
            return new TrajetCard[size];
        }
    };

    @NonNull
    @Override
    public String toString() {
        return "Trajet de "+trajet.getAdresse()+", le "+trajet.getDate()+" à "+trajet.getHeure()+". Créé par "+user.getPseudo();
    }

    @Override
    public int describeContents() {
        return 0;
    }

}
