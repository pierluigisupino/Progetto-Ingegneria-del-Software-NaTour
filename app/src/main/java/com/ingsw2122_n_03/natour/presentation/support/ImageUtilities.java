package com.ingsw2122_n_03.natour.presentation.support;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.exifinterface.media.ExifInterface;

import com.amplifyframework.core.Amplify;
import com.amplifyframework.predictions.models.LabelType;
import com.amplifyframework.predictions.result.IdentifyLabelsResult;
import com.ingsw2122_n_03.natour.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;


public class ImageUtilities {


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
        AtomicBoolean isUnsafe = new AtomicBoolean(true);

        Amplify.Predictions.identify(
                LabelType.MODERATION_LABELS,
                bitmap,
                result -> {
                    IdentifyLabelsResult identifyResult = (IdentifyLabelsResult) result;
                    isUnsafe.set(identifyResult.isUnsafeContent());
                    countDownLatch.countDown();
                },
                error -> {
                    Toast.makeText(context, context.getString(R.string.image_check_error), Toast.LENGTH_SHORT).show();
                    countDownLatch.countDown();
                }
        );

        countDownLatch.await();
        return isUnsafe.get();
    }

}
