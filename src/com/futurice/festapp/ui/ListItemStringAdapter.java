package com.futurice.festapp.ui;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.futurice.festapp.R;

public class ListItemStringAdapter extends ListAdapter<String> {

	public ListItemStringAdapter(Context context, List<String> items) {
		super(context, items);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (convertView == null) {
			view = inflater.inflate(R.layout.list_string_item, null);
		}
		TextView text = (TextView) view.findViewById(R.id.text);
		text.setText((String) getItem(position));
		return view;
	}

}
