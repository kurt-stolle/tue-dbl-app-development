package nl.tue.tuego.Models;

import android.graphics.Bitmap;

public class ImageModel {
    public String UUID;
    public String Uploader;   // UUID of the Uploader
    public String UploadTime; // Format: 2017-02-20 16:23
    public String Finder;     // UUID of the Finder
    public Bitmap Image;      // The actual image
}
