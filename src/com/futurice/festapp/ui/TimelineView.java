package com.futurice.festapp.ui;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.futurice.festapp.domain.Gig;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class TimelineView extends ScrollView {
	private Map<String, List<Gig>> gigs;
	public TimelineView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	public TimelineView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		gigs = new HashMap<String, List<Gig>>();
	}
	public void addStage(String name, List<Gig> gigs) {
		this.gigs.put(name, gigs);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}
}
