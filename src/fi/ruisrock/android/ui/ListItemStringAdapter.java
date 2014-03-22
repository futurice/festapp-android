package fi.ruisrock.android.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import fi.ruisrock.android.R;

public class ListItemStringAdapter extends BaseAdapter {

	private Context context;
	private List<String> items = new ArrayList<String>();
	private LayoutInflater inflater = null;

	public ListItemStringAdapter(Context context, List<String> items) {
		this.context = context;
		this.items = items;
		inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position; // Use the array index as a unique id.
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (convertView == null) {
			view = inflater.inflate(R.layout.list_string_item, null);
		}
		TextView text = (TextView) view.findViewById(R.id.text);
		text.setText(items.get(position));
		return view;
	}

}
