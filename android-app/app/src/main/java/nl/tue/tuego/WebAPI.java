package nl.tue.tuego;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

// Interface for callback handling - also able to handle errors
interface APICallback{
    void done(String res);
    void fail(String res);
}

// APICall (was: WebAPI) handles calls to the Web API
class APICall extends AsyncTask<String, Void, String> {


    // Privates
    private String method;
    private String route;
    private Object model;
    private APICallback callback;
    private boolean success;

    // Constructor
    APICall (String method, String route, Object model, APICallback callback) {
        if (this.model == null && (this.method == "POST" || this.method == "PATCH" || this.method == "PUT")){
            // This means that we are doing a PUSH-type request, but without any data model. Something is wrong, so log an error!

            Log.e("API","Making push-type request, but without any data provided. Assuming GET request");

            method="GET";
            model=null;
        }

        // Set properties of request
        this.method = method;
        this.route = route;
        this.model = model;
        this.callback = callback;
    }

    // AsyncTask execution stage
    @Override
    protected String doInBackground(String... params) {
        // Initialize GSON
        byte[] json = {};
        boolean isPushRequest = (this.model != null && (this.method == "POST" || this.method == "PATCH" || this.method == "PUT"));

        if (isPushRequest){
            Gson gson = new Gson();
            json = gson.toJson(this.model).getBytes();
        }

        // Perform the request
        URL url;
        HttpURLConnection client = null;

        try {
            url = new URL("http://tue-dbl-app-development.herokuapp.com" + this.route);

            // Initialize and setup client
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod(this.method);
            client.addRequestProperty("Accept", "application/json");

            // TODO: Load API token from local storage
            //client.addRequestProperty("Authorization","Bearer " + <JWT TOKEN FROM LOGIN GOES HERE> );

            // Some stuff is different when doing a PUSH-type request
            // KURT: I have defined "Push request" as I don't know the proper term. Basically means that we're using a method like PATCH/PUT/POST to push data to the server
            if (isPushRequest) {
                client.setDoOutput(true);
                client.addRequestProperty("Content-Type", "application/json");
                client.setFixedLengthStreamingMode(json.length);

                OutputStream out = new BufferedOutputStream(client.getOutputStream());
                out.write(json);
                out.flush();
                out.close();
            }

            // Input stream reading using buffer
            // All responsed have input - this is a rule defined by the WebAPI design
            InputStream in = new BufferedInputStream(client.getInputStream());
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
//                reader.close();
                // Parse the response
                // To string
                String res = stringBuilder.toString();

                // Debug print
                Log.d("API", res);

                // Determine whether the call was successful
                int status = client.getResponseCode();
                if (status == 200){
                    this.success = true;
                } else { // Just to be explicit about it.
                    this.success = false;
                }

                // Return string, most likely JSON-encoded
                return res;
            }
            finally {
                Log.d("API", "Async request finished");
                client.disconnect();
            }


        }
        catch(MalformedURLException e) {
            //Handles an incorrectly entered URL
            e.printStackTrace();
        }
        catch (IOException e) {
            //Handles input and output errors
            e.printStackTrace();
        } finally {
            // Disconnect if all went successfully
            if (client != null) {
                client.disconnect();
            }
        }

        return null;
    }

    // AsyncTask response handling
    @Override
    protected void onPostExecute(String res) {
        // Callback
        // KURT: I've defined success as being a HTTP response code of 200. This means that success has to do with whether the API has deemed the request successfull,
        // so NOT whether Android was ABLE to MAKE the call. If this is unclear, please contact me!
        if (this.success == true) {
            this.callback.done(res);
        } else {
            this.callback.fail(res);
        }

    }
}