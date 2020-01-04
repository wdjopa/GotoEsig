package fr.tchatat.gotoesig.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;

public class AvisTrajetCard implements Parcelable {
    private AvisTrajet avis;
    private Trajet trajet;
    private User user;

    public AvisTrajet getAvis() {
        return avis;
    }

    public void setAvis(AvisTrajet avis) {
        this.avis = avis;
    }

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

    public AvisTrajetCard(AvisTrajet avis, Trajet trajet, User user) {
        this.avis = avis;
        this.trajet = trajet;
        this.user = user;
    }

    protected AvisTrajetCard(Parcel in) {
        avis = in.readParcelable(AvisTrajet.class.getClassLoader());
        trajet = in.readParcelable(Trajet.class.getClassLoader());
        user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<AvisTrajetCard> CREATOR = new Creator<AvisTrajetCard>() {
        @Override
        public AvisTrajetCard createFromParcel(Parcel in) {
            return new AvisTrajetCard(in);
        }

        @Override
        public AvisTrajetCard[] newArray(int size) {
            return new AvisTrajetCard[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(avis, flags);
        dest.writeParcelable(trajet, flags);
        dest.writeParcelable(user, flags);
    }
}
