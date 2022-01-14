package com.ingsw2122_n_03.natour.infastructure.implementations;

import android.util.Log;

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
    private String lastkey;

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

    /** TO MODIFY, FOR TESTING INSERT THE ITER ID TO SHOW ITS PHOTOS **/
    public void downloadImagesByIter(int id) {

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("iterid", String.valueOf(id));
        if(lastkey != null)
            queryParams.put("lastkey", lastkey);

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
                            //return
                            //NO PHOTO TO DOWNLOAD
                        }else{
                            for(int i = 0; i < photoCount; ++i){
                                Log.i("PHOTO0", result.getString("photo0"));
                                Log.i("PHOTO1", result.getString("photo1"));
                                Log.i("PHOTO2", result.getString("photo2"));
                                //etc
                            }
                            lastkey = result.getString("lastkey");
                        }

                    } catch (JSONException e) {
                        Log.i("exc", e.getMessage());
                    }

                },
                error -> Log.e("Error", error.getMessage())
        );

    }

}
