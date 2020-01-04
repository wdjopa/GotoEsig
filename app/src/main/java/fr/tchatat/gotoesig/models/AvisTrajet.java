package fr.tchatat.gotoesig.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class AvisTrajet implements Parcelable {
    private String message;
    private String date;
    private float note;
    private User user;

    public AvisTrajet() {
    }

    public AvisTrajet(String message, String date, float note, User user) {
        this.message = message;
        this.date = date;
        this.note = note;
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getNote() {
        return note;
    }

    public void setNote(float note) {
        this.note = note;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    protected AvisTrajet(Parcel in) {
        message = in.readString();
        note = in.readFloat();
        user = in.readParcelable(User.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(message);
        dest.writeFloat(note);
        dest.writeParcelable(user, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AvisTrajet> CREATOR = new Creator<AvisTrajet>() {
        @Override
        public AvisTrajet createFromParcel(Parcel in) {
            return new AvisTrajet(in);
        }

        @Override
        public AvisTrajet[] newArray(int size) {
            return new AvisTrajet[size];
        }
    };
}
