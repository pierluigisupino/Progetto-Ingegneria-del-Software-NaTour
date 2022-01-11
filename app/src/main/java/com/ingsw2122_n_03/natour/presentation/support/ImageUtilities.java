package com.ingsw2122_n_03.natour.presentation.support;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.exifinterface.media.ExifInterface;

import com.amplifyframework.core.Amplify;
import com.amplifyframework.predictions.models.LabelType;
import com.amplifyframework.predictions.result.IdentifyLabelsResult;

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

    public double[] getImageLocation(byte[] bytes)  {

        double[] latLong = new double[2];

        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            ExifInterface exifInterface = null;
            try {
                exifInterface = new ExifInterface(bis);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            assert exifInterface != null;
            latLong = exifInterface.getLatLong();
        }

        return latLong;

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
