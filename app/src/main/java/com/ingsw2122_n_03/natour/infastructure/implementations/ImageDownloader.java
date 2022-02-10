package com.ingsw2122_n_03.natour.infastructure.implementations;

import android.annotation.SuppressLint;

import com.amplifyframework.api.rest.RestOptions;
import com.amplifyframework.core.Amplify;
import com.ingsw2122_n_03.natour.application.IterController;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class ImageDownloader {

    private int delimiter;
    private String lastPhotoKey;

    private final ArrayList<Thread> backgroundDownloads = new ArrayList<>();

    private final IterController controller;

    public ImageDownloader(IterController controller) { this.controller = controller; }


    public void downloadImages() {

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("iterid", String.valueOf(delimiter));
        queryParams.put("lastkey", lastPhotoKey);

        RestOptions options = RestOptions.builder()
                .addPath("/items/photos")
                .addQueryParameters(queryParams)
                .build();

        Amplify.API.get(
                options,
                response -> {

                    try {

                        JSONObject result = response.getData().asJSONObject();

                        int photoCount = result.getInt("KeyCount");

                        if(photoCount == 0){
                            return;
                        }

                        lastPhotoKey = result.getString("LastKey");

                        for(int i = 0; i < photoCount; ++i) {
                            String url = result.getJSONObject("Urls").getString("url"+i);
                            Thread t = new Thread(()-> downloadImageFromUrl(url));
                            backgroundDownloads.add(t);
                            t.start();
                        }

                    } catch (JSONException e) {
                        controller.onRetrievePhotosError();
                    }
                },

                error -> controller.onRetrievePhotosError()
        );

    }


        @SuppressLint("NewApi")
    private void downloadImageFromUrl(String urlString) {

        try {

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            if(connection.getResponseCode() != 200)
                return;

            String inputLine;
            StringBuilder imageEncoded = new StringBuilder();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((inputLine = in.readLine()) != null) { imageEncoded.append(inputLine); }
            in.close();

            controller.onRetrievePhotoSuccess(Base64.getDecoder().decode(imageEncoded.toString()));

        } catch (IOException ignored){}

    }


    public void interruptSession(){
        for(Thread t : backgroundDownloads){
            t.interrupt();
        }
    }


    public void resetSession(int delimiter) {
        this.delimiter = delimiter;
        lastPhotoKey = null;
        backgroundDownloads.clear();
    }

}
