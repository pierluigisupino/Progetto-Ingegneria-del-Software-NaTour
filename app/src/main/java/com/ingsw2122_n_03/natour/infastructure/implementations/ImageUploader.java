package com.ingsw2122_n_03.natour.infastructure.implementations;

import android.annotation.SuppressLint;

import com.amplifyframework.api.rest.RestOptions;
import com.amplifyframework.core.Amplify;
import com.ingsw2122_n_03.natour.application.IterController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicInteger;

public class ImageUploader {

    private final IterController controller;

    public ImageUploader(IterController controller) {
        this.controller = controller;
    }


    public void uploadImages(int iterID, ArrayList<byte[]> imagesBytes) {

        AtomicInteger pivot = new AtomicInteger(0);

        for(int i=0; i<imagesBytes.size(); ++i) {

            RestOptions options;

            try {
                options = getUploadingOptions(iterID, imagesBytes.get(i));
            } catch (JSONException e) {
                controller.onUploadPhotoError();
                return;
            }

            Amplify.API.put(
                    options,
                    response-> {

                        try {
                            if(response.getData().asJSONObject().getInt("Code") == 200)
                                controller.onItineraryInsertFinish(pivot.getAndIncrement());
                            else
                                controller.onUploadPhotoError();
                        } catch (JSONException e) {
                            controller.onUploadPhotoError();
                        }

                    },
                    error -> controller.onUploadPhotoError()
            );

        }

    }


    public void uploadImage(int iterID, byte[] photo) {

        RestOptions options;

        try {
            options = getUploadingOptions(iterID, photo);
        } catch (JSONException e) {
            controller.onUploadPhotoFinish(false);
            return;
        }

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

    @SuppressLint("NewApi")
    private RestOptions getUploadingOptions(int iterID, byte[] photo) throws JSONException {

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("iterID", iterID);
        jsonObject.put("photo", Base64.getEncoder().encodeToString(photo));


        return RestOptions.builder()
                .addPath("/items/photo")
                .addBody(jsonObject.toString().getBytes())
                .build();

    }


}
