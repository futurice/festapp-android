package fi.ruisrock2011.android.util;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import fi.ruisrock2011.android.R;

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

}
