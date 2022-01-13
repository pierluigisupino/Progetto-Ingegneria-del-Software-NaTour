package com.ingsw2122_n_03.natour.presentation.support;

import android.content.Context;
import android.net.Uri;

import androidx.exifinterface.media.ExifInterface;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


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

}
