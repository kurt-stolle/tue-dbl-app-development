package nl.tue.tuego.WebAPI;

// Interface for callback handling - also able to handle errors
public interface APICallback {
    void done(String res);
    void fail(String res);
}