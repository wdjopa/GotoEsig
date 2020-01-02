package fr.tchatat.gotoesig.models;

import java.util.ArrayList;

public class TrajetCard {
    private Trajet trajet;
    private User user;
    private ArrayList<User> users;

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

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public TrajetCard(User user, Trajet trajet) {
        this.user = user;
        this.trajet = trajet;
    }


}
