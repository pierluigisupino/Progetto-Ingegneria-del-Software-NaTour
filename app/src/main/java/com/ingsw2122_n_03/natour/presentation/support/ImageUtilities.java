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

        int len;
        byte[] buffer = new byte[1024];
        while ((len = iStream.read(buffer)) != -1) { byteBuffer.write(buffer, 0, len); }
        byteBuffer.close();

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

}
