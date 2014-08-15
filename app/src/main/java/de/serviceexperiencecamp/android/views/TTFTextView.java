package de.serviceexperiencecamp.android.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import de.serviceexperiencecamp.android.utils.FontUtils;

/**
 * @author andre.medeiros@futurice.com
 */
public class TTFTextView extends TextView {

    public TTFTextView(Context context) {
        super(context, null);
    }

    public TTFTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        FontUtils.setCustomFont(this, context, attrs);
    }

    public TTFTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        FontUtils.setCustomFont(this, context, attrs);
    }

    @Override
    public boolean isInTouchMode() {
        return true;
    }
}