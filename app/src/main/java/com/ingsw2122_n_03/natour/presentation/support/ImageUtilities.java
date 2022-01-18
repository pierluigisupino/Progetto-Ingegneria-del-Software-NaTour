package com.ingsw2122_n_03.natour.presentation.support;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.exifinterface.media.ExifInterface;

import com.amplifyframework.core.Amplify;
import com.amplifyframework.predictions.models.LabelType;
import com.amplifyframework.predictions.result.IdentifyLabelsResult;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class ImageUtilities {


    public byte[] getBytes(Context context, Uri imageUri) throws IOException {

        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);

        // TODO: 18/01/2022 sincronizzare e gestire
        isImageSafe(bitmap);

        InputStream iStream = context.getContentResolver().openInputStream(imageUri);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int len;

        while ((len = iStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }


    public double[] getImageLocation(byte[] bytes)  {

        double[] latLong = new double[2];

        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);

        try {
            ExifInterface exifInterface = new ExifInterface(bis);
            latLong = exifInterface.getLatLong();
        } catch (IOException ignored) { }

        return latLong;

    }

    public void isImageSafe(Bitmap image){

        Amplify.Predictions.identify(
                LabelType.MODERATION_LABELS,
                image,
                result -> {
                    IdentifyLabelsResult identifyResult = (IdentifyLabelsResult) result;
                    boolean isUnsafe = identifyResult.isUnsafeContent();
                },
                error -> { Log.e("NaTour", "errore"); }
        );
    }

}
