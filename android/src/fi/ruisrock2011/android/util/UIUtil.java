package fi.ruisrock2011.android.util;

import fi.ruisrock2011.android.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class UIUtil {
	
	public static void showDialog(String title, String message, Context context) {
		AlertDialog alertDialog = generateDialog(title, message, context);
		alertDialog.show();
	}
	
	private static AlertDialog generateDialog(String title, String message, Context context) {
		AlertDialog.Builder builder;
		AlertDialog alertDialog;

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.message_dialog, null);

		TextView text = (TextView) layout.findViewById(R.id.messageContent);
		text.setText(message);

		builder = new AlertDialog.Builder(context);
		builder.setView(layout);
		alertDialog = builder.create();
		alertDialog.setTitle(title);
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		});
		return alertDialog;
	}

}
