package com.futurice.festapp.ui;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

public abstract class ListAdapter<T> extends BaseAdapter {
	private Context context;
	private List<T> items;
	protected LayoutInflater inflater = null;

	public ListAdapter(Context context, List<T> items) {
		this.context = context;
		this.items = items;
		this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		// Use the array index as a unique id.
		return position;
	}

}
