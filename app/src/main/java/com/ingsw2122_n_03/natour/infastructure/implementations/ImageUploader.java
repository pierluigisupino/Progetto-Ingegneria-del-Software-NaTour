package com.ingsw2122_n_03.natour.infastructure.implementations;

import android.os.Build;

import com.amplifyframework.api.rest.RestOptions;
import com.amplifyframework.core.Amplify;
import com.ingsw2122_n_03.natour.application.IterController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class ImageUploader {

    private final IterController controller;

    private int delimiter;
    private String lastPhotoKey;

    public ImageUploader(IterController controller) {
        this.controller = controller;
    }


    public void uploadImages(int iterID, ArrayList<byte[]> imagesBytes) {

        int photoCount = imagesBytes.size();
        String[] encodedStrings = new String[photoCount];

        for(int i=0; i<photoCount; ++i) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
            controller.onItineraryInsertComplete(false);
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
                        controller.onItineraryInsertComplete(response.getData().asJSONObject().getInt("Code") == 200);
                    } catch (JSONException e) {
                        controller.onItineraryInsertComplete(false);
                    }

                },
                error -> controller.onItineraryInsertComplete(false)
        );

    }


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
                            controller.onRetrievePhotosFinish();
                            return;
                        }

                        ArrayList<byte[]> images = new ArrayList<>();
                        for(int i = 0; i < photoCount; ++i){
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                images.add(Base64.getDecoder().decode(result.getString("photo"+i)));
                            }
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
