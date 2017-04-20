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
    private static String uuid = "";
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
            Log.d("ReadToken", "Token could not be accessed");
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

    // Reading UUID from local storage
    private static String readUuid(Context c){
        FileInputStream fis = null;
        BufferedReader bufferedReader = null;
        try {
            fis = c.openFileInput("UUID");
            InputStreamReader isr = new InputStreamReader(fis);
            bufferedReader = new BufferedReader(isr);

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            uuid = sb.toString();
        } catch (FileNotFoundException e) {
            Log.d("Storage", "UUID not found");
        } catch (IOException e) {
            Log.d("Storage", "UUID could not be accessed");
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
        Log.d("Storage", "UUID = " + uuid);
        return uuid;
    }

    // Reading username from local storage
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
            Log.d("Storage", "Username could not be accessed");
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

    // Sets the token of the current user to t (for this session)
    public static void setToken(String t) {
        token = t;
    }

    // Sets the UUID of the current user to u
    public static void setUuid(String u) {
        uuid = u;
    }

    // Sets the username of the current user to u (for this session)
    public static void setUsername(String u) {
        username = u;
    }

    // Gets the token of the current user (for this session)
    public static String getToken(Context c) {
        if (token.equals("")) {
            return readToken(c);
        } else {
            return token;
        }
    }

    // Gets the UUID of the current user
    public static String getUuid(Context c) {
        if (uuid.equals("")) {
            return readUuid(c);
        } else {
            return uuid;
        }
    }

    // Gets the username of the current user
    public static String getUsername(Context c) {
        if (username.equals("")) {
            return readUsername(c);
        } else {
            return username;
        }
    }

    // Clears all stored data (for this session)
    public static void logout() {
        token = "";
        uuid = "";
        username = "";
    }
}
