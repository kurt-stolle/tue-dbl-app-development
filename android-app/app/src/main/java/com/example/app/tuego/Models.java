package com.example.app.tuego;

class RegistrationModel {
    String Name;
    String Email;
    String Password;
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
}