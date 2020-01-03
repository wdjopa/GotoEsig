package fr.tchatat.gotoesig.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class User implements Parcelable {
    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
    private String uid = "";
    private String pseudo = "";
    private String profileImage = "";
    private String nom = "";
    private String prenom = "";
    private String email = "";
    private int score = 0;
    private String tel = "";
    private String adresse = "";

    public User(String uid) {
        this.uid = uid;
    }

    public User() {
    }

    protected User(Parcel in) {
        uid = in.readString();
        pseudo = in.readString();
        profileImage = in.readString();
        nom = in.readString();
        prenom = in.readString();
        email = in.readString();
        score = in.readInt();
        tel = in.readString();
        adresse = in.readString();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(pseudo);
        dest.writeString(profileImage);
        dest.writeString(nom);
        dest.writeString(prenom);
        dest.writeString(email);
        dest.writeInt(score);
        dest.writeString(tel);
        dest.writeString(adresse);
    }

    @NonNull
    @Override
    public String toString() {
        return "pseudo ="+pseudo+"; tel="+tel+"; email ="+email+"; adresse ="+adresse+"; score = "+score;
    }
}
