package nl.tue.tuego;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


class CallAPI extends AsyncTask<String, Void, String> {

    private String methodAPI;
    private String routeAPI;
    private Object dataClassAPI;

    CallAPI (String method, String route, Object dataClass) {
        this.methodAPI = method;
        this.routeAPI = route;
        this.dataClassAPI = dataClass;
    }

    @Override
    protected String doInBackground(String... params) {

        Gson gson = new Gson();

        // Generate JSON from the data class
        // Store as bytes, because that is what buffers use

        byte[] json = gson.toJson(dataClassAPI).getBytes();

        // Perform the request

        URL url;
        HttpURLConnection client = null;
        try {
            url = new URL("http://tue-dbl-app-development.herokuapp.com" + routeAPI);

            // Initialize and setup client
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod(methodAPI);
            client.setDoOutput(true);
            client.addRequestProperty("Content-Type","application/json");
            client.addRequestProperty("Accept","application/json");
            //client.addRequestProperty("Authorization","Bearer " + <JWT TOKEN FROM LOGIN GOES HERE> );
            client.setFixedLengthStreamingMode(json.length);
            //client.setChunkedStreamingMode(0);

            // Write json to output stream
            OutputStream out = new BufferedOutputStream(client.getOutputStream());
            out.write(json);
            out.flush();
            out.close();

            // Input stream reading using buffer
            InputStream  in = new BufferedInputStream(client.getInputStream());


            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
//                reader.close();
                // Parse the response


                return result.toString();
            }
            finally {
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






}
