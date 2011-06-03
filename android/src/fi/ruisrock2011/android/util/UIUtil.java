package fi.ruisrock2011.android.util;

import fi.ruisrock2011.android.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class UIUtil {
	
	public static void showDialog(String title, String message, Context context) {
		/*
		AlertDialog alertDialog = generateDialog(title, message, context);
		alertDialog.show();
		*/
		
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
