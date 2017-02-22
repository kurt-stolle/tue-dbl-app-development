package com.example.app.tuego;

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

class APIError extends Exception {
    public APIError(String message) {
        super(message);
    }
}

// WebAPI can be used to manupulate the API.
class WebAPI {
    // Call is used for calling the API.
    // - method can be: GET, POST, PATCH or PUSH
    // - route is a route (e.g. /login or /images/2015)
    // - dataClass is a data model
    // Due to language limitations, this cannot be static.
    static String Call(String method, String route, Object dataClass) throws APIError {
        // Initialize GSON
        Gson gson = new Gson();

        // Generate JSON from the data class
        // Store as bytes, because that is what buffers use
        byte[] json = gson.toJson(dataClass).getBytes();

        // Perform the request

        URL url;
        HttpURLConnection client = null;

           try {
               url = new URL("http://tue-dbl-app-development.herokuapp.com" + route);

               // Initialize and setup client
               client = (HttpURLConnection) url.openConnection();
               client.setRequestMethod(method);
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

               if( client.getResponseCode() != HttpURLConnection.HTTP_OK ){
                   throw new APIError(client.getResponseMessage());
               }

               BufferedReader reader = new BufferedReader(new InputStreamReader(in));
               StringBuilder result = new StringBuilder();
               String line;
               while ((line = reader.readLine()) != null) {
                   result.append(line);
               }

               // Parse the response
               return reader.toString();
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

        // Decode the model
        return null;
    }
}
