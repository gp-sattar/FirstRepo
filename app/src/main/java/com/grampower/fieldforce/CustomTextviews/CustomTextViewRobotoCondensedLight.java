package com.grampower.fieldforce.CustomTextviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by kapil on 10/4/17.
 */

public class CustomTextViewRobotoCondensedLight extends TextView {

    public CustomTextViewRobotoCondensedLight(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/RobotoCondensed-Light.ttf"));

    }


}
