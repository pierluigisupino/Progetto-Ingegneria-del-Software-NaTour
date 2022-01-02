package com.ingsw2122_n_03.natour.infastructure.implementations;

import android.util.Log;

import com.amplifyframework.api.rest.RestOptions;
import com.amplifyframework.core.Amplify;
import com.ingsw2122_n_03.natour.application.IterController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Base64;

public class ImageUploader {

    private IterController controller;

    public ImageUploader(IterController controller) {
        this.controller = controller;
    }

    public void uploadImages(int iterID, ArrayList<byte[]> imagesBytes) {

        int photoCount = imagesBytes.size();
        String[] encodedStrings = new String[photoCount];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            for(int i=0; i<photoCount; ++i) {
                encodedStrings[i] = Base64.getEncoder().encodeToString(imagesBytes.get(i));
            }
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("iterID", iterID);
            jsonObject.put("photo_count", photoCount);
            for(int i=0; i<photoCount; ++i) {
                jsonObject.put("photo"+i, encodedStrings[i]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RestOptions options = RestOptions.builder()
                .addPath("/items/photos")
                .addBody(jsonObject.toString().getBytes())
                .build();

        Amplify.API.post(options,
                response-> controller.onItineraryInsertComplete(),
                error -> {}
                );

    }

}
