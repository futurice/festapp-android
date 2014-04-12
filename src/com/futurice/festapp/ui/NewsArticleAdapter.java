package com.futurice.festapp.ui;

import java.util.List;

import com.futurice.festapp.domain.NewsArticle;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.futurice.festapp.R;

public class NewsArticleAdapter extends ListAdapter<NewsArticle> {

	public NewsArticleAdapter(Context context, List<NewsArticle> articles) {
		super(context, articles);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (convertView == null) {
			view = inflater.inflate(R.layout.news_item, null);
		}

		NewsArticle newsArticle = (NewsArticle) getItem(position);
		TextView newsTitle = ((TextView) view.findViewById(R.id.newsTitle));
		newsTitle.setText(newsArticle.getTitle());
		TextView newsDate = (TextView) view.findViewById(R.id.newsDate);
		newsDate.setText(newsArticle.getDateString());
		return view;
	}

}
