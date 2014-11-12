package com.futurice.festapp.android.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.futurice.festapp.android.FestApp;
/**
 * Created by amed on 09.09.14.
 */
public class FileUtils {

    public static void writeToCacheFile(String filename, String content) {
        Context context = FestApp.getAppContext();
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                context.openFileOutput(filename, Context.MODE_PRIVATE)
            );
            outputStreamWriter.write(content);
            outputStreamWriter.close();
        }
        catch (Exception e) {
            Log.e("FestApp", "File write failed: " + e.toString());
        }
    }

    public static String readFromCacheFile(String filename) {
        Context context = FestApp.getAppContext();
        String ret = "";
        try {
            InputStream inputStream = context.openFileInput(filename);
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (Exception e) {
            Log.e("FestApp", "File read failed: " + e.toString());
        }
        return ret;
    }
}
