package fr.tchatat.gotoesig.tools;

import android.app.Application;

import fr.tchatat.gotoesig.models.User;

public class Global extends Application {

    private User user = new User();
    public static int trajets=0;
    public static int parmoi=0;
    public static float score=0;
    public static float montant=0;
    public static float note =0;

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }


}
