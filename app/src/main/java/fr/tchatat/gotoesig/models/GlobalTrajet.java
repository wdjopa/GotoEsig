package fr.tchatat.gotoesig.models;

import java.util.ArrayList;

public class GlobalTrajet {

    private Trajet trajet;
    private ArrayList<AvisTrajet> avis = new ArrayList<>();

    public Trajet getTrajet() {
        return trajet;
    }

    public void setTrajet(Trajet trajet) {
        this.trajet = trajet;
    }

    public ArrayList<AvisTrajet> getAvis() {
        return avis;
    }

    public void setAvis(ArrayList<AvisTrajet> avis) {
        this.avis = avis;
    }
}
