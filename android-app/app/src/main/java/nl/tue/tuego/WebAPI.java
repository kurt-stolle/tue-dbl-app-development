package nl.tue.tuego;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

// Interface for callback handling - also able to handle errors
interface APICallback {
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
    APICall(String method, String route, Object model, APICallback callback) {
        // Set properties of request
        this.method = method;
        this.route = route;
        this.model = model;
        this.callback = callback;
        this.success = false;

        if (this.model == null && (this.method.equals("POST") || this.method.equals("PATCH") || this.method.equals("PUT"))) {
            // This means that we are doing a PUSH-type request, but without any data model. Something is wrong, so log an error!

            Log.e("API", "Making push-type request, but without any data provided. Assuming GET request");
            this.method = "GET";
        }
    }

    // AsyncTask execution stage
    @Override
    protected String doInBackground(String... params) {
        // Initialize GSON
        byte[] json = {};
        boolean isPushRequest = (this.model != null && (this.method.equals("POST") || this.method.equals("PATCH") || this.method.equals("PUT")));

        if (isPushRequest) {
            Gson gson = new Gson();
            json = gson.toJson(this.model).getBytes();
        }

        // Perform the request
        HttpURLConnection client = null;
        try {
            URL url = new URL("http://tue-dbl-app-development.herokupp.com" + this.route);

            // Initialize and setup client
            client = (HttpURLConnection) url.openConnection();
            client.setConnectTimeout(3000);
            client.setReadTimeout(3000);
            client.setRequestMethod(this.method);
            client.addRequestProperty("Accept", "application/json");

            // TODO: Load API token from local storage
            // client.addRequestProperty("Authorization","Bearer " + <JWT TOKEN FROM LOGIN GOES HERE> );

            // Some stuff is different when doing a PUSH-type request
            if (isPushRequest) {
                client.setDoOutput(true);
                client.addRequestProperty("Content-Type", "application/json");
                client.setFixedLengthStreamingMode(json.length);

                OutputStream out = new BufferedOutputStream(client.getOutputStream());
                out.write(json);
                out.flush();
                out.close();
            }

            // Check response code of the client
            int responseCode = client.getResponseCode();
            if (responseCode == 200) {
                // If OK continue
                success = true;

                InputStream in = null;
                BufferedReader reader = null;

                try {
                    // Input stream reading using buffer
                    // All responses have input - this is a rule defined by the WebAPI design
                    in = new BufferedInputStream(client.getInputStream());
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }

                    // Parse the response to string
                    String res = stringBuilder.toString();

                    // Debug print
                    Log.d("API", res);

                    Log.d("API", "Request successful");

                    // Return string, most likely JSON-encoded
                    return res;
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("API", "Failed to create input stream");
                } finally {
                    closeStream(in);
                    closeStream(reader);
                }
            } else {
                // Just to be explicit about it
                success = false;
                Log.d("API", "Request rejected, " + client.getResponseMessage());
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
        }

        return null;
    }

    // AsyncTask response handling
    @Override
    protected void onPostExecute(String res) {
        // Callback
        // KURT: I've defined success as being a HTTP response code of 200. This means that success has to do with whether the API has deemed the request successful,
        // so NOT whether Android was ABLE to MAKE the call. If this is unclear, please contact me!
        if (this.success) {
            this.callback.done(res);
        } else {
            this.callback.fail(res);
        }
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