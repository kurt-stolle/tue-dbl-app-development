package nl.tue.tuego.Models;

// Interface for callback handling - also able to handle errors
public interface APICallback {
    void done(String res);
    void fail(String res);
}