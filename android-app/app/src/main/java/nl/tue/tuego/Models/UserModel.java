package nl.tue.tuego.Models;

public class UserModel {
    public String UUID;
    public String Name;
    public String Email;
    public int Points;

    public UserModel(String UUID, String Name, String Email, int Points){
        this.UUID = UUID;
        this.Name = Name;
        this.Email = Email;
        this.Points = Points;
    }
}