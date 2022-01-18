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
import java.util.concurrent.CountDownLatch;


public class ImageUtilities {

    private static boolean isUnsafe;

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

        try {
            ExifInterface exifInterface = new ExifInterface(bis);
            latLong = exifInterface.getLatLong();
        } catch (IOException ignored) { }

        return latLong;

    }

    public boolean isImageUnsafe(Context context, Uri imageUri) throws InterruptedException, IOException {

        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);

        CountDownLatch countDownLatch = new CountDownLatch(1);

        Amplify.Predictions.identify(
                LabelType.MODERATION_LABELS,
                bitmap,
                result -> {
                    IdentifyLabelsResult identifyResult = (IdentifyLabelsResult) result;
                    isUnsafe = identifyResult.isUnsafeContent();
                    countDownLatch.countDown();
                },
                error -> {
                    Log.e("NaTour", "predictions errore"); // TODO: 18/01/2022 da getsire
                    countDownLatch.countDown();
                }
        );
        countDownLatch.await();
        return isUnsafe;
    }

}
