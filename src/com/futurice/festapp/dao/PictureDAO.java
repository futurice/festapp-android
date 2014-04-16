package com.futurice.festapp.dao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import com.futurice.festapp.domain.Gig;
import com.futurice.festapp.domain.to.HTTPBackendResponse;
import com.futurice.festapp.util.HTTPUtil;
import com.futurice.festapp.util.UIUtil;

public class PictureDAO {

	public static Bitmap getFromDatabase(Gig gig, int width, int height, Context context) {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = new DatabaseHelper(context).getReadableDatabase();
			cursor = db.rawQuery("SELECT picture FROM picture WHERE id = ?",
					new String[] { gig.getArtistImage() });
			cursor.moveToFirst();
			if (cursor.getCount() > 0 && !cursor.isNull(0)) {
				byte[] pictureBytes = cursor.getBlob(0);
				return UIUtil.decodeSampledBitmapFromByteArray(pictureBytes,
						width, height);
			}
		} finally {
			if (db != null) {
				db.close();
			}
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}

	public static void updateOverHttp(Gig gig, Context context)
			throws Exception {
		HTTPBackendResponse response = new HTTPUtil().performGet(gig
				.getArtistImage());
		if (!response.isValid() || response.getStringContent() == null) {
			return;
		}
		SQLiteDatabase db = null;
		try {
			db = new DatabaseHelper(context).getWritableDatabase();
			db.beginTransaction();
			ContentValues contVal = new ContentValues();
			contVal.put("picture",
					inputStreamToByteArray(response.getContent()));
			contVal.put("id", gig.getArtistImage());
			if (db.update("picture", contVal, "id = ?",
					new String[] { gig.getArtistImage() }) == 0) {
				db.insert("picture", null, contVal);
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			if (db != null) {
				db.close();
			}
		}
	}

	private static byte[] inputStreamToByteArray(InputStream inputStream)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int reads = inputStream.read();
		while (reads != -1) {
			baos.write(reads);
			reads = inputStream.read();
		}
		return baos.toByteArray();
	}

}
