package nl.tue.tuego.WebAPI;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import nl.tue.tuego.Activities.LoginActivity;
import nl.tue.tuego.Models.APICallback;

// APICall (was: WebAPI) handles calls to the Web API
public class APIPostPicture extends AsyncTask<String, Void, String> {
    // Private variables
    private String filepath;
    private String token;
    private Bitmap file;
    private Map<String, String> parmas;
    private APICallback callback;
    private boolean success;

    // Constructor
    public APIPostPicture(String filepath, Bitmap file, String token, Map<String, String> parmas, APICallback callback) {
        // Set properties of request
        this.filepath = filepath;
        this.token = token;
        this.file = file;
        this.parmas = parmas;
        this.callback = callback;
        this.success = false;
    }

    // AsyncTask execution stage
    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;

        String twoHyphens = "--";
        String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        String lineEnd = "\r\n";

        String result;

        String[] q = filepath.split("/");
        int idx = q.length - 1;

        try {
            URL url = new URL("http://tue-dbl-app-development.herokuapp.com/images");
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            connection.setRequestProperty("Authorization", "Bearer " + token);

            outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + q[idx] + "\"" + lineEnd);
            outputStream.writeBytes("Content-Type: " + "jpg" + lineEnd);
            outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);

            outputStream.writeBytes(lineEnd);
            file = Bitmap.createScaledBitmap(file, 3000, 2000, false);
            file.compress(Bitmap.CompressFormat.JPEG, 30, outputStream);
            outputStream.writeBytes(lineEnd);

            // Upload POST Data
            for (String key : parmas.keySet()) {
                String value = parmas.get(key);

                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(value);
                outputStream.writeBytes(lineEnd);
            }

            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // debug for reading the file size
            Log.d("APIPicture", "File size: " + outputStream.size() / (double) (1024 * 1024));

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Log.d("APIPicture", "Upload picture successful");
                success = true;
            } else {
                Log.d("APIPicture", "Failed to upload picture: " + connection.getResponseCode() + " " + connection.getResponseMessage());

                inputStream = connection.getErrorStream();
                String errorMessage = convertStreamToString(inputStream);

                Log.d("APIPicture", errorMessage);
                success = false;
            }

            inputStream = connection.getInputStream();
            result = this.convertStreamToString(inputStream);

            inputStream.close();
            outputStream.flush();
            outputStream.close();

            return result;
        } catch (Exception e) {
            Log.e("APIPicture", "Error " + e + " occurred");
            e.printStackTrace();
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
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}