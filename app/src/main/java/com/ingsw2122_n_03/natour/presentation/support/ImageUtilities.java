package com.ingsw2122_n_03.natour.presentation.support;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.amplifyframework.core.Amplify;
import com.amplifyframework.predictions.models.LabelType;
import com.amplifyframework.predictions.result.IdentifyLabelsResult;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.presentation.itinerary.addItinerary.AddItineraryActivity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ImageUtilities {


    //@TODO IMAGE SAFE
    public Bitmap getImageBitmap(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public byte[] getBytes(Context context, Uri imageUri) throws IOException {

        InputStream iStream = context.getContentResolver().openInputStream(imageUri);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int len;

        while ((len = iStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public ArrayList<Double> getImageLocation(Context context, byte[] bytes)  {

        ArrayList<Double> coordinates = new ArrayList<>();

        String latitudeRef = "";
        String latitude = "";
        String longitudeRef = "";
        String longitude = "";

        double dLatitude;
        double dLongitude;

        try {

            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            Metadata metadata = ImageMetadataReader.readMetadata(bis);

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

            if(!latitude.equals("") && !longitude.equals("")) {

                latitude = latitude.replace(',', '.');
                longitude = longitude.replace(',', '.');

                double latitudeDegrees = Double.parseDouble(latitude.substring(0, (latitude.indexOf("° "))));
                double latitudeMinutes = Double.parseDouble(latitude.substring((latitude.indexOf("° ")) + 1, (latitude.indexOf("'"))));
                double latitudeSeconds = Double.parseDouble(latitude.substring((latitude.indexOf("'")) + 1, latitude.indexOf('"')));

                double longitudeDegrees = Double.parseDouble(longitude.substring(0, (longitude.indexOf("° "))));
                double longitudeMinutes = Double.parseDouble(longitude.substring((longitude.indexOf("° ")) + 1, (longitude.indexOf("'"))));
                double longitudeSeconds = Double.parseDouble(longitude.substring((longitude.indexOf("'")) + 1, longitude.indexOf('"')));

                dLatitude = latitudeDegrees + (((latitudeMinutes * 60) + latitudeSeconds) / 3600);
                dLongitude = longitudeDegrees + (((longitudeMinutes * 60) + longitudeSeconds) / 3600);

                if (latitudeRef.equals("S")) {
                    dLatitude = -dLatitude;
                }

                if (longitudeRef.equals("W")) {
                    dLongitude = -dLongitude;
                }

                coordinates.add(dLatitude);
                coordinates.add(dLongitude);

                Log.i("Coordinates", "Latitude: " + dLatitude + " Longitude: " + dLongitude);

                return coordinates;
            }

        } catch (ImageProcessingException | IOException e) {
            ((AddItineraryActivity) context).onFail(context.getString(R.string.generic_error));
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
