package com.grampower.fieldforce.CustomTextviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by kapil on 24/4/17.
 */

public class CustomTextViewRobotoBold extends android.support.v7.widget.AppCompatTextView {

    public CustomTextViewRobotoBold(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/Roboto-Bold.ttf"));

    }
}
