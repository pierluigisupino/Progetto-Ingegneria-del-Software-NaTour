package com.ingsw2122_n_03.natour.presentation.support;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageUtilities {

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

}
