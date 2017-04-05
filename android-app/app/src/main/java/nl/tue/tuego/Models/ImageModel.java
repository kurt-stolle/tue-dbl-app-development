package nl.tue.tuego.Models;

public class ImageModel {
    public String UUID;
    public String Uploader; // UUID of the Uploader
    public String UploadTime; // Format: 2017-02-20 16:23
    public String Finder; // UUID of the Finder

    public ImageModel(String UUID, String Uploader, String UploadTime, String Finder) {
        this.UUID = UUID;
        this.Uploader = Uploader;
        this.UploadTime = UploadTime;
        this.Finder = Finder;
    }
}