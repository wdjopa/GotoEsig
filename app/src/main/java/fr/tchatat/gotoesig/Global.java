package fr.tchatat.gotoesig;

import android.app.Application;

import fr.tchatat.gotoesig.models.User;

public class Global extends Application {

    private User user = new User();

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
