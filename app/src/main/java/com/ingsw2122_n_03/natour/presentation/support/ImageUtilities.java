package com.ingsw2122_n_03.natour.presentation.support;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import com.amplifyframework.core.Amplify;
import com.amplifyframework.predictions.models.LabelType;
import com.amplifyframework.predictions.result.IdentifyLabelsResult;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ImageUtilities {


    public void addImageBitmap(Uri imageUri, Context context, ArrayList<Bitmap> bitmapArray) throws IOException{

        Bitmap bitmap;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.getContentResolver(), imageUri));
        else
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);

        //isImageSafe(bitmap, bitmapArray);
        bitmapArray.add(bitmap);
    }


    public byte[] createImageBytes(Bitmap imageBitmap){

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bytes;

        imageBitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
        bytes = byteArrayOutputStream.toByteArray();

        return bytes;

    }

    /* TODO: 10/01/2022
        - tipo di ritorno temporaneo
        - Uri deve essere sostituito da byte[] lo stesso che verrà inserito nel db
        - le coordinate sono recuperate nel formato DMS (vedi log) devono essere convertite in double */

    public String[] getImageLocation(Context context, Uri uri){

        String[] coordinates = new String[2];

        String latitudeRef = "";
        String latitude = "";
        String longitudeRef = "";
        String longitude = "";

        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);

            Metadata metadata = ImageMetadataReader.readMetadata(inputStream);

            for (Directory directory : metadata.getDirectories()) {

                if (directory.getName().equals("GPS")) {
                    for (Tag tag : directory.getTags()) {

                        switch (tag.getTagName()){
                            case "GPS Latitude Ref":
                                latitudeRef = tag.getDescription();
                                break;
                            case "GPS Latitude":
                                latitude = tag.getDescription();
                                break;
                            case "GPS Longitude Ref":
                                longitudeRef = tag.getDescription();
                                break;
                            case "GPS Longitude":
                                longitude = tag.getDescription();
                                break;
                        }
                    }
                }
            }

            String mLatitude = latitude + latitudeRef;
            String mLongitude = longitude + longitudeRef;

            coordinates[0] = mLatitude;
            coordinates[1] = mLongitude;

            Log.i("Coordinates", "Latitude: " + coordinates[0] + " Longitude: " + coordinates[1]);

            return  coordinates;

        } catch (ImageProcessingException | IOException e) {
            e.printStackTrace();
        }

        return coordinates;
    }

    //@TODO VERIFY SYNC
    private void isImageSafe(Bitmap image, ArrayList<Bitmap> bitmapArray){

        Amplify.Predictions.identify(
                LabelType.MODERATION_LABELS,
                image,
                result -> {
                    IdentifyLabelsResult identifyResult = (IdentifyLabelsResult) result;
                    if(!identifyResult.isUnsafeContent())
                        bitmapArray.add(image);
                        /** Set Adapter here is a good choice? **/
                },
                error -> { }
        );
    }

}
