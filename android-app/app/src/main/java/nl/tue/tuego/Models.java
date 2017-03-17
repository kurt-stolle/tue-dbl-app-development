package nl.tue.tuego;

class RegistrationModel {
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

class LoginModel {
    String Email;
    String Password;
}

class TokenModel {
    String Token;
}

class UserModel {
    String UUID;
    String Name;
    String Email;
    int Points;
}

class ImageModel {
    String UUID;
    String Uploader; // UUID of the Uploader
    String UploadTime; // Format: 2017-02-20 16:23
    String Finder; // UUID of the Finder

    public ImageModel(String UUID, String Uploader, String UploadTime, String Finder) {
        this.UUID = UUID;
        this.Uploader = Uploader;
        this.UploadTime = UploadTime;
        this.Finder = Finder;
    }
}