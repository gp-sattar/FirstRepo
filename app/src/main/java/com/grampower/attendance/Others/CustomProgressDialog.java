package com.grampower.attendance.Others;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.developers.coolprogressviews.CircleWithArcProgress;
import com.grampower.attendance.R;

/**
 * Created by kapil on 15/4/17.
 */

public class CustomProgressDialog extends Dialog {

    public CircleWithArcProgress cap;

    TextView tv;


    public CustomProgressDialog(Context context, String text) {
        super(context,R.style.transparentProgressDialog);
        WindowManager.LayoutParams wlmp = getWindow().getAttributes();
        wlmp.gravity = Gravity.CENTER_HORIZONTAL;
        getWindow().setAttributes(wlmp);
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(120,120);

        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(400,90);


        cap = new CircleWithArcProgress(context,null);
        cap.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));


        layout.addView(cap,params1);

        tv = new TextView(context);
        tv.setText(text);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,40);
        tv.setTextColor(Color.parseColor("#ffffff"));
        //tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        layout.addView(tv,params2);
        layout.setGravity(Gravity.CENTER);

        addContentView(layout, params);

        WindowManager.LayoutParams lp=getWindow().getAttributes();
        //set transparency of background
        lp.dimAmount=0.8f;  // dimAmount between 0.0f and 1.0f, 1.0f is completely dark
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

    }

    public void setTextMessage(String message) {
        tv.setText(message);
    }

       /* public void StartAnimation(){
            avi.show();
        }

        public void StopAnimation(){
            avi.hide();
        }*/


    @Override
    public void show() {

       // cap.set

        if (!super.isShowing())
            super.show();
    }

    @Override
    public void dismiss() {

       // a.smoothToHide();

        if (super.isShowing())
            super.dismiss();
    }
}
