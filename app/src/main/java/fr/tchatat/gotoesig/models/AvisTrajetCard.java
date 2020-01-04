package fr.tchatat.gotoesig.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;

public class AvisTrajetCard implements Parcelable {
    private String message;
    private Date date;
    private Trajet trajet;
    private User user;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setTrajet(Trajet trajet) {
        this.trajet = trajet;
    }

    protected AvisTrajetCard(Parcel in) {
        message = in.readString();
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

    public String getTrajet() {
        return message;
    }

    public void setTrajet(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AvisTrajetCard() {
    }

    @NonNull
    @Override
    public String toString() {
        return "Avis de "+user.getPseudo()+", le "+date+" pour le trajet "+trajet.getAdresse()+". Message : "+message;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(message);
        dest.writeParcelable(user, flags);
    }
}
