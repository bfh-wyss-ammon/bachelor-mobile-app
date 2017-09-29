package mps.bachelor2017.bfh.ti.ch.mobiltypricing;

/**
 * Created by Pascal on 29.09.2017.
 */

public class User {
    private String mail;
    private String password;

    public User() {

    }

    public User(String mail, String password) {
        this.mail = mail;
        this.password = password;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
