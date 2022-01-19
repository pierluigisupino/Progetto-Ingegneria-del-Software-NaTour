package com.ingsw2122_n_03.natour.infastructure.implementations;

import android.annotation.SuppressLint;

import com.amplifyframework.api.rest.RestOptions;
import com.amplifyframework.core.Amplify;
import com.ingsw2122_n_03.natour.application.IterController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Base64;

public class ImageUploader {

    private final IterController controller;

    public ImageUploader(IterController controller) {
        this.controller = controller;
    }


    @SuppressLint("NewApi")
    public void uploadImages(int iterID, ArrayList<byte[]> imagesBytes) {

        int photoCount = imagesBytes.size();
        String[] encodedStrings = new String[photoCount];

        for(int i=0; i<photoCount; ++i) {
            encodedStrings[i] = Base64.getEncoder().encodeToString(imagesBytes.get(i));
        }


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("iterID", iterID);
            jsonObject.put("photo_count", photoCount);
            for(int i=0; i<photoCount; ++i) {
                jsonObject.put("photo"+i, encodedStrings[i]);
            }
        } catch (JSONException e) {
            controller.onItineraryInsertFinish(false);
            return;
        }

        RestOptions options = RestOptions.builder()
                .addPath("/items/photos")
                .addBody(jsonObject.toString().getBytes())
                .build();

        Amplify.API.post(
                options,
                response-> {

                    try {
                        controller.onItineraryInsertFinish(response.getData().asJSONObject().getInt("Code") == 200);
                    } catch (JSONException e) {
                        controller.onItineraryInsertFinish(false);
                    }

                },
                error -> controller.onItineraryInsertFinish(false)
        );

    }

    //TODO CODE REVIEW
    @SuppressLint("NewApi")
    public void uploadImage(int iterID, byte[] photo) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("iterID", iterID);
            jsonObject.put("photo", Base64.getEncoder().encodeToString(photo));
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        RestOptions options = RestOptions.builder()
                .addPath("/items/photo")
                .addBody(jsonObject.toString().getBytes())
                .build();

        Amplify.API.put(
                options,
                response-> {

                    try {
                        controller.onUploadPhotoFinish(response.getData().asJSONObject().getInt("Code") == 200);
                    } catch (JSONException e) {
                        controller.onUploadPhotoFinish(false);
                    }

                },
                error -> controller.onUploadPhotoFinish(false)
        );

    }


}
