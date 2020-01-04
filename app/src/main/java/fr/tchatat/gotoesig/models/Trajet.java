package fr.tchatat.gotoesig.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Trajet implements Parcelable {

    public Trajet() {}

    private String id;
    private String uid;
    private String moyen;
    private String adresse;
    private String date;
    private String heure;
    private int retard;
    private int nombre;
    private String autoroute;
    private float contribution;
    private String distance;
    private String temps;

    protected Trajet(Parcel in) {
        id = in.readString();
        uid = in.readString();
        moyen = in.readString();
        adresse = in.readString();
        date = in.readString();
        heure = in.readString();
        retard = in.readInt();
        nombre = in.readInt();
        autoroute = in.readString();
        contribution = in.readFloat();
        distance = in.readString();
        temps = in.readString();
    }

    public static final Creator<Trajet> CREATOR = new Creator<Trajet>() {
        @Override
        public Trajet createFromParcel(Parcel in) {
            return new Trajet(in);
        }

        @Override
        public Trajet[] newArray(int size) {
            return new Trajet[size];
        }
    };

    public String getMoyen() {
        return moyen;
    }

    public void setMoyen(String moyen) {
        this.moyen = moyen;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHeure() {
        return heure;
    }

    public void setHeure(String heure) {
        this.heure = heure;
    }

    public int getRetard() {
        return retard;
    }

    public void setRetard(int retard) {
        this.retard = retard;
    }

    public int getNombre() {
        return nombre;
    }

    public void setNombre(int nombre) {
        this.nombre = nombre;
    }

    public String getAutoroute() {
        return autoroute;
    }

    public void setAutoroute(String autoroute) {
        this.autoroute = autoroute;
    }

    public float getContribution() {
        return contribution;
    }

    public void setContribution(float contribution) {
        this.contribution = contribution;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getTemps() {
        return temps;
    }

    public void setTemps(String temps) {
        this.temps = temps;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(uid);
        parcel.writeString(moyen);
        parcel.writeString(adresse);
        parcel.writeString(date);
        parcel.writeString(heure);
        parcel.writeInt(retard);
        parcel.writeInt(nombre);
        parcel.writeString(autoroute);
        parcel.writeFloat(contribution);
        parcel.writeString(distance);
        parcel.writeString(temps);
    }
}
