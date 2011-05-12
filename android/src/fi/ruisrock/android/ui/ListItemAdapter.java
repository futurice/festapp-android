package fi.ruisrock.android.ui;

import java.util.ArrayList;
import java.util.List;

import fi.ruisrock.android.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListItemAdapter extends BaseAdapter {

	private Context context;
	private List<ListItem> items = new ArrayList<ListItem>();
	private LayoutInflater inflater = null;

	public ListItemAdapter(Context context, List<ListItem> items) {
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
			view = inflater.inflate(R.layout.main_item, null);
		}
		TextView text = (TextView) view.findViewById(R.id.text);
		ImageView image = (ImageView) view.findViewById(R.id.image);

		text.setText(items.get(position).getName());
		image.setImageDrawable(items.get(position).getDrawable());
		return view;
	}

}
