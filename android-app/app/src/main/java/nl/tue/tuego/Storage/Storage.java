package nl.tue.tuego.Storage;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import nl.tue.tuego.WebAPI.APICall;
import nl.tue.tuego.WebAPI.APICallback;

// Manages all global variables of this session
public class Storage {
    private static String token = "";
    private static String username = "";

    // Reading key from local storage
    private static String readToken(Context c){
        FileInputStream fis = null;
        BufferedReader bufferedReader = null;
        try {
            fis = c.openFileInput("Token");
            InputStreamReader isr = new InputStreamReader(fis);
            bufferedReader = new BufferedReader(isr);

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            token = sb.toString();
        } catch (FileNotFoundException e) {
            Log.d("ReadToken", "Token not found");
        } catch (IOException e) {
            Log.d("ReadToken", "Reading token failed");
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        Log.d("ReadToken", "Token = " + token);
        return token;
    }

    public static void setToken(String t) {
        token = t;
    }

    public static String getToken(Context c) {
        if (token.equals("")) {
            return readToken(c);
        } else {
            return token;
        }
    }

    // Reading key from local storage
    private static String readUsername(Context c){
        FileInputStream fis = null;
        BufferedReader bufferedReader = null;
        try {
            fis = c.openFileInput("Username");
            InputStreamReader isr = new InputStreamReader(fis);
            bufferedReader = new BufferedReader(isr);

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            username = sb.toString();
        } catch (FileNotFoundException e) {
            Log.d("Storage", "Username not found");
        } catch (IOException e) {
            Log.d("Storage", "Reading username failed");
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        Log.d("Storage", "Username = " + username);
        return username;
    }

    public static void setUsername(String u) {
        username = u;
    }

    // Gets the username of the current user
    public static String getUsername(Context c) {
        if (username.equals("")) {
            return readUsername(c);
        } else {
            return username;
        }
    }

    // Clears all stored data of this session
    public static void logout() {
        token = "";
        username = "";
    }
}
