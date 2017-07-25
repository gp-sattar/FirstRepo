package com.grampower.attendance.CustomTextviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by kapil on 20/5/17.
 */

public class CustomTextViewOpenSansRegular extends android.support.v7.widget.AppCompatTextView {

    public CustomTextViewOpenSansRegular(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/OpenSans-Regular.ttf"));

    }
}
