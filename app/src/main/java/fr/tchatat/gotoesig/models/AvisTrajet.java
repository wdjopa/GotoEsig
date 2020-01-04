package fr.tchatat.gotoesig.models;

import java.util.Date;

public class AvisTrajet {
    private String message;
    private Date date;

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

    public AvisTrajet(String message, Date date) {
        this.message = message;
        this.date = date;
    }
    public AvisTrajet(AvisTrajet avisTrajet) {
        this.message = avisTrajet.getMessage();
        this.date = avisTrajet.getDate();
    }
}
