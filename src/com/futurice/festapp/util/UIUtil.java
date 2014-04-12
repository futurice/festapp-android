package com.futurice.festapp.util;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.futurice.festapp.R;

public class UIUtil {
	
	public static void showDialog(String title, String message, Context context) {
		final Dialog dialog = new Dialog(context, R.style.Dialog);
		dialog.setContentView(R.layout.dialog);
		((TextView) dialog.findViewById(R.id.title)).setText(title);
		((TextView) dialog.findViewById(R.id.message)).setText(message);

		Button closeButton = (Button) dialog.findViewById(R.id.positiveButton);
		closeButton.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		        dialog.dismiss();
		    }
		});
		
		dialog.show();
	}

	public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeResource(res, resId, options);

	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	    options.inJustDecodeBounds = false;

	    return BitmapFactory.decodeResource(res, resId, options);
	}
	public static Bitmap decodeSampledBitmapFromByteArray(byte[] array, int reqWidth, int reqHeight) {
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(array, 0, array.length, options);


	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	    options.inJustDecodeBounds = false;

	    return BitmapFactory.decodeByteArray(array, 0, array.length, options);
	}
	
	private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    System.err.println("reqWidth: " + reqWidth);
    System.err.println("reqHeight: " + reqHeight);
    System.err.println("height: " + height);

    System.err.println("width: " + width);

    if (height > reqHeight || width > reqWidth) {
        final int heightRatio = 1 + Math.round((float) height / (float) reqHeight);
        final int widthRatio = 1 + Math.round((float) width / (float) reqWidth);
        
        System.err.println("heightRatio: " + heightRatio);
        System.err.println("widthRatio: " + widthRatio);

        
        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
    }
    
    System.err.println("inSampleSize: " + inSampleSize);

    return inSampleSize;
}
}
