package fi.ruisrock2011.android.util;

import fi.ruisrock2011.android.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class UIUtil {
	
	public static void showErrorDialog(String title, String message, Context context) {
		AlertDialog alertDialog = generateAlertDialog(R.drawable.icon_error, title, message, context);
		alertDialog.show();
	}
	
	private static AlertDialog generateAlertDialog(int iconId, String title, String message, Context context) {
		AlertDialog.Builder builder;
		AlertDialog alertDialog;

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.message_dialog, null);

		TextView text = (TextView) layout.findViewById(R.id.message);
		text.setText(message);

		ImageView image = (ImageView) layout.findViewById(R.id.image);
		image.setImageResource(iconId);

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
