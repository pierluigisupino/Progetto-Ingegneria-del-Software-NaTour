package com.ingsw2122_n_03.natour.presentation.support;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import com.amplifyframework.core.Amplify;
import com.amplifyframework.predictions.models.LabelType;
import com.amplifyframework.predictions.result.IdentifyLabelsResult;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
