package com.grampower.fieldforce.CustomTextviews;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by sam on 20/7/17.
 */

public class CustomTextViewNeoSansBold extends AppCompatTextView {

    public CustomTextViewNeoSansBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/neo_sans_bold.ttf"));
    }

}
