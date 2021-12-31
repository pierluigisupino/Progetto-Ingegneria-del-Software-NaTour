package com.ingsw2122_n_03.natour.presentation.support;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import androidx.exifinterface.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import com.amplifyframework.core.Amplify;
import com.amplifyframework.predictions.models.LabelType;
import com.amplifyframework.predictions.result.IdentifyLabelsResult;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtilities {

    private static boolean isSafe = false;

    public Bitmap createImageBitmap(Uri imageUri, Context context) throws IOException{

        Bitmap bitmap;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.getContentResolver(), imageUri));
        else
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);

        return bitmap;

    }


    public byte[] createImageBytes(Bitmap imageBitmap){

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bytes;

        imageBitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
        bytes = byteArrayOutputStream.toByteArray();

        return bytes;

    }

    public double[] getPositionFromUri(Uri imageUri, Context context) throws IOException {

        double[] latLong = new double[2];

        InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            ExifInterface exifInterface = new ExifInterface(inputStream);
            latLong = exifInterface.getLatLong();
        }

        return latLong;

    }

    public boolean isImageSafe(Bitmap image){

        Amplify.Predictions.identify(
                LabelType.MODERATION_LABELS,
                image,
                result -> {
                    IdentifyLabelsResult identifyResult = (IdentifyLabelsResult) result;
                    isSafe = identifyResult.isUnsafeContent();
                },
                error -> {
                    isSafe = false;
                    Log.e("NaTour", "errore");
                }
        );

        return isSafe;
    }
}
