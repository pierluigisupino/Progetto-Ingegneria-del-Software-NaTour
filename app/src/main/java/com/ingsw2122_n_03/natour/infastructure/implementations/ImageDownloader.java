package com.ingsw2122_n_03.natour.infastructure.implementations;

import android.annotation.SuppressLint;

import com.amplifyframework.api.rest.RestOptions;
import com.amplifyframework.core.Amplify;
import com.ingsw2122_n_03.natour.application.IterController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class ImageDownloader {

    private int delimiter;
    private String lastPhotoKey;

    private final IterController controller;

    public ImageDownloader(IterController controller) { this.controller = controller; }


    @SuppressLint("NewApi")
    public void downloadImages() {

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("iterid", String.valueOf(delimiter));
        if(lastPhotoKey != null)
            queryParams.put("lastkey", lastPhotoKey);

        RestOptions options = RestOptions.builder()
                .addPath("/items/photos")
                .addQueryParameters(queryParams)
                .build();

        Amplify.API.get(
                options,
                response -> {

                    try {

                        JSONObject result = response.getData().asJSONObject().getJSONObject("Result");
                        int photoCount = result.getInt("count");

                        if(photoCount == 0){
                            controller.onRetrievePhotosEnd();
                            return;
                        }

                        ArrayList<byte[]> images = new ArrayList<>();
                        for(int i = 0; i < photoCount; ++i){
                            images.add(Base64.getDecoder().decode(result.getString("photo"+i)));
                        }
                        lastPhotoKey = result.getString("lastkey");
                        controller.onRetrievePhotosSuccess(images);

                    } catch (JSONException e) {
                        controller.onRetrievePhotosError();
                    }

                },
                error -> controller.onRetrievePhotosError()
        );

    }

    public void ResetSession(int delimiter) {
        this.delimiter = delimiter;
        lastPhotoKey = null;
    }

}
