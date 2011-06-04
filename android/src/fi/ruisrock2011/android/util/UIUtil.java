package fi.ruisrock2011.android.util;

import fi.ruisrock2011.android.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
	
	public static void showInitialFavoriteInfoOnFirstVisit(Context context) {
		SharedPreferences pref = context.getSharedPreferences(RuisrockConstants.PREFERENCE_GLOBAL, Context.MODE_PRIVATE);
		final String key = "showFavoriteInfo";
		
		// TODO: Start DEBUG
		/*
		Editor ed = pref.edit();
		ed.putBoolean(key, true);
		ed.commit();
		*/
		// END DEBUG
		
		if (pref.getBoolean(key, true)) {
			Editor editor = pref.edit();
			editor.putBoolean(key, false);
			editor.commit();
			UIUtil.showDialog(context.getString(R.string.timelineActivity_initialInfo_title), context.getString(R.string.timelineActivity_initialInfo_msg), context);
		}
	}

}
