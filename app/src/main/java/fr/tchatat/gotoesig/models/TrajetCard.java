package fr.tchatat.gotoesig.models;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class TrajetCard {
    private Trajet trajet;
    private User user;
    private ArrayList<User> users = new ArrayList<User>();

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
}
