package com.ingsw2122_n_03.natour.infastructure.implementations;

import android.annotation.SuppressLint;

import com.amplifyframework.api.rest.RestOptions;
import com.amplifyframework.core.Amplify;
import com.ingsw2122_n_03.natour.application.IterController;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;

public class ImageUploader {

    private final IterController controller;

    public ImageUploader(IterController controller) {
        this.controller = controller;
    }


    @SuppressLint("NewApi")
    public void uploadImages(int iterID, ArrayList<byte[]> imagesBytes) {

        RestOptions options;

        try {
            options = buildUploadingOptions(iterID, imagesBytes.size());
        } catch (JSONException e) {
            controller.onUploadPhotosError();
            return;
        }

        Amplify.API.put(
                options,
                response -> {

                    for(int i = 0; i < imagesBytes.size(); ++i){

                        try {

                            JSONObject result = response.getData().asJSONObject().getJSONObject("Urls");
                            String urlString = result.getString("url"+i);
                            URL url = new URL(urlString);
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setDoOutput(true);
                            connection.setRequestMethod("PUT");
                            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                            out.write(Base64.getEncoder().encodeToString(imagesBytes.get(i)));
                            out.close();

                            if(connection.getResponseCode() != 200) {
                                controller.onUploadPhotosError();
                                return;
                            }

                        } catch (IOException | JSONException e) {
                            controller.onUploadPhotosError();
                            return;
                        }
                    }

                    controller.onItineraryInsertFinish();

                },

                error -> controller.onUploadPhotosError()
        );

    }



    @SuppressLint("NewApi")
    public void uploadImage(int iterID, byte[] photo) {

        RestOptions options;

        try {
            options = buildUploadingOptions(iterID, 1);
        } catch (JSONException e) {
            controller.onUploadPhotoFinish(false);
            return;
        }

        Amplify.API.put(
                options,
                response-> {

                    try {

                        JSONObject result = response.getData().asJSONObject().getJSONObject("Urls");
                        String urlString = result.getString("url0");
                        URL url = new URL(urlString);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoOutput(true);
                        connection.setRequestMethod("PUT");
                        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                        out.write(Base64.getEncoder().encodeToString(photo));
                        out.close();

                        controller.onUploadPhotoFinish(connection.getResponseCode() == 200);

                    } catch (IOException | JSONException e) {
                        controller.onUploadPhotoFinish(false);
                    }

                },

                error -> controller.onUploadPhotoFinish(false)
        );

    }


    @SuppressLint("NewApi")
    private RestOptions buildUploadingOptions(int iterID, int photoCount) throws JSONException {

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("iterID", iterID);
        jsonObject.put("photoCount", photoCount);


        return RestOptions.builder()
                .addPath("/items/photo")
                .addBody(jsonObject.toString().getBytes())
                .build();

    }


}
