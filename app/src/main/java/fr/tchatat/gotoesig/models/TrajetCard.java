package fr.tchatat.gotoesig.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class TrajetCard implements Parcelable {
    private Trajet trajet;
    private User user;
    private ArrayList<User> users = new ArrayList<User>();

    protected TrajetCard(Parcel in) {
        user = in.readParcelable(User.class.getClassLoader());
        users = in.createTypedArrayList(User.CREATOR);
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

    public ArrayList<User> getUsers() {
        return users;
    }

    public int total() {
        return users.size();
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public TrajetCard(User user, Trajet trajet) {
        this.user = user;
        this.trajet = trajet;
    }

    @NonNull
    @Override
    public String toString() {
        return "Trajet de "+trajet.getAdresse()+", le "+trajet.getDate()+" à "+trajet.getHeure()+". Créé par "+user.getPseudo();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(user, flags);
        dest.writeTypedList(users);
    }
}
