package nl.tue.tuego.Models;

public class RegistrationModel {
    String Name;
    String Email;
    String Password;

    // Constructor for registration model
    public RegistrationModel(String n, String e, String p) {
        this.Name = n;
        this.Email = e;
        this.Password = p;
    }
}
