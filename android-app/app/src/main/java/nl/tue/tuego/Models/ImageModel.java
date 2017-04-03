package nl.tue.tuego.Models;

public class ImageModel {
    private String UUID;
    private String Uploader; // UUID of the Uploader
    private String UploadTime; // Format: 2017-02-20 16:23
    private String Finder; // UUID of the Finder

    public ImageModel(String UUID, String Uploader, String UploadTime, String Finder) {
        this.UUID = UUID;
        this.Uploader = Uploader;
        this.UploadTime = UploadTime;
        this.Finder = Finder;
    }

    public String getUUID() {
        return UUID;
    }

    public String getUploader() {
        return Uploader;
    }

    public String getUploadTime() {
        return UploadTime;
    }

    public String getFinder() {
        return Finder;
    }
}