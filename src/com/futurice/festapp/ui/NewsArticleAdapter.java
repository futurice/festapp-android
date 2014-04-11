package com.futurice.festapp.ui;

import java.util.ArrayList;
import java.util.List;

import com.futurice.festapp.domain.NewsArticle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.futurice.festapp.R;

public class NewsArticleAdapter extends BaseAdapter {

	private Context context;
	private List<NewsArticle> items = new ArrayList<NewsArticle>();
	private LayoutInflater inflater = null;
	
	public NewsArticleAdapter(Context context, List<NewsArticle> articles) {
		this.context = context;
		this.items = articles;
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
		return position; // Use the array index as a unique id.
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    View view = convertView;
	    if (convertView == null) {
	      view = inflater.inflate(R.layout.news_item, null);
	    }
	    TextView newsTitle = (TextView) view.findViewById(R.id.newsTitle);
	    TextView newsDate = (TextView) view.findViewById(R.id.newsDate);
	    
	    newsTitle.setText(items.get(position).getTitle());
	    newsDate.setText(items.get(position).getDateString());
	    return view;
	  }

}
