package nl.tue.tuego.WebAPI;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

import nl.tue.tuego.Storage.Storage;

// APICall (was: WebAPI) handles calls to the Web API
public class APICall extends AsyncTask<String, Void, String> {

    // Statics
    public static String URL = "http://dblappdev.professorsonstage.nl";

    // Private variables
    private String method;
    private String route;
    private Object model;
    private APICallback callback;
    private boolean success;
    private Context context;

    // Constructor
    public APICall(String method, String route, Object model, APICallback callback, Context context) {
        // Set properties of request
        this.method = method;
        this.route = route;
        this.model = model;
        this.callback = callback;
        this.success = false;
        this.context = context;

        // This means that we are doing a PUSH-type request, but without any data model. Something is wrong, so log an error!
        if (this.model == null && (this.method.equals("POST") || this.method.equals("PATCH") || this.method.equals("PUT"))) {
            Log.e("API", "Making push-type request, but without any data provided. Assuming GET request");
            this.method = "GET";
        }
    }

    // AsyncTask execution stage
    @Override
    protected String doInBackground(String... params) {
        // Initialize GSON
        byte[] json = {};

        // Initialization connection and streams
        HttpURLConnection client = null;
        OutputStream out = null;
        InputStream in = null;

        boolean isPushRequest = (this.model != null && (this.method.equals("POST") || this.method.equals("PATCH") || this.method.equals("PUT")));
        if (isPushRequest) {
            Gson gson = new Gson();
            String jsonString = gson.toJson(this.model);
            Log.d("API", "Writing: " + jsonString);
            json = gson.toJson(this.model).getBytes();
        }

        // Perform the request
        try {
            URL url = new URL(this.URL + this.route);

            // Initialize and setup client
            client = (HttpURLConnection) url.openConnection();
            client.setConnectTimeout(3000);
            client.setReadTimeout(3000);
            client.setRequestMethod(this.method);
            client.addRequestProperty("Accept", "application/json");

            String token = Storage.getToken(context);
            if (token.equals("")) {
                Log.d("API", "No token found");
            } else {
                client.addRequestProperty("Authorization","Bearer " + token);
                Log.d("API", "Added authorization key");
            }

            // Some stuff is different when doing a PUSH-type request
            if (isPushRequest) {
                client.setDoOutput(true);
                client.addRequestProperty("Content-Type", "application/json");
                client.setFixedLengthStreamingMode(json.length);

                out = new BufferedOutputStream(client.getOutputStream());
                out.write(json);
                out.flush();
            }

            try {
                // Determine whether the call was successful
                int status = client.getResponseCode();
                if (status == HttpURLConnection.HTTP_OK) {
                    Log.d("API", "Request successful");
                    this.success = true;

                    in = new BufferedInputStream(client.getInputStream());
                } else { // Just to be explicit about it.
                    Log.d("API", "Request rejected, " + status);
                    this.success = false;

                    in = new BufferedInputStream(client.getErrorStream());
                }

                String res = convertStreamToString(in);

                // Debug print
                Log.d("API", "Response: " + res);

                // Return string, most likely JSON-encoded
                return res;
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("API", "Failed to create input stream");
            } finally {
                // Close streams
                closeStream(in);
                Log.d("API", "Async request finished");
            }
        } catch (SocketTimeoutException e) {
            // Handles connection timeout to the server
            Log.d("API", "Could not connect to the web API");
        } catch (MalformedURLException e) {
            // Handles an incorrectly entered URL
            Log.d("API", "Incorrect URL");
        } catch (IOException e) {
            // Handles input and output errors
            e.printStackTrace();
            Log.d("API", "Failed to create output stream");
        } finally {
            // Disconnect the client
            if (client != null) {
                client.disconnect();
            }

            // Close the stream
            closeStream(out);
        }
        return null;
    }

    // AsyncTask response handling
    @Override
    protected void onPostExecute(String res) {
        // Callback
        if (this.success) {
            this.callback.done(res);
        } else {
            this.callback.fail(res);
        }
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeStream(is);
        }
        return sb.toString();
    }

    private void closeStream(Closeable stream) {
        try {
            if (stream != null) {
                stream.close();
            }
        } catch (IOException e) {
            Log.d("Stream", "Stream already closed");
        }
    }
}