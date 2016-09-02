package com.idt.test.app.function;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.idt.test.app.R;

import java.io.File;

/**
 * Created by Omid Heshmatinia on 9/1/2016.
 */
public class ImageHelper {

    Context mContext;

    public ImageHelper(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * It get an address and width and height. then make a bitmap from that address. and at last convert that to
     * the given height and width
     * @param fileAddress String
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap decodeBitmapFromFile(String fileAddress,int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fileAddress, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap image= BitmapFactory.decodeFile(fileAddress, options);
        image=rotateBitmapByDegree(image,180);
        return image;
    }

    /**
     * Used to rotate any bitmap to any amount of degree
     * @param icon
     * @param degree
     * @return the bitmap
     */
    public static Bitmap rotateBitmapByDegree(Bitmap icon ,int degree){
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(icon,icon.getWidth(),icon.getHeight(),true);
        icon = Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap .getWidth(), scaledBitmap .getHeight(), matrix, true);
        return icon;
    }

    /**
     * calculate the sample size of the bitmap if it is too large.
     * <p>
     *     the inSampleSize is used to reduce the dimension of a bitmap to prevent memory leak
     * </p>
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return int InSampleSize according to given width and height
     */
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
